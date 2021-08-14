package com.satrango.ui.auth.provider_signup.provider_sign_up_three

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.satrango.R
import com.satrango.databinding.ActivityProviderSignUpThreeBinding
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.ProviderSignUpFour
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.TimeslotResponse
import com.satrango.utils.ProviderUtils
import com.satrango.utils.snackBar
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList

class ProviderSignUpThree : AppCompatActivity() {

    private lateinit var binding: ActivityProviderSignUpThreeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderSignUpThreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            addSlot.setOnClickListener { addView() }

            nextBtn.setOnClickListener {
                validateFields()
            }

            backBtn.setOnClickListener { onBackPressed() }
        }

    }

    private fun validateFields() {
        when {
            binding.perHour.text.toString().isEmpty() -> {
                snackBar(binding.nextBtn, "Enter Per Hour Charges")
            }
            binding.perDay.text.toString().isEmpty() -> {
                snackBar(binding.nextBtn, "Enter Per Day Charges")
            }
            binding.minCharge.text.toString().isEmpty() -> {
                snackBar(binding.nextBtn, "Enter Minimum Charges")
            }
            binding.extraCharge.text.toString().isEmpty() -> {
                snackBar(binding.nextBtn, "Enter Extra Charges")
            }
            JSONArray(validateSlots()).length() == 0 -> {
                snackBar(binding.nextBtn, "Add Slots")
            }
            else -> {
                ProviderUtils.perDay = binding.perDay.text.toString().trim()
                ProviderUtils.perHour = binding.perHour.text.toString().trim()
                ProviderUtils.minCharge = binding.minCharge.text.toString().trim()
                ProviderUtils.extraCharge = binding.extraCharge.text.toString().trim()
                ProviderUtils.slotsList = validateSlots()
                startActivity(Intent(this@ProviderSignUpThree, ProviderSignUpFour::class.java))

            }
        }
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
            everyDay.setBackgroundResource(R.drawable.greenbutton)
            everyDay.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekDays.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            weekDays.setBackgroundResource(R.drawable.purpleborderbutton)
            weekEnds.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            weekEnds.setBackgroundResource(R.drawable.purpleborderbutton)

            sunday.setBackgroundResource(R.drawable.greenbutton)
            sunday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            monday.setBackgroundResource(R.drawable.greenbutton)
            monday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            tuesday.setBackgroundResource(R.drawable.greenbutton)
            tuesday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            wednesday.setBackgroundResource(R.drawable.greenbutton)
            wednesday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            thursday.setBackgroundResource(R.drawable.greenbutton)
            thursday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            friday.setBackgroundResource(R.drawable.greenbutton)
            friday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            saturday.setBackgroundResource(R.drawable.greenbutton)
            saturday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        }
        weekDays.setOnClickListener {
            weekDays.setBackgroundResource(R.drawable.greenbutton)
            weekDays.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            everyDay.setBackgroundResource(R.drawable.purpleborderbutton)
            everyDay.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            weekEnds.setBackgroundResource(R.drawable.purpleborderbutton)
            weekEnds.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )

            monday.setBackgroundResource(R.drawable.greenbutton)
            monday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            tuesday.setBackgroundResource(R.drawable.greenbutton)
            tuesday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            wednesday.setBackgroundResource(R.drawable.greenbutton)
            wednesday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            thursday.setBackgroundResource(R.drawable.greenbutton)
            thursday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            friday.setBackgroundResource(R.drawable.greenbutton)
            friday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))

            saturday.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            saturday.setBackgroundResource(R.drawable.purpleborderbutton)
            sunday.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            sunday.setBackgroundResource(R.drawable.purpleborderbutton)
        }
        weekEnds.setOnClickListener {
            weekEnds.setBackgroundResource(R.drawable.greenbutton)
            weekEnds.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekDays.setBackgroundResource(R.drawable.purpleborderbutton)
            weekDays.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            everyDay.setBackgroundResource(R.drawable.purpleborderbutton)
            everyDay.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )

            monday.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            monday.setBackgroundResource(R.drawable.purpleborderbutton)
            tuesday.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            tuesday.setBackgroundResource(R.drawable.purpleborderbutton)
            wednesday.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            wednesday.setBackgroundResource(R.drawable.purpleborderbutton)
            thursday.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            thursday.setBackgroundResource(R.drawable.purpleborderbutton)
            friday.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            friday.setBackgroundResource(R.drawable.purpleborderbutton)

            saturday.setBackgroundResource(R.drawable.greenbutton)
            saturday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            sunday.setBackgroundResource(R.drawable.greenbutton)
            sunday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        }

        sunday.setOnClickListener {
            if (sunday.currentTextColor == Color.WHITE) {
                sunday.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.vendorPrimaryColor
                    )
                )
                sunday.setBackgroundResource(R.drawable.purpleborderbutton)
            } else {
                sunday.setBackgroundResource(R.drawable.greenbutton)
                sunday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purpleborderbutton)
            everyDay.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekDays.setBackgroundResource(R.drawable.purpleborderbutton)
            weekDays.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekEnds.setBackgroundResource(R.drawable.purpleborderbutton)
            weekEnds.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        }
        monday.setOnClickListener {
            if (monday.currentTextColor == Color.WHITE) {
                monday.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.vendorPrimaryColor
                    )
                )
                monday.setBackgroundResource(R.drawable.purpleborderbutton)
            } else {
                monday.setBackgroundResource(R.drawable.greenbutton)
                monday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purpleborderbutton)
            everyDay.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekDays.setBackgroundResource(R.drawable.purpleborderbutton)
            weekDays.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekEnds.setBackgroundResource(R.drawable.purpleborderbutton)
            weekEnds.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        }
        tuesday.setOnClickListener {
            if (tuesday.currentTextColor == Color.WHITE) {
                tuesday.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.vendorPrimaryColor
                    )
                )
                tuesday.setBackgroundResource(R.drawable.purpleborderbutton)
            } else {
                tuesday.setBackgroundResource(R.drawable.greenbutton)
                tuesday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purpleborderbutton)
            everyDay.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekDays.setBackgroundResource(R.drawable.purpleborderbutton)
            weekDays.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekEnds.setBackgroundResource(R.drawable.purpleborderbutton)
            weekEnds.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        }
        wednesday.setOnClickListener {
            if (wednesday.currentTextColor == Color.WHITE) {
                wednesday.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.vendorPrimaryColor
                    )
                )
                wednesday.setBackgroundResource(R.drawable.purpleborderbutton)
            } else {
                wednesday.setBackgroundResource(R.drawable.greenbutton)
                wednesday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purpleborderbutton)
            everyDay.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekDays.setBackgroundResource(R.drawable.purpleborderbutton)
            weekDays.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekEnds.setBackgroundResource(R.drawable.purpleborderbutton)
            weekEnds.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        }
        thursday.setOnClickListener {
            if (thursday.currentTextColor == Color.WHITE) {
                thursday.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.vendorPrimaryColor
                    )
                )
                thursday.setBackgroundResource(R.drawable.purpleborderbutton)
            } else {
                thursday.setBackgroundResource(R.drawable.greenbutton)
                thursday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purpleborderbutton)
            everyDay.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekDays.setBackgroundResource(R.drawable.purpleborderbutton)
            weekDays.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekEnds.setBackgroundResource(R.drawable.purpleborderbutton)
            weekEnds.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        }
        friday.setOnClickListener {
            if (friday.currentTextColor == Color.WHITE) {
                friday.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.vendorPrimaryColor
                    )
                )
                friday.setBackgroundResource(R.drawable.purpleborderbutton)
            } else {
                friday.setBackgroundResource(R.drawable.greenbutton)
                friday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purpleborderbutton)
            everyDay.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekDays.setBackgroundResource(R.drawable.purpleborderbutton)
            weekDays.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekEnds.setBackgroundResource(R.drawable.purpleborderbutton)
            weekEnds.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        }
        saturday.setOnClickListener {
            if (saturday.currentTextColor == Color.WHITE) {
                saturday.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.vendorPrimaryColor
                    )
                )
                saturday.setBackgroundResource(R.drawable.purpleborderbutton)
            } else {
                saturday.setBackgroundResource(R.drawable.greenbutton)
                saturday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purpleborderbutton)
            everyDay.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekDays.setBackgroundResource(R.drawable.purpleborderbutton)
            weekDays.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekEnds.setBackgroundResource(R.drawable.purpleborderbutton)
            weekEnds.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        }
        binding.layoutList.addView(cricketerView)
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun openDatePickerDialog(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)
        val calendarMinute = calendar.get(Calendar.MINUTE)
        val timepickerdialog = TimePickerDialog(
            this@ProviderSignUpThree,
            { view, hourOfDay, minute ->
//                var format = ""
//                var hour = hourOfDay
//                if (hour == 0) {
//                    hour += 12
//                    format = "AM"
//                } else if (hourOfDay == 12) {
//                    format = "PM"
//                } else if (hourOfDay > 12) {
//                    hour -= 12
//                    format = "PM"
//                } else {
//                    format = "AM"
//                }
//                editText.setText("${java.lang.String.format("%02d", hour)}:${java.lang.String.format("%02d", minute)}$format")
                editText.setText("${java.lang.String.format("%02d", hourOfDay)}:${java.lang.String.format("%02d", minute)}")
            }, calendarHour, calendarMinute, false
        )
        timepickerdialog.show()
    }

    private fun removeView(view: View) {
        binding.layoutList.removeView(view)
    }

    private fun validateSlots(): ArrayList<TimeslotResponse> {

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

            val daysList = ArrayList<String>()
            var dayType = ""
            var selectedFromTime = ""
            var selectedToTime = ""

            if (fromTime.text.toString().isNotEmpty()) {
                selectedFromTime = fromTime.text.toString().trim()
            }
            if (toTime.text.toString().isNotEmpty()) {
                selectedToTime = toTime.text.toString().trim()
            }

            val from = selectedFromTime.split(":")[0].toInt()
            val to = selectedToTime.split(":")[0].toInt()

            for (time in from..to) {
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

                slotList.add(
                    TimeslotResponse(TextUtils.join(",", daysList), "$from:${selectedFromTime.split(":")[1]}:${selectedFromTime.split(":")[2]}", "${from + 1}:${selectedFromTime.split(":")[1]}:${selectedFromTime.split(":")[2]}"))
            }


        }

        return slotList
    }


}