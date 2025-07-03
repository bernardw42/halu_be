package com.example.halu_be.services.middleware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
//inside to outside MIDDLEWARE
import com.example.halu_be.dtos.middleware.soapmodels.Currency;
import com.example.halu_be.services.middleware.soapclient_pretend.CountryInfoService;
import com.example.halu_be.services.middleware.soapclient_pretend.CountryInfoServiceSoapType;

@Service
@Slf4j
public class CurrencySoapClient {

    public String getCurrencyByCountryCode(String countryCode) {
        try {
            CountryInfoService service = new CountryInfoService();
            CountryInfoServiceSoapType soap = service.getCountryInfoServiceSoap();

            Currency currency = soap.countryCurrency(countryCode.toUpperCase());
            return currency.getSName(); // e.g., "Dollar"
        } catch (Exception e) {
            log.error("Failed to call SOAP service for country code {}: {}", countryCode, e.getMessage(), e);
            throw new RuntimeException("Unable to fetch currency info right now. Please try again.", e);
        }
    }
}
