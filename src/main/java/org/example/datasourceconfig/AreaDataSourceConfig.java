package org.example.datasourceconfig;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 在指定多个数据源 （比如同一个数据库的不同数据表，或者不同数据库的不同数据表）的时候，
 * Spring 就无法完成自动配置，此时就需要设计数据源配置类去手动配置每一个数据源，
 * 具体来说需要指定以下 3 个组件：
 * <ol>
 *     <li>数据源 (DataSource)：管理数据库连接池，负责分配 / 释放数据库连接。</li>
 *     <li>
 *         实体管理器工厂 (LocalContainerEntityManagerFactoryBean)：
 *         管理 JPA 的实体管理器（EntityManager）的生命周期。
 *         而实体管理器则管理每个 JPA 实体（由 Entity 注解）与数据表的映射关系。
 *     </li>
 *     <li>
 *         事务管理器 (PlatformTransactionManager)：
 *         控制事务边界，确保数据库操作的原子性（即确保所有操作全部成功，否则就全部回滚）。
 *     </li>
 * </ol>
 *
 */
class DataSourceConfigInstruction {}

/**
 * 主数据源配置类。
 * <li>Configuration 注解表明，这是一个 Spring 配置类，用于定义 Bean。</li>
 * <li>EnableJpaRepositories 注解表明，启用 JPA 仓库 (Repository) 的功能，并指定下面的参数：</li>
 * <ol>
 *     <li>basePackages: 扫描 Repository 接口的包路径（这里是 org.example.databaseaccess.area.AreaRepository 类）</li>
 *     <li>entityManagerFactoryRef: 指定该数据源对应的实体管理器工厂 Bean 名称。</li>
 *     <li>transactionManagerRef: 指定该数据源对应的事务管理器 Bean 名称</li>
 * </ol>
 * */
@Configuration
@EnableJpaRepositories(
        basePackages            = "org.example.databaseaccess.area",
        entityManagerFactoryRef = "primaryEntityManagerFactory",
        transactionManagerRef   = "primaryTransactionManager"
)
public class AreaDataSourceConfig
{
    /**
     * 获取主数据源的工厂方法。
     * <ol>
     *     <li>Primary 注解表明，该 Bean 为主数据源，当存在多个数据源时，Spring 会优先使用这个 Bean。</li>
     *     <li>
     *          Bean 注解是 Spring-Boot 的核心注解。<br>
     *          被它注解的方法所返回的资源会放入 Spring 容器，由它进行注入，生命周期管理等操作。
     *          它的 name 属性的默认值就是方法名，这里写上保证可读性。
     *     </li>
     *      <li>
     *           ConfigurationProperties 注解用于将 application.properties 配置中，
     *           以 spring.datasource.primary 为前缀 (prefix) 的配置绑定到此数据源。
     *      </li>
     * </ol>
     *
     * @return 数据源
     * */
    @Primary
    @Bean(name = "primaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primaryDataSource()
    {
        /*
          create() 是 DataSourceBuilder 类的工厂方法，用于构建一个数据源构建器。
          type()   指定连接池类型为 Hikari Connect Pool。
          build()  执行构建。
        */
        return DataSourceBuilder.create()
                                .type(HikariDataSource.class)
                                .build();
    }

    /**
     * 实体管理器的工厂方法。
     * <li>Primary 注解表明，该 Bean 为主数据源，当存在多个数据源时，Spring 会优先使用这个 Bean。</li>
     *
     * @param dataSource 数据源，这里被 Qualifier 注解，
     *                   表明数据源是 primaryDataSource() 这个方法返回的实例注入的，避免歧义。
     *
     * @return 负责创建 JPA 的 EntityManagerFactory，此为 JPA 的核心配置。
     */
    @Primary
    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            @Qualifier("primaryDataSource") DataSource dataSource
    )
    {
        var localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        // 设置数据源
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);

        // 设置数据表实体类的扫描包路径
        localContainerEntityManagerFactoryBean.setPackagesToScan("org.example.databaseaccess.area");

        // 指定 JPA 的实现厂商（这里是 Hibernate JPA）
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        // JPA 属性配置表
        Map<String, Object> properties = new HashMap<>();

        // 1. 自动根据实体类更新表结构
        properties.put("hibernate.hbm2ddl.auto", "update");

        // 2. 指定数据库方言，这条可以省略 Hibernate 6 代及以上都可以自行识别。
        // properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

        // 提交配置属性
        localContainerEntityManagerFactoryBean.setJpaPropertyMap(properties);

        return localContainerEntityManagerFactoryBean;
    }

    /**
     * 事物管理器的 Bean 定义。
     *
     * <li>
     *     Qualifier 注解表明，
     *     实体管理器工厂是由 primaryEntityManagerFactory 返回的实例注入的，
     *     这样可以避免歧义。
     * </li>
     *
     * @param entityManagerFactory 实体管理器工厂实例
     *
     * @return 通过实体管理器工厂实例构建的 JPA 事务管理器。
     */
    @Primary
    @Bean(name = "primaryTransactionManager")
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("primaryEntityManagerFactory") @NotNull
            LocalContainerEntityManagerFactoryBean entityManagerFactory
    ) {
        return new JpaTransactionManager(
                /*
                    这里使用 Objects.requireNonNull()，
                    确保实体管理器工厂实例返回的实体管理器不为空，
                    不过 Spring 初始化时就能保证其存在，这里只为了增加可读性。
                */
                Objects.requireNonNull(entityManagerFactory.getObject())
        );
    }
}
