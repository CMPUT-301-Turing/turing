## 4. CRC Cards (Draft — Core Classes)

The six classes below were identified in both initial object-oriented analyses. These cards incorporate the subsequent responsibility review. They remain **proposals for team review**, not finalized implementation decisions. Collaborators are included for meaningful *uses* or *has-a* relationships, not solely inheritance or database storage.

### 4.1 Entrant

| Responsibilities | Collaborators |
| --- | --- |
| Maintain entrant-specific participation information | — |
| Maintain relationships to lottery registrations and withdrawals | `WaitingList` |
| Maintain associations with the entrant's reservations | `Reservation` |
| Maintain associations with submitted campsite reviews | `Review`, `Campsite` |

**Relevant user stories:** US 01.02.01–02, US 01.06.01–02, US 01.13.01.  
**Design note:** Shared identity and notification preferences belong to `User`, not `Entrant`. Actual registration and cancellation workflows are coordinated elsewhere. The final role/profile representation awaits team review.

### 4.2 CampsiteOwner

| Responsibilities | Collaborators |
| --- | --- |
| Maintain associations with campsites owned by the user | `Campsite` |
| Provide ownership information needed by authorized workflows | `Campsite` |

**Relevant user stories:** US 02.01.01–04, US 02.03.01, US 02.07.01–03, US 02.12.01.  
**Design note:** Do not assign reservation or message storage to `CampsiteOwner`. A proposed `MessagingController` coordinates owner-to-entrant messaging authorization using ownership and reservation information, while the data-access layer handles persistence. No separate owner-facing controller is assumed.

### 4.3 Campsite

| Responsibilities | Collaborators |
| --- | --- |
| Maintain campsite attributes, description, location, capacity, and relevant rules | — |
| Maintain campsite ownership information | `CampsiteOwner` |
| Own the campsite's bookable dates and scheduling restrictions | `AvailabilitySchedule` |
| Provide campsite details needed by other workflows | — |

**Relevant user stories:** US 01.01.02–05, US 02.01.01–04, US 02.06.01, US 02.11.01.  
**Design note:** `AvailabilitySchedule` is composed by `Campsite`. `Lottery` references participating campsites, but `Campsite` does **not** maintain reciprocal lottery references. Existing reservations remain authoritative for occupied dates; `BookingService` coordinates availability checks.

### 4.4 Lottery

| Responsibilities | Collaborators |
| --- | --- |
| Maintain registration period, lottery status, and selection rules | — |
| Reference one or more participating campsites | `Campsite` |
| Own and consult the associated waiting list | `WaitingList` |
| Select initial and, where required, replacement winners | `WaitingList` |
| Maintain winner entrant IDs and their selection/response statuses | — |
| Evaluate lottery-specific scheduling rules using supplied relevant lottery information | — |

**Relevant user stories:** US 01.02.01–02, US 01.03.01–02, US 02.04.01–07, US 02.05.01.  
**Design note:** `LotteryManager` triggers lottery operations, handles winner-response callbacks, and coordinates cross-lottery checks by retrieving other lottery records through `DatabaseController`. `Lottery` owns winner selection; `LotteryManager` does not duplicate that algorithm. Winner IDs and statuses need not imply a direct `Entrant` object collaborator. Specific replacement rules and cross-lottery restrictions require confirmation against requirements/team decisions.

### 4.5 Reservation

| Responsibilities | Collaborators |
| --- | --- |
| Maintain its own reservation ID, entrant ID, campsite ID, dates, and status | `Entrant`, `Campsite` |
| Provide access to details of this individual reservation | — |
| Maintain valid transitions of its own reservation status | — |

**Relevant user stories:** US 01.04.01–03, US 01.05.01–02, US 01.06.01–02, US 01.07.01–02, US 02.07.01–03, US 02.08.01.  
**Design note:** `BookingService` coordinates booking, cancellation, eligibility, and availability; `Reservation` does not retrieve other reservations, persist itself, send confirmations, or manage reservation history. The `Entrant`/`Campsite` collaborators represent the booking's domain associations; if implemented exclusively as IDs, the team can revisit whether they qualify as direct CRC collaborators.

### 4.6 Notification

| Responsibilities | Collaborators |
| --- | --- |
| Maintain recipient, content, type, timestamp, and status | `User` |
| Provide its notification details | — |
| Maintain its own read/status information | — |

**Relevant user stories:** US 01.03.01–02, US 01.05.01, US 01.07.02, US 01.10.01, US 02.10.01–02, US 03.08.01.  
**Design note:** `User` is the agreed collaborator. Relevant workflow controllers initiate notifications; `DatabaseController` and `DatabaseManager` retrieve/persist records; the UI displays them. No new notification-delivery service is proposed at this stage. `Message` is proposed as a subclass of `Notification`, but inheritance alone is not a collaborator relationship.

### 4.7 Supporting architecture proposals (not additional CRC cards in this draft)

- **Shared identity:** One `User` ID; notification preferences belong to `User`. Owner access depends on campsite ownership. `Administrator` and `AdminController` handle distinct moderation responsibilities; role/profile details remain open for team review.
- **Lottery coordination:** `LotteryManager` handles workflow callbacks and cross-lottery coordination; `Lottery` selects winners and owns its `WaitingList` through composition.
- **Booking:** `BookingService` coordinates `AvailabilitySchedule`, reservation information, and persistence, without taking ownership of each model's rules.
- **Persistence:** `DatabaseController` is the shared data-access entry point, delegating actual reads/writes to `DatabaseManager`. This is the proposed alternative to per-domain repositories.
- **Messaging:** `MessagingController` is a proposed new class for authorized owner-to-entrant messaging; `Message` is proposed to inherit `Notification` for the required one-way communication behavior. Both are pending team review.

**Review status:** Ready to add as a separate draft PR and request Himanshu's review. This section does not overwrite or imply approval of his existing OO analysis and design decisions.
