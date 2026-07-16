package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.FundingPool;
import com.pirivena_project.pirivena.repository.FundingPoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funding-pools")
public class FundingPoolController {

    @Autowired
    private FundingPoolRepository poolRepository;

    @GetMapping
    public ResponseEntity<List<FundingPool>> getAllPools() {
        return ResponseEntity.ok(poolRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<FundingPool> createPool(@RequestBody FundingPool pool) {
        FundingPool savedPool = poolRepository.save(pool);
        return new ResponseEntity<>(savedPool, HttpStatus.CREATED);
    }
}
