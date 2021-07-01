package com.satrango.ui.auth.user_signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.ActivityTermsAndConditionScreenBinding
import com.satrango.utils.UserUtils
import de.hdodenhof.circleimageview.CircleImageView

class TermsAndConditionScreen : AppCompatActivity() {

    private lateinit var binding: ActivityTermsAndConditionScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsAndConditionScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.terms_amp_conditions)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        Glide.with(profilePic).load(UserUtils.getUserProfilePic(this)).into(profilePic)

        binding.apply {


        }
    }
}