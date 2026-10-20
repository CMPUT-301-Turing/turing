# CMPUT 301 - Object-Oriented Analysis (CRC Cards)
**Team:** Turing\
**Project:** Campsite Reservation\
**Status:** Draft

## 1.Overview
This document captures the object-oriented design for the campsite reservation system


## 2.Class Traceability
This table maps every single assigned User story to its primary domain classes

### | **Class**                          |   **Type**                                   |   **Relevant User Story**                                                             |       **Checkpoint**    |

---

| `User` (Base)                          |  Model        | `US 01.07.03`, `US 01.08.01`, `US 01.09.01`, `US 01.10.01`                                                                                        | **Draft**|\
| `Entrant`                              |  Model        | `US 01.01.01-06`, `US 01.02.01-02`, `US 01.04.01-03`, `US 01.11.01`, `US 01.13.01`                                           |**Draft**|\
| `CampsiteOwner`                 |  Model        | `US 02.01.01-04`, `US 02.03.01`, `US 02.07.01-03`, `US 02.08.01`, `US 02.10.01-02`, `US 02.12.01`                 |**Draft**|\
| `Administrator`                 |  Model         | `US 03.01.01-08`, `US 03.11.01`, `US 03.12.01`                                                                                                           |**Draft**|\
| `Campsite`                            |  Model         | `US 01.01.02-05`, `US 02.01.01-04`, `US 02.06.01`, `US 02.11.01`                                                                           |**Draft**|\
| `Lottery`                              |  Model         | `US 01.02.01`, `US 01.03.01-02`, `US 02.04.01-06`, `US 02.05.01`                                                                           |**Draft**|\
| `LotteryManager`              | Controller     | `US 02.04.05`, `US 02.05.01`, `US 01.04.03`                                                                                                                  |**Draft**|\
| `WaitingList`                     |  Model         | `US 01.02.01-02`, `US 02.04.07`, `US 02.09.01`                                                                                                            |**Draft**|\
| `Reservation`                     |  Model         | `US 01.04.01–03`, `US 01.05.01–02`, `US 01.06.01–02`, `US 01.07.01–02`, `US 02.07.01–03`, `US 02.08.01`    |**Draft**|\
| `AvailabilitySchedule` |  Model         | `US 01.04.01-02`, `US 02.06.01`                                                                                                                                      |**Draft**|\
| `Notification`                   |  Model         | `US 01.03.01–02`, `US 01.05.01`, `US 01.07.02`, `US 01.10.01`, `US 02.10.01–02`, `US 03.08.01`                       |**Draft**|\
| `QRCodeHandler`                 |  Utility          | `US 01.12.01`, `US 02.02.01`                                                                                                                                           |**Draft**|\
| `Message`                              |  Model         | `US 02.12.01`, `US 03.12.01`                                                                                                                                           |**Draft**|\
| `Review`                                |  Model         | `US 01.13.01`                                                                                                                                                                     |**Draft**|\
| `DeviceAuthManager`        |  Service        | `US 01.09.01`                                                                                                                                                                     |**Draft**|\
| `GeoLocationService`      |  Service        | `US 01.01.02`, `US 02.01.02`                                                                                                                                           |**Draft**|\
| `ImageService`                   |  Service        | `US 01.07.03`, `US 02.01.02`, `US 03.03.01`, `US 03.06.01`                                                                                       |**Draft**|\
| `CSVExporter`                     |  Utility          | `US 01.05.02`, `US 01.07.02`                                                                                                                                           |**Draft**|\
| `DatabaseManager`            |  Persistence  | `US 01.08.01`, `US 03.01.01-03`                                                                                                                                     |**Draft**|\
| `SearchFilterManager`   | Controller     | `US 01.01.01`, `US 01.01.02`, `US 01.01.03`                                                                                                                 |**Draft**|\
| `AuditLog`                           | Service     | `US 03.08.01`, `US 03.12.01`                                                                                                                                                |**Draft**|

## 3.Design Choice & Decisions

##### **1. User Inheritance:** 

 We Made `User` a base class that `Entant`, `CampsideOwner`, `Administrator` extend. This matches the `US 01.09.01` (device based login) and handles `US 03.11.01` (admins 
 being able to act as owners and entrants without creating seperate accounts).
 
