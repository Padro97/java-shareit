package ru.practicum.shareit.comment;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    CommentDto toDto(Comment comment);

    Comment fromDto(CommentDto commentDto);

    List<CommentDto> toDtoList(List<Comment> comments);
}
