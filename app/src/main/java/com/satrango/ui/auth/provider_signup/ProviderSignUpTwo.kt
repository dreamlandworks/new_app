package com.satrango.ui.auth.provider_signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.satrango.R
import com.satrango.databinding.ActivityProviderSignUpTwoBinding

class ProviderSignUpTwo : AppCompatActivity() {

    private lateinit var binding: ActivityProviderSignUpTwoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderSignUpTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.profile)

        binding.apply {

            nextBtn.setOnClickListener {
                startActivity(Intent(this@ProviderSignUpTwo, ProviderSignUpThree::class.java))
            }

            cancelBtn.setOnClickListener { onBackPressed() }

        }

    }
}