/*
 * Copyright (c) 2019. @aek - (anicetkeric@gmail.com)
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.boottech.springshirojwt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

    @NotNull
    private String password;

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
