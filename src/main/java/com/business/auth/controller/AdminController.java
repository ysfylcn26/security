package com.business.auth.controller;

import com.business.auth.security.enums.Role;
import com.business.auth.security.request.SignUpRequest;
import com.business.auth.service.AdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/demo")
    public ResponseEntity<String> get() {
        return ResponseEntity.ok("Admin authorization successful");
    }

    @PostMapping("/create/manager")
    public ResponseEntity<Void> createManager(@Valid @RequestBody SignUpRequest request) {
        request.setRole(Role.MANAGER);
        adminService.createAccountByAdmin(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create/admin")
    public ResponseEntity<Void> createAdmin(@Valid @RequestBody SignUpRequest request) {
        request.setRole(Role.ADMIN);
        adminService.createAccountByAdmin(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/account")
    public ResponseEntity<String> deleteAccount(@RequestParam(name = "email") @Email String email) {
        adminService.deletedAccountByAdmin(email);
        return ResponseEntity.ok("Deleted Successfully");
    }
}
