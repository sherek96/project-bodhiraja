package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.enums.GuardianStatus;
import com.pirivena_project.pirivena.modal.Guardian;
import com.pirivena_project.pirivena.repository.GuardianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.time.DateTimeException;
import java.time.LocalDate;
import com.pirivena_project.pirivena.enums.Gender;

@Service
public class GuardianService {

    @Autowired
    private GuardianRepository guardianRepository;
    @Autowired
    private ProfilePictureStorageService profilePictureStorageService;

    public List<Guardian> getAllGuardians() {
        return guardianRepository.findAll();
    }

    public Guardian getGuardianById(Integer id) {
        return guardianRepository.findById(id).orElseThrow(() -> new RuntimeException("Guardian not found with ID: " + id));
    }

    public Guardian createGuardian(Guardian guardian) {
        validateGuardian(guardian);
        // 1. Validation Rules (Unique checks)
        if (guardianRepository.existsByNic(guardian.getNic())) {
            throw new RuntimeException("NIC already exists");
        }
        if (guardianRepository.existsByPhonePrimary(guardian.getPhonePrimary())) {
            throw new RuntimeException("PHONE primary already exists");
        }

        // 2. Set Default Configuration values
        guardian.setStatus(GuardianStatus.ACTIVE);

        // 3. Save to database
        return guardianRepository.save(guardian);
    }

    public Guardian updateGuardian(Guardian guardian) {
        validateGuardian(guardian);
        // 1. Fetch existing managed record (The Snapshot)
        Guardian existingGuardian = guardianRepository.findById(guardian.getId())
                .orElseThrow(() -> new RuntimeException("Guardian not found"));

        // 2. Validate Duplicates (Excluding current record ID)
        if (guardianRepository.existsByNicAndIdNot(guardian.getNic(), guardian.getId())) {
            throw new RuntimeException("NIC already exists");
        }
        if (guardianRepository.existsByPhonePrimaryAndIdNot(guardian.getPhonePrimary(), guardian.getId())) {
            throw new RuntimeException("Phone number already exists");
        }

        // 3. Update fields on the managed object instance
        existingGuardian.setNic(guardian.getNic());
        existingGuardian.setPhonePrimary(guardian.getPhonePrimary());
        existingGuardian.setStatus(guardian.getStatus());
        existingGuardian.setAddress(guardian.getAddress());
        existingGuardian.setTitle(guardian.getTitle());
        existingGuardian.setFullName(guardian.getFullName());
        existingGuardian.setPhoneSecondary(guardian.getPhoneSecondary());
        existingGuardian.setDob(guardian.getDob());
        existingGuardian.setGender(guardian.getGender());

        // 4. Save and return updated entity
        return guardianRepository.save(existingGuardian);
    }

    public Guardian deleteGuardian(Integer id) {
        // 1. Fetch original record or throw error if missing
        Guardian guardianToDelete = guardianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guardian Not Found"));

        // 2. Set Soft Delete state to INACTIVE
        guardianToDelete.setStatus(GuardianStatus.INACTIVE);

        // 3. Persist change back to database
        return guardianRepository.save(guardianToDelete);
    }

    public Guardian uploadProfilePicture(Integer id, MultipartFile file) {
        Guardian guardian = getGuardianById(id);
        guardian.setProfilePicture(profilePictureStorageService.save(file, guardian.getProfilePicture(), "guardians"));
        return guardianRepository.save(guardian);
    }

    private void validateGuardian(Guardian guardian) {
        if (guardian.getFullName() == null || guardian.getFullName().trim().split("\\s+").length < 2) {
            throw new RuntimeException("Guardian full name must contain at least two words");
        }
        if (guardian.getDob() == null || guardian.getGender() == null) {
            throw new RuntimeException("Guardian date of birth and gender are required");
        }
        if (guardian.getDob().plusYears(18).isAfter(LocalDate.now())) {
            throw new RuntimeException("Guardian must be at least 18 years old");
        }
        if (guardian.getPhonePrimary() == null || !guardian.getPhonePrimary().matches("07\\d{8}")) {
            throw new RuntimeException("Primary phone must contain 10 digits and start with 07");
        }
        if (guardian.getPhoneSecondary() != null && !guardian.getPhoneSecondary().isBlank()
                && !guardian.getPhoneSecondary().matches("07\\d{8}")) {
            throw new RuntimeException("WhatsApp number must contain 10 digits and start with 07");
        }

        String nic = guardian.getNic() == null ? "" : guardian.getNic().trim();
        boolean oldFormat = nic.matches("\\d{9}[vVxX]");
        boolean newFormat = nic.matches("\\d{12}");
        if (!oldFormat && !newFormat) throw new RuntimeException("Invalid NIC format");
        int year = oldFormat ? 1900 + Integer.parseInt(nic.substring(0, 2)) : Integer.parseInt(nic.substring(0, 4));
        int encodedDay = oldFormat ? Integer.parseInt(nic.substring(2, 5)) : Integer.parseInt(nic.substring(4, 7));
        Gender inferredGender = encodedDay > 500 ? Gender.FEMALE : Gender.MALE;
        int dayOfYear = encodedDay > 500 ? encodedDay - 500 : encodedDay;
        try {
            LocalDate inferredDob = LocalDate.ofYearDay(year, dayOfYear);
            if (!inferredDob.equals(guardian.getDob())) throw new RuntimeException("Guardian date of birth does not match the NIC");
            if (inferredGender != guardian.getGender()) throw new RuntimeException("Guardian gender does not match the NIC");
        } catch (DateTimeException e) {
            throw new RuntimeException("Invalid NIC birth date");
        }
    }


}
