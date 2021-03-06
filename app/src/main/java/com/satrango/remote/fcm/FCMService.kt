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
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.satrango.R
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.FCMReqModel
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.user.bookings.provider_response.ProviderBookingResponseScreen
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant


class FCMService : FirebaseMessagingService() {

    companion object {
        var fcmInstant: Instant? = null
        lateinit var notificationManager: NotificationManager
    }

    private lateinit var builder: android.app.Notification.Builder

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun handleIntent(intent: Intent) {
        val bundle = intent.extras
        if (bundle != null) {
            for (key in bundle.keySet()) {
                val value = bundle[key]
//                Log.e("FCM", "Key: $key Value: $value")
            }
        }
        super.handleIntent(intent)
        removeFirebaseOriginalNotifications()
        if (bundle!!.containsKey("gcm.notification.body")) {
            val body = bundle["gcm.notification.body"] as String?
            if (bundle.containsKey("gcm.notification.title")) {
                val title = bundle["gcm.notification.title"] as String?
//                Log.e("FCMMESSAGE:", title!! + "|||" + body.toString())
                if (title == "accepted") {
                    if (body!!.split("|")[4] == "selected" || body.split("|")[3] == "accept") {
                        notificationManager.cancelAll()
                        if (ProviderDashboard.bottomSheetDialog != null) {
                            if (ProviderDashboard.bottomSheetDialog!!.isShowing) {
                                ProviderDashboard.bottomSheetDialog!!.dismiss()
                                UserUtils.saveFromFCMService(this, false)
                                ProviderDashboard.FROM_FCM_SERVICE = false
//                                Log.e("FCM MESSAGE CLOSED:", "$title|$body")
                            }
                        } else {
                            val intentFilterOne = Intent(getString(R.string.INTENT_FILTER_ONE))
                            intentFilterOne.putExtra(resources.getString(R.string.sp_id), body.split("|")[2])
                            sendBroadcast(intentFilterOne)
                        }
                    }
                } else if (title == "user") {
                    if (!UserUtils.getFromFCMService(this)) {
                        if (UserUtils.getSpStatus(this)) {
                            notificationManager.cancelAll()
                            addNotification(body)
//                            ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = false
                            ProviderDashboard.FROM_FCM_SERVICE = true
                            UserUtils.saveFromFCMService(this, true)
//                            Log.e("FCMMESSAGE RECEIVED:", "$title|$body")
                        }
                    }
                } else {
                    if (title == "otp") {
//                        Log.e("FCM SERVICE:", body.toString())
                        val notificationIntent = Intent(getString(R.string.OTP_INTENT_FILTER))
                        notificationIntent.putExtra(application.getString(R.string.booking_id), body!!.split("|")[0])
                        notificationIntent.putExtra(application.getString(R.string.category_id), body.split("|")[1])
                        notificationIntent.putExtra(application.getString(R.string.user_id), body.split("|")[2])
                        sendBroadcast(notificationIntent)
                    } else if (title == "otpResponse") {
                        if (body!!.split("|")[3] == "user") {
                            val notificationIntent = Intent(getString(R.string.OTP_RESPONSE_INTENT_FILTER))
                            notificationIntent.putExtra(application.getString(R.string.booking_id), body.split("|")[0])
                            notificationIntent.putExtra(application.getString(R.string.category_id), body.split("|")[1])
                            notificationIntent.putExtra(application.getString(R.string.user_id), body.split("|")[2])
                            sendBroadcast(notificationIntent)
                        } else {
                            val notificationIntent = Intent(getString(R.string.OTP_RESPONSE_INTENT_FILTER_DONE))
                            notificationIntent.putExtra(application.getString(R.string.booking_id), body.split("|")[0])
                            notificationIntent.putExtra(application.getString(R.string.category_id), body.split("|")[1])
                            notificationIntent.putExtra(application.getString(R.string.user_id), body.split("|")[2])
                            sendBroadcast(notificationIntent)
                        }
                    } else if (title == "extraDemand") {
                        val notificationIntent = Intent(getString(R.string.EXTRA_DEMAND_ACCEPT_REJECT))
                        if (body!!.split("|").size == 3) {
                            notificationIntent.putExtra(application.getString(R.string.booking_id), body.split("|")[0])
                            notificationIntent.putExtra(application.getString(R.string.category_id), body.split("|")[1])
                            notificationIntent.putExtra(application.getString(R.string.user_id), body.split("|")[2])
                        } else {
                            notificationIntent.putExtra(application.getString(R.string.booking_id), body.split("|")[0])
                            notificationIntent.putExtra(application.getString(R.string.category_id), body.split("|")[1])
                            notificationIntent.putExtra(application.getString(R.string.user_id), body.split("|")[2] + "|" + body.split("|")[3])
                        }
                        sendBroadcast(notificationIntent)
                    } else if (body!!.split("|")[4] == "selected" || body.split("|")[3] == "accept") {
//                        Log.e("FCM ELSE PART:", "$title::$body")
                        val acceptedIntent = Intent(this, ProviderBookingResponseScreen::class.java)
                        acceptedIntent.putExtra("response", body)
                        acceptedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(acceptedIntent)
                    } else {
                        val rejectedIntent = Intent(this, ProviderBookingResponseScreen::class.java)
                        rejectedIntent.putExtra("response", body)
                        rejectedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(rejectedIntent)
                    }
                }
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.notification?.let {
            if (it.title!! == "user") {
                val notificationIntent = Intent(getString(R.string.INTENT_FILTER))
                notificationIntent.putExtra(application.getString(R.string.booking_id), it.body!!.split("|")[0])
                notificationIntent.putExtra(application.getString(R.string.category_id), it.body!!.split("|")[1])
                notificationIntent.putExtra(application.getString(R.string.user_id), it.body!!.split("|")[2])
                notificationIntent.putExtra(application.getString(R.string.booking_type), it.body!!.split("|")[3])
                sendBroadcast(notificationIntent)
            }
            if (it.title!! == "otp") {
//                Log.e("FCM SERVICE:", it.body.toString())
                val notificationIntent = Intent(getString(R.string.OTP_INTENT_FILTER))
                notificationIntent.putExtra(application.getString(R.string.booking_id), it.body!!.split("|")[0])
                notificationIntent.putExtra(application.getString(R.string.category_id), it.body!!.split("|")[1])
                notificationIntent.putExtra(application.getString(R.string.user_id), it.body!!.split("|")[2])
                sendBroadcast(notificationIntent)
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant", "UnspecifiedImmutableFlag")
    private fun addNotification(body: String?) {
        val requestID = System.currentTimeMillis().toInt()
        val alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationIntent = Intent(applicationContext, ProviderDashboard::class.java)
        notificationIntent.putExtra(application.getString(R.string.booking_id), body?.split("|")!![0])
        notificationIntent.putExtra(application.getString(R.string.category_id), body.split("|")[1])
        notificationIntent.putExtra(application.getString(R.string.user_id), body.split("|")[2])
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
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.circlelogo))
        } else {
            builder = android.app.Notification.Builder(this)
                .setSmallIcon(R.drawable.circlelogo)
                .setContentTitle(getString(R.string.app_name))
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setContentText("You got a booking request!")
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.circlelogo))
        }
        notificationManager.notify(1234, builder.build())

        fcmInstant = Instant.now()

    }

    private fun removeFirebaseOriginalNotifications() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        val activeNotifications = notificationManager.activeNotifications ?: return
        for (tmp in activeNotifications) {
            val tag = tmp.tag
            val id = tmp.id
            if (tag != null && tag.contains("FCM-Notification")) notificationManager.cancel(tag, id)
        }
    }

}

