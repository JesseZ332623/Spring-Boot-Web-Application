# Spring-Boot 数据源配置类详解
---

### **1. 类定义部分**
```java
@Configuration
@EnableJpaRepositories(
    basePackages            = "org.example.databaseaccess.area",
    entityManagerFactoryRef = "primaryEntityManagerFactory",
    transactionManagerRef   = "primaryTransactionManager"
)
public class PrimaryDataSourceConfig {
```
- **`@Configuration`**：
  表明这是一个 Spring 配置类，用于定义 Bean。

- **`@EnableJpaRepositories`**：
  启用 JPA 仓库（Repository）功能，并指定以下参数：
  - `basePackages`：扫描 Repository 接口的包路径（如 `area` 包下的 `XxxRepository`）。
  - `entityManagerFactoryRef`：指定该数据源对应的实体管理器工厂 Bean 名称。
  - `transactionManagerRef`：指定该数据源对应的事务管理器 Bean 名称。

---

### **2. 主数据源 Bean 定义**
```java
@Primary
@Bean(name = "primaryDataSource")
@ConfigurationProperties(prefix = "spring.datasource.primary")
public DataSource primaryDataSource() {
    return DataSourceBuilder.create()
                            .type(HikariDataSource.class)
                            .build();
}
```
- **`@Primary`**：
  标记此 Bean 为“主”数据源，当存在多个数据源时，Spring 会优先使用此 Bean。

- **`@Bean(name = "primaryDataSource")`**：
  定义一个名为 `primaryDataSource` 的 Bean，类型是 `DataSource`。

- **`@ConfigurationProperties(prefix = "spring.datasource.primary")`**：
  将 `application.properties` 中以 `spring.datasource.primary` 开头的配置绑定到此数据源。例如：
  ```properties
  spring.datasource.primary.jdbc-url=...
  spring.datasource.primary.username=...
  spring.datasource.primary.password=...
  ```

- **`DataSourceBuilder.create()`**：
  创建一个 `DataSource` 的构建器。

- **`.type(HikariDataSource.class)`**：
  明确指定使用 HikariCP 连接池（默认是 Spring Boot 自动选择的，但显式声明更安全）。

---

### **3. 实体管理器工厂 Bean 定义**
```java
@Primary
@Bean(name = "primaryEntityManagerFactory")
public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
    @Qualifier("primaryDataSource") DataSource dataSource
) {
    var localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
    localContainerEntityManagerFactoryBean.setDataSource(dataSource);
    localContainerEntityManagerFactoryBean.setPackagesToScan("org.example.databaseaccess.area");
    localContainerEntityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

    Map<String, Object> properties = new HashMap<>();
    properties.put("hibernate.hbm2ddl.auto", "update");
    properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
    localContainerEntityManagerFactoryBean.setJpaPropertyMap(properties);

    return localContainerEntityManagerFactoryBean;
}
```
- **`@Primary` 和 `@Bean`**：
  同上，标记此实体管理器工厂为“主”，并定义 Bean 名称。

- **`@Qualifier("primaryDataSource")`**：
  明确注入名为 `primaryDataSource` 的数据源 Bean，避免歧义。

- **`LocalContainerEntityManagerFactoryBean`**：
  负责创建 JPA 的 `EntityManagerFactory`，是 JPA 的核心配置。

- **`setDataSource(dataSource)`**：
  绑定数据源，告知 JPA 使用哪个数据库。

- **`setPackagesToScan`**：
  扫描实体类（`@Entity`）的包路径，此处是 `org.example.databaseaccess.area`。

- **`setJpaVendorAdapter`**：
  指定 JPA 实现厂商（这里是 Hibernate）。

- **`JPA 属性配置（hibernate.*）**：
  - `hibernate.hbm2ddl.auto=update`：自动根据实体类更新数据库表结构。
  - `hibernate.dialect`：指定数据库方言（但根据日志提示，Hibernate 6+ 可以自动检测，此处可移除）。

---

### **4. 事务管理器 Bean 定义**
```java
@Primary
@Bean(name = "primaryTransactionManager")
public PlatformTransactionManager primaryTransactionManager(
    @Qualifier("primaryEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory
) {
    return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
}
```
- **`@Primary` 和 `@Bean`**：
  同上，标记此事务管理器为“主”。

- **`@Qualifier`**：
  注入名为 `primaryEntityManagerFactory` 的实体管理器工厂 Bean。

- **`JpaTransactionManager`**：
  负责管理 JPA 事务，确保数据库操作的原子性。

- **`Objects.requireNonNull`**：
  确保 `entityManagerFactory.getObject()` 不为空（Spring 初始化时会保证其存在）。

---

### **为什么需要这么多配置？**
1. **多数据源场景**：
   当有多个数据源时，必须明确指定每个数据源、实体管理器、事务管理器的关联关系，否则 Spring 无法自动处理。

2. **职责分离**：
   - **数据源**：管理数据库连接。
   - **实体管理器工厂**：管理 JPA 的 `EntityManager` 生命周期。
   - **事务管理器**：控制事务边界。

3. **Spring Boot 自动配置的局限性**：
   Spring Boot 默认只能配置单数据源。多数据源时需手动定义所有相关 Bean。

---

### **可以简化的地方？**
1. **移除方言配置**：
   根据日志提示，Hibernate 6+ 可以自动检测数据库方言，移除 `hibernate.dialect` 属性：
   ```java
   // 删除此行
   // properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
   ```

2. **合并属性配置**：
   如果不需要额外 Hibernate 属性，可简化 JPA 配置（但 `hibernate.hbm2ddl.auto` 通常需要保留）。

---

### **总结**
- **核心流程**：
  数据源 → 实体管理器工厂 → 事务管理器 → Repository。

- **每个 Bean 的作用**：
  - `DataSource`：连接数据库。
  - `LocalContainerEntityManagerFactoryBean`：创建 JPA 实体管理器。
  - `JpaTransactionManager`：管理事务。

- **注解的关键性**：
  `@Primary`、`@Qualifier` 确保多数据源环境下 Spring 能正确注入依赖。

这样逐层配置后，`org.example.databaseaccess.area` 包下的 Repository 就能正确使用主数据源操作数据库了！