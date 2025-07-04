package com.example.lab8.service;

import com.example.lab8.model.Location;
import com.example.lab8.model.SunriseSunset;
import com.example.lab8.repository.LocationRepository;
import com.example.lab8.repository.SunriseSunsetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private SunriseSunsetRepository sunriseSunsetRepository;

    @Mock
    private Map<String, List<Location>> locationCache;

    @InjectMocks
    private LocationService locationService;

    private Location location;
    private SunriseSunset sunriseSunset;

    @BeforeEach
    void setUp() {
        location = new Location();
        location.setId(1L);
        location.setName("Test Location");
        location.setCountry("Test Country");

        sunriseSunset = new SunriseSunset();
        sunriseSunset.setId(1L);
        sunriseSunset.setDate("2025-04-04");
    }

    @Test
    void testGetAllWhenCachedReturnsCachedData() {
        when(locationCache.containsKey("all_locations")).thenReturn(true);
        when(locationCache.get("all_locations")).thenReturn(List.of(location));

        List<Location> result = locationService.getAll();

        assertEquals(1, result.size());
        assertEquals(location, result.get(0));
        verify(locationRepository, never()).findAll();
    }

    @Test
    void testGetAllWhenNotCachedReturnsDatabaseData() {
        when(locationCache.containsKey("all_locations")).thenReturn(false);
        when(locationRepository.findAll()).thenReturn(List.of(location));

        List<Location> result = locationService.getAll();

        assertEquals(1, result.size());
        assertEquals(location, result.get(0));
        verify(locationCache).put("all_locations", List.of(location));
    }

    @Test
    void testGetByIdWhenCachedReturnsCachedData() {
        when(locationCache.containsKey("location_1")).thenReturn(true);
        when(locationCache.get("location_1")).thenReturn(List.of(location));

        Optional<Location> result = locationService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(location, result.get());
        verify(locationRepository, never()).findById(1L);
    }

    @Test
    void testGetByIdWhenNotCachedReturnsDatabaseData() {
        when(locationCache.containsKey("location_1")).thenReturn(false);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        Optional<Location> result = locationService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(location, result.get());
        verify(locationCache).put("location_1", List.of(location));
    }

    @Test
    void testGetByIdWhenNotFoundReturnsEmpty() {
        when(locationCache.containsKey("location_1")).thenReturn(false);
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Location> result = locationService.getById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testCreateWithSunriseSunsetIdsAddsRelationsAndClearsCache() {
        when(sunriseSunsetRepository.findAllById(List.of(1L))).thenReturn(List.of(sunriseSunset));
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        Location result = locationService.create(location, List.of(1L));

        assertEquals(location, result);
        assertEquals(1, location.getSunriseSunsets().size());
        verify(locationCache).clear();
    }

    @Test
    void testUpdateWhenExistsUpdatesDataAndClearsCache() {
        Location updatedData = new Location();
        updatedData.setName("Updated Name");
        updatedData.setCountry("Updated Country");

        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(sunriseSunsetRepository.findAllById(List.of(1L))).thenReturn(List.of(sunriseSunset));
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        Optional<Location> result = locationService.update(1L, updatedData, List.of(1L));

        assertTrue(result.isPresent());
        assertEquals("Updated Name", result.get().getName());
        assertEquals("Updated Country", result.get().getCountry());
        assertEquals(1, result.get().getSunriseSunsets().size());
        verify(locationCache).clear();
    }

    @Test
    void testUpdateWhenNotFoundReturnsEmpty() {
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Location> result = locationService.update(1L, location, List.of(1L));

        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteWhenExistsDeletesAndClearsCache() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        boolean result = locationService.delete(1L);

        assertTrue(result);
        verify(locationRepository).delete(location);
        verify(locationCache).clear();
    }

    @Test
    void testDeleteWhenNotFoundReturnsFalse() {
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = locationService.delete(1L);

        assertFalse(result);
        verify(locationRepository, never()).delete(any());
    }

    @Test
    void testGetLocationsByDateWhenCachedReturnsCachedData() {
        when(locationCache.containsKey("locations_date_2025-04-04")).thenReturn(true);
        when(locationCache.get("locations_date_2025-04-04")).thenReturn(List.of(location));

        List<Location> result = locationService.getLocationsByDate("2025-04-04");

        assertEquals(1, result.size());
        assertEquals(location, result.get(0));
        verify(locationRepository, never()).findLocationsBySunriseSunsetDate("2025-04-04");
    }

    @Test
    void testGetLocationsByDateWhenNotCachedReturnsDatabaseData() {
        when(locationCache.containsKey("locations_date_2025-04-04")).thenReturn(false);
        when(locationRepository.findLocationsBySunriseSunsetDate("2025-04-04")).thenReturn(List.of(location));

        List<Location> result = locationService.getLocationsByDate("2025-04-04");

        assertEquals(1, result.size());
        assertEquals(location, result.get(0));
        verify(locationCache).put("locations_date_2025-04-04", List.of(location));
    }

    @Test
    void testBulkCreateOrUpdateProcessesNewAndExistingLocations() {
        Location newLocation = new Location();
        newLocation.setName("New Location");
        newLocation.setCountry("New Country");

        Location existingLocation = new Location();
        existingLocation.setId(1L);
        existingLocation.setName("Existing Location");
        existingLocation.setCountry("Existing Country");

        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(locationRepository.findById(0L)).thenReturn(Optional.empty());
        when(sunriseSunsetRepository.findAllById(List.of(1L))).thenReturn(List.of(sunriseSunset));
        when(locationRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Location> result = locationService.bulkCreateOrUpdate(List.of(newLocation, existingLocation), List.of(1L));

        assertEquals(2, result.size());
        assertEquals("New Location", result.get(0).getName());
        assertEquals("Existing Location", result.get(1).getName());
        assertEquals(1, result.get(0).getSunriseSunsets().size());
        assertEquals(1, result.get(1).getSunriseSunsets().size());
        verify(locationCache).clear();
    }
}