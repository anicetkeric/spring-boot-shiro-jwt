package com.boottech.springshirojwt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

/**
 * <h2>UserDTO</h2>
 *
 * @author aek
 * <p>
 * Description:
 */
@Setter
@Getter
public class UserDTO {

    private UUID id;

    @NotNull
    @Size(min = 4, max = 24)
    @NotBlank(message = "username is mandatory")
    private String username;
    @JsonProperty("firstname")
    private String firstName;
    @JsonProperty("lastname")
    private String lastName;

    @Email
    @NotNull
    @NotBlank(message = "email is mandatory")
    private String email;

    private boolean enabled;

    @JsonProperty("roles")
    private List<GroupRoleDTO> groupRoles;

    private String nickname;

    private String mobile;

}
