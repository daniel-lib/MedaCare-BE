package com.medacare.backend.initialization;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.medacare.backend.model.Role;
import com.medacare.backend.model.RoleEnum;
import com.medacare.backend.repository.RoleRepository;

import java.util.*;
@Component
public class RoleSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepo;


    public RoleSeeder(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadRoles();
    }

    private void loadRoles() {
        RoleEnum[] roleNames = new RoleEnum[] 
        { RoleEnum.USER, RoleEnum.ADMIN, RoleEnum.SUPER_ADMIN, RoleEnum.PATIENT, RoleEnum.PHYSCIAN, RoleEnum.ORG_ADMIN, RoleEnum.ORG_USER };
        Map<RoleEnum, String> roleDescriptionMap = Map.of(
            RoleEnum.USER, "Default user role",
            RoleEnum.ADMIN, "Administrator role",
            RoleEnum.SUPER_ADMIN, "Super Administrator role",
            RoleEnum.PATIENT, "Patient role",
            RoleEnum.PHYSCIAN, "Physician role",
            RoleEnum.ORG_ADMIN, "Organization/Institution Administrator role",
            RoleEnum.ORG_USER, "Organization/Institution User role"
        );

        Arrays.stream(roleNames).forEach((roleName) -> {
            Optional<Role> optionalRole = roleRepo.findByName(roleName);

            optionalRole.ifPresentOrElse(System.out::println, () -> {
                Role newRole = new Role();

                newRole.setName(roleName);
                newRole.setDescription(roleDescriptionMap.get(roleName));

                roleRepo.save(newRole);
            });
        });
    }
}
