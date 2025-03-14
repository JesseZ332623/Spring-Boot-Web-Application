package org.example.service.areaqueryservice;

import org.example.databaseaccess.area.Area;
import java.util.List;

/**
 * 查询服务器接口，提供一些数据库操作的接口。
 */
public interface AreaQueryService
{
    Area getAreaById(int id);

    Integer addNewItem(Area area);

    Integer deleteAreaById(int id);

//    area updateAreaByID(
//            int id, int provinceID, int cityID, int areaID,
//            String proivnce, String city, String area,
//            String code, String center
//    );

    Area updateAreaByID(Area updateArea);

    List<Area> getAllAreas();
}
