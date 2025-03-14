package org.example.databaseaccess.area;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AreaRepository 继承自 JpaRepository 接口，该类提供查询接口，
 * 将查询的结果序列化后传递给 area 实体。
 */
@Repository
public interface AreaRepository extends JpaRepository<Area, Integer>
{
    List<Area> findByid(Integer id);
}
