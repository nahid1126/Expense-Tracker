############################################
# 🔷 BASIC & DEBUGGING
############################################

-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod, SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

# Keep coroutine metadata
-keep class kotlin.coroutines.Continuation

############################################
# 🔷 DOMAIN & DATA MODELS (SURGICAL)
############################################

# Preserving fields and constructors for Firestore and Serialization
-keep class com.nahid.expensetracker.domain.model.** {
    <fields>;
    <init>(...);
}
-keep class com.nahid.expensetracker.data.model.** {
    <fields>;
    <init>(...);
}
-keep class com.nahid.expensetracker.data.local.entity.** {
    <fields>;
    <init>(...);
}
-keep class com.nahid.expensetracker.domain.uiconfig.** {
    <fields>;
    <init>(...);
}

# Keep classes used for Type-Safe Navigation
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# Keep Destination class names for Navigation
-keepnames class com.nahid.expensetracker.ui.presentation.navigation.Destinations** { *; }

############################################
# 🔷 KOIN (VIEWMODELS)
############################################

# Preserve ViewModel constructors so Koin can instantiate them
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}
-keep class org.koin.** { *; }
-dontwarn org.koin.**

############################################
# 🔷 ROOM
############################################

-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity
-keep interface * extends androidx.room.Dao
-dontwarn androidx.room.**

############################################
# 🔷 FIREBASE & GMS (MINIMAL)
############################################

# Firebase often needs its internal classes kept if reflection is used
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Credential Manager
-keep class androidx.credentials.** { *; }
-keep class com.google.android.libraries.identity.googleid.** { *; }

############################################
# 🔷 WORK MANAGER
############################################

# Keep Worker constructors for system instantiation
-keepclassmembers class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

############################################
# 🔷 COMPOSE & OTHER LIBRARIES
############################################

# We DO NOT keep all of Compose, Ktor, or Lottie.
# R8 should shrink these. Only add specific rules if they crash.
-dontwarn androidx.compose.**
-dontwarn io.ktor.**
-dontwarn com.airbnb.lottie.**
-dontwarn kotlinx.coroutines.**
-dontwarn org.jetbrains.annotations.**
