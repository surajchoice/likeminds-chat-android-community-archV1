# Keeping all models

-keep class com.likeminds.chatmm.branding.model.** { *; }
-keep class com.likeminds.chatmm.chat.model.** { *; }
-keep class com.likeminds.chatmm.chatroom.detail.model.** { *; }
-keep class com.likeminds.chatmm.chatroom.explore.model.** { *; }
-keep class com.likeminds.chatmm.explore.model.** { *; }
-keep class com.likeminds.chatmm.conversation.model.** { *; }
-keep class com.likeminds.chatmm.dm.model.** { *; }
-keep class com.likeminds.chatmm.homefeed.model.** { *; }
-keep class com.likeminds.chatmm.media.model.** { *; }
-keep class com.likeminds.chatmm.member.model.** { *; }
-keep class com.likeminds.chatmm.overflowmenu.model.** { *; }
-keep class com.likeminds.chatmm.polls.model.** { *; }
-keep class com.likeminds.chatmm.polls.model.** { *; }
-keep class com.likeminds.chatmm.pushnotification.** { *; }
-keep class com.likeminds.chatmm.reactions.model.** { *; }
-keep class com.likeminds.chatmm.utils.file.model.** { *; }
-keep class com.likeminds.feedsx.utils.mediauploader.model.** { *; }
-keep class com.likeminds.feedsx.utils.membertagging.model.** { *; }
-keep class com.likeminds.feedsx.utils.model.** { *; }
-keep class androidx.databinding.DataBindingComponent {*;}

# for parcelable classes
-keepnames class * implements android.os.Parcelable

# Class names are needed in reflection
-keepnames class com.amazonaws.**
-keepnames class com.amazon.**

# Enums are not obfuscated correctly in combination with Gson
-keepclassmembers enum * { *; }

# Request handlers defined in request.handlers
-keep class com.amazonaws.services.**.*Handler

# The following are referenced but aren't required to run
-dontwarn com.fasterxml.jackson.**

# Android 6.0 release removes support for the Apache HTTP client
-dontwarn org.apache.http.**

# The SDK has several references of Apache HTTP client
-dontwarn com.amazonaws.http.**
-dontwarn com.amazonaws.metrics.**

# AGP 8 enables R8 full-mode optimization, which will remove constructors of classes that are only
# instantiated via reflection. These classes are instantiated via reflection in the SignerFactory.
-keep class com.amazonaws.auth.AWS4Signer { *; }
-keep class com.amazonaws.auth.QueryStringSigner { *; }
-keep class com.amazonaws.auth.NoOpSigner { *; }
-dontwarn com.amazonaws.mobileconnectors.cognitoauth.Auth$Builder
-dontwarn com.amazonaws.mobileconnectors.cognitoauth.Auth

# Kotlin
-keep class kotlin.coroutines.Continuation

# Gson
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken