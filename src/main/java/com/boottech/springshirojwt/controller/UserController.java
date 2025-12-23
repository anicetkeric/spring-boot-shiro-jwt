package com.boottech.springshirojwt.controller;

import com.boottech.springshirojwt.dto.UserDTO;
import com.boottech.springshirojwt.entities.User;
import com.boottech.springshirojwt.services.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequiresAuthentication
    @GetMapping("/me")
    public String me() { return "Authenticated"; }

    @RequiresRoles("ADMIN")
    @GetMapping("/admin")
    public String admin() { return "Admin only"; }

    @RequiresPermissions("user:read")
    @GetMapping("/user")
    public ResponseEntity<List<UserDTO>> readUsers() {
        List<UserDTO> list = userService.getUsers().stream().map(this::copyUserEntityToDto).toList();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    private UserDTO copyUserEntityToDto(User userEntity) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userEntity, userDTO);
        return userDTO;
    }

}