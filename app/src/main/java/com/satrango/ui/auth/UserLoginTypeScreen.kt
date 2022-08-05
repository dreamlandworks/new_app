package com.satrango.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.OpenableColumns
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.satrango.databinding.ActivityUserLoginTypeScreenBinding
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import java.io.*
import java.util.*


class UserLoginTypeScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserLoginTypeScreenBinding

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserLoginTypeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Firebase.messaging.isAutoInitEnabled = true

        if (!UserUtils.updateNewFCMToken(this)) {
//            snackBar(binding.userBtn, "Please check internet connection!")
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
                UserUtils.saveFromFCMService(this@UserLoginTypeScreen, false)
                startActivity(Intent(this@UserLoginTypeScreen, ProviderDashboard::class.java))
                finish()
            }

        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            data?.data?.also { _ ->
//                toast(this, getStringPdf(data.data!!)!!)
//                val string = data.data?.let { contentResolver.openInputStream(it).use { it.reader().readText() } }
            }
        }
    }

    private fun getStringPdf(filepath: Uri?): String? {
        var inputStream: InputStream? = null
        var byteArrayOutputStream = ByteArrayOutputStream()
        try {
            inputStream = contentResolver.openInputStream(filepath!!)
            val buffer = ByteArray(1024)
            byteArrayOutputStream = ByteArrayOutputStream()
            var bytesRead: Int
            while (inputStream!!.read(buffer).also { bytesRead = it } != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        val pdfByteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(pdfByteArray, Base64.DEFAULT)
    }

}