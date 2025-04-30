package com.example.lab8.service;

import com.example.lab8.model.SunriseSunset;
import com.example.lab8.repository.SunriseSunsetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SunriseSunsetService {

    private static final Logger logger = LoggerFactory.getLogger(SunriseSunsetService.class);

    @Autowired
    private SunriseSunsetRepository repository;

    @Transactional(readOnly = true)
    public List<SunriseSunset> getAll() {
        RequestCounter.increment();
        return repository.findAll();
    }

    @Transactional
    public SunriseSunset create(SunriseSunset sunriseSunset) {
        RequestCounter.increment();
        SunriseSunset saved = repository.save(sunriseSunset);
        return saved;
    }

    @Transactional
    public SunriseSunset update(Long id, SunriseSunset sunriseSunset) {
        RequestCounter.increment();
        SunriseSunset existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SunriseSunset with id " + id + " not found"));
        existing.setDate(sunriseSunset.getDate());
        existing.setLatitude(sunriseSunset.getLatitude());
        existing.setLongitude(sunriseSunset.getLongitude());
        existing.setSunrise(sunriseSunset.getSunrise());
        existing.setSunset(sunriseSunset.getSunset());
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        RequestCounter.increment();
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SunriseSunset> getByDate(String date) {
        RequestCounter.increment();
        return repository.findByDate(date);
    }
}