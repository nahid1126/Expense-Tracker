############################################
# 🔷 BASIC
############################################

-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod

# Keep coroutine metadata (safe minimal keep)
-keep class kotlin.coroutines.Continuation

############################################
# 🔷 GSON / FIRESTORE
############################################

# Keep only your DTOs / entities (narrow scope = better)
-keep class com.nahid.expensetracker.data.model.** { <fields>; }
-keep class com.nahid.expensetracker.data.local.entity.** { <fields>; }

# Optional (only if using @Expose heavily)
-keepattributes *Annotation*

############################################
# 🔷 KOTLINX SERIALIZATION (if used)
############################################

-keepattributes kotlinx.serialization.Serializable
-keepclassmembers class ** {
    @kotlinx.serialization.SerialName <fields>;
}

-keep class kotlinx.serialization.** { *; }

############################################
# 🔷 KTOR CLIENT (OPTIMIZED)
############################################

# Keep only required reflection-heavy parts (NOT whole library)
-keep class io.ktor.client.engine.** { *; }
-keep class io.ktor.util.** { *; }

-keep class io.ktor.client.plugins.logging.** { *; }

# Avoid crashes in engines / interceptors
-keep class io.ktor.client.* { *; }

-dontwarn io.ktor.**
-dontwarn io.netty.**
-dontwarn org.slf4j.**
-dontwarn com.typesafe.**

############################################
# 🔷 COROUTINES (SAFE MINIMAL)
############################################

-keepclassmembers class kotlinx.coroutines.** {
    *;
}

-dontwarn kotlinx.atomicfu.**

############################################
# 🔷 CONSCRYPT / SECURITY
############################################

-keep class com.android.org.conscrypt.** { *; }
-keep class javax.annotation.** { *; }

############################################
# 🔷 OKHTTP (IMPORTANT for KTOR engine)
############################################

-dontwarn okhttp3.**
-dontwarn okio.**

############################################
# 🔷 SAFE DEFAULT CONSTRUCTORS (LIMITED)
############################################

# ⚠️ REQUIRED for Firestore / Serialization to instantiate objects
-keepclassmembers class com.nahid.expensetracker.data.** {
    public <init>();
}
