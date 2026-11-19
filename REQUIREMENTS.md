# Habit Tracker App — Requirements

This document describes the development environment, project dependencies, and expected app behavior for the Habit Tracker App. Toolchain and feature details below are based on the current project configuration and implementation. Items not specified by the project are marked for confirmation.

## 1. System & Development Environment

### Development machine

| Requirement | Minimum / configuration |
| --- | --- |
| Operating system | [Specify supported development OS and version]. Android Studio and the Gradle wrapper should be used on a supported host OS. |
| IDE | Android Studio [Specify version]. |
| Java runtime | JDK 17; Java source/target compatibility and Kotlin JVM target are configured as 17. |
| Android SDK | Android SDK Platform 36 (compile and target SDK). Install the SDK build tools required by the local Android Gradle Plugin / Android Studio setup. |
| Android Gradle Plugin | 8.1.4, as declared in the root Gradle build. |
| Kotlin Gradle Plugin | 1.9.20, as declared in the root Gradle build. |
| Gradle | Use the project wrapper, configured for Gradle 8.10. |
| Network access | Required for the initial Gradle sync/build to download dependencies if they are not already cached. |

### Test device

- **Emulator:** Android Virtual Device running Android 8.0 (API 26) or later.
- **Physical device:** Android device running Android 8.0 (API 26) or later, with USB debugging enabled for deployment from Android Studio.
- **App SDK configuration:** `minSdkVersion` 26, `compileSdkVersion` 36, and `targetSdkVersion` 36.
- **Hardware profile:** [Specify emulator/device memory, screen size, and other hardware requirements if needed]. No particular hardware profile is defined in the project configuration.

## 2. Project Dependencies & Libraries

The following are declared in `app/build.gradle`:

### Build plugins

| Plugin | Version / source |
| --- | --- |
| Android application plugin (`com.android.application`) | Android Gradle Plugin 8.1.4 (root build configuration) |
| Kotlin Android plugin (`org.jetbrains.kotlin.android`) | Kotlin Gradle Plugin 1.9.20 (root build configuration) |

### App libraries

| Category | Dependency | Version |
| --- | --- | --- |
| AndroidX core extensions | `androidx.core:core-ktx` | 1.13.1 |
| AndroidX app compatibility | `androidx.appcompat:appcompat` | 1.7.0 |
| Material UI components | `com.google.android.material:material` | 1.12.0 |
| Lists | `androidx.recyclerview:recyclerview` | 1.3.2 |
| Cards | `androidx.cardview:cardview` | 1.0.0 |
| Layouts | `androidx.constraintlayout:constraintlayout` | 2.1.4 |
| Charts | `com.github.PhilJay:MPAndroidChart` | 3.1.0 |
| JSON serialization | `com.google.code.gson:gson` | 2.11.0 |

Android platform components used by the app include activities, fragments, notifications, alarms, broadcast receivers, and app widgets. These are provided through the Android SDK and are not separate app-library dependencies in the current dependency list.

## 3. Functional Requirements

### Onboarding and navigation

- **FR-01:** The app shall present introductory onboarding screens and allow the user to continue or skip onboarding.
- **FR-02:** The app shall request a user name before continuing to the main app.
- **FR-03:** The main interface shall provide navigation between Habits, Mood, Trends, and Settings.

### Habit management

- **FR-04:** The user shall be able to create a habit with a name, goal, and unit, and may provide a category and icon.
- **FR-05:** The user shall be able to edit or delete a saved habit.
- **FR-06:** The user shall be able to increase or decrease habit progress.
- **FR-07:** The app shall display habit completion and today's overall habit progress.

### Mood journal and trends

- **FR-08:** The user shall be able to create a mood entry with a rating from 1 to 5 and an optional note.
- **FR-09:** The user shall be able to edit and delete saved mood entries.
- **FR-10:** The app shall display mood entries and provide mood-over-time and mood-distribution charts.
- **FR-11:** The Trends screen shall display mood summary information, including average mood and tracked days.

### Hydration, reminders, and widget

