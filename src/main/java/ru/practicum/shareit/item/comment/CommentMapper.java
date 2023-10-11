package ru.practicum.shareit.item.comment;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class CommentMapper {
    public CommentDto commentMap(Comment comment) {
        return CommentDto
                .builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(comment.getItem())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public Comment commentTextDtoMapping(CommentTextDto commentTextDto, Item item, User author) {
        return Comment
                .builder()
                .text(commentTextDto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }

    public List<CommentDto> commentListMap(List<Comment> comments) {
        List<CommentDto> result = new ArrayList<>();
        comments.forEach(comment -> result.add(commentMap(comment)));
        return result;
    }
}
