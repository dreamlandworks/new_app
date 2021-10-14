package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.review

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderRatingReviewScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingViewModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderMyBookingsScreen
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import de.hdodenhof.circleimageview.CircleImageView

class ProviderRatingReviewScreen : AppCompatActivity() {

    private lateinit var binding: ActivityProviderRatingReviewScreenBinding
    private lateinit var progressDialog: BeautifulProgressDialog
    private var categoryId = ""
    private var bookingId = ""
    private var userId = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderRatingReviewScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeProgressDialog()
        bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
        categoryId = intent.getStringExtra(getString(R.string.category_id))!!
        userId = intent.getStringExtra(getString(R.string.user_id))!!

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.view_details)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        binding.apply {

            overAllRatingBtn.onRatingBarChangeListener =
                RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
                    overAllRating.text = "$rating/5"
                }

            customerRatingBtn.onRatingBarChangeListener =
                RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
                    customerRating.text = "$rating/5"
                }

            bookingQualityRatingBtn.onRatingBarChangeListener =
                RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
                    bookingQualityRating.text = "$rating/5"
                }

            appReviewBtn.onRatingBarChangeListener =
                RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
                    appReview.text = "$rating/5"
                }

            jobSatisfactionBtn.onRatingBarChangeListener =
                RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
                    jobSatisfaction.text = "$rating/5"
                }


            submitBtn.setOnClickListener {

                when {
                    feedBack.text.toString().isEmpty() -> {
                        snackBar(overAllRating, "Please Provide Feedback")
                    }
                    overAllRatingBtn.rating == 0.0f -> {
                        snackBar(overAllRating, "Please Provide Over All Review")
                    }
                    customerRatingBtn.rating == 0.0f -> {
                        snackBar(overAllRating, "Please Provide Customer Rating")
                    }
                    bookingQualityRatingBtn.rating == 0.0f -> {
                        snackBar(overAllRating, "Please Provide Booking Quality Rating")
                    }
                    appReviewBtn.rating == 0.0f -> {
                        snackBar(overAllRating, "Please Provide Over App Review")
                    }
                    jobSatisfactionBtn.rating == 0.0f -> {
                        snackBar(overAllRating, "Please Provide Over Job Satisfaction Rating")
                    }
                    else -> {
                        postUserRating()
                    }
                }

            }

        }
    }

    private fun postUserRating() {
        val factory = ViewModelFactory(ProviderBookingRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderBookingViewModel::class.java]
        val requestBody = UserRatingReqModel(
            binding.appReviewBtn.rating,
            bookingId.toInt(),
            binding.bookingQualityRatingBtn.rating,
            binding.feedBack.text.toString().trim(),
            binding.jobSatisfactionBtn.rating,
            RetrofitBuilder.PROVIDER_KEY,
            binding.overAllRatingBtn.rating,
            userId.toInt(),
            binding.customerRatingBtn.rating
        )
        viewModel.userRating(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    snackBar(binding.overAllRating, "Thank you for Feedback")
                    Handler().postDelayed({
                        startActivity(Intent(this, ProviderMyBookingsScreen::class.java))
                    }, 3000)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.overAllRating, it.message!!)
                }
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }
}