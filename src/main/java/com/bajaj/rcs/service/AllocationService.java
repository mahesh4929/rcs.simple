package com.bajaj.rcs.service;

import com.bajaj.rcs.entity.Claim;
import com.bajaj.rcs.entity.ClaimHandler;
import com.bajaj.rcs.repository.ClaimHandlerRepository;
import com.bajaj.rcs.repository.ClaimRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
public class AllocationService {

    private final ClaimRepository claimRepository;
    private final ClaimHandlerRepository handlerRepository;

    public AllocationService(ClaimRepository claimRepository,
                             ClaimHandlerRepository handlerRepository) {
        this.claimRepository = claimRepository;
        this.handlerRepository = handlerRepository;
    }

    @Transactional
    public ClaimHandler allocate(String claimNumber) {

        Claim claim = claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found: " + claimNumber));

        // Already allocated - return the same handler instead of allocating twice.
        if (claim.getHandler() != null) {
            return claim.getHandler();
        }

        // Step 1: which handlers are eligible?
        List<ClaimHandler> eligible = handlerRepository.findByActiveTrue().stream()
                .filter(h -> h.canHandle(claim.getClaimLocation(), claim.getLob(),
                                         claim.getLossReserveAmount(), LocalDate.now()))
                .toList();

        if (eligible.isEmpty()) {
            throw new IllegalStateException("No eligible handler for claim " + claimNumber);
        }

        // Step 2: of those, pick the one with the fewest open claims.
        ClaimHandler chosen = eligible.get(0);
        long lowestCount = openClaimCount(chosen);

        for (ClaimHandler handler : eligible) {
            long count = openClaimCount(handler);
            if (count < lowestCount) {
                lowestCount = count;
                chosen = handler;
            }
        }

        claim.allocateTo(chosen);
        return chosen;
    }

    private long openClaimCount(ClaimHandler handler) {
        return claimRepository.countByHandlerIdAndStatus(handler.getId(), "OPEN");
    }
}
