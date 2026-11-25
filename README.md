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
  <img width="200" height="400" alt="Login Screen" src="https://github.com/user-attachments/assets/381f3bfd-d16c-4a25-8327-13060a978e4f" />
  <img width="200" height="400" alt="Login Screen Dark" src="https://github.com/user-attachments/assets/cd13848e-542b-4f03-be49-0e75be8a9a0a" />
  &nbsp;&nbsp;&nbsp;
  <img width="200" height="400" alt="Dashboard" src="https://github.com/user-attachments/assets/29cca38b-27ff-4170-a8fd-64003c3a61af" />
  <img width="200" height="400" alt="Dashboard Dark" src="https://github.com/user-attachments/assets/d1ca71d7-00ea-47fa-94e6-dd96fc4b8e31" />
  &nbsp;&nbsp;&nbsp;
  <img width="200" height="400" alt="Add Expense" src="https://github.com/user-attachments/assets/7c8b99a4-0198-49bf-b823-f7fb3f255546" />
  <img width="200" height="400" alt="Add Expense Dark" src="https://github.com/user-attachments/assets/064a9226-767a-4b7e-a07f-29a3ce301d04" />
  &nbsp;&nbsp;&nbsp;
  <img width="200" height="400" alt="Transaction History" src="https://github.com/user-attachments/assets/7583590f-1a0e-45ce-a08b-5b188c8c9660" />
  <img width="200" height="400" alt="Transaction History Dark" src="https://github.com/user-attachments/assets/878dcb6d-62c0-4b85-88d9-5774325375c0" />
  &nbsp;&nbsp;&nbsp;
  <img width="200" height="400" alt="Transaction History Expense" src="https://github.com/user-attachments/assets/4c8acf3f-0103-4e00-8769-4faedda0c9e9" />
  <img width="200" height="400" alt="Transaction History Expense" src="https://github.com/user-attachments/assets/cdd6d191-a828-43d9-87bd-59d023d61695" />

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
