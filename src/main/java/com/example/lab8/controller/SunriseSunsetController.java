package com.example.lab8.controller;

import com.example.lab8.model.SunriseSunset;
import com.example.lab8.service.SunriseSunsetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/sunrise-sunset")
public class SunriseSunsetController {

    private static final Logger logger = LoggerFactory.getLogger(SunriseSunsetController.class);

    @Autowired
    private SunriseSunsetService sunriseSunsetService;

    @GetMapping
    public ResponseEntity<List<SunriseSunset>> getAll() {
        logger.info("Getting all sunrise/sunset records");
        return ResponseEntity.ok(sunriseSunsetService.getAll());
    }

    @PostMapping
    public ResponseEntity<SunriseSunset> create(@Valid @RequestBody SunriseSunset sunriseSunset) {
        logger.info("Creating sunrise/sunset record: {}", sunriseSunset);
        SunriseSunset created = sunriseSunsetService.create(sunriseSunset);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SunriseSunset> update(@PathVariable Long id, @Valid @RequestBody SunriseSunset sunriseSunset) {
        logger.info("Updating sunrise/sunset record with id {}: {}", id, sunriseSunset);
        SunriseSunset updated = sunriseSunsetService.update(id, sunriseSunset);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Deleting sunrise/sunset record with id {}", id);
        sunriseSunsetService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-date")
    public ResponseEntity<?> getByDate(@RequestParam(required = false) String date) {
        if (date == null || date.trim().isEmpty()) {
            logger.warn("Date parameter is missing or empty");
            return ResponseEntity.badRequest().body("Date parameter is required");
        }
        try {
            logger.info("Getting sunrise/sunset records by date: {}", date);
            List<SunriseSunset> sunriseSunsets = sunriseSunsetService.getByDate(date);
            return ResponseEntity.ok(sunriseSunsets);
        } catch (Exception e) {
            logger.error("Error while fetching sunrise/sunset records by date: {}", date, e);
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }
}