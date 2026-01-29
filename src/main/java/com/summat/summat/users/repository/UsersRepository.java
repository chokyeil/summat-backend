package com.summat.summat.users.repository;

import com.summat.summat.users.dto.mypage.ProfileResDto;
import com.summat.summat.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    boolean existsByUserNickName(String checkNickName);

    boolean existsByEmail(String email);
}
