package com.library.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.responseDTO.ApiResponse;
import com.library.responseDTO.Status;

@RestController
public class PathNotFoundErrorController {

    @RequestMapping("/**")
    public ResponseEntity<ApiResponse<String>> handleUndefinedPaths() {
        ApiResponse<String> response = ApiResponse.<String>builder()
            .status(Status.FAILURE)
            .message("Page not found")
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
