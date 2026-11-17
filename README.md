# Tanglish Video Translator Player

A Kotlin + Jetpack Compose Android app for local and streaming video playback with live Tanglish subtitles generated from speech using the Gemini API.

## Features included in this phase

- AndroidX Media3 ExoPlayer integration
- Local media browsing via MediaStore
- Library / recents / favorites / settings navigation
- MVVM + Hilt + Coroutines/Flow architecture
- Material 3 theme
- Gemini API integration scaffold with retry/backoff and quota handling
- Subtitle overlay and Room cache foundation
- Export formatting for SRT, VTT, and TXT
- DataStore + encrypted settings for Gemini key storage

## Phase roadmap

1. Player and library
2. Audio chunking and Gemini integration
3. Subtitle overlay and cache
4. Export and settings
5. Polish and release

## Requirements

- Android Studio Ladybug or newer
- JDK 17
- Android SDK 35
- Gradle 8.7+ via wrapper or local install

## Local setup

1. Clone the repository.
2. Open the project in Android Studio.
3. In Settings > SDK Manager, install Android SDK 35 and platform build tools.
4. Set your local Android SDK path if Android Studio does not detect it automatically.
5. Sync Gradle.
6. Run the app on a connected device or emulator.

## Gemini API key setup

1. Open the app settings screen.
2. Paste your Gemini API key.
3. Review the disclosure and confirm consent the first time the app uses AI translation.
4. The API key is kept in encrypted preferences for the settings layer.

## Important notes

This repository is structured as a working base for the full product described in the project brief. The advanced translation workflow and playback details are built incrementally to keep the project stable and compilable.

## Build command

```bash
./gradlew assembleDebug
```

If Gradle wrapper is not available in your environment, generate it in Android Studio or run:

```bash
gradle wrapper
```
