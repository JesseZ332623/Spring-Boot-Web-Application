package org.example.databaseaccess.countryinworld;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, String>
{
    /**
     * 自己声明的通过主键查询数据行的方法。
     */
    List<Country> findByCode(String code);
}
