package com.bajaj.rcs.controller;

import com.bajaj.rcs.entity.Claim;
import com.bajaj.rcs.entity.ClaimHandler;
import com.bajaj.rcs.repository.ClaimHandlerRepository;
import com.bajaj.rcs.repository.ClaimRepository;
import com.bajaj.rcs.service.AllocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final AllocationService allocationService;
    private final ClaimRepository claimRepository;
    private final ClaimHandlerRepository handlerRepository;

    public ClaimController(AllocationService allocationService,
                           ClaimRepository claimRepository,
                           ClaimHandlerRepository handlerRepository) {
        this.allocationService = allocationService;
        this.claimRepository = claimRepository;
        this.handlerRepository = handlerRepository;
    }

    /** Allocate a claim to an eligible handler. */
    @PostMapping("/{claimNumber}/allocate")
    public Map<String, Object> allocate(@PathVariable String claimNumber) {
        ClaimHandler handler = allocationService.allocate(claimNumber);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("claimNumber", claimNumber);
        response.put("handlerCode", handler.getHandlerCode());
        response.put("handlerName", handler.getHandlerName());
        response.put("message", "Claim allocated successfully");
        return response;
    }

    /** One claim, showing which handler it went to. */
    @GetMapping("/{claimNumber}")
    public Map<String, Object> getClaim(@PathVariable String claimNumber) {
        Claim claim = claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found: " + claimNumber));
        return toMap(claim);
    }

    /** All claims with their handlers. */
    @GetMapping
    public List<Map<String, Object>> getAllClaims() {
        return claimRepository.findAll().stream().map(this::toMap).toList();
    }

    /** All claims allocated to one handler. */
    @GetMapping("/handler/{handlerId}")
    public List<Map<String, Object>> getClaimsByHandler(@PathVariable Long handlerId) {
        return claimRepository.findByHandlerId(handlerId).stream().map(this::toMap).toList();
    }

    /** The handler master, so you can see the configuration. */
    @GetMapping("/handlers")
    public List<ClaimHandler> getHandlers() {
        return handlerRepository.findAll();
    }

    private Map<String, Object> toMap(Claim claim) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("claimNumber", claim.getClaimNumber());
        map.put("claimLocation", claim.getClaimLocation());
        map.put("lob", claim.getLob());
        map.put("amount", claim.getLossReserveAmount());
        map.put("status", claim.getStatus());
        if (claim.getHandler() != null) {
            map.put("handlerCode", claim.getHandler().getHandlerCode());
            map.put("handlerName", claim.getHandler().getHandlerName());
        } else {
            map.put("handlerCode", null);
            map.put("handlerName", "Not allocated");
        }
        return map;
    }

    /** Turns the two exceptions this controller can throw into proper status codes. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> notFound(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> noHandler(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("error", ex.getMessage()));
    }
}
