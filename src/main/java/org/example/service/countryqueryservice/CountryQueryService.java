package org.example.service.countryqueryservice;

import org.example.databaseaccess.countryinworld.Country;

import java.util.List;

public interface CountryQueryService
{
    Country getCountryByCode(String code);

    Country addNewCountry(Country newCountry);

    Country  deleteCountryByCode(String code);

    Country updateCountry(Country updatedCountry);

    List<Country> getAllCountries();
}
