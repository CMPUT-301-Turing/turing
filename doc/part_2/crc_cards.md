# CMPUT 301 - Object-Oriented Analysis (CRC Cards)
**Team:** Turing\
**Project:** Campsite Reservation\
**Status:** Draft

## 1.Overview
This document captures the object-oriented design for the campsite reservation system


## 2.Class Traceability
This table maps every single assigned User story to its primary domain classes

### | **Class**                          |   **Type**                                   |   **Relevant User Story**                                                             |       **Checkpoint**    |

| ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |

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
 
##### **3. Lottery Management: **

 We added `LotteryManager` as a controller/logic coordinator to handel random winner draws and replacement picks without stuffing that logic into the `Lottery` model. 
 
##### **4.Why `WaitingList` is its own class instead od a field inside `Lottery`:**

 The user stories mention optional waitlist capacity caps `US 02.04.07`, entrants leaving the waitlist voluntarily `US 01.02.02`, and owners checking the list of entrants `US 02.09.01`. Giving `WaitingList` its own class keeps `Lottery` clean and focused on running the draw.



Here is the complete, finalized Markdown code. You can copy this entire block and paste it directly into your `crc_draft.md` file in your repository.


## 4. CRC Cards (Draft — Core Classes)

The classes below were identified in both initial object-oriented analyses. These cards incorporate the subsequent responsibility review. They remain **proposals for team review**, not finalized implementation decisions. Collaborators are included for meaningful *uses* or *has-a* relationships, not solely inheritance or database storage.

