package com.bajaj.rcs.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "claim_handler")
public class ClaimHandler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String handlerCode;

    @Column(nullable = false, length = 100)
    private String handlerName;

    @Column(nullable = false, length = 50)
    private String claimLocation;

    @Column(nullable = false, length = 20)
    private String lob;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal minAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal maxAmount;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean onLeave = false;

    private LocalDate leaveFrom;
    private LocalDate leaveTo;

    protected ClaimHandler() { }


    public boolean canHandle(String location, String claimLob, BigDecimal amount, LocalDate today) {
        if (!active) return false;
        if (!claimLocation.equalsIgnoreCase(location)) return false;
        if (!lob.equalsIgnoreCase(claimLob)) return false;
        if (amount == null) return false;
        if (amount.compareTo(minAmount) < 0 || amount.compareTo(maxAmount) > 0) return false;
        return !isOnLeaveOn(today);
    }

    private boolean isOnLeaveOn(LocalDate date) {
        if (!onLeave) return false;
        if (leaveFrom == null || leaveTo == null) return true;
        return !date.isBefore(leaveFrom) && !date.isAfter(leaveTo);
    }

    public Long getId()              { return id; }
    public String getHandlerCode()   { return handlerCode; }
    public String getHandlerName()   { return handlerName; }
    public String getClaimLocation() { return claimLocation; }
    public String getLob()           { return lob; }
    public BigDecimal getMinAmount() { return minAmount; }
    public BigDecimal getMaxAmount() { return maxAmount; }
    public boolean isActive()        { return active; }
    public boolean isOnLeave()       { return onLeave; }
}
