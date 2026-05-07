# ShambaGuard (Android Client)

ShambaGuard is a blockchain-powered agricultural parametric insurance application. It provides automated, oracle-triggered insurance payouts for farmers in Kenya based on satellite-verified drought metrics, completely eliminating the need for manual claim processing. 

This repository contains the **Android client** application, which serves three distinct roles: **Admin**, **Agent**, and **Farmer**, offering tailored dashboards, offline-first capabilities, and comprehensive farm tracking.

## 🚀 Features

The application is structured into role-based feature modules:

### 👨‍🌾 Farmer
* **Dashboard & Early Warnings:** Views 14-day forecast data and receives actionable advice on crop planting and drought mitigation.
* **Parametric Policy Management:** Browse and select insurance tiers, paying premiums directly via **M-Pesa STK Push**.
* **Payout History:** Access transparent, immutable logs of automated payouts verified via IPFS links and Polygon transaction hashes.
* **Carbon Credits:** Track accumulated carbon tonnes from sustainable farming practices and monitor expected compensation.

### 🕵️ Agent
* **Farmer Onboarding:** Register farmers securely and capture National ID details.
* **Farm Polygon Mapping:** Utilize the **Google Maps SDK** to draw and save highly accurate farm boundaries (GeoJSON).
* **Evidence Photos:** Capture farm state photos using **CameraX**, with strict GPS and timestamp EXIF metadata embedding.
* **Offline-First Sync:** Use the app in low-connectivity rural areas. Data (like practice logs and evidence) is queued locally and synced automatically using **WorkManager** when network connectivity is restored.

### 👑 Admin
* **System Overview:** Monitor total farmers, pool balance, active policies, and approve/suspend agents.
* **Drought Monitor & Maps:** View all registered farm polygons overlaid with NDVI heatmaps.
* **Pool Health & Revenue:** Monitor smart contract pool balances vs. liability to ensure coverage ratio remains above 150%.

## 🛠 Tech Stack

This project strictly adheres to modern Android development practices, leveraging a feature-based MVVM and Clean Architecture model.

* **Language:** Kotlin 2.3+
* **UI:** Jetpack Compose & Material 3
* **Architecture:** Feature-based MVVM (Model-View-ViewModel) + Clean Architecture Principles
* **Navigation:** **Navigation 3** (`NavDisplay`) utilizing a custom manual `mutableStateListOf<Any>` backstack and `@Serializable` key objects for deep type-safety.
* **Dependency Injection:** Dagger Hilt
* **Local Database:** Room (for robust offline-first capabilities)
* **Background Processing:** WorkManager
* **Networking:** Retrofit + OkHttp
* **Maps & Location:** Google Maps SDK
* **Hardware:** CameraX for strict metadata-embedded photo capture

## 🔒 Security & Key Management

**Do not hardcode sensitive data or API keys into the codebase.**
ShambaGuard utilizes a secure keystore workflow:
1. All API keys (such as `MAPS_API_KEY`) and backend base URLs must be defined in a `keys.properties` file in the root project directory.
2. The `keys.properties` file is strictly ignored by Git to prevent credential leakage.
3. These properties are injected securely into the application via the `BuildConfig`.

## ⚙️ Getting Started

### Prerequisites
* **Android Studio:** Latest stable version (Koala or newer recommended).
* **JDK:** Java 21 is required for compilation (specifically for KSP and Gradle daemon stability).

### Setup Instructions
1. **Clone the repository:**
   ```bash
   git clone https://github.com/korryrdev/shambaguard.git
   cd ShambaGuard
   ```
2. **Setup API Keys:**
   Create a `keys.properties` file in the root directory and define your variables. At minimum, you will need:
   ```properties
   MAPS_API_KEY=your_google_maps_api_key
   ```
3. **Build & Run:**
   Sync the project with Gradle and run the `app` module on a physical device or emulator running Android 8.0 (API 26) or higher.

## 📖 Architecture Reference

For detailed contribution guidelines, UI/UX requirements, and codebase conventions, please refer to:
* `docs/prd.md`: The complete Product Requirements Document (PRD).
* `CLAUDE.md`: Internal engineering rules, standards, and architecture layout.