##### **2. Availability vs. Campsite:**
 
 Instead of cluttering `Campsite` with raw date range math, we split off `AvailabilitySchedule` to handle calendar lookups and booked dates.
 
##### **3. Lottery Management:**

 We added `LotteryManager` as a controller/logic coordinator to handel random winner draws and replacement picks without stuffing that logic into the `Lottery` model. 
 
##### **4.Why `WaitingList` is its own class instead od a field inside `Lottery`:**

 The user stories mention optional waitlist capacity caps `US 02.04.07`, entrants leaving the waitlist voluntarily `US 01.02.02`, and owners checking the list of entrants `US 02.09.01`. Giving `WaitingList` its own class keeps `Lottery` clean and focused on running the draw.



Here is the complete, finalized Markdown code. You can copy this entire block and paste it directly into your `crc_draft.md` file in your repository.


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

### 4.7 User

| Responsibilities | Collaborators |
| --- | --- |
| Maintain device-based identity (UUID) | — |
| Maintain shared personal profile information and avatar reference | — |
| Maintain global notification preferences | — |

**Relevant user stories:** US 01.07.03, US 01.08.01, US 01.09.01, US 01.10.01.  
**Design note:** Base class for `Entrant`, `CampsiteOwner`, and `Administrator`. Represents shared identity across the app. Actual authentication flows are handled by the system, and data-access layers handle persistence.

### 4.8 Administrator

| Responsibilities | Collaborators |
| --- | --- |
| Maintain administrative access and identity | — |
| Provide identity verification for system moderation workflows | — |

**Relevant user stories:** US 03.01.01–08, US 03.11.01, US 03.12.01.  
**Design note:** Role assumed by a `User`. Does not directly manipulate records; a proposed `AdminController` coordinates moderation actions (removing profiles, campsites, images) through the data-access layer.

### 4.9 AvailabilitySchedule

| Responsibilities | Collaborators |
| --- | --- |
| Maintain booked and blocked date ranges for a specific campsite | `Reservation` |
| Provide availability status for requested date ranges | — |

**Relevant user stories:** US 01.04.01–02, US 02.06.01.  
**Design note:** Composed by `Campsite` to separate raw date-range tracking from the main property entity. It consults existing `Reservation` states to determine true availability for the UI and `BookingService`.

### 4.10 WaitingList

| Responsibilities | Collaborators |
| --- | --- |
| Maintain the pool of registered entrants for a specific lottery | `Entrant` |
| Enforce maximum capacity rules if configured by the owner | — |
| Prevent duplicate entries | `Entrant` |
| Provide the candidate pool for winner selection | — |

**Relevant user stories:** US 01.02.01–02, US 02.04.07, US 02.09.01.  
**Design note:** Owned by `Lottery` through composition. Extracting this from `Lottery` directly addresses the owner's need to view waitlisted entrants separately from the core draw mechanics.

### 4.11 Message

| Responsibilities | Collaborators |
| --- | --- |
| Maintain sender, recipient, associated reservation, content, and timestamp | `User`, `Reservation` |
| Provide message details for audit logging | — |

**Relevant user stories:** US 02.12.01, US 03.12.01.  
**Design note:** Proposed to inherit from `Notification` to share basic delivery and read-status behavior, but specific to owner-to-entrant communication regarding an active `Reservation`. `MessagingController` handles authorization.

### 4.12 Review

| Responsibilities | Collaborators |
| --- | --- |
| Maintain star rating, written feedback, and timestamp | — |
| Maintain association with the targeted campsite and the authoring entrant | `Entrant`, `Campsite` |

**Relevant user stories:** US 01.13.01.  
**Design note:** Independent entity created by an `Entrant` after a completed stay. Target for the final project checkpoint.

### 4.13 GeoLocationService

| Responsibilities | Collaborators |
| --- | --- |
| Compute distances between user device coordinates and campsites | `Campsite` |
| Filter and return campsite listings within a specified geographic radius | `Campsite` |

**Relevant user stories:** US 01.01.02, US 02.01.02.  
**Design note:** Added as a meaningful service collaborator per TA guidance. Fulfills the location-based mapping and radius-filtering stories independently of the UI.

### 4.14 LotteryManager

