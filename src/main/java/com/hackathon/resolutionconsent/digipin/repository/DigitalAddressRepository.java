package com.hackathon.resolutionconsent.digipin.repository;

import com.hackathon.resolutionconsent.digipin.models.DigitalAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DigitalAddressRepository extends JpaRepository<DigitalAddress, Long> {
    Optional<DigitalAddress> findByDigitalAddress(String digipin);
    List<DigitalAddress> findByUserId(Long userId);
}
