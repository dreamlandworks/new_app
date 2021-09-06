package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.satrango.databinding.ActivitySetGoalsScreenBinding

class SetGoalsScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySetGoalsScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetGoalsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}