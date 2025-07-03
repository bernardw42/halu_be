package com.example.halu_be.services.middleware.soapclient_pretend;

// inside to outside MIDDLEWARE
public class CountryInfoService {
    public CountryInfoServiceSoapType getCountryInfoServiceSoap() {
        // ✅ In real SOAP, this would create a real WSDL-based stub
        return new CountryInfoServiceSoapType();
    }
}
