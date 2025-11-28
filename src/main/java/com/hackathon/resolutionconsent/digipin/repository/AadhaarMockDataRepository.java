package com.hackathon.resolutionconsent.digipin.repository;

import com.hackathon.resolutionconsent.digipin.models.AadhaarMockData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AadhaarMockDataRepository extends JpaRepository<AadhaarMockData, Long> {
    Optional<AadhaarMockData> findByAadhaarNumber(String aadhaarNumber);
}
