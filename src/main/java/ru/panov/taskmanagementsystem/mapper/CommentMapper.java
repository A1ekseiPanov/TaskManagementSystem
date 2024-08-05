package ru.panov.taskmanagementsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.panov.taskmanagementsystem.model.Comment;
import ru.panov.taskmanagementsystem.model.dto.response.CommentResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "commentId", source = "comment.id")
    CommentResponse commentToResponseEntity(Comment comment);

    List<CommentResponse> listCommentToListResponseEntity(List<Comment> comments);

}