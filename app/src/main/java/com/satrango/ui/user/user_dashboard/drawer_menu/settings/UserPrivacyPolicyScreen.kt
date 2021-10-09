package com.satrango.ui.user.user_dashboard.drawer_menu.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.ActivityUserPrivacyPolicyScreenBinding
import com.satrango.utils.UserUtils
import de.hdodenhof.circleimageview.CircleImageView

class UserPrivacyPolicyScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserPrivacyPolicyScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserPrivacyPolicyScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()

        binding.privacyPolicy.text = HtmlCompat.fromHtml(getString(R.string.privacy_policy_data) + "<br/><br/>" + getString(R.string.disclaimer), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.privacy_policy)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        Glide.with(profilePic).load(UserUtils.getUserProfilePic(this)).into(profilePic)
        if (UserSettingsScreen.FROM_PROVIDER) {
            toolBar.setBackgroundColor(resources.getColor(R.color.purple_500))
        }
    }
}