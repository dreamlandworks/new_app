package com.satrango.ui.user_dashboard.drawer_menu.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.ActivityUserSettingsScreenBinding
import com.satrango.ui.auth.user_signup.TermsAndConditionScreen
import com.satrango.utils.UserUtils
import de.hdodenhof.circleimageview.CircleImageView

class UserSettingsScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserSettingsScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSettingsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.settings)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        Glide.with(profilePic).load(UserUtils.getUserProfilePic(this)).into(profilePic)


        binding.apply {
            termsAndConditions.setOnClickListener { startActivity(Intent(this@UserSettingsScreen, TermsAndConditionScreen::class.java)) }
            privacyPolicy.setOnClickListener { startActivity(Intent(this@UserSettingsScreen, UserPrivacyPolicyScreen::class.java)) }
        }

    }
}