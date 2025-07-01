package com.example.halu_be.controllers.middleware;
//inside to outside MIDDLEWARE
import com.example.halu_be.services.middleware.CurrencySoapClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencySoapClient currencySoapClient;

    @GetMapping("/{countryCode}")
    public String getCurrency(@PathVariable String countryCode) {
        return currencySoapClient.getCurrencyByCountryCode(countryCode);
    }
}
