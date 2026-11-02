# Turing — Campsite Reservation System

**CMPUT 301 | University of Alberta | Fall 2026**



## Project Structure

```text
turing/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/turing/campsite/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── TuringCampsiteApp.kt
│   │   │   │   ├── navigation/
│   │   │   │   │   ├── Routes.kt
│   │   │   │   │   ├── AppScaffold.kt
│   │   │   │   │   ├── EntrantNavigation.kt
│   │   │   │   └── ui/
│   │   │   │       ├── components/
│   │   │   │       ├── screens/
│   │   │   │       │   ├── entrant/
│   │   │   │       │   │   ├── EntrantHomeScreen.kt
│   │   │   │       │   │   ├── EntrantProfileScreen.kt
│   │   │   │       │   │   └── ...
│   │   │   │       │   ├── owner/
│   │   │   │       │   ├── admin/
│   │   │   │       │   └── shared/
│   │   │   │       └── theme/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   ├── test/
│   │   └── androidTest/
│   └── build.gradle.kts
├── doc/
│   ├── core-classes-crc-draft.md
│   ├── part_2/
│   │   └── crc_cards.md
│   └── team.txt
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

*Simplified structure showing the main files and directories.*

### Application Organization

- **MainActivity.kt:** Android application entry point.
- **TuringCampsiteApp.kt:** Initializes the shared navigation controller and registers navigation graphs.
- **navigation/:** Defines routes, role-specific navigation, and shared application scaffolding.
- **ui/screens/:** Contains individual screens organized by entrant, owner, admin, and shared functionality.
- **ui/components/:** Contains reusable UI elements, including navigation bars and campsite, reservation, lottery, and notification cards.
- **ui/theme/:** Contains application styling and theme definitions.
- **doc/:** Contains project documentation and design materials.
```


Open the project in **Android Studio**, sync Gradle, and run

## Documentation

- [CRC Cards](doc/part_2/crc_cards.md)
- [CRC Draft](doc/core-classes-crc-draft.md)
- [Team Information](doc/team.txt)


## Resources

- [CRC Cards](doc/part_2/crc_cards.md)
- [CRC Draft](doc/core-classes-crc-draft.md)
- [Team Information](doc/team.txt)
---

**Team Turing** — CMPUT 301, University of Alberta
