package com.satrango.remote.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.media.RingtoneManager.getDefaultUri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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
    }

    private lateinit var notificationManager: NotificationManager
    private lateinit var builder: android.app.Notification.Builder

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        addNotification(remoteMessage.data["body"])
        remoteMessage.notification?.let {
            Log.e("FCMMESSAGE:", it.title!! + "|" + it.body.toString() + "|" + ProviderDashboard.FROM_FCM_SERVICE.toString() + "|" + UserUtils.getSpStatus(this).toString())
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (it.title == "accepted") {
                notificationManager.cancelAll()
                if (ProviderDashboard.bottomSheetDialog != null) {
                    if (ProviderDashboard.bottomSheetDialog!!.isShowing) {
                        ProviderDashboard.bottomSheetDialog!!.dismiss()
                        ProviderDashboard.FROM_FCM_SERVICE = false
                        Log.e("FCMMESSAGE CLOSED:", it.title!! + "|" + it.body.toString())
                    }
                }
            } else if (it.title == "user") {
                if (!ProviderDashboard.FROM_FCM_SERVICE) {
                    if (UserUtils.getSpStatus(this)) {
                        addNotification(it.body)
                        Log.e("FCMMESSAGE RECEIVED:", it.title!! + "|" + it.body.toString())
                    }
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
        ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = false
        ProviderDashboard.FROM_FCM_SERVICE = true
        val notificationIntent = Intent(applicationContext, ProviderDashboard::class.java)
        notificationIntent.putExtra(application.getString(R.string.booking_id), body?.split("|")!![0])
        notificationIntent.putExtra(application.getString(R.string.category_id), body.split("|")[1])
        notificationIntent.putExtra(application.getString(R.string.user_id), body.split("|")[2])
        val contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("channelId", "description", NotificationManager.IMPORTANCE_HIGH)
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

        fcmInstant = Instant.now()

    }

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context?, intent: Intent) {
            val body = intent.extras?.getString("body")
            addNotification(body!!)
        }

    }
}

