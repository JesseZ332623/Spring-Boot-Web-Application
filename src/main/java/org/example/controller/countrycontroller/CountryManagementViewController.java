package org.example.controller.countrycontroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CountryManagementViewController
{
    @GetMapping("/country/CountryManagement")
    public String countryManagementView() {
        return "CountryManagement";
    }
}
