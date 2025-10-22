package ru.practicum.events.dto.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import ru.practicum.requests.model.Status;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {
    @NotEmpty
    @UniqueElements
    List<Long> requestIds;
    Status status;
}
