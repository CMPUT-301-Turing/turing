# Module Turing Campsite

Android app for the CMPUT 301 team project (Team Turing). Built with Jetpack Compose
and Material 3.

# Package com.turing.campsite

Application entry point and top-level screens. `MainActivity` hosts the Compose UI,
and `CampsiteApp` owns the navigation graph between screens.

# Package com.turing.campsite.ui.theme

Material 3 theme for the app: colour schemes, typography, and the `TuringTheme`
wrapper every screen is rendered inside.

# Package com.turing.campsite.data.auth

Device-based identity. `DeviceAuthManager` signs the device in anonymously with
Firebase Auth and exposes the resulting user id. `TempUser` holds a fixed test
uid until sign-in on launch is connected.

# Package com.turing.campsite.data.model

Plain data classes mirroring the Firestore collections described in
`doc/part_3/database_design.md`. No logic beyond small helpers.

# Package com.turing.campsite.data.repository

One repository per Firestore collection. All reads and writes to Firestore go
through these classes; screens and view models never touch Firestore directly.

# Package com.turing.campsite.ui.viewmodel

One view model per repository. Screens read their lists from these and call
their functions, so no composable calls a repository or Firestore directly.
