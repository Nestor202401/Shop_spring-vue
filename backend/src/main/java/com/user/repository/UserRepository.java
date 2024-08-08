package com.user.repository;

import com.user.model.dto.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    // 使用 JPQL 查找用戶名或電子郵件匹配的用戶
    @Query("SELECT u FROM UserEntity u WHERE u.account = ?1 OR u.email = ?1")
    Optional<UserEntity> findByAccountOrEmail(String text);
}
