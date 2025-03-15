package org.example.service.countryqueryservice;

import jakarta.transaction.Transactional;
import org.example.databaseaccess.countryinworld.Country;
import org.example.databaseaccess.countryinworld.CountryRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CountryQueryServiceImplements implements CountryQueryService
{
    private CountryRepository countryRepository;

    private boolean stringColumnCheck(String oldStr, String newStr) {
        return (StringUtils.hasLength(newStr) && !oldStr.equals(newStr));
    }

    @Autowired
    public CountryQueryServiceImplements(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public Country getCountryByCode(String code)
    {
        return this.countryRepository.findById(code).orElseThrow(
                () -> new RuntimeException(
                        String.format(
                                "Countory code: {%s} not exsist in this table.", code
                        )
                )
        );
    }

    @Override
    @Transactional
    public Country addNewCountry(@NotNull Country newCountry)
    {
        List<Country> uniqueQueryResult
                = this.countryRepository.findByCode(newCountry.getCode());

        if (!CollectionUtils.isEmpty(uniqueQueryResult))
        {
            throw new IllegalStateException(
                    String.format(
                            "Countory code: {%s} has been exsist in this table.",
                            newCountry.getCode()
                    )
            );
        }

        this.countryRepository.save(newCountry);

        return newCountry;
    }

    @Override
    public Country deleteCountryByCode(String code)
    {
        Country queryResult = this.countryRepository.findById(code).orElseThrow(
                () -> new IllegalStateException(
                        String.format(
                                "Country code: {%s} not exsist in this table.", code
                        )
                )
        );

        this.countryRepository.deleteById(code);

        return queryResult;
    }

    @Override
    @Transactional
    public Country updateCountry(@NotNull Country updatedCountry)
    {
        Country queryResult
                = this.countryRepository.findById(updatedCountry.getCode())
                                        .orElseThrow(
                                                () -> new IllegalStateException(
                                                        String.format(
                                                                "Country code: {%s} not exsist in this table.",
                                                                updatedCountry.getCode()
                                                        )
                                                )
                                        );


        if (this.stringColumnCheck(queryResult.getCode(), updatedCountry.getCode())) {
            queryResult.setCode(updatedCountry.getCode());
        }
        if (this.stringColumnCheck(queryResult.getName(), updatedCountry.getName())) {
            queryResult.setName(updatedCountry.getName());
        }
        if (this.stringColumnCheck(queryResult.getContinent(), updatedCountry.getContinent())) {
            queryResult.setContinet(updatedCountry.getContinent());
        }
        if (this.stringColumnCheck(queryResult.getRegion(), updatedCountry.getRegion())) {
            queryResult.setRegion(updatedCountry.getRegion());
        }

        queryResult.setSurfaceArea(updatedCountry.getSurfaceArea());
        queryResult.setIndepYear(updatedCountry.getIndepYear());
        queryResult.setPopulation(updatedCountry.getPopulation());
        queryResult.setLifeExpectancy(updatedCountry.getLifeExpectancy());
        queryResult.setGnp(updatedCountry.getGnp());
        queryResult.setGnpOld(updatedCountry.getGnpOld());

        if (this.stringColumnCheck(queryResult.getLocalName(), updatedCountry.getLocalName())) {
            queryResult.setLocalName(updatedCountry.getLocalName());
        }
        if (this.stringColumnCheck(queryResult.getGovernmentForm(), updatedCountry.getGovernmentForm())) {
            queryResult.setGovernmentForm(updatedCountry.getGovernmentForm());
        }
        if (this.stringColumnCheck(queryResult.getHeadOfState(), updatedCountry.getHeadOfState())) {
            queryResult.setHeadOfState(updatedCountry.getHeadOfState());
        }

        queryResult.setCapital(updatedCountry.getCapital());

        if (this.stringColumnCheck(queryResult.getCode2(), updatedCountry.getCode2())) {
            queryResult.setCode2(updatedCountry.getCode2());
        }

        this.countryRepository.save(queryResult);

        return queryResult;
    }

    @Override
    public List<Country> getAllCountries() {
        return this.countryRepository.findAll();
    }
}
