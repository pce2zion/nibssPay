package com.example.nibsstransfer.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
/**
 * @author Peace Obute
 * @since 09/06/2024
 */

@Data
public class OtherDetailsRequest {

    @NotBlank(message = "description should not be blank")
    private String description;
}
