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


class SortAndFilterServiceProvider : AppCompatActivity() {

    companion object {
        var FROM_PROVIDER = false
    }

    private lateinit var binding: ActivitySortAndFilterServiceProviderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySortAndFilterServiceProviderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()

        binding.apply {

            resetBtn.setOnClickListener {
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
                if (FROM_PROVIDER) {
                    if (rating.currentTextColor == Color.parseColor("#FFFFFF")) {
                        rating.setBackgroundResource(R.drawable.purple_out_line)
                        rating.setTextColor(Color.parseColor("#0A84FF"))
                    } else {
                        rating.setBackgroundResource(R.drawable.provider_btn_bg)
                        rating.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                } else {
                    if (rating.currentTextColor == Color.parseColor("#FFFFFF")) {
                        rating.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        rating.setTextColor(Color.parseColor("#0A84FF"))
                    } else {
                        rating.setBackgroundResource(R.drawable.user_btn_bg)
                        rating.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                }
            }
            ranking.setOnClickListener {
                if (FROM_PROVIDER) {
                    if (ranking.currentTextColor == Color.parseColor("#FFFFFF")) {
                        ranking.setBackgroundResource(R.drawable.purple_out_line)
                        ranking.setTextColor(Color.parseColor("#0A84FF"))
                    } else {
                        ranking.setBackgroundResource(R.drawable.provider_btn_bg)
                        ranking.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                } else {
                    if (ranking.currentTextColor == Color.parseColor("#FFFFFF")) {
                        ranking.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        ranking.setTextColor(Color.parseColor("#0A84FF"))
                    } else {
                        ranking.setBackgroundResource(R.drawable.user_btn_bg)
                        ranking.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                }
            }
            nearMe.setOnClickListener {
                if (FROM_PROVIDER) {
                    if (nearMe.currentTextColor == Color.parseColor("#FFFFFF")) {
                        nearMe.setBackgroundResource(R.drawable.purple_out_line)
                        nearMe.setTextColor(Color.parseColor("#0A84FF"))
                    } else {
                        nearMe.setBackgroundResource(R.drawable.provider_btn_bg)
                        nearMe.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                } else {
                    if (nearMe.currentTextColor == Color.parseColor("#FFFFFF")) {
                        nearMe.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        nearMe.setTextColor(Color.parseColor("#0A84FF"))
                    } else {
                        nearMe.setBackgroundResource(R.drawable.user_btn_bg)
                        nearMe.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                }
            }

            priceLowToHigh.setOnClickListener {
                if (FROM_PROVIDER) {
                    if (priceLowToHigh.currentTextColor == Color.parseColor("#FFFFFF")) {
                        priceLowToHigh.setBackgroundResource(R.drawable.purple_out_line)
                        priceLowToHigh.setTextColor(Color.parseColor("#0A84FF"))
                        priceHighToLow.setBackgroundResource(R.drawable.provider_btn_bg)
                        priceHighToLow.setTextColor(Color.parseColor("#FFFFFF"))
                    } else {
                        priceLowToHigh.setBackgroundResource(R.drawable.provider_btn_bg)
                        priceLowToHigh.setTextColor(Color.parseColor("#FFFFFF"))
                        priceHighToLow.setBackgroundResource(R.drawable.purple_out_line)
                        priceHighToLow.setTextColor(Color.parseColor("#0A84FF"))
                    }
                } else {
                    if (priceLowToHigh.currentTextColor == Color.parseColor("#FFFFFF")) {
                        priceLowToHigh.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceLowToHigh.setTextColor(Color.parseColor("#0A84FF"))
                        priceHighToLow.setBackgroundResource(R.drawable.user_btn_bg)
                        priceHighToLow.setTextColor(Color.parseColor("#FFFFFF"))
                    } else {
                        priceLowToHigh.setBackgroundResource(R.drawable.user_btn_bg)
                        priceLowToHigh.setTextColor(Color.parseColor("#FFFFFF"))
                        priceHighToLow.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceHighToLow.setTextColor(Color.parseColor("#0A84FF"))
                    }
                }
            }
            priceHighToLow.setOnClickListener {
                if (FROM_PROVIDER) {
                    if (priceHighToLow.currentTextColor == Color.parseColor("#FFFFFF")) {
                        priceHighToLow.setBackgroundResource(R.drawable.purple_out_line)
                        priceHighToLow.setTextColor(Color.parseColor("#0A84FF"))
                        priceLowToHigh.setBackgroundResource(R.drawable.provider_btn_bg)
                        priceLowToHigh.setTextColor(Color.parseColor("#FFFFFF"))
                    } else {
                        priceHighToLow.setBackgroundResource(R.drawable.provider_btn_bg)
                        priceHighToLow.setTextColor(Color.parseColor("#FFFFFF"))
                        priceLowToHigh.setBackgroundResource(R.drawable.purple_out_line)
                        priceLowToHigh.setTextColor(Color.parseColor("#0A84FF"))
                    }
                } else {
                    if (priceHighToLow.currentTextColor == Color.parseColor("#FFFFFF")) {
                        priceHighToLow.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceHighToLow.setTextColor(Color.parseColor("#0A84FF"))
                        priceLowToHigh.setBackgroundResource(R.drawable.user_btn_bg)
                        priceLowToHigh.setTextColor(Color.parseColor("#FFFFFF"))
                    } else {
                        priceHighToLow.setBackgroundResource(R.drawable.user_btn_bg)
                        priceHighToLow.setTextColor(Color.parseColor("#FFFFFF"))
                        priceLowToHigh.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                        priceLowToHigh.setTextColor(Color.parseColor("#0A84FF"))
                    }
                }
            }
            fresher.setOnClickListener {
                if (FROM_PROVIDER) {
                    if (fresher.currentTextColor == Color.parseColor("#FFFFFF")) {
                        fresher.setBackgroundResource(R.drawable.purple_out_line)
                        fresher.setTextColor(Color.parseColor("#0A84FF"))
                        experience.setBackgroundResource(R.drawable.provider_btn_bg)
                        experience.setTextColor(Color.parseColor("#FFFFFF"))
                        any.setBackgroundResource(R.drawable.provider_btn_bg)
                        any.setTextColor(Color.parseColor("#FFFFFF"))
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
                        experience.setBackgroundResource(R.drawable.user_btn_bg)
                        experience.setTextColor(Color.parseColor("#FFFFFF"))
                        any.setBackgroundResource(R.drawable.user_btn_bg)
                        any.setTextColor(Color.parseColor("#FFFFFF"))
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
                if (FROM_PROVIDER) {
                    if (experience.currentTextColor == Color.parseColor("#FFFFFF")) {
                        experience.setBackgroundResource(R.drawable.purple_out_line)
                        experience.setTextColor(Color.parseColor("#0A84FF"))
                        fresher.setBackgroundResource(R.drawable.provider_btn_bg)
                        fresher.setTextColor(Color.parseColor("#FFFFFF"))
                        any.setBackgroundResource(R.drawable.provider_btn_bg)
                        any.setTextColor(Color.parseColor("#FFFFFF"))
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
                        fresher.setBackgroundResource(R.drawable.user_btn_bg)
                        fresher.setTextColor(Color.parseColor("#FFFFFF"))
                        any.setBackgroundResource(R.drawable.user_btn_bg)
                        any.setTextColor(Color.parseColor("#FFFFFF"))
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
                if (FROM_PROVIDER) {
                    if (any.currentTextColor == Color.parseColor("#FFFFFF")) {
                        any.setBackgroundResource(R.drawable.purple_out_line)
                        any.setTextColor(Color.parseColor("#0A84FF"))
                        fresher.setBackgroundResource(R.drawable.provider_btn_bg)
                        fresher.setTextColor(Color.parseColor("#FFFFFF"))
                        experience.setBackgroundResource(R.drawable.provider_btn_bg)
                        experience.setTextColor(Color.parseColor("#FFFFFF"))
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
                        fresher.setBackgroundResource(R.drawable.user_btn_bg)
                        fresher.setTextColor(Color.parseColor("#FFFFFF"))
                        experience.setBackgroundResource(R.drawable.user_btn_bg)
                        experience.setTextColor(Color.parseColor("#FFFFFF"))
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
                binding.amountFrom.text = slider.values[0].toString()
                binding.amountTo.text = slider.values[1].toString()
            }

            if (UserUtils.getSearchFilter(this@SortAndFilterServiceProvider).isNotEmpty() && UserUtils.getSelectedSPDetails(this@SortAndFilterServiceProvider).isNotEmpty()) {
                val filter = Gson().fromJson(UserUtils.getSearchFilter(this@SortAndFilterServiceProvider), SearchFilterModel::class.java)
                if (filter.rating) {
                    rating.setBackgroundResource(R.drawable.user_btn_bg)
                    rating.setTextColor(Color.parseColor("#ffffff"))
                }
                if (filter.ranking) {
                    rating.setBackgroundResource(R.drawable.user_btn_bg)
                    rating.setTextColor(Color.parseColor("#ffffff"))
                }
                if (filter.nearMe) {
                    rating.setBackgroundResource(R.drawable.user_btn_bg)
                    rating.setTextColor(Color.parseColor("#ffffff"))
                }
                if (filter.lowToHigh) {
                    rating.setBackgroundResource(R.drawable.user_btn_bg)
                    rating.setTextColor(Color.parseColor("#ffffff"))
                }
                if (filter.highToLow) {
                    rating.setBackgroundResource(R.drawable.user_btn_bg)
                    rating.setTextColor(Color.parseColor("#ffffff"))
                }
                if (filter.fresher) {
                    rating.setBackgroundResource(R.drawable.user_btn_bg)
                    rating.setTextColor(Color.parseColor("#ffffff"))
                }
                if (filter.experience) {
                    rating.setBackgroundResource(R.drawable.user_btn_bg)
                    rating.setTextColor(Color.parseColor("#ffffff"))
                }
                if (filter.any) {
                    rating.setBackgroundResource(R.drawable.user_btn_bg)
                    rating.setTextColor(Color.parseColor("#ffffff"))
                }


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
                priceRangeFromValue = binding.amountFrom.text.toString()
                priceRangeToValue = binding.amountTo.text.toString()
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
                if (FROM_PROVIDER) {
                    startActivity(Intent(this@SortAndFilterServiceProvider, ProviderMyBidsScreen::class.java))
                } else {
                    SearchServiceProvidersScreen.FROM_POPULAR_SERVICES = false
                    startActivity(Intent(this@SortAndFilterServiceProvider, SearchServiceProvidersScreen::class.java))
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
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.sort_filter)
        val imageView = toolBar.findViewById<ImageView>(R.id.toolBarImage)
        imageView.visibility = View.GONE

        if (FROM_PROVIDER) {
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

}