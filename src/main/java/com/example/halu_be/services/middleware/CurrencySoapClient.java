package com.example.halu_be.services.middleware;

import com.example.halu_be.soapclient.CountryInfoService;
import com.example.halu_be.soapclient.CountryInfoServiceSoapType;
import com.example.halu_be.soapclient.Currency;
import org.springframework.stereotype.Service;

@Service
public class CurrencySoapClient {

    public String getCurrencyByCountryCode(String countryCode) {
        CountryInfoService service = new CountryInfoService();
        CountryInfoServiceSoapType soap = service.getCountryInfoServiceSoap();

        Currency currency = soap.countryCurrency(countryCode.toUpperCase());

        return currency.getSName(); // e.g., "Dollar"
    }
}
