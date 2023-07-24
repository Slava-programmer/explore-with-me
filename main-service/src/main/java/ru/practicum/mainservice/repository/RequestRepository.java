package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.entity.Request;
import ru.practicum.mainservice.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Integer countByEventIdAndStatus(Long eventId, RequestStatus confirmed);

    Boolean existsByEventIdAndRequesterId(Long eventId, Long userId);

    List<Request> findAllByRequesterId(Long userId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);
}
