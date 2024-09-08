package com.tannv.jobhunter.repository;

import com.tannv.jobhunter.domain.Company;
import com.tannv.jobhunter.domain.User;
import com.tannv.jobhunter.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User findByRefreshTokenAndEmail(String token, String email);
    List<User> findByCompany(Company company);

    @Query(value = "SELECT u.* FROM users u WHERE u.id = :userId",
            nativeQuery = true)
    User getUserById(@Param("userId") Long userId);
}
