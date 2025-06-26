package com.example.halu_be.soapmodels;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "ConfirmPaymentRequest", namespace = "http://halu_be.example.com/payment")
@XmlType(name = "", propOrder = { "reference", "status" })
public class ConfirmPaymentRequest {

    private String reference;
    private String status;

    @XmlElement(namespace = "http://halu_be.example.com/payment")
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @XmlElement(namespace = "http://halu_be.example.com/payment")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
