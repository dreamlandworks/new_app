package com.satrango.ui.user.user_dashboard.search_service_providers

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.satrango.R
import com.satrango.databinding.ActivitySearchViewProfileBinding
import com.satrango.ui.user.bookings.booking_address.BookingAddressScreen
import com.satrango.ui.user.bookings.booking_attachments.BookingAttachmentsScreen
import com.satrango.ui.user.bookings.booking_date_time.BookingDateAndTimeScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.utils.UserUtils
import java.text.DecimalFormat
import java.util.*

class UserSearchViewProfileScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySearchViewProfileBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchViewProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()

        val data = intent.getSerializableExtra(getString(R.string.service_provider)) as Data
        val spDetails = Gson().fromJson(
            UserUtils.getSelectedSPDetails(this),
            SearchServiceProviderResModel::class.java
        )
        Log.e("JSON", Gson().toJson(spDetails))
        for (sp in spDetails.slots_data) {
            if (data.users_id == sp.user_id) {
                for (booking in sp.blocked_time_slots) {
                    val calender = Calendar.getInstance()
                    val comingHour = calender.get(Calendar.HOUR_OF_DAY)
                    if (comingHour + 1 == booking.time_slot_from.split(":")[0].toInt()) {
                        binding.bookNowBtn.visibility = View.GONE
                    }
                }
            }
        }

        binding.apply {

            Glide.with(profilePic).load(data.profile_pic).placeholder(R.drawable.images).into(
                profilePic
            )
            userName.text = data.fname
            occupation.text = data.profession
            costPerHour.text = data.per_hour
            val df = DecimalFormat("#.##")
            val distanceInKms = data.distance_miles.toDouble() * 1.609344
            distance.text = "${df.format(distanceInKms)} Kms"
            skills.text = data.profession
            experience.text = data.exp
            aboutMe.text = data.about_me
            languages.text = data.languages_known

//            ranking.text = data.points_count
//            rating.text = data.points_count
//            reviews.text = data.points_count
//            jobs.text = data.points_count
//            overAllReviews.text = data.

//            audience.text = data.
//            professionRating.text = data.
//            behaviourRating.text = data.
//            satisfactionRating.text = data.
//            skillsRating.text = data.

            bookLaterBtn.setOnClickListener {
                BookingDateAndTimeScreen.FROM_PROVIDER = false
                UserUtils.saveFromInstantBooking(binding.root.context, false)
                val intent = Intent(this@UserSearchViewProfileScreen, BookingDateAndTimeScreen::class.java)
                intent.putExtra(getString(R.string.service_provider), data)
                startActivity(intent)
            }
            bookNowBtn.setOnClickListener {
                UserUtils.saveFromInstantBooking(binding.root.context, true)
                if (data.category_id == "3") {
                    val intent = Intent(this@UserSearchViewProfileScreen, BookingAddressScreen::class.java)
                    intent.putExtra(getString(R.string.service_provider), data)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@UserSearchViewProfileScreen, BookingAttachmentsScreen::class.java)
                    intent.putExtra(getString(R.string.service_provider), data)
                    startActivity(intent)
                }
            }
        }
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        val backTextBtn = toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
        backTextBtn.text = resources.getString(R.string.back)
        backTextBtn.setOnClickListener { onBackPressed() }
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.view_profile)
        val imageView = toolBar.findViewById<ImageView>(R.id.toolBarImage)
        imageView.visibility = View.GONE
    }

    override fun onBackPressed() {
        finish()
        startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
    }
}