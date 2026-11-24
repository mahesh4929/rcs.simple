package com.bajaj.rcs.repository;

import com.bajaj.rcs.entity.ClaimHandler;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimHandlerRepository extends JpaRepository<ClaimHandler, Long> {

    List<ClaimHandler> findByActiveTrue();
}
