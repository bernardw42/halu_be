package com.example.halu_be.soapclient;

public class CountryInfoServiceSoapType {
    public Currency countryCurrency(String countryCode) {
        // Simulated SOAP response
        return new Currency("USD", "Dollar");
    }
}
