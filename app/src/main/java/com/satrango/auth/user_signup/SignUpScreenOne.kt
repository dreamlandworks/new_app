package com.satrango.auth.user_signup

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.satrango.R
import com.satrango.auth.LoginScreen
import com.satrango.databinding.ActivitySignUpScreenOneBinding
import com.satrango.utils.PermissionUtils

class SignUpScreenOne : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpScreenOneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenOneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PermissionUtils.checkAndRequestPermissions(this)

        binding.apply {

            loginBtn.setOnClickListener {
                finish()
                startActivity(Intent(this@SignUpScreenOne, LoginScreen::class.java))
            }

            nextBtn.setOnClickListener { startActivity(
                Intent(
                    this@SignUpScreenOne,
                    SignUpScreenTwo::class.java
                )
            ) }

            mobileNo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (count == 10) {
                        mobileNo.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_greencheck,
                            0
                        )
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtils.PERMISSIONS_CODE == requestCode && grantResults.isNotEmpty()) {
            for (grant in grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    PermissionUtils.checkAndRequestPermissions(this)
                }
            }
        }
    }
}