package com.example.halu_be.repositories;

import com.example.halu_be.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByNationalId(String nationalId);

    // ✅ Manual SQL: only select created_at for a given username
    @Query(value = "SELECT u.created_at FROM users u WHERE u.username = :username", nativeQuery = true)
    Optional<LocalDateTime> findCreatedAtByUsernameManual(@Param("username") String username);
}
