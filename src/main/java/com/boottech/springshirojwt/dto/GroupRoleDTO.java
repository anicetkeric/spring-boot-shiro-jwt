package com.boottech.springshirojwt.dto;


import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/**
 * This class used for convert ApplicationGroupRole data
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GroupRoleDTO {

    private long id;
    
    @NotNull
    @NotBlank(message = "Name role is mandatory")
    @Size(min = 1, max = 255, message = "size must be between 1 and 255")
    private String name;

    @NotNull
    @NotBlank(message = "Code role is mandatory")
    @Size(min = 1, max = 50, message = "The code role '${validatedValue}' must be between {min} and {max} characters long")
    private String code;

}
