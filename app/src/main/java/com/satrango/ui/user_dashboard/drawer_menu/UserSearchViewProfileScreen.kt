package com.satrango.ui.user_dashboard.drawer_menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.ActivitySearchViewProfileBinding
import com.satrango.utils.UserUtils

class UserSearchViewProfileScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySearchViewProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchViewProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        val backTextBtn = toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
        backTextBtn.text = resources.getString(R.string.back)
        backTextBtn.setOnClickListener { onBackPressed() }
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.view_profile)
        val imageView = toolBar.findViewById<ImageView>(R.id.toolBarImage)
        imageView.visibility = View.GONE

        binding.apply {

            Glide.with(profilePic).load(UserUtils.getUserProfilePic(this@UserSearchViewProfileScreen)).into(profilePic)
            userName.text = UserUtils.getUserName(this@UserSearchViewProfileScreen)

        }

    }
}