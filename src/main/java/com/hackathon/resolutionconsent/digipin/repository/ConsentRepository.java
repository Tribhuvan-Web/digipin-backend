package com.hackathon.resolutionconsent.digipin.repository;

import com.hackathon.resolutionconsent.digipin.models.Consent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {
    
    Optional<Consent> findByDigitalAddressIdAndIsActiveTrue(Long digitalAddressId);
    
    List<Consent> findByUserIdAndIsActiveTrue(Long userId);
    
    Optional<Consent> findByConsentTokenAndIsActiveTrue(String consentToken);
    
    Optional<Consent> findByDigitalAddressId(Long digitalAddressId);
}
