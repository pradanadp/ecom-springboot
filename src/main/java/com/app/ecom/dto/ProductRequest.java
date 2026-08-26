package com.app.ecom.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;
    @NotNull
    @Min(0)
    private Integer stockQuantity;
    @NotBlank
    private String category;
    @NotBlank
    private String imageUrl;
}
