package com.example.halu_be.services.middleware.soapclient_pretend;

import com.example.halu_be.dtos.middleware.soapmodels.Currency;

//inside to outside MIDDLEWARE
public class CountryInfoServiceSoapType {
    public Currency countryCurrency(String countryCode) {
        // Simulated SOAP response
        return new Currency("USD", "Dollar");
    }
}
