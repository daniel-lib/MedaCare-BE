package com.medacare.backend.controller;

import java.lang.StackWalker.Option;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medacare.backend.config.FixedVars;
import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Patient;
import com.medacare.backend.model.Physician;
import com.medacare.backend.model.RoleEnum;
import com.medacare.backend.repository.InstitutionRepository;
import com.medacare.backend.repository.PatientRepository;
import com.medacare.backend.repository.PhysicianRepository;
import com.medacare.backend.repository.RoleRepository;
import com.medacare.backend.model.User;
import com.medacare.backend.service.AuthenticationService;
import com.medacare.backend.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(FixedVars.BASE_API_VERSION + "/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {
    private final PhysicianRepository physicianRepository;
    private final PatientRepository patientRepository;
    private final InstitutionRepository institutionRepository;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final RoleRepository roleRepository;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/current")
    public ResponseEntity<User> currentAuthenticatedUser() {
        User currentUser = authenticationService.getCurrentUser();
        RoleEnum role = currentUser.getRole().getName();
        currentUser.setRoleName(role.toString());
        if (role == RoleEnum.PHYSICIAN) {
            Physician physicianProfile = physicianRepository.findByUserId(currentUser.getId());
            currentUser.setEntity(physicianProfile);
        }

        if (role == RoleEnum.PATIENT) {
            Patient patientProfile = patientRepository.findByUserId(currentUser.getId());
            currentUser.setEntity(patientProfile);
        }

        if (role == RoleEnum.ORG_ADMIN) {
            Institution instititionProfile = institutionRepository.findByAdminUser(currentUser);
            currentUser.setEntity(instititionProfile);
        }

        return ResponseEntity.ok(currentUser);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @GetMapping()
    public List<User> getUsers() {
        return userService.getAllUsers();
    }
}
