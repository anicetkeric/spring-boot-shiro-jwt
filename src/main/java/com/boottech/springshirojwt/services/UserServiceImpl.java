package com.boottech.springshirojwt.services;

import com.boottech.springshirojwt.entities.User;
import com.boottech.springshirojwt.repositories.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getUserByUsername(String usernameValue) {
        // trim username value
        String username = StringUtils.trimToNull(usernameValue);

        if(StringUtils.isNotEmpty(username)) {

            Optional<User> user;

            if (username.contains("@")) {
                user = userRepository.findByEmail(username);
            } else {
                user = userRepository.findByUsername(username);
            }

            return user;
        }else {
            return Optional.empty();
        }

    }

}
