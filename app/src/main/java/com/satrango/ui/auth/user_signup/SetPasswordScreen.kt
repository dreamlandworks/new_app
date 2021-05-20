package com.satrango.ui.auth.user_signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.satrango.R
import com.satrango.ui.auth.LoginScreen
import com.satrango.databinding.ActivitySetPasswordScreenBinding

class SetPasswordScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySetPasswordScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetPasswordScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            textWatchers()

            nextBtn.setOnClickListener { showCustomDialog() }

        }

    }

    private fun textWatchers() {
        binding.password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.password.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_greencheck,
                    0
                )
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.reEnterPassword.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == binding.password.text.toString().trim()) {
                    binding.reEnterPassword.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_greencheck,
                        0
                    )
                }
            }

        })
    }

    private fun showCustomDialog() {
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView: View =
            LayoutInflater.from(this).inflate(R.layout.user_signup_success_dialog, viewGroup, false)
        val loginBtn = dialogView.findViewById<TextView>(R.id.loginBtn)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        loginBtn.setOnClickListener {
            val intent = Intent(this@SetPasswordScreen, LoginScreen::class.java)
            startActivity(intent)
            finish()
        }
        closeBtn.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}