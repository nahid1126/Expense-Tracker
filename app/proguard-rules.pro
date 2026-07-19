############################################
# 🔷 BASIC
############################################

-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod, SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

# Keep coroutine metadata
-keep class kotlin.coroutines.Continuation

############################################
# 🔷 DOMAIN & DATA MODELS (CRITICAL)
############################################

# Keep all domain and data models as they are used in Serialization and Firebase
-keep class com.nahid.expensetracker.domain.model.** { *; }
-keep class com.nahid.expensetracker.data.model.** { *; }
-keep class com.nahid.expensetracker.data.local.entity.** { *; }
-keep class com.nahid.expensetracker.domain.uiconfig.** { *; }

# Keep all classes annotated with @Serializable
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

############################################
# 🔷 FIREBASE & GMS
############################################

-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Credential Manager
-keep class androidx.credentials.** { *; }
-keep class com.google.android.libraries.identity.googleid.** { *; }

############################################
# 🔷 ROOM
############################################

-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity
-keep interface * extends androidx.room.Dao

############################################
# 🔷 KOIN (CRITICAL FOR VIEWMODELS)
############################################

-keep class org.koin.** { *; }
-dontwarn org.koin.**

# Keep ViewModels and their constructors for Koin injection
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

############################################
# 🔷 WORK MANAGER
############################################

-keep class androidx.work.** { *; }
-keep class * extends androidx.work.ListenableWorker {
    <init>(...);
}

############################################
# 🔷 COMPOSE
############################################

-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

############################################
# 🔷 LOTTIE
############################################

-keep class com.airbnb.lottie.** { *; }

############################################
# 🔷 MISC
############################################

-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**
