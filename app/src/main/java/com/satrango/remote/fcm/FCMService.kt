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
import com.google.gson.Gson
import com.satrango.R
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.FCMReqModel
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.user.bookings.provider_response.ProviderBookingResponseScreen
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.NumberFormatException
import java.time.Instant


class FCMService : FirebaseMessagingService() {

    companion object {
        var fcmInstant: Instant? = null
        val INTENT_FILTER = "INTENT_FILTER"
        val INTENT_FILTER_ONE = "INTENT_FILTER_ONE"
    }

    private var broadcaster: LocalBroadcastManager? = null
    private lateinit var notificationManager: NotificationManager
    private lateinit var builder: android.app.Notification.Builder

//    override fun onCreate() {
//        broadcaster = LocalBroadcastManager.getInstance(this)
//        registerReceiver(myReceiver, IntentFilter(INTENT_FILTER_ONE));
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        unregisterReceiver(myReceiver)
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun handleIntent(intent: Intent) {
        val bundle = intent.extras
        if (bundle != null) {
            for (key in bundle.keySet()) {
                val value = bundle[key]
                Log.e("FCM", "Key: $key Value: $value")
            }
        }
        super.handleIntent(intent)
        removeFirebaseOriginalNotifications()
        if (bundle!!.containsKey("gcm.notification.body")) {
            val body = bundle["gcm.notification.body"] as String?
            if (bundle.containsKey("gcm.notification.title")) {
                val title = bundle["gcm.notification.title"] as String?
                notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                Log.e("FCMMESSAGE:", title!! + "|||" + body.toString())
                if (title == "accepted") {
                    if (body!!.split("|")[4] == "selected" || body.split("|")[3] == "accept") {
                        notificationManager.cancelAll()
                        if (ProviderDashboard.bottomSheetDialog != null) {
                            if (ProviderDashboard.bottomSheetDialog!!.isShowing) {
                                ProviderDashboard.bottomSheetDialog!!.dismiss()
                                UserUtils.saveFromFCMService(this, false)
                                ProviderDashboard.FROM_FCM_SERVICE = false
                                Log.e("FCM MESSAGE CLOSED:", "$title|$body")
                            }
                        } else {
                            val intent = Intent(INTENT_FILTER_ONE)
                            intent.putExtra(resources.getString(R.string.sp_id), body.split("|")[2])
                            sendBroadcast(intent)
                        }
                    }
                } else if (title == "user") {
                    if (!UserUtils.getFromFCMService(this)) {
                        if (UserUtils.getSpStatus(this)) {
                            notificationManager.cancelAll()
                            addNotification(body)
                            ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = false
                            ProviderDashboard.FROM_FCM_SERVICE = true
                            UserUtils.saveFromFCMService(this, true)
                            Log.e("FCMMESSAGE RECEIVED:", "$title|$body")
                        }
                    }
                } else {
                    if (body!!.split("|")[4] == "selected" || body.split("|")[3] == "accept") {
                        Log.e("FCM ELSE PART:", "$title::$body")
                        val intent = Intent(this, ProviderBookingResponseScreen::class.java)
                        intent.putExtra("response", body)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else {
                        val spDetails = Gson().fromJson(UserUtils.getSelectedSPDetails(this), SearchServiceProviderResModel::class.java)
                        val count = UserUtils.getInstantBookingSpCount(this) + 1
                        UserUtils.setInstantBookingSpCount(this, count)
                        if (UserUtils.getInstantBookingSpCount(this) == spDetails.data.size) {
                            Log.e("FCM ELSE PART:", "$title::$body")
                            val intent = Intent(this, ProviderBookingResponseScreen::class.java)
                            intent.putExtra("response", body)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.notification?.let {
            if (it.title!! == "user") {
                val notificationIntent = Intent(INTENT_FILTER)
                notificationIntent.putExtra(application.getString(R.string.booking_id), it.body!!.split("|")[0])
                notificationIntent.putExtra(application.getString(R.string.category_id), it.body!!.split("|")[1])
                notificationIntent.putExtra(application.getString(R.string.user_id), it.body!!.split("|")[2])
                notificationIntent.putExtra(application.getString(R.string.booking_type), it.body!!.split("|")[4])
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
        Log.e("FCM TOKEN UPDATE", token)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    private fun addNotification(body: String?) {
        val requestID = System.currentTimeMillis().toInt()
        val alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        Log.e("FROM_FCM_SERVICE01:", UserUtils.getFromFCMService(this).toString())
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

    private fun removeFirebaseOriginalNotifications() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        val activeNotifications = notificationManager.activeNotifications ?: return
        for (tmp in activeNotifications) {
            Log.d("FCM StatusBarNotificat:", "tag/id: " + tmp.tag + " / " + tmp.id)
            val tag = tmp.tag
            val id = tmp.id
            if (tag != null && tag.contains("FCM-Notification")) notificationManager.cancel(tag, id)
        }
    }

//    private val myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        @RequiresApi(Build.VERSION_CODES.O)
//        override fun onReceive(context: Context, intent: Intent) {
//            UserUtils.saveFromFCMService(context, true)
//        }
//    }

}

