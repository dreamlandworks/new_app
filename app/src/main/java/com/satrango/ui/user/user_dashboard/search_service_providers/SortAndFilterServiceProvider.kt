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
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchFilterModel
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.utils.UserUtils


class SortAndFilterServiceProvider : AppCompatActivity() {

    private lateinit var binding: ActivitySortAndFilterServiceProviderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySortAndFilterServiceProviderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        val backTextBtn = toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
        backTextBtn.text = resources.getString(R.string.back)
        backTextBtn.setOnClickListener { onBackPressed() }
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.sort_filter)
        val imageView = toolBar.findViewById<ImageView>(R.id.toolBarImage)
        imageView.visibility = View.GONE

        binding.apply {

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
                if (rating.currentTextColor == Color.parseColor("#FFFFFF")) {
                    rating.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                    rating.setTextColor(Color.parseColor("#0A84FF"))
                } else {
                    rating.setBackgroundResource(R.drawable.user_btn_bg)
                    rating.setTextColor(Color.parseColor("#FFFFFF"))
                }
            }
            ranking.setOnClickListener {
                if (ranking.currentTextColor == Color.parseColor("#FFFFFF")) {
                    ranking.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                    ranking.setTextColor(Color.parseColor("#0A84FF"))
                } else {
                    ranking.setBackgroundResource(R.drawable.user_btn_bg)
                    ranking.setTextColor(Color.parseColor("#FFFFFF"))
                }
            }
            nearMe.setOnClickListener {
                if (nearMe.currentTextColor == Color.parseColor("#FFFFFF")) {
                    nearMe.setBackgroundResource(R.drawable.btn_bg_sm_blue_border)
                    nearMe.setTextColor(Color.parseColor("#0A84FF"))
                } else {
                    nearMe.setBackgroundResource(R.drawable.user_btn_bg)
                    nearMe.setTextColor(Color.parseColor("#FFFFFF"))
                }
            }

            priceLowToHigh.setOnClickListener {
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
            priceHighToLow.setOnClickListener {
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
            fresher.setOnClickListener {
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
            experience.setOnClickListener {
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
            any.setOnClickListener {
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

            priceRange.addOnChangeListener { slider, value, fromUser ->
                binding.amountFrom.text = slider.values[0].toString()
                binding.amountTo.text = slider.values[1].toString()
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
                val filter = SearchFilterModel(ratingValue, rankingValue, nearMeValue, lowToHighValue, highToLowValue, fresherValue, experienceValue, anyValue, distanceValue, priceRangeFromValue, priceRangeToValue)
                UserUtils.saveSearchFilter(this@SortAndFilterServiceProvider, Gson().toJson(filter))
                finish()
                startActivity(Intent(this@SortAndFilterServiceProvider, SearchServiceProvidersScreen::class.java))
            }
        }
    }

}