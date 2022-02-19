package com.boottech.springshirojwt.web;

import com.boottech.springshirojwt.dto.GroupRoleDTO;
import com.boottech.springshirojwt.dto.UserDTO;
import com.boottech.springshirojwt.entities.GroupRole;
import com.boottech.springshirojwt.entities.User;
import com.boottech.springshirojwt.services.UserService;
import com.boottech.springshirojwt.web.request.LoginRequest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserResource {

    private final UserService userService;

    @Autowired
    DefaultPasswordService passwordservice;


    public UserResource(UserService userService) {
        this.userService = userService;
    }


    @RequestMapping("/sign-in")
    public String login(@RequestBody LoginRequest userRequest) {
        UsernamePasswordToken token = new UsernamePasswordToken(userRequest.getUsername(), userRequest.getPassword());
        token.setRememberMe(userRequest.getRememberme());
        try {
            SecurityUtils.getSubject().login(token);
            return "login success";
        } catch ( UnknownAccountException uae ) {
            return "error username";
        } catch ( IncorrectCredentialsException ice ) {
            return "error password";
        } catch ( LockedAccountException lae ) {
           return "locked user";
        }
    }

    @PostMapping("/sign-up")
    public User adduser(@RequestBody UserDTO dto){

        User user= User.builder()
                .username(dto.getUsername()).email(dto.getEmail())
                .firstName(dto.getFirstName()).lastName(dto.getLastName())
                .nickname(dto.getNickname()).mobile(dto.getMobile())
                .password(passwordservice.encryptPassword(dto.getPassword()))
                .enabled(true).roles(dto.getGroupRoles().stream().map(this::copyDtoToGroupRoleEntity).collect(Collectors.toList()))
                .build();

        //return userService.save(user);
        return user;
    }

    @GetMapping()
    public ResponseEntity<List<UserDTO>> getAll() {
        List<UserDTO> list = userService.getUsers().stream().map(this::copyUserEntityToDto).collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }



    private UserDTO copyUserEntityToDto(User userEntity) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userEntity, userDTO);
        return userDTO;
    }

    private GroupRole copyDtoToGroupRoleEntity(GroupRoleDTO dto) {
        GroupRole groupRole = new GroupRole();
        BeanUtils.copyProperties(dto, groupRole);
        return groupRole;
    }
}
