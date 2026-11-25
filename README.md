# 💰 Expense Tracker | Android

A **modern, offline-first personal finance manager** built to showcase professional Android development skills using the latest tools and best practices.

---

## ✨ Features

- 📊 **Expense Tracking:** Effortlessly log and categorize income and expenses to monitor your financial health.
- 📶 **Offline Transactions:** After signing in, add, edit, and delete transactions without an internet connection. Room keeps changes pending until they sync.
- 🔄 **Cloud Synchronization:** Seamless background data sync with Firebase Firestore using **WorkManager**.
- 🔒 **Secure Authentication:** Integrated **Google Sign-In** using the modern Credential Manager API.
- 🌓 **Dynamic Theming:** Beautiful UI with support for Dark and Light modes.
- 🧭 **Type-Safe Navigation:** Robust screen transitions using Jetpack Compose Navigation with Kotlin Serialization.
- 🎨 **Modern UI:** Built entirely with **Jetpack Compose** and **Material 3** for a sleek, responsive experience.

---

## Release builds

Set `EXPENSE_RELEASE_STORE_FILE`, `EXPENSE_RELEASE_STORE_PASSWORD`, `EXPENSE_RELEASE_KEY_ALIAS`, and `EXPENSE_RELEASE_KEY_PASSWORD` in your local environment or CI secret store. Use an absolute keystore path outside the checkout. A release build without these variables produces an unsigned artifact.

## Existing data

The Room migration preserves version 1 rows. Since those rows did not record account ownership, they are excluded from account views and background uploads. Previously synced transactions come back from the signed-in account's cloud history. Unsynced version 1 rows remain in the local database for deliberate recovery.

## 🛠 Tech Stack

| Category             | Tools / Libraries                                   |
|----------------------|-----------------------------------------------------|
| **Language**         | Kotlin                                              |
| **UI Framework**     | Jetpack Compose (Material 3)                        |
| **Architecture**     | MVVM + Repository Pattern (Offline-First)           |
| **Dependency Injection**| Koin                                             |
| **Local Database**   | Room                                                |
| **Cloud Storage**    | Firebase Firestore                                  |
| **Auth**             | Firebase Auth + Google Credential Manager           |
| **Background Tasks** | WorkManager                                         |
| **State Management** | StateFlow, Flow, ViewModel                          |
| **Navigation**       | Jetpack Compose Navigation (Type-Safe)              |
| **Other**            | Lottie Animations, Jetpack DataStore, Coroutines    |

---

## 📸 Screenshots

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

## 🔥 Learning Outcomes

- Mastered **Offline-First Architecture** ensuring data consistency between Room and Firestore.
- Implemented **Background Synchronization** logic with WorkManager to handle network intermittent issues.
- Integrated **Google Authentication** using the latest Credential Manager API for a modern login flow.
- Deepened expertise in **Jetpack Compose** custom layouts and complex state management.
- Applied **Clean Architecture** principles to maintain a scalable and testable codebase.

## 📸 Author  

**Nahid Hasan**  
💼 Android Developer  
📍 Bangladesh  
🔗 [**GitHub Profile**](https://github.com/nahid1126)

---

<p align="center">
  ⭐ <em>If you find this project useful, please star the repository!</em>
</p>
