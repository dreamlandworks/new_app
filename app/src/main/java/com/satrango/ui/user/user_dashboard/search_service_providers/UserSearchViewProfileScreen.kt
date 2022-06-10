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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.satrango.R
import com.satrango.databinding.ActivitySearchViewProfileBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.booking_address.BookingAddressScreen
import com.satrango.ui.user.bookings.booking_attachments.BookingAttachmentsScreen
import com.satrango.ui.user.bookings.booking_date_time.BookingDateAndTimeScreen
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isProvider
import com.satrango.utils.UserUtils.isReschedule
import com.satrango.utils.snackBar
import java.text.DecimalFormat
import java.util.*
import kotlin.math.round

class UserSearchViewProfileScreen : AppCompatActivity() {

    private lateinit var data: Data
    private lateinit var binding: ActivitySearchViewProfileBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchViewProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()

        if (UserUtils.getSelectedSPDetails(this).isNotEmpty()) {
            data = Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java)
            val spDetails = Gson().fromJson(UserUtils.getSelectedAllSPDetails(this), SearchServiceProviderResModel::class.java)
//            Log.e("JSON", Gson().toJson(spDetails))
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
        } else {
            snackBar(binding.aboutMe, "Please select Service provider")
            onBackPressed()
            return
        }

        binding.apply {

            Glide.with(profilePic).load(data.profile_pic).placeholder(R.drawable.images).into(profilePic)
            userName.text = data.fname
            occupation.text = data.profession
            costPerHour.text = "Rs. ${round(data.final_amount.toDouble()).toInt()}/-"
            val df = DecimalFormat("#.##")
            val distanceInKms = data.distance_miles.toDouble() * 1.609344
            distance.text = "${df.format(distanceInKms)} Kms"
            skills.text = data.profession
            experience.text = data.exp
            aboutMe.text = data.about_me
            languages.text = data.languages_known

            ranking.text = data.rank.toString()
            rating.text = data.rating
            reviews.text = data.total_people.toString()
            jobs.text = data.jobs_count.toString()

            overAllReviews.text = data.rating
            audience.text = data.total_people.toString()
            professionRating.text = data.professionalism
            behaviourRating.text = data.behaviour
            satisfactionRating.text = data.satisfaction
            skillsRating.text = data.skill

            bookLaterBtn.setOnClickListener {
//                UserUtils.bookingType = "selected"
                isReschedule(this@UserSearchViewProfileScreen, false)
                isProvider(this@UserSearchViewProfileScreen, false)
                UserUtils.saveFromInstantBooking(binding.root.context, false)
                startActivity(Intent(this@UserSearchViewProfileScreen, BookingDateAndTimeScreen::class.java))
            }
            bookNowBtn.setOnClickListener {
//                UserUtils.bookingType = "selected"
                UserUtils.saveFromInstantBooking(binding.root.context, true)
                if (data.category_id == "3") {
                    startActivity(Intent(this@UserSearchViewProfileScreen, BookingAddressScreen::class.java))
                } else {
                    val database = Firebase.database
                    database.getReference(UserUtils.getFCMToken(binding.root.context)).removeValue()
                    startActivity(Intent(this@UserSearchViewProfileScreen, BookingAttachmentsScreen::class.java))
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

}