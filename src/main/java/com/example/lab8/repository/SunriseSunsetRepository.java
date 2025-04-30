package com.example.lab8.repository;

import com.example.lab8.model.SunriseSunset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SunriseSunsetRepository extends JpaRepository<SunriseSunset, Long> {
    List<SunriseSunset> findByDate(String date);
}