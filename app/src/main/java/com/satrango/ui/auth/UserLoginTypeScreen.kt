package com.satrango.ui.auth

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.satrango.databinding.ActivityUserLoginTypeScreenBinding
import com.satrango.remote.Notification
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.fcm.FCMMessageReqModel
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*


class UserLoginTypeScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserLoginTypeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserLoginTypeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (PermissionUtils.isNetworkConnected(this)) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM ERROR:", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result
                CoroutineScope(Dispatchers.Main).launch {
                    val response = RetrofitBuilder.getUserRetrofitInstance().updateFCMToken(
                        FCMReqModel(
                            token, RetrofitBuilder.USER_KEY, UserUtils.getUserId(
                                this@UserLoginTypeScreen
                            )
                        )
                    )
                    val responses = RetrofitBuilder.getUserRetrofitInstance().updateFCMToken(
                        FCMReqModel(
                            token,
                            RetrofitBuilder.USER_KEY,
                            "2"
                        )
                    )
                    val jsonResponse = JSONObject(response.string())
                    if (jsonResponse.getInt("status") != 200) {
                        snackBar(binding.userBtn, "Please check internet connection!")
                        Handler().postDelayed({
                            finish()
                        }, 3000)
                    }
                }
                Log.e("FCM TOKEN", token)
            })
        } else {
            snackBar(binding.userBtn, "Please check internet connection!")
            Handler().postDelayed({
                finish()
            }, 3000)
        }


        binding.apply {

            userBtn.setOnClickListener {
                startActivity(Intent(this@UserLoginTypeScreen, UserDashboardScreen::class.java))
                finish()
            }

            serviceProviderBtn.setOnClickListener {
                startActivity(Intent(this@UserLoginTypeScreen, ProviderDashboard::class.java))
//                startActivity(Intent(this@UserLoginTypeScreen, ProviderSignUpOne::class.java))
                finish()
            }

        }
    }


    override fun onBackPressed() {
        moveTaskToBack(true)
    }


}