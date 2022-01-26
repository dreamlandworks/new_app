package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.review

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderRatingReviewScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingViewModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderMyBookingsScreen
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.UserMyBookingsScreen
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView

class ProviderRatingReviewScreen : AppCompatActivity() {

    private lateinit var binding: ActivityProviderRatingReviewScreenBinding
    private lateinit var progressDialog: BeautifulProgressDialog

    companion object {
        var FROM_PROVIDER = true
        var userId = ""
        var bookingId = ""
        var categoryId = ""
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderRatingReviewScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeProgressDialog()

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.view_details)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)

        if (!FROM_PROVIDER) {
            binding.text.text =
                "You have successfully completed a booking. Please help us in rating your experience with service provider."
            binding.overAllRatingText.text = "Overall Rating"
            binding.customerRatingText.text = "Professionalism"
            binding.bookingQualityRatingText.text = "Skills"
            binding.appReviewText.text = "Behaviour"
            binding.jobSatisfactionText.text = "Satisfaction"
            binding.submitBtn.setBackgroundResource(R.drawable.category_bg)
            toolBar.setBackgroundColor(resources.getColor(R.color.blue))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window: Window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = resources.getColor(R.color.blue)
            }
        } else {
            toolBar.setBackgroundColor(resources.getColor(R.color.purple_500))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window: Window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = resources.getColor(R.color.purple_700)
            }
        }

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

                if (FROM_PROVIDER) {
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
                } else {
                    when {
                        feedBack.text.toString().isEmpty() -> {
                            snackBar(overAllRating, "Please Provide Feedback")
                        }
                        overAllRatingBtn.rating == 0.0f -> {
                            snackBar(overAllRating, "Please Provide Over All Rating")
                        }
                        customerRatingBtn.rating == 0.0f -> {
                            snackBar(overAllRating, "Please Provide Professionalism")
                        }
                        bookingQualityRatingBtn.rating == 0.0f -> {
                            snackBar(overAllRating, "Please Provide Rating for Skills")
                        }
                        appReviewBtn.rating == 0.0f -> {
                            snackBar(overAllRating, "Please Provide Rating for Behaviour")
                        }
                        jobSatisfactionBtn.rating == 0.0f -> {
                            snackBar(overAllRating, "Please Provide Rating for Satisfaction")
                        }
                        else -> {
                            postProviderRating()
                        }
                    }
                }


            }

        }
    }

    private fun postProviderRating() {
        val factory = ViewModelFactory(ProviderBookingRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderBookingViewModel::class.java]
        val requestBody = ProviderRatingReqModel(
            binding.overAllRatingBtn.rating,
            binding.customerRatingBtn.rating,
            binding.bookingQualityRatingBtn.rating,
            binding.appReviewBtn.rating,
            binding.jobSatisfactionBtn.rating,
            binding.feedBack.text.toString().trim(),
            bookingId.toInt(),
            userId.toInt(),
            RetrofitBuilder.USER_KEY
        )
//        toast(this, Gson().toJson(requestBody))
        viewModel.providerRating(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    snackBar(binding.overAllRating, "Thank you for Feedback")
                    Handler().postDelayed({
                        startActivity(Intent(this, UserMyBookingsScreen::class.java))
                    }, 3000)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.overAllRating, it.message!!)
                }
            }
        })
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
                    binding.feedBack.setText("")
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

    override fun onBackPressed() {
        if (FROM_PROVIDER) {
            startActivity(Intent(this, ProviderDashboard::class.java))
        } else {
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
    }
}