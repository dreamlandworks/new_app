package com.satrango.ui.service_provider.provider_dashboard

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.satrango.R
import com.satrango.databinding.ActivityProviderDashboardBinding
import com.satrango.ui.auth.UserLoginTypeScreen
import com.satrango.ui.auth.provider_signup.ProviderSignUpOne

class ProviderDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityProviderDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showActivationDialog()

    }

    private fun showActivationDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.service_provider_activation_dialog, null)
        dialog.setCancelable(false)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val yesBtn = dialogView.findViewById<TextView>(R.id.yesBtn)
        val noBtn = dialogView.findViewById<TextView>(R.id.noBtn)
        closeBtn.setOnClickListener { dialog.dismiss() }
        yesBtn.setOnClickListener {
            startActivity(Intent(this, ProviderSignUpOne::class.java))
            dialog.dismiss()
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

}