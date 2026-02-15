package com.cinect.controller;

import com.cinect.dto.response.ApiResponse;
import com.cinect.dto.response.SnackResponse;
import com.cinect.service.SnackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/snacks")
@RequiredArgsConstructor
public class SnackController {

    private final SnackService snackService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SnackResponse>>> findAll() {
        var data = snackService.findAll();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/cinema/{cinemaId}")
    public ResponseEntity<ApiResponse<List<SnackResponse>>> findByCinema(@PathVariable UUID cinemaId) {
        var data = snackService.findByCinema(cinemaId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
