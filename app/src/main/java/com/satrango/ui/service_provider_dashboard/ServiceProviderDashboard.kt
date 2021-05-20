package com.satrango.ui.service_provider_dashboard

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.satrango.R
import com.satrango.databinding.ActivityServiceProviderDashboardBinding
import com.satrango.ui.auth.UserLoginTypeScreen
import com.satrango.ui.auth.provider_signup.ProviderSignUpOne

class ServiceProviderDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityServiceProviderDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceProviderDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openActivationDialog()

    }

    private fun openActivationDialog() {
        val builder = AlertDialog.Builder(this@ServiceProviderDashboard)
        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.provider_activation_dialogue, null)
        builder.setCancelable(false)
        builder.setView(dialogView)

        val img_close = dialogView.findViewById<View>(R.id.closeBtn) as ImageView
        val yesBtn = dialogView.findViewById<TextView>(R.id.yesBtn)
        val noBtn = dialogView.findViewById<TextView>(R.id.noBtn)
        val dialog: Dialog = builder.create()

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        yesBtn.setOnClickListener {
            startActivity(Intent(this@ServiceProviderDashboard, ProviderSignUpOne::class.java))
            finish()
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this@ServiceProviderDashboard, UserLoginTypeScreen::class.java))
            finish()
        }

        img_close.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this@ServiceProviderDashboard, UserLoginTypeScreen::class.java))
            finish()
        }
        dialog.show()
    }

}