package com.satrango.ui.user.user_dashboard.search_service_providers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.ActivitySearchViewProfileBinding
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data
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

        val data = intent.getSerializableExtra(getString(R.string.service_provider)) as Data

        binding.apply {

            Glide.with(profilePic).load(data.profile_pic).placeholder(R.drawable.images).into(profilePic)
            userName.text = data.fname
            occupation.text = data.profession
            costPerHour.text = data.per_hour
//            ranking.text = data.points_count
//            rating.text = data.points_count
//            reviews.text = data.points_count
//            jobs.text = data.points_count
            experience.text = data.exp
//            languages.text = data.
//            distance.text = data.
//            skills.text = data.
            aboutMe.text = data.about_me
//            overAllReviews.text = data.
//            audience.text = data.
//            professionRating.text = data.
//            behaviourRating.text = data.
//            satisfactionRating.text = data.
//            skillsRating.text = data.
        }

    }
}