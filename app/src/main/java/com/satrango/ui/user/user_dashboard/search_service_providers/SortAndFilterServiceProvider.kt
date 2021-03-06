package com.satrango.ui.user.user_dashboard.search_service_providers

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.satrango.R
import com.satrango.databinding.ActivitySortAndFilterServiceProviderBinding
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.ProviderMyBidsScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchFilterModel
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isProvider


class SortAndFilterServiceProvider : AppCompatActivity() {

    private lateinit var binding: ActivitySortAndFilterServiceProviderBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySortAndFilterServiceProviderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()

        binding.apply {

            resetBtn.setOnClickListener {
                UserUtils.saveSearchFilter(this@SortAndFilterServiceProvider, "")
                onBackPressed()
            }

            kmSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                @SuppressLint("SetTextI18n")
                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int,
                    fromUser: Boolean
                ) {
                    binding.kms.text = "$progress Kms"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }
            })

            rating.setOnClickListener {
                if (isProvider(this@SortAndFilterServiceProvider)) {
                    if (rating.currentTextColor == Color.parseColor("#FFFFFF")) {
                        rating.setBackgroundResource(R.drawable.purple_out_line)
                        rating.setTextColor(Color.parseColor("#8D5FF5"))
                        ranking.setBackgroundResource(R.drawable.purple_out_line)
                        ranking.setTextColor(Color.parseColor("#8D5FF5"))
                        nearMe.setBackgroundResource(R.drawable.purple_out_line)
                        nearMe.setTextColor(Color.parseColor("#8D5FF5"))
                        priceLowToHigh.setBackgroundResource(R.drawable.purple_out_line)
                        priceLowToHigh.setTextColor(Color.parseColor("#8D5FF5"))
                        priceHighToLow.setBackgroundResource(R.drawable.purple_out_line)
                        priceHighToLow.setTextColor(Color.parseColor("#8D5FF5"))
                    } else {
                        rating.setBackgroundResource(R.drawable.provider_btn_bg)
                        rating.setTextColor(Color.parseColor("#FFFFFF"))
                        ranking.setBackgroundResource(R.drawable.purple_out_line)
                        ranking.setTextColor(Color.parseColor("#8D5FF5"))
                        nearMe.setBackgroundResource(R.drawable.purple_out_line)
                        nearMe.setTextColor(Color.parseColor("#8D5FF5"))
                        priceLowToHigh.setBackgroundResource(R.drawable.purple_out_line)
                        priceLowToHigh.setTextColor(Color.parseColor("#8D5FF5"))
                        priceHighToLow.setBackgroundResource(R.drawable.purple_out_line)
                        priceHighToLow.setTextColor(Color.parseColor("#8D5FF5"))
                    }
                } else {
                    if (rating.currentTextColor == Color.parseColor("#FFFFFF")) {
                        rating.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        rating.setTextColor(Color.parseColor("#0A84FF"))
                        ranking.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        ranking.setTextColor(Color.parseColor("#0A84FF"))
                        nearMe.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        nearMe.setTextColor(Color.parseColor("#0A84FF"))
                        priceLowToHigh.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceLowToHigh.setTextColor(Color.parseColor("#0A84FF"))
                        priceHighToLow.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceHighToLow.setTextColor(Color.parseColor("#0A84FF"))
                    } else {
                        rating.setBackgroundResource(R.drawable.user_btn_bg)
                        rating.setTextColor(Color.parseColor("#FFFFFF"))
                        ranking.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        ranking.setTextColor(Color.parseColor("#0A84FF"))
                        nearMe.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        nearMe.setTextColor(Color.parseColor("#0A84FF"))
                        priceLowToHigh.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceLowToHigh.setTextColor(Color.parseColor("#0A84FF"))
                        priceHighToLow.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceHighToLow.setTextColor(Color.parseColor("#0A84FF"))
                    }
                }
            }
            ranking.setOnClickListener {
                if (isProvider(this@SortAndFilterServiceProvider)) {
                    if (ranking.currentTextColor == Color.parseColor("#FFFFFF")) {
                        ranking.setBackgroundResource(R.drawable.purple_out_line)
                        ranking.setTextColor(Color.parseColor("#8D5FF5"))
                        rating.setBackgroundResource(R.drawable.purple_out_line)
                        rating.setTextColor(Color.parseColor("#8D5FF5"))
                        nearMe.setBackgroundResource(R.drawable.purple_out_line)
                        nearMe.setTextColor(Color.parseColor("#8D5FF5"))
                        priceLowToHigh.setBackgroundResource(R.drawable.purple_out_line)
                        priceLowToHigh.setTextColor(Color.parseColor("#8D5FF5"))
                        priceHighToLow.setBackgroundResource(R.drawable.purple_out_line)
                        priceHighToLow.setTextColor(Color.parseColor("#8D5FF5"))
                    } else {
                        ranking.setBackgroundResource(R.drawable.provider_btn_bg)
                        ranking.setTextColor(Color.parseColor("#FFFFFF"))
                        rating.setBackgroundResource(R.drawable.purple_out_line)
                        rating.setTextColor(Color.parseColor("#8D5FF5"))
                        nearMe.setBackgroundResource(R.drawable.purple_out_line)
                        nearMe.setTextColor(Color.parseColor("#8D5FF5"))
                        priceLowToHigh.setBackgroundResource(R.drawable.purple_out_line)
                        priceLowToHigh.setTextColor(Color.parseColor("#8D5FF5"))
                        priceHighToLow.setBackgroundResource(R.drawable.purple_out_line)
                        priceHighToLow.setTextColor(Color.parseColor("#8D5FF5"))
                    }
                } else {
                    if (ranking.currentTextColor == Color.parseColor("#FFFFFF")) {
                        ranking.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        ranking.setTextColor(Color.parseColor("#0A84FF"))
                        rating.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        rating.setTextColor(Color.parseColor("#0A84FF"))
                        nearMe.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        nearMe.setTextColor(Color.parseColor("#0A84FF"))
                        priceLowToHigh.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceLowToHigh.setTextColor(Color.parseColor("#0A84FF"))
                        priceHighToLow.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceHighToLow.setTextColor(Color.parseColor("#0A84FF"))
                    } else {
                        ranking.setBackgroundResource(R.drawable.user_btn_bg)
                        ranking.setTextColor(Color.parseColor("#FFFFFF"))
                        rating.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        rating.setTextColor(Color.parseColor("#0A84FF"))
                        nearMe.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        nearMe.setTextColor(Color.parseColor("#0A84FF"))
                        priceLowToHigh.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceLowToHigh.setTextColor(Color.parseColor("#0A84FF"))
                        priceHighToLow.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceHighToLow.setTextColor(Color.parseColor("#0A84FF"))
                    }
                }
            }
            nearMe.setOnClickListener {
                if (isProvider(this@SortAndFilterServiceProvider)) {
                    if (nearMe.currentTextColor == Color.parseColor("#FFFFFF")) {
                        nearMe.setBackgroundResource(R.drawable.purple_out_line)
                        nearMe.setTextColor(Color.parseColor("#8D5FF5"))
                        ranking.setBackgroundResource(R.drawable.purple_out_line)
                        ranking.setTextColor(Color.parseColor("#8D5FF5"))
                        rating.setBackgroundResource(R.drawable.purple_out_line)
                        rating.setTextColor(Color.parseColor("#8D5FF5"))
                        priceLowToHigh.setBackgroundResource(R.drawable.purple_out_line)
                        priceLowToHigh.setTextColor(Color.parseColor("#8D5FF5"))
                        priceHighToLow.setBackgroundResource(R.drawable.purple_out_line)
                        priceHighToLow.setTextColor(Color.parseColor("#8D5FF5"))
                    } else {
                        nearMe.setBackgroundResource(R.drawable.provider_btn_bg)
                        nearMe.setTextColor(Color.parseColor("#FFFFFF"))
                        ranking.setBackgroundResource(R.drawable.purple_out_line)
                        ranking.setTextColor(Color.parseColor("#8D5FF5"))
                        rating.setBackgroundResource(R.drawable.purple_out_line)
                        rating.setTextColor(Color.parseColor("#8D5FF5"))
                        priceLowToHigh.setBackgroundResource(R.drawable.purple_out_line)
                        priceLowToHigh.setTextColor(Color.parseColor("#8D5FF5"))
                        priceHighToLow.setBackgroundResource(R.drawable.purple_out_line)
                        priceHighToLow.setTextColor(Color.parseColor("#8D5FF5"))
                    }
                } else {
                    if (nearMe.currentTextColor == Color.parseColor("#FFFFFF")) {
                        nearMe.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        nearMe.setTextColor(Color.parseColor("#0A84FF"))
                        ranking.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        ranking.setTextColor(Color.parseColor("#0A84FF"))
                        rating.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        rating.setTextColor(Color.parseColor("#0A84FF"))
                        priceLowToHigh.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceLowToHigh.setTextColor(Color.parseColor("#0A84FF"))
                        priceHighToLow.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceHighToLow.setTextColor(Color.parseColor("#0A84FF"))
                    } else {
                        nearMe.setBackgroundResource(R.drawable.user_btn_bg)
                        nearMe.setTextColor(Color.parseColor("#FFFFFF"))
                        ranking.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        ranking.setTextColor(Color.parseColor("#0A84FF"))
                        rating.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        rating.setTextColor(Color.parseColor("#0A84FF"))
                        priceLowToHigh.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceLowToHigh.setTextColor(Color.parseColor("#0A84FF"))
                        priceHighToLow.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceHighToLow.setTextColor(Color.parseColor("#0A84FF"))
                    }
                }
            }

            priceLowToHigh.setOnClickListener {
                if (isProvider(this@SortAndFilterServiceProvider)) {
                    if (priceLowToHigh.currentTextColor == Color.parseColor("#FFFFFF")) {
                        priceLowToHigh.setBackgroundResource(R.drawable.purple_out_line)
                        priceLowToHigh.setTextColor(Color.parseColor("#0A84FF"))
                        priceHighToLow.setBackgroundResource(R.drawable.purple_out_line)
                        priceHighToLow.setTextColor(Color.parseColor("#0A84FF"))
                        nearMe.setBackgroundResource(R.drawable.purple_out_line)
                        nearMe.setTextColor(Color.parseColor("#8D5FF5"))
                        ranking.setBackgroundResource(R.drawable.purple_out_line)
                        ranking.setTextColor(Color.parseColor("#8D5FF5"))
                        rating.setBackgroundResource(R.drawable.purple_out_line)
                        rating.setTextColor(Color.parseColor("#8D5FF5"))
                    } else {
                        priceLowToHigh.setBackgroundResource(R.drawable.provider_btn_bg)
                        priceLowToHigh.setTextColor(Color.parseColor("#FFFFFF"))
                        priceHighToLow.setBackgroundResource(R.drawable.purple_out_line)
                        priceHighToLow.setTextColor(Color.parseColor("#8D5FF5"))
                        nearMe.setBackgroundResource(R.drawable.purple_out_line)
                        nearMe.setTextColor(Color.parseColor("#8D5FF5"))
                        ranking.setBackgroundResource(R.drawable.purple_out_line)
                        ranking.setTextColor(Color.parseColor("#8D5FF5"))
                        rating.setBackgroundResource(R.drawable.purple_out_line)
                        rating.setTextColor(Color.parseColor("#8D5FF5"))
                    }
                } else {
                    if (priceLowToHigh.currentTextColor == Color.parseColor("#FFFFFF")) {
                        priceLowToHigh.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceLowToHigh.setTextColor(Color.parseColor("#0A84FF"))
                        priceHighToLow.setBackgroundResource(R.drawable.user_btn_bg)
                        priceHighToLow.setTextColor(Color.parseColor("#FFFFFF"))
                        nearMe.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        nearMe.setTextColor(Color.parseColor("#0A84FF"))
                        ranking.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        ranking.setTextColor(Color.parseColor("#0A84FF"))
                        rating.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        rating.setTextColor(Color.parseColor("#0A84FF"))
                    } else {
                        priceLowToHigh.setBackgroundResource(R.drawable.user_btn_bg)
                        priceLowToHigh.setTextColor(Color.parseColor("#FFFFFF"))
                        priceHighToLow.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceHighToLow.setTextColor(Color.parseColor("#0A84FF"))
                        nearMe.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        nearMe.setTextColor(Color.parseColor("#0A84FF"))
                        ranking.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        ranking.setTextColor(Color.parseColor("#0A84FF"))
                        rating.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        rating.setTextColor(Color.parseColor("#0A84FF"))
                    }
                }
            }
            priceHighToLow.setOnClickListener {
                if (isProvider(this@SortAndFilterServiceProvider)) {
                    if (priceHighToLow.currentTextColor == Color.parseColor("#FFFFFF")) {
                        priceHighToLow.setBackgroundResource(R.drawable.purple_out_line)
                        priceHighToLow.setTextColor(Color.parseColor("#0A84FF"))
                        priceLowToHigh.setBackgroundResource(R.drawable.purple_out_line)
                        priceLowToHigh.setTextColor(Color.parseColor("#0A84FF"))
                        nearMe.setBackgroundResource(R.drawable.purple_out_line)
                        nearMe.setTextColor(Color.parseColor("#8D5FF5"))
                        ranking.setBackgroundResource(R.drawable.purple_out_line)
                        ranking.setTextColor(Color.parseColor("#8D5FF5"))
                        rating.setBackgroundResource(R.drawable.purple_out_line)
                        rating.setTextColor(Color.parseColor("#8D5FF5"))
                    } else {
                        priceHighToLow.setBackgroundResource(R.drawable.provider_btn_bg)
                        priceHighToLow.setTextColor(Color.parseColor("#FFFFFF"))
                        priceLowToHigh.setBackgroundResource(R.drawable.purple_out_line)
                        priceLowToHigh.setTextColor(Color.parseColor("#0A84FF"))
                        nearMe.setBackgroundResource(R.drawable.purple_out_line)
                        nearMe.setTextColor(Color.parseColor("#8D5FF5"))
                        ranking.setBackgroundResource(R.drawable.purple_out_line)
                        ranking.setTextColor(Color.parseColor("#8D5FF5"))
                        rating.setBackgroundResource(R.drawable.purple_out_line)
                        rating.setTextColor(Color.parseColor("#8D5FF5"))
                    }
                } else {
                    if (priceHighToLow.currentTextColor == Color.parseColor("#FFFFFF")) {
                        priceHighToLow.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceHighToLow.setTextColor(Color.parseColor("#0A84FF"))
                        priceLowToHigh.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceLowToHigh.setTextColor(Color.parseColor("#0A84FF"))
                        nearMe.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        nearMe.setTextColor(Color.parseColor("#0A84FF"))
                        ranking.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        ranking.setTextColor(Color.parseColor("#0A84FF"))
                        rating.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        rating.setTextColor(Color.parseColor("#0A84FF"))
                    } else {
                        priceHighToLow.setBackgroundResource(R.drawable.user_btn_bg)
                        priceHighToLow.setTextColor(Color.parseColor("#FFFFFF"))
                        priceLowToHigh.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceLowToHigh.setTextColor(Color.parseColor("#0A84FF"))
                        nearMe.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        nearMe.setTextColor(Color.parseColor("#0A84FF"))
                        ranking.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        ranking.setTextColor(Color.parseColor("#0A84FF"))
                        rating.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        rating.setTextColor(Color.parseColor("#0A84FF"))
                    }
                }
            }
            fresher.setOnClickListener {
                if (isProvider(this@SortAndFilterServiceProvider)) {
                    if (fresher.currentTextColor == Color.parseColor("#FFFFFF")) {
                        fresher.setBackgroundResource(R.drawable.purple_out_line)
                        fresher.setTextColor(Color.parseColor("#0A84FF"))
                        experience.setBackgroundResource(R.drawable.purple_out_line)
                        experience.setTextColor(Color.parseColor("#0A84FF"))
                        any.setBackgroundResource(R.drawable.purple_out_line)
                        any.setTextColor(Color.parseColor("#0A84FF"))
                    } else {
                        fresher.setBackgroundResource(R.drawable.provider_btn_bg)
                        fresher.setTextColor(Color.parseColor("#FFFFFF"))
                        experience.setBackgroundResource(R.drawable.purple_out_line)
                        experience.setTextColor(Color.parseColor("#0A84FF"))
                        any.setBackgroundResource(R.drawable.purple_out_line)
                        any.setTextColor(Color.parseColor("#0A84FF"))
                    }
                } else {
                    if (fresher.currentTextColor == Color.parseColor("#FFFFFF")) {
                        fresher.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        fresher.setTextColor(Color.parseColor("#0A84FF"))
                        experience.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        experience.setTextColor(Color.parseColor("#0A84FF"))
                        any.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        any.setTextColor(Color.parseColor("#0A84FF"))
                    } else {
                        fresher.setBackgroundResource(R.drawable.user_btn_bg)
                        fresher.setTextColor(Color.parseColor("#FFFFFF"))
                        experience.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        experience.setTextColor(Color.parseColor("#0A84FF"))
                        any.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        any.setTextColor(Color.parseColor("#0A84FF"))
                    }
                }
            }
            experience.setOnClickListener {
                if (isProvider(this@SortAndFilterServiceProvider)) {
                    if (experience.currentTextColor == Color.parseColor("#FFFFFF")) {
                        experience.setBackgroundResource(R.drawable.purple_out_line)
                        experience.setTextColor(Color.parseColor("#0A84FF"))
                        fresher.setBackgroundResource(R.drawable.purple_out_line)
                        fresher.setTextColor(Color.parseColor("#0A84FF"))
                        any.setBackgroundResource(R.drawable.purple_out_line)
                        any.setTextColor(Color.parseColor("#0A84FF"))
                    } else {
                        experience.setBackgroundResource(R.drawable.provider_btn_bg)
                        experience.setTextColor(Color.parseColor("#FFFFFF"))
                        fresher.setBackgroundResource(R.drawable.purple_out_line)
                        fresher.setTextColor(Color.parseColor("#0A84FF"))
                        any.setBackgroundResource(R.drawable.purple_out_line)
                        any.setTextColor(Color.parseColor("#0A84FF"))
                    }
                } else {
                    if (experience.currentTextColor == Color.parseColor("#FFFFFF")) {
                        experience.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        experience.setTextColor(Color.parseColor("#0A84FF"))
                        fresher.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        fresher.setTextColor(Color.parseColor("#0A84FF"))
                        any.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        any.setTextColor(Color.parseColor("#0A84FF"))
                    } else {
                        experience.setBackgroundResource(R.drawable.user_btn_bg)
                        experience.setTextColor(Color.parseColor("#FFFFFF"))
                        fresher.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        fresher.setTextColor(Color.parseColor("#0A84FF"))
                        any.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        any.setTextColor(Color.parseColor("#0A84FF"))
                    }
                }
            }
            any.setOnClickListener {
                if (isProvider(this@SortAndFilterServiceProvider)) {
                    if (any.currentTextColor == Color.parseColor("#FFFFFF")) {
                        any.setBackgroundResource(R.drawable.purple_out_line)
                        any.setTextColor(Color.parseColor("#0A84FF"))
                        fresher.setBackgroundResource(R.drawable.purple_out_line)
                        fresher.setTextColor(Color.parseColor("#0A84FF"))
                        experience.setBackgroundResource(R.drawable.purple_out_line)
                        experience.setTextColor(Color.parseColor("#0A84FF"))
                    } else {
                        any.setBackgroundResource(R.drawable.provider_btn_bg)
                        any.setTextColor(Color.parseColor("#FFFFFF"))
                        fresher.setBackgroundResource(R.drawable.purple_out_line)
                        fresher.setTextColor(Color.parseColor("#0A84FF"))
                        experience.setBackgroundResource(R.drawable.purple_out_line)
                        experience.setTextColor(Color.parseColor("#0A84FF"))
                    }
                } else {
                    if (any.currentTextColor == Color.parseColor("#FFFFFF")) {
                        any.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        any.setTextColor(Color.parseColor("#0A84FF"))
                        fresher.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        fresher.setTextColor(Color.parseColor("#0A84FF"))
                        experience.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        experience.setTextColor(Color.parseColor("#0A84FF"))
                    } else {
                        any.setBackgroundResource(R.drawable.user_btn_bg)
                        any.setTextColor(Color.parseColor("#FFFFFF"))
                        fresher.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        fresher.setTextColor(Color.parseColor("#0A84FF"))
                        experience.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        experience.setTextColor(Color.parseColor("#0A84FF"))
                    }
                }
            }

            priceRange.addOnChangeListener { slider, value, fromUser ->
                binding.amountFrom.setText("Rs. ${slider.values[0].toInt()} /-")
                binding.amountTo.setText("Rs. ${slider.values[1].toInt()} /-")
            }

            if (UserUtils.getSearchFilter(this@SortAndFilterServiceProvider).isNotEmpty()) {
                val filter = Gson().fromJson(
                    UserUtils.getSearchFilter(this@SortAndFilterServiceProvider),
                    SearchFilterModel::class.java
                )
                if (filter.rating) {
                    rating.setBackgroundResource(R.drawable.user_btn_bg)
                    rating.setTextColor(Color.parseColor("#ffffff"))
                }
                if (filter.ranking) {
                    ranking.setBackgroundResource(R.drawable.user_btn_bg)
                    ranking.setTextColor(Color.parseColor("#ffffff"))
                }
                if (filter.nearMe) {
                    nearMe.setBackgroundResource(R.drawable.user_btn_bg)
                    nearMe.setTextColor(Color.parseColor("#ffffff"))
                }
                if (filter.lowToHigh) {
                    priceLowToHigh.setBackgroundResource(R.drawable.user_btn_bg)
                    priceLowToHigh.setTextColor(Color.parseColor("#ffffff"))
                }
                if (filter.highToLow) {
                    priceHighToLow.setBackgroundResource(R.drawable.user_btn_bg)
                    priceHighToLow.setTextColor(Color.parseColor("#ffffff"))
                }
                if (filter.fresher) {
                    fresher.setBackgroundResource(R.drawable.user_btn_bg)
                    fresher.setTextColor(Color.parseColor("#ffffff"))
                }
                if (filter.experience) {
                    experience.setBackgroundResource(R.drawable.user_btn_bg)
                    experience.setTextColor(Color.parseColor("#ffffff"))
                }
                if (filter.any) {
                    any.setBackgroundResource(R.drawable.user_btn_bg)
                    any.setTextColor(Color.parseColor("#ffffff"))
                }
                amountFrom.setText("Rs. ${filter.priceRangeFrom} /-")
                amountTo.setText("Rs. ${filter.priceRangeTo} /-")
                kms.text = "${filter.distance} Kms"
            }

            clearAll.setOnClickListener {
                UserUtils.saveSearchFilter(this@SortAndFilterServiceProvider, "")
                onBackPressed()
            }

            applyBtn.setOnClickListener {
                var ratingValue = false
                var rankingValue = false
                var nearMeValue = false
                var lowToHighValue = false
                var highToLowValue = false
                var fresherValue = false
                var experienceValue = false
                var anyValue = false
                var distanceValue = ""
                var priceRangeFromValue = ""
                var priceRangeToValue = ""

                if (rating.currentTextColor == Color.parseColor("#FFFFFF")) {
                    ratingValue = true
                }
                if (ranking.currentTextColor == Color.parseColor("#FFFFFF")) {
                    rankingValue = true
                }
                if (nearMe.currentTextColor == Color.parseColor("#FFFFFF")) {
                    nearMeValue = true
                }
                if (priceLowToHigh.currentTextColor == Color.parseColor("#FFFFFF")) {
                    lowToHighValue = true
                }
                if (priceHighToLow.currentTextColor == Color.parseColor("#FFFFFF")) {
                    highToLowValue = true
                }
                if (fresher.currentTextColor == Color.parseColor("#FFFFFF")) {
                    fresherValue = true
                }
                if (experience.currentTextColor == Color.parseColor("#FFFFFF")) {
                    experienceValue = true
                }
                if (any.currentTextColor == Color.parseColor("#FFFFFF")) {
                    anyValue = true
                }
                distanceValue = binding.kms.text.toString().split(" ")[0]
                priceRangeFromValue = binding.amountFrom.text.toString().split(" ")[1]
                priceRangeToValue = binding.amountTo.text.toString().split(" ")[1]
                val filter = SearchFilterModel(
                    ratingValue,
                    rankingValue,
                    nearMeValue,
                    lowToHighValue,
                    highToLowValue,
                    fresherValue,
                    experienceValue,
                    anyValue,
                    distanceValue,
                    priceRangeFromValue,
                    priceRangeToValue
                )
                UserUtils.saveSearchFilter(this@SortAndFilterServiceProvider, Gson().toJson(filter))
                finish()
                if (isProvider(this@SortAndFilterServiceProvider)) {
                    startActivity(
                        Intent(
                            this@SortAndFilterServiceProvider,
                            ProviderMyBidsScreen::class.java
                        )
                    )
                } else {
                    SearchServiceProvidersScreen.FROM_POPULAR_SERVICES = false
                    startActivity(
                        Intent(
                            this@SortAndFilterServiceProvider,
                            SearchServiceProvidersScreen::class.java
                        )
                    )
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
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.sort_filter)
        val imageView = toolBar.findViewById<ImageView>(R.id.toolBarImage)
        imageView.visibility = View.GONE

        if (isProvider(this@SortAndFilterServiceProvider)) {
            toolBar.setBackgroundResource(R.color.purple_500)
            binding.rating.setBackgroundResource(R.drawable.purple_out_line)
            binding.rating.setTextColor(resources.getColor(R.color.purple_500))
            binding.ranking.setBackgroundResource(R.drawable.purple_out_line)
            binding.ranking.setTextColor(resources.getColor(R.color.purple_500))
            binding.nearMe.setBackgroundResource(R.drawable.purple_out_line)
            binding.nearMe.setTextColor(resources.getColor(R.color.purple_500))
            binding.priceLowToHigh.setBackgroundResource(R.drawable.purple_out_line)
            binding.priceLowToHigh.setTextColor(resources.getColor(R.color.purple_500))
            binding.priceHighToLow.setBackgroundResource(R.drawable.purple_out_line)
            binding.priceHighToLow.setTextColor(resources.getColor(R.color.purple_500))
            binding.fresher.setBackgroundResource(R.drawable.purple_out_line)
            binding.experience.setBackgroundResource(R.drawable.purple_out_line)
            binding.any.setBackgroundResource(R.drawable.purple_out_line)
            binding.resetBtn.setBackgroundResource(R.drawable.purple_out_line)
            binding.resetBtn.setTextColor(resources.getColor(R.color.purple_500))
            binding.applyBtn.setBackgroundResource(R.drawable.provider_btn_bg)
            binding.applyBtn.setTextColor(resources.getColor(R.color.white))
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, SearchServiceProvidersScreen::class.java))
    }

}