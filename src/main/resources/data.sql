-- Four handlers. CH003 is on leave, CH004 is inactive.

INSERT INTO claim_handler
  (id, handler_code, handler_name, claim_location, lob, min_amount, max_amount,
   active, on_leave, leave_from, leave_to)
VALUES
  (1, 'CH001', 'Rohit Deshmukh', 'PUNE', 'MOTOR',      1.00,  500000.00, TRUE,  FALSE, NULL, NULL),
  (2, 'CH002', 'Priya Nair',     'PUNE', 'MOTOR',      1.00,  500000.00, TRUE,  FALSE, NULL, NULL),
  (3, 'CH003', 'Imran Shaikh',   'PUNE', 'MOTOR',      1.00,  500000.00, TRUE,  TRUE,  '2026-11-15', '2026-11-30'),
  (4, 'CH004', 'Anita Kulkarni', 'PUNE', 'MOTOR', 500001.00, 2500000.00, FALSE, FALSE, NULL, NULL);

-- Three claims waiting, plus two already in CH001's tray so the workloads differ.

INSERT INTO claim
  (id, claim_number, claim_location, lob, loss_reserve_amount, status, handler_id)
VALUES
  (1, 'CLM-001', 'PUNE', 'MOTOR',    25000.00, 'OPEN', NULL),
  (2, 'CLM-002', 'PUNE', 'MOTOR',    40000.00, 'OPEN', NULL),
  (3, 'CLM-003', 'PUNE', 'MOTOR', 90000000.00, 'OPEN', NULL),
  (4, 'CLM-100', 'PUNE', 'MOTOR',    11000.00, 'OPEN', 1),
  (5, 'CLM-101', 'PUNE', 'MOTOR',    12000.00, 'OPEN', 1);

-- CH001 has 2 open claims, CH002 has 0. So CLM-001 should go to CH002.
-- CLM-003 is 9 crore, which no handler's range covers, so it returns 422.
