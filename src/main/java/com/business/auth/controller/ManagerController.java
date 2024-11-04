package com.business.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manager/")
public class ManagerController {

    @GetMapping("/demo")
    public ResponseEntity<String> get() {
        return ResponseEntity.ok("Manager authorization successful");
    }

}
