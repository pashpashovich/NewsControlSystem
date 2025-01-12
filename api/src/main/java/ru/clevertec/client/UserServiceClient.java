package ru.clevertec.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.clevertec.api.ApiResponse;
import ru.clevertec.dto.UserDetailsDto;

@FeignClient(name = "user-service", url = "${feign.user-service.url}")
public interface UserServiceClient {
    @GetMapping("/users/{username}")
    ResponseEntity<ApiResponse<UserDetailsDto>> getUserByUsername(@PathVariable("username") String username);
}

