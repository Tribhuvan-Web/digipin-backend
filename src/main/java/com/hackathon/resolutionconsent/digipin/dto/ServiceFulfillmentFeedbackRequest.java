package com.hackathon.resolutionconsent.digipin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServiceFulfillmentFeedbackRequest {

    @NotBlank(message = "Digital address is required")
    private String digitalAddress;

    @NotNull(message = "Fulfillment status is required")
    private FulfillmentStatus fulfillmentStatus;

    @NotBlank(message = "AIU identifier is required")
    private String aiuIdentifier; 

    private String comments; 

    public enum FulfillmentStatus {
        SUCCESS,
        FAILURE,
        NEUTRAL
    }
}
