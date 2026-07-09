############################################
# 🔷 BASIC
############################################

-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod

# Keep coroutine metadata (safe minimal keep)
-keep class kotlin.coroutines.Continuation

############################################
# 🔷 GSON
############################################

# Keep only your DTOs / entities (narrow scope = better)
-keep class com.suffixit.fieldforce.data.remote.** { <fields>; }
-keep class com.suffixit.fieldforce.data.local.entity.** { <fields>; }

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
# 🔷 SAFE DEFAULT CONSTRUCTORS (LIMITED)
############################################

# ⚠️ ONLY for serialization frameworks (NOT global)
-keepclassmembers class com.suffixit.leafglt.data.** {
    public <init>();
}