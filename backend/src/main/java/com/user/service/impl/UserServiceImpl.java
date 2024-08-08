package com.user.service.impl;

import com.user.model.dto.UserEntity;
import com.user.repository.UserRepository;
import com.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<UserEntity> getUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    @Override
    public UserEntity createUser(UserEntity userEntity) {
        userEntity.setCreatedAt(LocalDateTime.now());
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity updateUser(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByAccountOrEmail(account)
                .orElseThrow(() -> new UsernameNotFoundException("帳號或密碼錯誤"));

        return  User
                .withUsername(account)
                .password(userEntity.getPassword())
                .roles(userEntity.getRole()) // 確保返回正確的角色名稱
                .build();
    }

//    public String registerEmailVerifyCode(String type, String email, String ip){
//
//        return null;
//    }

}
