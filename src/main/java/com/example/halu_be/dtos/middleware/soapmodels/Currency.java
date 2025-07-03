package com.example.halu_be.dtos.middleware.soapmodels;
//inside to outside MIDDLEWARE
public class Currency {
    private String sISOCode;
    private String sName;

    public Currency(String code, String name) {
        this.sISOCode = code;
        this.sName = name;
    }

    public String getSISOCode() { return sISOCode; }
    public String getSName() { return sName; }
}
