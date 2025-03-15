package org.example.controller.countrycontroller;

import org.example.databaseaccess.countryinworld.Country;
import org.example.service.countryqueryservice.CountryQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.example.response.Response;

@RestController
public class CountryController
{
    private CountryQueryService countryQueryService;

    @Autowired
    public CountryController(CountryQueryService countryQueryService) {
        this.countryQueryService = countryQueryService;
    }

    @GetMapping("/country/{code}")
    public Response<Country> getCountryByCode(@PathVariable String code)
    {
        try
        {
            Country queryResult = this.countryQueryService.getCountryByCode(code);

            return Response.newSuccess(
                    queryResult, String.format(
                            "Query country code = {%s} data row success.", code
                    )
            );
        }
        catch (RuntimeException runtimeException)
        {
            return Response.newFailed(
                    runtimeException.getMessage(), HttpStatus.BAD_REQUEST
            );
        }
    }

    @PostMapping("/country")
    public Response<Country> addNewCountry(@RequestBody Country newCountry)
    {
        try
        {
            Country newCountryReturn = this.countryQueryService.addNewCountry(newCountry);

            return Response.newSuccess(
                    newCountryReturn,
                    String.format(
                            "Add new country row country code = {%s}.",
                            newCountryReturn.getCode()
                    )
            );
        }
        catch (IllegalStateException illegalStateException)
        {
            return Response.newFailed(
                   illegalStateException.getMessage(), HttpStatus.BAD_REQUEST
            );
        }
    }

    @PutMapping("/country")
    public Response<Country> updateCountry(@RequestBody Country updateCountry)
    {
        try
        {
            Country updateContryReturn = this.countryQueryService.updateCountry(updateCountry);

            return Response.newSuccess(
                    updateContryReturn,
                    String.format(
                            "Update data row country code = {%s}.",
                            updateContryReturn.getCode()
                    )
            );
        }
        catch (IllegalStateException illegalStateException)
        {
            return Response.newFailed(
                    illegalStateException.getMessage(), HttpStatus.BAD_REQUEST
            );
        }
    }

    @DeleteMapping("/country/{code}")
    public Response<Country> deleteCountryByCode(@PathVariable String code)
    {
        try
        {
            Country deleteCountry = this.countryQueryService.deleteCountryByCode(code);

            return Response.newSuccess(
                    deleteCountry,
                    String.format(
                            "Delete data row country code = {%s}.",
                            deleteCountry.getCode()
                    )
            );
        }
        catch (IllegalStateException illegalStateException)
        {
            return Response.newFailed(
                    illegalStateException.getMessage(), HttpStatus.BAD_REQUEST
            );
        }
    }
}
