package com.hackathon.resolutionconsent.digipin.repository;

import com.hackathon.resolutionconsent.digipin.models.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
}
