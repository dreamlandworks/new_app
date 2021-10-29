package com.satrango.remote.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.media.RingtoneManager.getDefaultUri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.datatransport.runtime.scheduling.jobscheduling.SchedulerConfig
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.satrango.R
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.FCMReqModel
import com.satrango.ui.service_provider.provider_dashboard.ProviderDashboard
import com.satrango.ui.user.bookings.provider_response.ProviderBookingResponseScreen
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.Provider
import android.os.AsyncTask
import androidx.annotation.RequiresApi
import okhttp3.internal.waitMillis
import java.time.Instant
import java.util.concurrent.Executors


class FCMService : FirebaseMessagingService() {

    companion object {
        var fcmInstant: Instant? = null
    }
    private lateinit var notificationManager: NotificationManager
    private lateinit var builder: android.app.Notification.Builder

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            if (it.title == "accepted") {
                if (notificationManager != null) {
                    notificationManager.cancelAll()
                }
            } else if (it.title == "user") {
                if (!ProviderDashboard.FROM_FCM_SERVICE) {
                    addNotification(it.body)
                }
            } else {
                if (!UserUtils.getInstantBooking(this)) {
                    val intent = Intent(this, ProviderBookingResponseScreen::class.java)
                    intent.putExtra("response", it.title)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    if (it.body!!.split("|")[0] == "accept") {
                        val intent = Intent(this, ProviderBookingResponseScreen::class.java)
                        intent.putExtra("response", it.title)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (PermissionUtils.isNetworkConnected(this)) {
            CoroutineScope(Dispatchers.Main).launch {
                RetrofitBuilder.getUserRetrofitInstance().updateFCMToken(FCMReqModel(token, RetrofitBuilder.USER_KEY, UserUtils.getUserId(this@FCMService)))
            }
        }
        Log.e("FCM TOKEN UPDATE", token)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    private fun addNotification(body: String?) {

        val requestID = System.currentTimeMillis().toInt()

        val alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = false
        ProviderDashboard.FROM_FCM_SERVICE = true
        val notificationIntent = Intent(applicationContext, ProviderDashboard::class.java)
        notificationIntent.putExtra(application.getString(R.string.booking_id), body?.split("|")!![0])
        notificationIntent.putExtra(application.getString(R.string.category_id), body.split("|")[1])
        notificationIntent.putExtra(application.getString(R.string.user_id), body.split("|")[2])
//        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("channelId", "description", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)

            notificationManager.createNotificationChannel(notificationChannel)

            builder = android.app.Notification.Builder(this, "channelId")
                .setSmallIcon(R.drawable.circlelogo)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("You got a booking request!")
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.circlelogo))
        } else {
            builder = android.app.Notification.Builder(this)
                .setSmallIcon(R.drawable.circlelogo)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("You got a booking request!")
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.circlelogo))
        }
        notificationManager.notify(1234, builder.build())

        ProviderDashboard.minutes = 2
        ProviderDashboard.seconds = 59
        ProviderDashboard.progressTime = 180
//        ProviderDashboard.IN_PROVIDER_DASHBOARD = false
        fcmInstant = Instant.now()
        Handler().postDelayed({
            notificationManager.cancelAll()
        }, 180000)

//        Thread {
//            if (!ProviderDashboard.IN_PROVIDER_DASHBOARD) {
//                ProviderDashboard.progressTime -= 1
//                ProviderDashboard.seconds -= 1
//                if (ProviderDashboard.minutes == 0 && ProviderDashboard.seconds == 0) {
//                    notificationManager.cancelAll()
//                }
//                if (ProviderDashboard.seconds == 0) {
//                    ProviderDashboard.seconds = 59
//                    ProviderDashboard.minutes -= 1
//                }
//                Thread.sleep( 1000)
//            }
//    }.start()

//        val threadPool = Executors.newSingleThreadExecutor()
//        threadPool.submit(Runnable {
//            if (!ProviderDashboard.IN_PROVIDER_DASHBOARD) {
//                ProviderDashboard.progressTime -= 1
//                ProviderDashboard.seconds -= 1
//                if (ProviderDashboard.minutes == 0 && ProviderDashboard.seconds == 0) {
//                    notificationManager.cancelAll()
//                }
//                if (ProviderDashboard.seconds == 0) {
//                    ProviderDashboard.seconds = 59
//                    ProviderDashboard.minutes -= 1
//                }
//            }
//            Log.e("THREAD: ", "ONGOING....")
//            threadPool.waitMillis(1000)
//        })

//        val t: Thread = object : Thread() {
//            override fun run() {
//                try {
//                    if (!ProviderDashboard.IN_PROVIDER_DASHBOARD) {
//                        ProviderDashboard.progressTime -= 1
//                        ProviderDashboard.seconds -= 1
//                        if (ProviderDashboard.minutes == 0 && ProviderDashboard.seconds == 0) {
//                            notificationManager.cancelAll()
//                        }
//                        if (ProviderDashboard.seconds == 0) {
//                            ProviderDashboard.seconds = 59
//                            ProviderDashboard.minutes -= 1
//                        }
//                    }
//                    Log.e("THREAD: ", "ONGOING....")
//                    sleep(1000)
//                } finally {
//                    Log.e("FINAL THREAD: ", "ONGOING....")
//                }
//            }
//        }
//        t.start()

//        val mainHandler = Handler(Looper.getMainLooper())
//        mainHandler.post(object : Runnable {
//            override fun run() {
//                mainHandler.postDelayed(this, 1000)
//
//                if (!ProviderDashboard.IN_PROVIDER_DASHBOARD) {
//                    ProviderDashboard.progressTime -= 1
//                    ProviderDashboard.seconds -= 1
//                    if (ProviderDashboard.minutes == 0 && ProviderDashboard.seconds == 0) {
//                        notificationManager.cancelAll()
//                    }
//                    if (ProviderDashboard.seconds == 0) {
//                        ProviderDashboard.seconds = 59
//                        ProviderDashboard.minutes -= 1
//                    }
//                    Log.e("THREAD: ", "ONGOING....")
//                }
//            }
//        })

    }
}