package com.hackathon.resolutionconsent.digipin.service;

import com.hackathon.resolutionconsent.digipin.dto.CreateDigitalAddressRequest;
import com.hackathon.resolutionconsent.digipin.dto.UpdateDigitalAddressRequest;
import com.hackathon.resolutionconsent.digipin.models.DigitalAddress;
import com.hackathon.resolutionconsent.digipin.models.User;
import com.hackathon.resolutionconsent.digipin.repository.DigitalAddressRepository;
import com.hackathon.resolutionconsent.digipin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DigitalAddressService {

    @Autowired
    private DigitalAddressRepository digitalAddressRepository;

    @Autowired
    private UserRepository userRepository;

    public DigitalAddress createDigitalAddress(CreateDigitalAddressRequest request, String generatedDigipin) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String fullDigitalAddress = request.getUsername() + request.getSuffix();

        if (digitalAddressRepository.findByDigipin(fullDigitalAddress).isPresent()) {
            throw new RuntimeException("Digital address already exists");
        }

        DigitalAddress digitalAddress = new DigitalAddress();
        digitalAddress.setUserId(user.getId());
        digitalAddress.setDigipin(fullDigitalAddress);
        digitalAddress.setGeneratedDigipin(generatedDigipin);
        digitalAddress.setSuffix(request.getSuffix());
        digitalAddress.setLatitude(request.getLatitude());
        digitalAddress.setLongitude(request.getLongitude());
        digitalAddress.setAddress(request.getAddress());

        return digitalAddressRepository.save(digitalAddress);
    }

    public Optional<DigitalAddress> getDigitalAddressById(Long id) {
        return digitalAddressRepository.findById(id);
    }

    public Optional<DigitalAddress> getDigitalAddressByDigipin(String digipin) {
        return digitalAddressRepository.findByDigipin(digipin);
    }

    public List<DigitalAddress> getDigitalAddressesByUserId(Long userId) {
        return digitalAddressRepository.findByUserId(userId);
    }

    public List<DigitalAddress> getAllDigitalAddresses() {
        return digitalAddressRepository.findAll();
    }

    public DigitalAddress updateDigitalAddress(Long id, Long userId, UpdateDigitalAddressRequest request,
            String regeneratedDigipin) {
        DigitalAddress digitalAddress = digitalAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Digital address not found"));

        // Verify ownership
        if (!digitalAddress.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to update this digital address");
        }

        // Update only allowed fields
        if (request.getLatitude() != null) {
            digitalAddress.setLatitude(request.getLatitude());
            // Update the generated digipin when coordinates change
            if (regeneratedDigipin != null) {
                digitalAddress.setGeneratedDigipin(regeneratedDigipin);
            }
        }
        if (request.getLongitude() != null) {
            digitalAddress.setLongitude(request.getLongitude());
        }
        if (request.getAddress() != null) {
            digitalAddress.setAddress(request.getAddress());
        }

        return digitalAddressRepository.save(digitalAddress);
    }

    public DigitalAddress updateDigitalAddressByUserId(Long userId, UpdateDigitalAddressRequest request,
            String regeneratedDigipin) {
        List<DigitalAddress> userAddresses = digitalAddressRepository.findByUserId(userId);

        if (userAddresses.isEmpty()) {
            throw new RuntimeException("No digital address found for this user");
        }

        // Get the first digital address for the user
        DigitalAddress digitalAddress = userAddresses.get(0);
        if (request.getLatitude() != null) {
            digitalAddress.setLatitude(request.getLatitude());
            // Update the generated digipin when coordinates change
            if (regeneratedDigipin != null) {
                digitalAddress.setGeneratedDigipin(regeneratedDigipin);
            }
        }
        if (request.getLongitude() != null) {
            digitalAddress.setLongitude(request.getLongitude());
        }
        if (request.getAddress() != null) {
            digitalAddress.setAddress(request.getAddress());
        }

        return digitalAddressRepository.save(digitalAddress);
    }
}
