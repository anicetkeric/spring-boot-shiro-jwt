package com.boottech.springshirojwt.web;

import com.boottech.springshirojwt.dto.UserDTO;
import com.boottech.springshirojwt.entities.GroupRole;
import com.boottech.springshirojwt.entities.User;
import com.boottech.springshirojwt.services.TokenManagerService;
import com.boottech.springshirojwt.services.UserService;
import com.boottech.springshirojwt.web.request.LoginRequest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserResource {

    private final UserService userService;
    private final TokenManagerService tokenManagerService;
    private final DefaultPasswordService passwordservice;


    public UserResource(UserService userService, TokenManagerService tokenManagerService, DefaultPasswordService passwordservice) {
        this.userService = userService;
        this.tokenManagerService = tokenManagerService;
        this.passwordservice = passwordservice;
    }

    @PostMapping("/auth/sign-in")
    public ResponseEntity<Object> login(@RequestBody LoginRequest userRequest){

        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(userRequest.getUsername(), userRequest.getPassword());

        subject.login(usernamePasswordToken);

        Map<String,Object> authInfo = new HashMap<>() {{
            put("token", tokenManagerService.createTokenForUser(userRequest.getUsername()));
        }};

        return ResponseEntity.ok(authInfo);
    }

    @PostMapping("/auth/sign-up")
    public User adduser(@RequestBody UserDTO dto){

        User user= User.builder()
                .username(dto.getUsername()).email(dto.getEmail())
                .firstName(dto.getFirstName()).lastName(dto.getLastName())
                .nickname(dto.getNickname()).mobile(dto.getMobile())
                .password(passwordservice.encryptPassword(dto.getPassword()))
                .enabled(true).roles(dto.getGroupRoles().stream().map(r -> copyToGroupRoleEntity(r.getCode())).filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .build();

        return userService.save(user);
    }

   // @RequiresPermissions("document:read")
    @GetMapping("/user")
    public ResponseEntity<List<UserDTO>> getAll() {
        List<UserDTO> list = userService.getUsers().stream().map(this::copyUserEntityToDto).collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }



    private UserDTO copyUserEntityToDto(User userEntity) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userEntity, userDTO);
        return userDTO;
    }

    private GroupRole copyToGroupRoleEntity(String roleCode) {
        GroupRole groupRole = new GroupRole();

        Optional<GroupRole> roleOptional = userService.getGroupRoleByCode(roleCode);
        if(roleOptional.isPresent()){
            BeanUtils.copyProperties(roleOptional.get(), groupRole);
            return groupRole;
        }
        return null;
    }
}
