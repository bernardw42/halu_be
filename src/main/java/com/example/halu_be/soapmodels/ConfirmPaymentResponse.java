package com.example.halu_be.soapmodels;
//outside to inside MIDDLEWARE
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "ConfirmPaymentResponse", namespace = "http://halu_be.example.com/payment")
@XmlType(name = "", propOrder = { "message" })
public class ConfirmPaymentResponse {

    private String message;

    @XmlElement(namespace = "http://halu_be.example.com/payment")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}