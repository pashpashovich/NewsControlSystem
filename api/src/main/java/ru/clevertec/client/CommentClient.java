package ru.clevertec.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.clevertec.api.ApiResponse;
import ru.clevertec.dto.CommentDto;

import java.util.UUID;

@FeignClient(name = "comment-service", url = "${feign.comment-service.url}")
public interface CommentClient {

    @GetMapping("/comments/{newsId}")
    ResponseEntity<ApiResponse<Page<CommentDto>>> getCommentsForNews(@PathVariable UUID newsId);
}
