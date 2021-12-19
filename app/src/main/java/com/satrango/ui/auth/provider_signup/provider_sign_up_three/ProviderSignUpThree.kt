package com.satrango.ui.auth.provider_signup.provider_sign_up_three

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderSignUpThreeBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.provider_signup.provider_sign_up_five.ProviderSignUpFive
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.ProviderSignUpFour
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.ProviderSignUpFourRepository
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.ProviderSignUpFourViewModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.ProviderSignUpFourReqModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.TimeslotResponse
import com.satrango.utils.ProviderUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.*

class ProviderSignUpThree : AppCompatActivity() {

    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var experienceList: ArrayList<String>
    private lateinit var binding: ActivityProviderSignUpThreeBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderSignUpThreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeProgressDialog()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.purple_700)
        }

        binding.apply {

            if (ProviderUtils.profession!!.isNotEmpty()) {
                tariffOneText.text = "My Tariff for ${ProviderUtils.profession!![0].name} is"
            }
            if (ProviderUtils.profession!!.size > 1) {
                tariffTwoText.text = "My Tariff for ${ProviderUtils.profession!![1].name} is"
            } else {
                tariffTwoLayout.visibility = View.GONE
                tariffThreeLayout.visibility = View.GONE
            }
            if (ProviderUtils.profession!!.size > 2) {
                tariffThreeText.text = "My Tariff for ${ProviderUtils.profession!![2].name} is"
            } else {
                tariffThreeLayout.visibility = View.GONE
            }

            addSlot.setOnClickListener { addView() }

            nextBtn.setOnClickListener {

                if (ProviderUtils.profession!!.isNotEmpty()) {
                    if (experienceOne.selectedItemPosition != 0) {
                        for (exp in ProviderUtils.experience!!) {
                            if (exp.exp == experienceOne.selectedItem.toString()) {
                                ProviderUtils.profession!![0].experience = exp.id
                            }
                        }

                    }
                    if (perHourOne.text.toString().trim().isNotEmpty()) {
                        ProviderUtils.profession!![0].tariff_per_hour = perHourOne.text.toString().trim()
                    }
                    if (perDayOne.text.toString().trim().isNotEmpty()) {
                        ProviderUtils.profession!![0].tariff_per_day = perDayOne.text.toString().trim()
                    }
                    if (minChargeOne.text.toString().trim().isNotEmpty()) {
                        ProviderUtils.profession!![0].tariff_min_charges = minChargeOne.text.toString().trim()
                    }
                    if (extraChargeOne.text.toString().trim().isNotEmpty()) {
                        ProviderUtils.profession!![0].tariff_extra_charges = extraChargeOne.text.toString().trim()
                    }
                }

                if (ProviderUtils.profession!!.size > 1) {
                    if (experienceTwo.selectedItemPosition != 0) {
                        for (exp in ProviderUtils.experience!!) {
                            if (exp.exp == experienceTwo.selectedItem.toString()) {
                                ProviderUtils.profession!![1].experience = exp.id
                            }
                        }
                    }
                    if (perHourTwo.text.toString().trim().isNotEmpty()) {
                        ProviderUtils.profession!![1].tariff_per_hour = perHourTwo.text.toString().trim()
                    }
                    if (perDayTwo.text.toString().trim().isNotEmpty()) {
                        ProviderUtils.profession!![1].tariff_per_day = perDayTwo.text.toString().trim()
                    }
                    if (minChargeTwo.text.toString().trim().isNotEmpty()) {
                        ProviderUtils.profession!![1].tariff_min_charges = minChargeTwo.text.toString().trim()
                    }
                    if (extraChargeTwo.text.toString().trim().isNotEmpty()) {
                        ProviderUtils.profession!![1].tariff_extra_charges = extraChargeTwo.text.toString().trim()
                    }
                }

                if (ProviderUtils.profession!!.size > 2) {
                    if (experienceThree.selectedItemPosition != 0) {
                        for (exp in ProviderUtils.experience!!) {
                            if (exp.exp == experienceThree.selectedItem.toString()) {
                                ProviderUtils.profession!![2].experience = exp.id
                            }
                        }
                    }
                    if (perHourThree.text.toString().trim().isNotEmpty()) {
                        ProviderUtils.profession!![2].tariff_per_hour = perHourThree.text.toString().trim()
                    }
                    if (perDayThree.text.toString().trim().isNotEmpty()) {
                        ProviderUtils.profession!![2].tariff_per_day = perDayThree.text.toString().trim()
                    }
                    if (minChargeThree.text.toString().trim().isNotEmpty()) {
                        ProviderUtils.profession!![2].tariff_min_charges = minChargeThree.text.toString().trim()
                    }
                    if (extraChargeThree.text.toString().trim().isNotEmpty()) {
                        ProviderUtils.profession!![2].tariff_extra_charges = extraChargeThree.text.toString().trim()
                    }
                }

                for (keyword in ProviderUtils.profession!!) {
                    if (keyword.tariff_extra_charges.isEmpty()) {
                        snackBar(nextBtn, "Please Enter Tariff Extra Charges for ${keyword.name} profession")
                        return@setOnClickListener
                    }
                    if (keyword.tariff_min_charges.isEmpty()) {
                        snackBar(nextBtn, "Please Enter Tariff Minimum Charges for ${keyword.name} profession")
                        return@setOnClickListener
                    }
                    if (keyword.tariff_per_day.isEmpty()) {
                        snackBar(nextBtn, "Please Enter Tariff Per Day for ${keyword.name} profession")
                        return@setOnClickListener
                    }
                    if (keyword.tariff_per_hour.isEmpty()) {
                        snackBar(nextBtn, "Please Enter Tariff Per Hour for ${keyword.name} profession")
                        return@setOnClickListener
                    }
                    if (keyword.tariff_per_day < keyword.tariff_per_hour) {
                        snackBar(nextBtn, "Please Enter Per Hour charges less than Per Day Charges For ${keyword.name} Profession")
                        return@setOnClickListener
                    }
                    if (keyword.experience.isEmpty()) {
                        snackBar(nextBtn, "Please Enter Tariff Experience for ${keyword.name} profession")
                        return@setOnClickListener
                    }
                }

                Log.e("TIMESLOTS:", validateSlots().toString())
                Log.e("PROFESSION:", Gson().toJson(ProviderUtils.profession!!))
                if (validateSlots().isEmpty()) {
                    snackBar(nextBtn, "Please select timeslots")
                    return@setOnClickListener
                }

                ProviderUtils.slotsList = validateSlots()
                submitActivationDetailsToServer()
            }

            backBtn.setOnClickListener { onBackPressed() }

            experienceList = ArrayList<String>()
            experienceList.add("Select Experience")
            for (data in ProviderUtils.experience!!) {
                experienceList.add(data.exp)
            }
            val experienceAdapter = ArrayAdapter(this@ProviderSignUpThree, android.R.layout.simple_spinner_dropdown_item, experienceList)
            experienceOne.adapter = experienceAdapter
            experienceTwo.adapter = experienceAdapter
            experienceThree.adapter = experienceAdapter
        }

    }

    private fun submitActivationDetailsToServer() {
        val requestBody = ProviderSignUpFourReqModel(
            ProviderUtils.aboutMe,
            RetrofitBuilder.PROVIDER_KEY,
            ProviderUtils.languagesKnown!!,
            ProviderUtils.profession!!,
            ProviderUtils.qualification!!,
            ProviderUtils.slotsList!!,
            UserUtils.getUserId(this)
        )
        Log.e("JSON", Gson().toJson(requestBody))
        val factory = ViewModelFactory(ProviderSignUpFourRepository())
        val viewModel = ViewModelProvider(this, factory)[ProviderSignUpFourViewModel::class.java]
        viewModel.providerActivation(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    toast(this, it.data!!)
                    startActivity(Intent(this@ProviderSignUpThree, ProviderSignUpFour::class.java))

                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.message!!)
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun addView() {
        val cricketerView: View = layoutInflater.inflate(R.layout.row_slot_item, null, false)

        val fromDate = cricketerView.findViewById<TextInputEditText>(R.id.fromTime)
        val toDate = cricketerView.findViewById<TextInputEditText>(R.id.toTime)
        val everyDay = cricketerView.findViewById<TextView>(R.id.everyDay)
        val weekDays = cricketerView.findViewById<TextView>(R.id.weekDays)
        val weekEnds = cricketerView.findViewById<TextView>(R.id.weekEnd)
        val sunday = cricketerView.findViewById<TextView>(R.id.sunday)
        val monday = cricketerView.findViewById<TextView>(R.id.monday)
        val tuesday = cricketerView.findViewById<TextView>(R.id.tuesday)
        val wednesday = cricketerView.findViewById<TextView>(R.id.wednesday)
        val thursday = cricketerView.findViewById<TextView>(R.id.thursday)
        val friday = cricketerView.findViewById<TextView>(R.id.friday)
        val saturday = cricketerView.findViewById<TextView>(R.id.saturday)

        val imageClose = cricketerView.findViewById<View>(R.id.image_remove) as ImageView
        imageClose.setOnClickListener { removeView(cricketerView) }

        fromDate.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                openDatePickerDialog(fromDate)
            }
        }
        toDate.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                openDatePickerDialog(toDate)
            }
        }

        everyDay.setOnClickListener {
            everyDay.setBackgroundResource(R.drawable.provider_btn_bg)
            everyDay.setTextColor(ContextCompat.getColor(this, R.color.white))
            weekDays.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purple_500
                )
            )
            weekDays.setBackgroundResource(R.drawable.purple_out_line)
            weekEnds.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purple_500
                )
            )
            weekEnds.setBackgroundResource(R.drawable.purple_out_line)

            sunday.setBackgroundResource(R.drawable.provider_btn_bg)
            sunday.setTextColor(ContextCompat.getColor(this, R.color.white))
            monday.setBackgroundResource(R.drawable.provider_btn_bg)
            monday.setTextColor(ContextCompat.getColor(this, R.color.white))
            tuesday.setBackgroundResource(R.drawable.provider_btn_bg)
            tuesday.setTextColor(ContextCompat.getColor(this, R.color.white))
            wednesday.setBackgroundResource(R.drawable.provider_btn_bg)
            wednesday.setTextColor(ContextCompat.getColor(this, R.color.white))
            thursday.setBackgroundResource(R.drawable.provider_btn_bg)
            thursday.setTextColor(ContextCompat.getColor(this, R.color.white))
            friday.setBackgroundResource(R.drawable.provider_btn_bg)
            friday.setTextColor(ContextCompat.getColor(this, R.color.white))
            saturday.setBackgroundResource(R.drawable.provider_btn_bg)
            saturday.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
        weekDays.setOnClickListener {
            weekDays.setBackgroundResource(R.drawable.provider_btn_bg)
            weekDays.setTextColor(ContextCompat.getColor(this, R.color.white))
            everyDay.setBackgroundResource(R.drawable.purple_out_line)
            everyDay.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purple_500
                )
            )
            weekEnds.setBackgroundResource(R.drawable.purple_out_line)
            weekEnds.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purple_500
                )
            )

            monday.setBackgroundResource(R.drawable.provider_btn_bg)
            monday.setTextColor(ContextCompat.getColor(this, R.color.white))
            tuesday.setBackgroundResource(R.drawable.provider_btn_bg)
            tuesday.setTextColor(ContextCompat.getColor(this, R.color.white))
            wednesday.setBackgroundResource(R.drawable.provider_btn_bg)
            wednesday.setTextColor(ContextCompat.getColor(this, R.color.white))
            thursday.setBackgroundResource(R.drawable.provider_btn_bg)
            thursday.setTextColor(ContextCompat.getColor(this, R.color.white))
            friday.setBackgroundResource(R.drawable.provider_btn_bg)
            friday.setTextColor(ContextCompat.getColor(this, R.color.white))

            saturday.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purple_500
                )
            )
            saturday.setBackgroundResource(R.drawable.purple_out_line)
            sunday.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purple_500
                )
            )
            sunday.setBackgroundResource(R.drawable.purple_out_line)
        }
        weekEnds.setOnClickListener {
            weekEnds.setBackgroundResource(R.drawable.provider_btn_bg)
            weekEnds.setTextColor(ContextCompat.getColor(this, R.color.white))
            weekDays.setBackgroundResource(R.drawable.purple_out_line)
            weekDays.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purple_500
                )
            )
            everyDay.setBackgroundResource(R.drawable.purple_out_line)
            everyDay.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purple_500
                )
            )

            monday.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purple_500
                )
            )
            monday.setBackgroundResource(R.drawable.purple_out_line)
            tuesday.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purple_500
                )
            )
            tuesday.setBackgroundResource(R.drawable.purple_out_line)
            wednesday.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purple_500
                )
            )
            wednesday.setBackgroundResource(R.drawable.purple_out_line)
            thursday.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purple_500
                )
            )
            thursday.setBackgroundResource(R.drawable.purple_out_line)
            friday.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purple_500
                )
            )
            friday.setBackgroundResource(R.drawable.purple_out_line)

            saturday.setBackgroundResource(R.drawable.provider_btn_bg)
            saturday.setTextColor(ContextCompat.getColor(this, R.color.white))
            sunday.setBackgroundResource(R.drawable.provider_btn_bg)
            sunday.setTextColor(ContextCompat.getColor(this, R.color.white))
        }

        sunday.setOnClickListener {
            if (sunday.currentTextColor == Color.WHITE) {
                sunday.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.purple_500
                    )
                )
                sunday.setBackgroundResource(R.drawable.purple_out_line)
            } else {
                sunday.setBackgroundResource(R.drawable.provider_btn_bg)
                sunday.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purple_out_line)
            everyDay.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
            weekDays.setBackgroundResource(R.drawable.purple_out_line)
            weekDays.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
            weekEnds.setBackgroundResource(R.drawable.purple_out_line)
            weekEnds.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
        }
        monday.setOnClickListener {
            if (monday.currentTextColor == Color.WHITE) {
                monday.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.purple_500
                    )
                )
                monday.setBackgroundResource(R.drawable.purple_out_line)
            } else {
                monday.setBackgroundResource(R.drawable.provider_btn_bg)
                monday.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purple_out_line)
            everyDay.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
            weekDays.setBackgroundResource(R.drawable.purple_out_line)
            weekDays.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
            weekEnds.setBackgroundResource(R.drawable.purple_out_line)
            weekEnds.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
        }
        tuesday.setOnClickListener {
            if (tuesday.currentTextColor == Color.WHITE) {
                tuesday.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.purple_500
                    )
                )
                tuesday.setBackgroundResource(R.drawable.purple_out_line)
            } else {
                tuesday.setBackgroundResource(R.drawable.provider_btn_bg)
                tuesday.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purple_out_line)
            everyDay.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
            weekDays.setBackgroundResource(R.drawable.purple_out_line)
            weekDays.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
            weekEnds.setBackgroundResource(R.drawable.purple_out_line)
            weekEnds.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
        }
        wednesday.setOnClickListener {
            if (wednesday.currentTextColor == Color.WHITE) {
                wednesday.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.purple_500
                    )
                )
                wednesday.setBackgroundResource(R.drawable.purple_out_line)
            } else {
                wednesday.setBackgroundResource(R.drawable.provider_btn_bg)
                wednesday.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purple_out_line)
            everyDay.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
            weekDays.setBackgroundResource(R.drawable.purple_out_line)
            weekDays.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
            weekEnds.setBackgroundResource(R.drawable.purple_out_line)
            weekEnds.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
        }
        thursday.setOnClickListener {
            if (thursday.currentTextColor == Color.WHITE) {
                thursday.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.purple_500
                    )
                )
                thursday.setBackgroundResource(R.drawable.purple_out_line)
            } else {
                thursday.setBackgroundResource(R.drawable.provider_btn_bg)
                thursday.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purple_out_line)
            everyDay.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
            weekDays.setBackgroundResource(R.drawable.purple_out_line)
            weekDays.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
            weekEnds.setBackgroundResource(R.drawable.purple_out_line)
            weekEnds.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
        }
        friday.setOnClickListener {
            if (friday.currentTextColor == Color.WHITE) {
                friday.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.purple_500
                    )
                )
                friday.setBackgroundResource(R.drawable.purple_out_line)
            } else {
                friday.setBackgroundResource(R.drawable.provider_btn_bg)
                friday.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purple_out_line)
            everyDay.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
            weekDays.setBackgroundResource(R.drawable.purple_out_line)
            weekDays.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
            weekEnds.setBackgroundResource(R.drawable.purple_out_line)
            weekEnds.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
        }
        saturday.setOnClickListener {
            if (saturday.currentTextColor == Color.WHITE) {
                saturday.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.purple_500
                    )
                )
                saturday.setBackgroundResource(R.drawable.purple_out_line)
            } else {
                saturday.setBackgroundResource(R.drawable.provider_btn_bg)
                saturday.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purple_out_line)
            everyDay.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
            weekDays.setBackgroundResource(R.drawable.purple_out_line)
            weekDays.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
            weekEnds.setBackgroundResource(R.drawable.purple_out_line)
            weekEnds.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
        }
        binding.layoutList.addView(cricketerView)
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun openDatePickerDialog(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)
        val calendarMinute = calendar.get(Calendar.MINUTE)
        val timepickerdialog = TimePickerDialog(
            this@ProviderSignUpThree, AlertDialog.THEME_HOLO_LIGHT,
            { view, hourOfDay, minute ->
                editText.setText("${java.lang.String.format("%02d", hourOfDay)}:${java.lang.String.format("%02d", minute)}")
            }, calendarHour, calendarMinute, false)
        timepickerdialog.show()
    }

    private fun removeView(view: View) {
        binding.layoutList.removeView(view)
    }

    private fun validateSlots(): List<TimeslotResponse> {

        val slotList = ArrayList<TimeslotResponse>()

        for (i in 0 until binding.layoutList.childCount) {
            val slotView = binding.layoutList.getChildAt(i)

            val fromTime = slotView.findViewById<TextInputEditText>(R.id.fromTime)
            val toTime = slotView.findViewById<TextInputEditText>(R.id.toTime)

            val everyDay = slotView.findViewById<TextView>(R.id.everyDay)
            val weekDays = slotView.findViewById<TextView>(R.id.weekDays)
            val weekEnds = slotView.findViewById<TextView>(R.id.weekEnd)

            val sunday = slotView.findViewById<TextView>(R.id.sunday)
            val monday = slotView.findViewById<TextView>(R.id.monday)
            val tuesday = slotView.findViewById<TextView>(R.id.tuesday)
            val wednesday = slotView.findViewById<TextView>(R.id.wednesday)
            val thursday = slotView.findViewById<TextView>(R.id.thursday)
            val friday = slotView.findViewById<TextView>(R.id.friday)
            val saturday = slotView.findViewById<TextView>(R.id.saturday)

            var dayType = ""
            var selectedFromTime = ""
            var selectedToTime = ""

            if (fromTime.text.toString().isNotEmpty()) {
                selectedFromTime = fromTime.text.toString().trim()
            } else {
                toast(this, "Select From Time")
                return emptyList()
            }
            if (toTime.text.toString().isNotEmpty()) {
                selectedToTime = toTime.text.toString().trim()
            } else {
                toast(this, "Select To Time")
                return emptyList()
            }

            var from = selectedFromTime.split(":")[0].toInt()
            from += from
            val to = selectedToTime.split(":")[0].toInt()
            for (time in from..to) {
                val daysList = ArrayList<String>()

                if (everyDay.currentTextColor == Color.WHITE) {
                    dayType = "EveryDay"
                }
                if (weekDays.currentTextColor == Color.WHITE) {
                    dayType = "WeekDays"
                }
                if (weekEnds.currentTextColor == Color.WHITE) {
                    dayType = "WeekEnds"
                }
                if (sunday.currentTextColor == Color.WHITE) {
                    daysList.add("1")
                }
                if (monday.currentTextColor == Color.WHITE) {
                    daysList.add("2")
                }
                if (tuesday.currentTextColor == Color.WHITE) {
                    daysList.add("3")
                }
                if (wednesday.currentTextColor == Color.WHITE) {
                    daysList.add("4")
                }
                if (thursday.currentTextColor == Color.WHITE) {
                    daysList.add("5")
                }
                if (friday.currentTextColor == Color.WHITE) {
                    daysList.add("6")
                }
                if (saturday.currentTextColor == Color.WHITE) {
                    daysList.add("7")
                }
                if (daysList.isEmpty()) {
                    toast(this, "Select Days")
                    return emptyList()
                } else {
                    var timeOneText = ""
                    var timeTwoText = ""
                    timeOneText = if (time < 10) {
                        "0$time"
                    } else {
                        "$time"
                    }
                    val nextTime = time + 1
                    if (nextTime < 10) {
                        timeTwoText = "0$nextTime"
                    } else {
                        timeTwoText = "$nextTime"
                    }
                    slotList.add(TimeslotResponse(TextUtils.join(",", daysList), "$timeOneText:00:00", "$timeTwoText:00:00"))
                }
            }
        }
        Log.e("VALIDATE:", slotList.toString())
        return slotList
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(
            this,
            BeautifulProgressDialog.withGIF,
            resources.getString(R.string.loading)
        )
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.purple_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

}