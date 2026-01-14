package com.osc.leaderboard.fetch.controller;

import java.time.Instant;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.osc.leaderboard.fetch.dtos.FetchDTO;
import com.osc.leaderboard.fetch.service.FetchService;

@RestController
@RequestMapping("/fetch")
public class FetchController {

    private final FetchService fetchService;

    public FetchController(FetchService fetchService) {
        this.fetchService = fetchService;
    }

    @PostMapping
    public ResponseEntity<FetchDTO> createFetch() {
        return new ResponseEntity<>(fetchService.createFetch(), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FetchDTO>> getFetchesByDate(@RequestParam(required = false) Instant start,
            @RequestParam(required = false) Instant end) throws BadRequestException {
        if (start == null && end == null) {
            throw new BadRequestException("A start or end date must be specified");
        }

        if (start == null) {
            return new ResponseEntity<>(fetchService.getFetchesByDateBefore(end), HttpStatus.OK);
        }

        if (end == null) {
            return new ResponseEntity<>(fetchService.getFetchesByDateAfter(start), HttpStatus.OK);
        }

        return new ResponseEntity<>(fetchService.getFetchesByDateBetween(start, end), HttpStatus.OK);
    }

}