| Responsibilities | Collaborators |
| --- | --- |
| Detect closed registration windows and trigger lottery selections | `Lottery` |
| Coordinate win/loss alert delivery based on draw results | `Notification`, `Entrant` |
| Trigger replacement winner draws when initial winners decline or expire | `Lottery`, `WaitingList` |

**Relevant user stories:** US 02.04.05, US 02.05.01, US 01.04.03.  
**Design note:** Controller that coordinates workflow callbacks and cross-lottery coordination without duplicating the selection algorithm owned by `Lottery`.

### 4.15 BookingService

| Responsibilities | Collaborators |
| --- | --- |
| Coordinate availability checks against existing reservations | `Campsite`, `AvailabilitySchedule` |
| Validate candidate booking dates and enforce eligibility | `Reservation` |

**Relevant user stories:** US 01.04.01, US 01.06.01, US 02.06.01.  
**Design note:** Coordinates `AvailabilitySchedule`, reservation information, and persistence, acting as the workflow controller so that `Campsite` and `Reservation` do not have to directly query the database.

### 4.16 QRCodeHandler

| Responsibilities | Collaborators |
| --- | --- |
| Generate QR bitmap images embedding campsite deep-link data | `Campsite` |
| Decode scanned QR codes to extract target campsite identifiers | `Campsite` |

**Relevant user stories:** US 01.12.01, US 02.02.01.  
**Design note:** Service class responsible for fulfilling the specific QR generation and scanning workflows required by the application.

### 4.17 ImageService

| Responsibilities | Collaborators |
| --- | --- |
| Handle image uploads for user profiles and campsite banners | `User`, `Campsite` |
| Provide public image URIs/references to the UI | `User`, `Campsite` |

**Relevant user stories:** US 01.07.03, US 02.01.02, US 03.03.01.  
**Design note:** Service class that coordinates media storage operations independently from the main structured database.

### 4.18 SearchFilterManager

| Responsibilities | Collaborators |
| --- | --- |
| Accept search parameters (dates, guest count, geographic radius) | — |
| Request filtered campsite lists based on attributes and location | `Campsite`, `GeoLocationService` |
| Request availability checks for filtered campsites | `AvailabilitySchedule` |

**Relevant user stories:** US 01.01.01–03.  
**Design note:** Controller that coordinates the filtering logic so that `Entrant` does not have to execute complex data queries directly.

### 4.19 DeviceAuthManager

| Responsibilities | Collaborators |
| --- | --- |
| Fetch or generate the persistent hardware UUID for the device | — |
| Provide device identity to the system to authenticate the user session | `User` |

**Relevant user stories:** US 01.09.01.  
**Design note:** Service class that abstracts the Android-specific hardware ID retrieval away from the pure `User` domain model.

### 4.20 CSVExporter

| Responsibilities | Collaborators |
| --- | --- |
| Format reservation metadata (Dates, Campsite, Status) into CSV rows | `Reservation` |
| Save the resulting CSV file to local device storage | — |

**Relevant user stories:** US 01.05.02, US 01.07.02.  
**Design note:** Utility service. Included as a card because it explicitly fulfills the downloadable CSV requirements, separating file I/O operations from the `Reservation` model.

---

### 4.21 Supporting architecture proposals (not additional CRC cards in this draft)

- **Shared identity:** One `User` ID; notification preferences belong to `User`. Owner access depends on campsite ownership. `Administrator` and `AdminController` handle distinct moderation responsibilities; role/profile details remain open for team review.
- **Lottery coordination:** `LotteryManager` handles workflow callbacks and cross-lottery coordination; `Lottery` selects winners and owns its `WaitingList` through composition.
- **Booking & Filtering:** `BookingService` coordinates `AvailabilitySchedule`, reservation information, and persistence. A `SearchFilterManager` handles coordinate/date filtering for the UI.
- **Persistence (Helper/Utility classes):** `DatabaseController` is the shared data-access entry point, delegating actual reads/writes to `DatabaseManager`. As per TA guidance, these general helper/utility classes are mentioned here in the design notes rather than receiving dedicated CRC cards.
- **Messaging:** `MessagingController` is a proposed new class for authorized owner-to-entrant messaging; `Message` is proposed to inherit `Notification` for the required one-way communication behavior. Both are pending team review.

**Review status:** Ready to add as a separate draft PR and request team review. This section does not overwrite or imply approval of existing OO analysis and design decisions.