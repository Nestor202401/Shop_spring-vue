package com.user.service;

import com.user.model.dto.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    List<UserEntity> getAllUsers();

    Optional<UserEntity> getUserById(Integer userId);

    UserEntity createUser(UserEntity userEntity);

    UserEntity updateUser(UserEntity userEntity);

    void deleteUser(Integer userId);

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
//    String registerEmailVerifyCode(String type, String email, String ip);
}