- **FR-12:** The user shall be able to set a daily hydration goal and adjust the current glass count.
- **FR-13:** The user shall be able to enable or disable hydration reminders and select an available reminder interval.
- **FR-14:** When enabled, hydration reminders shall be scheduled using Android alarm and notification mechanisms, subject to applicable Android permissions and settings.
- **FR-15:** The Today home-screen widget shall display hydration progress and provide an action to add a glass of water.

### Settings and persistence

- **FR-16:** The Settings screen shall allow the user to save the available reminder and wellness preferences.
- **FR-17:** The app shall store habits, mood entries, the user name, hydration values, and supported preferences locally using the app's current persistence mechanism.
- **FR-18:** Saved local data shall remain available when the app is closed and reopened, unless the app's data is cleared or the app is uninstalled.

## 4. Non-Functional Requirements

### Usability and accessibility

- **NFR-01:** The primary screens and navigation shall use consistent labels and controls that make core workflows easy to locate.
- **NFR-02:** Text, buttons, and progress indicators shall remain readable and usable on supported device screen sizes.
- **NFR-03:** Interactive controls shall provide clear state or validation feedback where applicable.
- **NFR-04:** Accessibility support, including screen-reader descriptions and contrast expectations, should be verified on supported devices. Detailed accessibility targets: [Specify if required].

### Performance and responsiveness

- **NFR-05:** Screen navigation and common interactions shall complete without unnecessary blocking of the UI thread.
- **NFR-06:** Lists and charts shall remain usable with typical personal habit and mood-entry histories.
- **NFR-07:** Response-time and data-volume targets: [Specify measurable thresholds if required].

### Reliability

- **NFR-08:** The app shall handle empty habit and mood lists with an appropriate empty state rather than requiring pre-existing data.
- **NFR-09:** Reminder scheduling and notification behavior shall account for platform permissions and device settings that can restrict delivery.
- **NFR-10:** The app should recover gracefully from process restarts and device reboots without losing locally saved preferences or data; reminder behavior after reboot should be verified on supported Android versions.

### Data safety and privacy

- **NFR-11:** App data shall be stored locally using the current app-private SharedPreferences-based implementation.
- **NFR-12:** The app shall not imply cloud backup, account synchronization, or encryption at rest; these capabilities are not established by the current project implementation.
- **NFR-13:** Data migration and corruption handling should be tested for stored habit and mood data.

## 5. Setup & Verification Checklist

### Environment

- [ ] Android Studio [Specify version] is installed.
- [ ] JDK 17 is selected for Gradle.
- [ ] Android SDK Platform 36 is installed.
- [ ] An Android emulator or physical device running API 26 or later is available.
- [ ] The project can download dependencies from the configured Gradle repositories, or dependencies are already cached.

### Project setup

- [ ] Open the repository root in Android Studio and allow Gradle sync to finish.
- [ ] Confirm the project wrapper uses Gradle 8.10.
- [ ] Confirm Gradle resolves the Android Gradle Plugin 8.1.4 and Kotlin Gradle Plugin 1.9.20.
- [ ] Build the debug app using the wrapper:

  ```text
  Windows:       .\gradlew.bat :app:assembleDebug
  macOS/Linux:   ./gradlew :app:assembleDebug
  ```

- [ ] Install and launch the app on the selected emulator/device.

### Functional smoke checks

- [ ] Complete or skip onboarding and enter a name.
- [ ] Create a habit, update its progress, edit it, and delete it.
- [ ] Add a mood entry with a note; confirm it appears in the Mood screen and charts.
- [ ] Edit and delete a mood entry.
- [ ] Review the Trends screen with both empty and populated mood data.
- [ ] Change the hydration goal and glass count.
- [ ] Enable and disable hydration reminders; verify permission and notification behavior on the test Android version.
- [ ] Add the Today widget and verify its displayed hydration progress and add-glass action.
- [ ] Close and reopen the app and confirm saved local data and preferences are retained.

## Configuration Items to Confirm

- Development operating system and supported version: **[Specify]**
- Android Studio version: **[Specify]**
- Emulator/device hardware profile, if constrained: **[Specify]**
- Required accessibility targets and measurable performance limits: **[Specify if required]**
