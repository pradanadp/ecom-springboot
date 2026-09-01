package com.app.ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AddressDTO {
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
}
