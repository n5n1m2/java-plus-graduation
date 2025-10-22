package ru.practicum.compilation.dto.in;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Data
public class NewCompilationDto {
    @UniqueElements
    List<Long> events;
    Boolean pinned = false;
    @NotBlank(message = "Field: title. Error: must not be blank. Value: null")
    @Length(min = 1, max = 50)
    String title;
}
