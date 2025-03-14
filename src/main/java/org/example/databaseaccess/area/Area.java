package org.example.databaseaccess.area;

import jakarta.persistence.*;

/**
 * area 实体类是数据表 citydata.area 的映射。
 * 其内部的所有字段都一一对应数据表中的字段。
 */
@Entity
@Table(name = "area")
public class Area
{
    @Id // 该标记表明，这个字段是主键
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "province_id")
    private int provinceID;

    @Column(name = "city_id")
    private int cityID;

    @Column(name = "area_id")
    private int areaID;

    @Column(name = "province")
    private String provinceName;

    @Column(name = "city")
    private String cityName;

    @Column(name = "area")
    private String areaName;

    @Column(name = "code")
    private String code;

    @Column(name = "center")
    private String centerPos;

    @Override
    public String toString()
    {
        return String.format(
                """
                        data: {
                                    "id": %d,
                                    "provinceID": %d,
                                    "cityID": %d,
                                    "areaID": %d,
                                    "provinceName": "%s",
                                    "cityName": "%s",
                                    "areaName": "%s",
                                    "code": "%s",
                                    "centerPos": "%s"
                        }
                """, getId(), getProvinceID(), getCityID(),
                getAreaID(), getProvinceName(), getCityName(),
                getAreaName(), getCode(), getCenterPos()
        );
    }

    public int getId() {
        return this.id;
    }

    public int getProvinceID() {
        return provinceID;
    }

    public int getCityID() {
        return cityID;
    }

    public int getAreaID() {
        return areaID;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getCode() {
        return code;
    }

    public String getCenterPos() {
        return centerPos;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public void setAreaID(int areaID) {
        this.areaID = areaID;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCenterPos(String centerPos) {
        this.centerPos = centerPos;
    }
}
