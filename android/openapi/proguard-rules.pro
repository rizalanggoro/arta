-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations

# kotlinx-serialization-json specific. Add this if you have java.lang.NoClassDefFoundError kotlinx.serialization.json.JsonObjectSerializer
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }

# project specific.
-keep,includedescriptorclasses class id.my.rizalanggoro.arta.openapi.models.**$$serializer { *; }
-keepclassmembers class id.my.rizalanggoro.arta.openapi.models.** { *** Companion; }
-keepclasseswithmembers class id.my.rizalanggoro.arta.openapi.models.** { kotlinx.serialization.KSerializer serializer(...); }
