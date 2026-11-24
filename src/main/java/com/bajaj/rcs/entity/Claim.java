package com.bajaj.rcs.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "claim")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String claimNumber;

    @Column(nullable = false, length = 50)
    private String claimLocation;

    @Column(nullable = false, length = 20)
    private String lob;

    /** Money is BigDecimal, never double. */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal lossReserveAmount;

    @Column(nullable = false, length = 20)
    private String status = "OPEN";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handler_id")
    private ClaimHandler handler;

    protected Claim() { }

    public void allocateTo(ClaimHandler handler) {
        this.handler = handler;
    }

    public Long getId()                      { return id; }
    public String getClaimNumber()           { return claimNumber; }
    public String getClaimLocation()         { return claimLocation; }
    public String getLob()                   { return lob; }
    public BigDecimal getLossReserveAmount() { return lossReserveAmount; }
    public String getStatus()                { return status; }
    public ClaimHandler getHandler()         { return handler; }
}
