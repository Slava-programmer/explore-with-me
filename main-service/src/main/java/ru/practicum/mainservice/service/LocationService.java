package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.dto.location.LocationDto;
import ru.practicum.mainservice.entity.Location;
import ru.practicum.mainservice.repository.LocationRepository;
import ru.practicum.mainservice.service.mapper.LocationMapper;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

    @Transactional
    public Location createLocation(LocationDto locationDto) {
        return locationRepository.save(LocationMapper.toLocation(locationDto));
    }
}
