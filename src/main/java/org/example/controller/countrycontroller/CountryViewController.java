package org.example.controller.countrycontroller;

import org.example.databaseaccess.countryinworld.Country;
import org.example.service.countryqueryservice.CountryQueryService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CountryViewController
{
    private CountryQueryService countryQueryService;

    @Autowired
    public CountryViewController(CountryQueryService countryQueryService) {
        this.countryQueryService = countryQueryService;
    }

    @GetMapping("/country/CountriesList")
    public String listCountries(@NotNull Model module)
    {
        List<Country> allCountries = this.countryQueryService.getAllCountries();

        module.addAttribute("countries", allCountries);

        return "CountriesList";
    }
}
