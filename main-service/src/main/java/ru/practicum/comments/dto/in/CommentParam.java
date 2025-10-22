package ru.practicum.comments.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentParam {
    Long userId;
    Long eventId;
    Long commentId;
}