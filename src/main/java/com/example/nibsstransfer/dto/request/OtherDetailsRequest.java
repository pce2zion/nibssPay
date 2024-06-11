package com.example.nibsstransfer.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtherDetailsRequest {

    @NotBlank(message = "description should not be blank")
    private String description;
}
