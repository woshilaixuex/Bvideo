# Repository Guidelines

## Project Structure & Module Organization
`BRedio` is a Gradle multi-module Android project. The root includes `app`, `common`, `data`, `domain`, and `player`, wired in [settings.gradle.kts](D:\Computer_development\KoAndriod\BRedio\settings.gradle.kts). Production code lives under `*/src/main`, local JVM tests under `*/src/test`, and instrumented tests under `*/src/androidTest`. Current Kotlin sources are mainly in `app/src/main/java/com/elyric/bredio` and `common/src/main/java/com/elyric/common`; other modules are scaffolded and ready for implementation. XML layouts, navigation, and drawable assets live in each module’s `src/main/res`.

## Build, Test, and Development Commands
Use the Gradle wrapper from the repository root:

- `.\gradlew assembleDebug` builds a debug APK for the app and validates module wiring.
- `.\gradlew build` runs compilation plus unit tests across modules.
- `.\gradlew test` runs JVM unit tests in `*/src/test`.
- `.\gradlew connectedAndroidTest` runs instrumented tests on a device or emulator.
- `.\gradlew lint` checks Android lint issues before review.

## Coding Style & Naming Conventions
Write Kotlin with 4-space indentation and keep packages under `com.elyric.*`. Use `PascalCase` for classes and fragments (`HomeFragment`), `camelCase` for methods and properties, and `snake_case` for Android resources (`fragment_home.xml`, `ic_short_video.xml`). Keep feature UI code grouped by page under `app/src/main/java/com/elyric/bredio/view/...`. No formatter or static-analysis config is checked in yet, so keep imports tidy and follow existing Android Studio Kotlin defaults. Target Java 11 in Gradle files.

## Testing Guidelines
JUnit 4 is configured for unit tests, with AndroidX JUnit and Espresso for device tests through the version catalog in `gradle/libs.versions.toml`. Name tests after the class or behavior under test, and keep them beside the owning module. Replace the template `ExampleUnitTest.kt` and `ExampleInstrumentedTest.kt` files with real coverage as features land.

## Commit & Pull Request Guidelines
Current history uses a Conventional Commit style (`feat:初始化项目`). Continue with prefixes like `feat:`, `fix:`, `refactor:`, and keep subjects short. PRs should include a concise summary, affected modules, test evidence (`.\gradlew test`, screenshots for UI changes), and linked issues when applicable.

## Configuration Tips
Do not commit `local.properties`, SDK paths, `.idea`, or `build/` output. Keep machine-specific configuration local and let Gradle-managed files define shared project behavior.
