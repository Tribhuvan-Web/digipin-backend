package com.hackathon.resolutionconsent.digipin.repository;

import com.hackathon.resolutionconsent.digipin.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmailId(String emailId);
    Optional<User> findByAadhaarNumber(String aadhaarNumber);
}
