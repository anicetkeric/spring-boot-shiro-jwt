package com.boottech.springshirojwt.services;

import com.boottech.springshirojwt.entities.GroupRole;
import com.boottech.springshirojwt.entities.User;
import com.boottech.springshirojwt.repositories.GroupRoleRepository;
import com.boottech.springshirojwt.repositories.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final GroupRoleRepository groupRoleRepository;

    public UserServiceImpl(UserRepository userRepository, GroupRoleRepository groupRoleRepository) {
        this.userRepository = userRepository;
        this.groupRoleRepository = groupRoleRepository;
    }

    @Override
    public Optional<User> getUserByUsername(String usernameValue) {
        // trim username value
        String username = StringUtils.trimToNull(usernameValue);

        if(StringUtils.isNotEmpty(username)) {
            Optional<User> user;

            if (username.contains("@")) {
                user = userRepository.findActiveByEmail(username);
            } else {
                user = userRepository.findActiveByUsername(username);
            }
            return user;
        }else {
            return Optional.empty();
        }
    }


    @Override
    public Optional<User> getById(String id) {
        return userRepository.findById(UUID.fromString(id));
    }

    @Override
    public Optional<GroupRole> getGroupRoleByCode(String code) {
        return groupRoleRepository.findByCode(code);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }


    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

}
