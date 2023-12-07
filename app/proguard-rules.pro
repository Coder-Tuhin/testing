# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\AndroidStudioSDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# For Butterknife:
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
#for UserExperior SDK
-dontwarn com.userexperior.**
-keep class com.userexperior.** { *; }
# Version 7
-keep class **$$ViewBinder { *; }
# Version 8
-keep class **_ViewBinding { *; }

-keepclasseswithmembernames class * { @butterknife.* <fields>; }
-keepclasseswithmembernames class * { @butterknife.* <methods>; }

-ignorewarnings

-keep class * {
    public private *;
}

-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }

#### -- Picasso --
 #-dontwarn com.squareup.picasso.**

 #### -- OkHttp --
 #-dontwarn com.squareup.okhttp.internal.**

 #### -- Apache Commons --
 #-dontwarn org.apache.commons.logging.**