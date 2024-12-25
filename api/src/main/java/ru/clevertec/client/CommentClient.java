package ru.clevertec.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.clevertec.dto.CommentDto;

import java.util.UUID;

@FeignClient(name = "comment-service", url = "http://localhost:8082")
public interface CommentClient {

    @GetMapping("/comments/{newsId}")
    Page<CommentDto> getCommentsForNews(@PathVariable UUID newsId);
}
