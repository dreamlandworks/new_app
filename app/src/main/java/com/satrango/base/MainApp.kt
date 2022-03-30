package com.satrango.base

import android.app.Application


class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
//        ChatSDKFirebase.quickStart(this, "", resources.getString(R.string.google_maps_key), true)
//        try {
//            ChatSDK.builder()
//                .setGoogleMaps(resources.getString(R.string.google_maps_key))
//                .setPublicChatRoomLifetimeMinutes(TimeUnit.HOURS.toMinutes(24))
//                .build() // Add the Firebase network adapter module
//                .addModule(
//                    FirebaseModule.builder()
//                        .setFirebaseRootPath("pre_1")
//                        .setDevelopmentModeEnabled(true)
//                        .build()
//                ) // Add the UI module
//                .addModule(
//                    UIModule.builder()
//                        .setPublicRoomCreationEnabled(true)
//                        .setPublicRoomsEnabled(true)
//                        .build()
//                ) // Add modules to handle file uploads, push notifications
//                .addModule(FirebaseUploadModule.shared())
//                .addModule(FirebasePushModule.shared()) // Enable Firebase UI with phone and email auth
//                .addModule(
//                    FirebaseUIModule.builder()
//                        .setProviders(EmailAuthProvider.PROVIDER_ID, PhoneAuthProvider.PROVIDER_ID)
//                        .build()
//                ) // Activate
//                .build()
//                .activate(this)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }
}