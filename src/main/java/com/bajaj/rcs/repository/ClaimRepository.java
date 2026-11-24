package com.bajaj.rcs.repository;

import com.bajaj.rcs.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    Optional<Claim> findByClaimNumber(String claimNumber);

    long countByHandlerIdAndStatus(Long handlerId, String status);

    List<Claim> findByHandlerId(Long handlerId);
}
