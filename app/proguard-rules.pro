# LiftGenius R8/ProGuard rules for release builds.
# Most libraries (kotlinx.serialization, ktor, supabase-kt, Hilt) ship their own
# consumer rules; these cover this app's own reflectively-reached code.

# --- kotlinx.serialization ---
# Models are reached through generated serializers; keep them and their serializers.
-keep class com.kutluhangul.liftgenius.domain.model.** { *; }
-keep class com.kutluhangul.liftgenius.data.remote.** { *; }

-keepclassmembers class ** {
    *** Companion;
}
-keepclasseswithmembers class ** {
    kotlinx.serialization.KSerializer serializer(...);
}
# @SerialName enum values must survive shrinking so DB strings stay intact.
-keepclassmembers enum com.kutluhangul.liftgenius.domain.model.** {
    <fields>;
    **[] values();
    ** valueOf(java.lang.String);
}

# --- Ktor (defensive; engine picks classes reflectively) ---
-keep class io.ktor.client.engine.android.** { *; }
-dontwarn org.slf4j.**

# --- Coroutines ---
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
-dontwarn kotlinx.coroutines.**
