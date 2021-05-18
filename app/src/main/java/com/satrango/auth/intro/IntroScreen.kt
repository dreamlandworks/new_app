package com.satrango.auth.intro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.satrango.R
import com.satrango.auth.LoginScreen
import com.satrango.databinding.ActivityIntroScreenBinding

class IntroScreen : AppCompatActivity(), IntroInterface {

    private lateinit var binding: ActivityIntroScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val list = mutableListOf<Int>()
        list.add(R.drawable.slider1)
        list.add(R.drawable.slider2)
        list.add(R.drawable.slider3)
        list.add(R.drawable.slider4)
        binding.viewPager2.adapter = IntroAdapter(list, this)
    }

    override fun prevClick(position: Int) {
        if (binding.viewPager2.currentItem != 0) {
            binding.viewPager2.currentItem -= 1
        }
    }

    override fun nextClick(position: Int) {
        if (binding.viewPager2.currentItem != 3) {
            binding.viewPager2.currentItem += 1
        } else {
            finish()
            startActivity(Intent(this, LoginScreen::class.java))
        }
    }
}