
package com.tableturn.party;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record JoinRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 60, message = "Name must not exceed 60 characters")
        String name,

        @Min(value = 1, message = "Covers must be at least 1")
        @Max(value = 20, message = "Covers must not exceed 20")
        int covers,

        @Pattern(
        regexp = "^\\+[1-9][0-9]{1,14}$",
        message = "Phone must be in E.164 format"
)
String phone
) {}
