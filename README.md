# Expense Tracker for Android

Track income and expenses, see where your money goes, and keep recording transactions when you’re offline. The app is built with Kotlin, Jetpack Compose, and Material 3.

## Features

- **Home dashboard** with a time-based greeting, current balance, income and expense totals, and recent transactions. Spending by category shows color-coded bars, amounts, and percentages.
- **Transaction reports** with month and type filters. Switch between **Categories** for a ring chart and **Timeline** for daily or monthly income and expense bars. Tap a bar to see its totals.
- **Add and edit transactions** with a searchable category picker, category icons, and date selection. The bottom bar provides **Home**, **Add**, and **Transactions**.
- **Light and dark themes** with a sun/moon switch in the Home toolbar. The app follows the phone’s theme until you choose one, then remembers your selection.
- **Google sign-in** through Android Credential Manager and Firebase Authentication.

## Offline transactions

Sign in once while online. After that, you can add, edit, and delete transactions offline while signed in. Changes are saved in Room first and remain associated with your account. Pending transactions show **Pending sync** in the app. WorkManager attempts to sync with Firestore when connectivity returns. Opening Home also queues any remaining pending changes for your account.

## Run locally

1. Open the project in Android Studio and sync Gradle. The project compiles against Android SDK 36 and runs on Android 9 (API 28) or later.
2. Run the `app` configuration on an emulator or Android device.
3. For your own Firebase project, use its Android configuration in `app/google-services.json` and configure Google sign-in and Firestore for that project.

Build the debug APK and run the JVM tests and lint from the project root.

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

Room migration and account isolation instrumentation tests run on a connected emulator or device.

```sh
./gradlew :app:connectedDebugAndroidTest
```

## Release signing

Set `EXPENSE_RELEASE_STORE_FILE`, `EXPENSE_RELEASE_STORE_PASSWORD`, `EXPENSE_RELEASE_KEY_ALIAS`, and `EXPENSE_RELEASE_KEY_PASSWORD` in your local environment or CI secret store. Use an absolute keystore path outside the checkout. A release build without these variables produces an unsigned artifact.

## Existing data

The Room migration preserves version 1 rows. Since those rows did not record account ownership, they are excluded from account views and background uploads. Previously synced transactions come back from the signed-in account's cloud history. Unsynced version 1 rows remain in the local database for deliberate recovery.

## Tech stack

| Area | Tools |
| --- | --- |
| UI and navigation | Jetpack Compose, Material 3, type-safe Navigation Compose |
| Architecture and state | MVVM, repositories, Kotlin Coroutines, Flow, StateFlow |
| Local storage | Room, Preferences DataStore |
| Authentication and cloud | Credential Manager, Firebase Authentication, Firestore |
| Dependency injection and background work | Koin, WorkManager |

## 📸 Screenshots from an earlier version

The current layout is available in Android Studio through the Dashboard and Transactions Compose previews.

<p align="center">
  <img width="200" height="400" alt="Login Screen" src="https://github.com/user-attachments/assets/1dd45cf1-b134-46ba-86be-08dc8aef9493" />
  &nbsp;&nbsp;&nbsp;
  <img width="200" height="400" alt="Home Dashboard" src="https://github.com/user-attachments/assets/f609c4cd-c308-48cc-a218-4398d762944c" />
  &nbsp;&nbsp;&nbsp;
  <img width="200" height="400" alt="Add Expense" src="https://github.com/user-attachments/assets/b6e777fc-bfd6-448a-8930-acb362f80164" />
  &nbsp;&nbsp;&nbsp;
  <img width="200" height="400" alt="Transaction History Income" src="https://github.com/user-attachments/assets/dce5bcd3-ac76-445c-ba4a-87eeb84e5a44" />
  &nbsp;&nbsp;&nbsp;
  <img width="200" height="400" alt="Transaction History Expense" src="https://github.com/user-attachments/assets/bd39bcad-9b0d-4d84-8853-ccbd24b220bc" />
</p>

## Author

**Nahid Hasan**  
💼 Android Developer  
📍 Bangladesh  
🔗 [**GitHub Profile**](https://github.com/nahid1126)

---

<p align="center">
  ⭐ <em>If you find this project useful, please star the repository!</em>
</p>
