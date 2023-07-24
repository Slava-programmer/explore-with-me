package ru.practicum.mainservice.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.mainservice.exception.IncorrectRequestException;

@UtilityClass
public class PageableUtils {

    public PageRequest createPageable(Integer from, Integer size, String sort) {
        validateParameters(from, size);

        if (sort == null || sort.isEmpty()) {
            return PageRequest.of(from / size, size);
        }

        switch (sort.toUpperCase()) {
            case "EVENT_DATE":
                return PageRequest.of(from / size, size, Sort.by("eventDate"));
            case "VIEWS":
                return PageRequest.of(from / size, size, Sort.by("views").descending());
            default:
                throw new IncorrectRequestException("Unknown sort: " + sort);
        }
    }

    private void validateParameters(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new IncorrectRequestException(
                    "Parameter 'from' must be greater than or equal to 0 and 'size' is greater than 0");
        }
    }
}
