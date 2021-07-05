package com.satrango.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.satrango.R
import com.satrango.databinding.ActivityIntroScreenBinding
import com.satrango.ui.auth.loginscreen.LoginScreen
import com.satrango.utils.AuthUtils

class IntroScreen : AppCompatActivity(), IntroInterface {

    private lateinit var binding: ActivityIntroScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val list = mutableListOf<IntroModel>()
        list.add(IntroModel(R.drawable.splash_1, resources.getString(R.string.intro1)))
        list.add(IntroModel(R.drawable.splash_2, resources.getString(R.string.intro2)))
        list.add(IntroModel(R.drawable.splash_3, resources.getString(R.string.intro3)))
        list.add(IntroModel(R.drawable.splash_4, resources.getString(R.string.intro4)))
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
            AuthUtils.setFirstTimeLaunch(this)
            startActivity(Intent(this, LoginScreen::class.java))
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}