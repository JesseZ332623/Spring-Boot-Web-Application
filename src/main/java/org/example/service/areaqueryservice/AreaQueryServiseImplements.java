package org.example.service.areaqueryservice;

import jakarta.transaction.Transactional;
import org.example.databaseaccess.area.Area;
import org.example.databaseaccess.area.AreaRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.util.List;

/**
 * 服务端，调用数据访问层的 CRUD 方法操作数据，将结果作为相应返回给控制器端。
 */
@Service
public class AreaQueryServiseImplements implements AreaQueryService
{
    /**
     * Autowired 注解类在 Spring-Boot 中是一个很重要的注解类，
     * 通常数据访问层的接口 XXXRepository 类都会继承自 JpaRepository 接口，在运行时，
     * Spring Data JPA 会自动生成 XXXRepository 接口的实现类，将其注册为 Spring 容器中的 Bean。
     * 然后在生成 QueryServiseImplements 类的实例时，Spring 会查找容器中类型为 AreaRepository 的 Bean，
     * 将其注入到字段 areaRepository 中，此后就可以直接使用这个字段，而不需要手动实例化。
     */
    @Autowired
    private AreaRepository areaRepository;  // 通过注入的方式放入 AreaRepository

    /**
     * 为 updateAreaByID() 编写的辅助函数，
     * 用于检测新字符串是否为空串，以及新旧两串是否相同。
     */
    private boolean stringColumnCheck(String oldStr, String newStr) {
        return (StringUtils.hasLength(newStr) && !oldStr.equals(newStr));
    }

    /**
     * 通过主键 id 查询对应的数据行。
     *
     * @param id 待查询的主键 id
     */
    @Override
    public Area getAreaById(int id)
    {
        /*
            调用数据访问层的 findById() 方法查询指定 id 对应的整条数据，
            如果查不到就抛出一个运行时异常。
        */
        return this.areaRepository.findById(id).orElseThrow(
                () -> new RuntimeException(
                        String.format(
                                "Query id = %d not exsist in this data table.", id
                        )
                )
        );
    }

    /**
     * 在数据表中插入一条新的数据。
     *
     * @param area 从控制器端通过 POST 请求发出的数据。（我这边使用 PostMan 发起 Get 以外的请求）。
     */
    @Override
    @Transactional
    public Integer addNewItem(@NotNull Area area)
    {
        /*
        * 调用数据访问层的 findByid() 方法。
        * 这是我自定义的方法，虽然大部分情况下主键映射的值是唯一的。
        */
        List<Area> uniqueRes = this.areaRepository.findByid(area.getId());

        // 检查要插入的数据行的主键是否在数据表中有所对应的数据行。
        if (!CollectionUtils.isEmpty(uniqueRes))
        {
            // 如果要插入的数据在表中已经存在，抛出非法状态异常。
            throw new IllegalStateException(
                    String.format("ID {%d} has been exsist.", area.getId())
            );
        }

        // 将要插入的数据行保存到表中。
        this.areaRepository.save(area);

        // 返回完成插入的数据行的 id 作为响应。
        return area.getId();
    }

    /**
     * 通过主键 id 在表中搜索并删除对应的数据行。
     *
     * @param id 待查询的主键 id，由控制器端使用 DELETE 请求发出。
     */
    @Override
    public Integer deleteAreaById(int id)
    {
        /*
        * 通过 id 查询表中是否存在这行数据，如果找不到数据就抛出非法状态异常。
        */
        this.areaRepository.findById(id).orElseThrow(
                () -> new IllegalStateException(
                        String.format("area ID {%d} doesn't exsist.", id)
                )
        );

        // 删除指定的数据行
        this.areaRepository.deleteById(id);

        return id;  // 返回被删除的数据行的主键 id 作为响应。
    }

    /**
     * 更新表中指定主键对应的数据行中的所有字段。
     */
//    @Override
//    @Transactional
//    public area updateAreaByID(
//            int id, int provinceID, int cityID, int areaID,
//            String proivnce, String city, String area,
//            String code, String center
//    )
//    {
//        area areaDB = this.areaRepository.findById(id).orElseThrow(
//                () -> new IllegalStateException(
//                        String.format("area ID {%d} doesn't exsist.\n", id)
//                )
//        );
//
//        areaDB.setId(id);
//        areaDB.setProvinceID(provinceID);
//        areaDB.setCityID(cityID);
//        areaDB.setAreaID(areaID);
//
//        if (this.stringColumnCheck(areaDB.getProvinceName(), proivnce)) {
//            areaDB.setProvinceName(proivnce);
//        }
//        if (this.stringColumnCheck(areaDB.getCityName(), city)) {
//            areaDB.setCityName(city);
//        }
//        if (this.stringColumnCheck(areaDB.getAreaName(), area)) {
//            areaDB.setAreaName(area);
//        }
//        if (this.stringColumnCheck(areaDB.getCode(), code)) {
//            areaDB.setCode(code);
//        }
//        if (this.stringColumnCheck(areaDB.getCenterPos(), center)) {
//            areaDB.setCenterPos(center);
//        }
//
//        return this.areaRepository.save(areaDB);
//    }

    @Override
    @Transactional
    public Area updateAreaByID(@NotNull Area updateArea)
    {
        Area originalArea = this.areaRepository.findById(updateArea.getId()).orElseThrow(
                () -> new IllegalStateException(
                        String.format("area ID {%d} doesn't exsist.", updateArea.getId())
                )
            );

        originalArea.setId(updateArea.getId());
        originalArea.setProvinceID(updateArea.getProvinceID());
        originalArea.setCityID(updateArea.getCityID());
        originalArea.setAreaID(updateArea.getAreaID());

        if (this.stringColumnCheck(originalArea.getProvinceName(), updateArea.getProvinceName())) {
            originalArea.setProvinceName(updateArea.getProvinceName());
        }
        if (this.stringColumnCheck(originalArea.getCityName(), updateArea.getCityName())) {
            originalArea.setCityName(updateArea.getCityName());
        }
        if (this.stringColumnCheck(originalArea.getAreaName(), updateArea.getAreaName())) {
            originalArea.setAreaName(updateArea.getAreaName());
        }
        if (this.stringColumnCheck(originalArea.getCode(), updateArea.getCode())) {
            originalArea.setCode(updateArea.getCode());
        }
        if (this.stringColumnCheck(originalArea.getCenterPos(), updateArea.getCenterPos())) {
            originalArea.setCenterPos(updateArea.getCenterPos());
        }

        return this.areaRepository.save(originalArea);
    }

    /**
     * 调用数据访问层的 findAll() 方法，直接返回数据表中的所有条目到 List 容器中。
     * 由控制器端使用 Get 请求发出。
     */
    @Override
    public List<Area> getAllAreas() {
        return this.areaRepository.findAll();
    }
}
