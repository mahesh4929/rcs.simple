# RCS — Claim Allocation API

Allocates a claim to an eligible claim handler, and shows which claim sits with which
handler.

Spring Boot 3.3 · Java 17 · H2 in memory · 7 Java files.

## Run

```bash
mvn spring-boot:run
```

Four handlers and five claims are seeded at startup.

## Endpoints

| Method | Path | Purpose |
|---|---|---|
| POST | `/api/claims/{claimNumber}/allocate` | Allocate a claim |
| GET | `/api/claims/{claimNumber}` | One claim with its handler |
| GET | `/api/claims` | All claims with their handlers |
| GET | `/api/claims/handler/{handlerId}` | One handler's claims |
| GET | `/api/claims/handlers` | The handler master |

## How allocation works

1. Load all active handlers.
2. Keep the ones that can handle this claim — five checks: active, claim location
   matches, LOB matches, loss reserve amount falls in the handler's range, and the
   handler is not on leave today.
3. Of those, allocate to whoever has the fewest open claims.

If the claim is already allocated, the same handler is returned rather than allocating
it a second time.

## Try it

**Starting state:** CH001 has 2 open claims, CH002 has 0. CH003 is on leave, CH004 is
inactive.

```
GET  http://localhost:8080/api/claims
POST http://localhost:8080/api/claims/CLM-001/allocate     → goes to CH002
GET  http://localhost:8080/api/claims/CLM-001              → shows CH002
POST http://localhost:8080/api/claims/CLM-002/allocate     → CH002 again (1 claim vs CH001's 2)
GET  http://localhost:8080/api/claims/handler/2            → CH002's claims
POST http://localhost:8080/api/claims/CLM-003/allocate     → 422, no handler covers 9 crore
GET  http://localhost:8080/api/claims/XXX                  → 404
```

Browse the data at `http://localhost:8080/h2-console`
(JDBC URL `jdbc:h2:mem:rcs`, user `sa`, no password).

## Not done yet

- Only the five eligibility checks above. The spec also lists product code, policy
  number, partner type, insured name and insured segment.
- Round Robin when two handlers have the same workload — currently the first one wins.
- The `Calculate Current Workload = N` branch, which should use pure Round Robin.
- The Claim Handler Master screens (add policy number, add insured name, set leave,
  set half day).
