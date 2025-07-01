package com.example.halu_be.endpoints;
//outside to inside MIDDLEWARE
import com.example.halu_be.soapmodels.ConfirmPaymentRequest;
import com.example.halu_be.soapmodels.ConfirmPaymentResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class PaymentConfirmationEndpoint {

    private static final String NAMESPACE = "http://halu_be.example.com/payment";

    @PayloadRoot(namespace = NAMESPACE, localPart = "ConfirmPaymentRequest")
    @ResponsePayload
    public ConfirmPaymentResponse confirmPayment(@RequestPayload ConfirmPaymentRequest request) {
        System.out.println("Received SOAP confirmation: Ref=" + request.getReference() + ", Status=" + request.getStatus());

        ConfirmPaymentResponse response = new ConfirmPaymentResponse();
        response.setMessage("Received and processed payment " + request.getReference());

        return response;
    }
}
