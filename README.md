<div align="center">

# Habit Tracker App

Build healthier routines, check in with your mood, and keep your wellness progress close at hand.

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)


</div>

## Overview

Habit Tracker App is a personal wellness app for tracking daily habits, recording mood check-ins, and monitoring hydration. It was built as a Mobile App Development (MAD) project to bring these small, repeatable wellness activities together in one Android app.

The app stores its data locally on the device. Its main areas—Habits, Mood, Trends, and Settings—are available from the bottom navigation.

## Features

- **Habit tracking:** Add, edit, and remove habits; set a goal and unit; record progress and see today's completion.
- **Mood journal:** Save mood ratings from 1 to 5 with an optional note. Review, edit, or delete entries.
- **Mood trends:** See mood-over-time and mood-distribution charts, along with summary statistics.
- **Hydration tracking:** Set a daily water goal and update the current glass count.
- **Hydration reminders:** Enable periodic reminders and choose an interval in Settings.
- **Home-screen widget:** View hydration progress and add a glass of water from the Android home screen.
- **Onboarding:** A short introduction to the app followed by a name entry screen.
- **Preferences:** Configure available reminder and wellness preferences in Settings.

## Tech Stack

| Area | Technology |
| --- | --- |
| Platform | Native Android |
| Language | Kotlin |
| UI | Android XML layouts, AndroidX, AppCompat, and Material Components |
| Lists and navigation | RecyclerView and Android bottom navigation |
| Charts | MPAndroidChart |
| Local data | SharedPreferences with Gson serialization |
| Background features | Android notifications, alarms, broadcast receivers, and an app widget |
| Build | Gradle wrapper, Android Gradle Plugin, and Kotlin Gradle Plugin |

### Architecture

The app uses Android `Activity` and `Fragment` screens with XML layouts. Adapters bind habit, mood, and onboarding data to list and pager views. Model classes represent habits and mood entries, while `PreferencesHelper` reads and writes app data locally using `SharedPreferences` and Gson. Separate utility, reminder, receiver, and widget classes support hydration notifications and the home-screen widget.

This describes the structure currently in the project; no additional architecture framework is assumed.

## Project Structure

```text
.
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/wellness/habittracker/
│       │   ├── activities/     # Onboarding, login, main, and habit-management screens
│       │   ├── adapters/       # RecyclerView and onboarding adapters
│       │   ├── boot/           # Reschedule reminders after device boot
│       │   ├── fragments/      # Habits, Mood, Trends, and Settings screens
│       │   ├── models/         # Habit, mood, and onboarding data models
│       │   ├── reminders/      # Hydration reminder and action receivers
│       │   ├── utils/          # Preferences, notification, and scheduling helpers
│       │   ├── views/          # Custom mood chart view
│       │   └── widget/         # Today hydration widget
│       └── res/
│           ├── drawable/       # UI backgrounds, buttons, and icons
│           ├── font/           # Inter font files
│           ├── layout/         # Activity, fragment, dialog, item, and widget layouts
│           ├── menu/            # Bottom navigation menu
│           ├── mipmap-*/        # Launcher icons and onboarding illustrations
│           ├── values/          # Colors, styles, and strings
│           └── xml/             # App widget configuration
├── gradle/wrapper/              # Gradle wrapper configuration
├── build.gradle                 # Root build configuration
├── gradle.properties
├── gradlew
├── gradlew.bat
└── settings.gradle
```

## Prerequisites

- Android Studio with Android SDK Platform 36 installed.
- JDK 17 (Android Studio's bundled JDK can be selected in Gradle settings).
- An Android device or emulator running Android 8.0 (API 26) or later.
- Git, if cloning the repository from the command line.

The app's current Gradle configuration uses Kotlin 1.9.20, Android Gradle Plugin 8.1.4, and Gradle wrapper 8.10. The app is configured with `minSdkVersion` 26 and `targetSdkVersion` 36.

## Installation, Build, and Run

### Open in Android Studio

1. Clone or download this repository.
2. In Android Studio, choose **Open** and select the project folder.
3. Allow Gradle to sync and install any requested Android SDK components.
4. Select an Android 8.0 (API 26) or newer emulator/device.
5. Choose the `app` run configuration and click **Run**.

### Build from the command line

Clone the repository and enter its folder:

```bash
git clone https://github.com/pramodstd/habit-tracker-app.git
cd habit-tracker-app
```

On Windows, build and install with the Gradle wrapper:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:installDebug
```

On macOS or Linux:

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

The debug APK is generated under `app/build/outputs/apk/debug/`. To launch it, start the app from the device or emulator.

## Using the App

1. **Get started:** Move through or skip the onboarding pages, then enter a name to continue.
2. **Habits:** Use the add control to create a habit with a name, goal, and optional category, icon, or unit. Update progress from the habit list, or edit and delete habits as needed.
3. **Mood:** Add a mood rating and optional note. The Mood screen also shows saved entries and charts; entries can be edited or deleted.
4. **Trends:** Open Trends to review mood history, average mood, tracked days, and mood distribution.
5. **Settings:** Adjust the hydration goal and logged water count, configure hydration reminder intervals, and save available preferences.
6. **Widget:** Add the app's Today widget from the Android widget picker to see hydration progress or log another glass from the home screen.

## Roadmap

Possible future enhancements (not currently included as implemented features):

- Add automated unit and UI tests.
- Explore secure backup or sync options for user data.
- Expand progress summaries and habit trend visualizations.
- Improve accessibility and add additional appearance options.

## Author

**pramodstd** — [GitHub profile](https://github.com/pramodstd)

## 📄 Project Documentation
You can view or download the complete project report here:
- [Project Documentation (PDF)](docs/App_Document.pdf)

## License

No license file or license declaration is currently included in this repository. Add a `LICENSE` file and update this section and the badge above once a license has been chosen. Until then, do not assume that the project is available for reuse under a particular open-source license.
