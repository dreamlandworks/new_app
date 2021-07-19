package com.satrango.ui.auth.provider_signup.provider_sign_up_three

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.satrango.R
import com.satrango.databinding.ActivityProviderSignUpThreeBinding
import com.satrango.ui.auth.provider_signup.ProviderSignUpFour
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
                Log.e("SLOTS DATA", validateSlots())
                startActivity(Intent(this@ProviderSignUpThree, ProviderSignUpFour::class.java))
            }

            btnCancel.setOnClickListener { onBackPressed() }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun addView() {
        val cricketerView: View = layoutInflater.inflate(R.layout.row_slot_item, null, false)

        val fromDate = cricketerView.findViewById<TextInputEditText>(R.id.fromDate)
        val toDate = cricketerView.findViewById<TextInputEditText>(R.id.toDate)
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
            weekDays.setBackgroundResource(R.drawable.greenborderbutton)
            weekEnds.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            weekEnds.setBackgroundResource(R.drawable.greenborderbutton)

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
            everyDay.setBackgroundResource(R.drawable.greenborderbutton)
            everyDay.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            weekEnds.setBackgroundResource(R.drawable.greenborderbutton)
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
            saturday.setBackgroundResource(R.drawable.greenborderbutton)
            sunday.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            sunday.setBackgroundResource(R.drawable.greenborderbutton)
        }
        weekEnds.setOnClickListener {
            weekEnds.setBackgroundResource(R.drawable.greenbutton)
            weekEnds.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekDays.setBackgroundResource(R.drawable.greenborderbutton)
            weekDays.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            everyDay.setBackgroundResource(R.drawable.greenborderbutton)
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
            monday.setBackgroundResource(R.drawable.greenborderbutton)
            tuesday.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            tuesday.setBackgroundResource(R.drawable.greenborderbutton)
            wednesday.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            wednesday.setBackgroundResource(R.drawable.greenborderbutton)
            thursday.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            thursday.setBackgroundResource(R.drawable.greenborderbutton)
            friday.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.vendorPrimaryColor
                )
            )
            friday.setBackgroundResource(R.drawable.greenborderbutton)

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
                sunday.setBackgroundResource(R.drawable.greenborderbutton)
            } else {
                sunday.setBackgroundResource(R.drawable.greenbutton)
                sunday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.greenborderbutton)
            everyDay.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekDays.setBackgroundResource(R.drawable.greenborderbutton)
            weekDays.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekEnds.setBackgroundResource(R.drawable.greenborderbutton)
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
                monday.setBackgroundResource(R.drawable.greenborderbutton)
            } else {
                monday.setBackgroundResource(R.drawable.greenbutton)
                monday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.greenborderbutton)
            everyDay.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekDays.setBackgroundResource(R.drawable.greenborderbutton)
            weekDays.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekEnds.setBackgroundResource(R.drawable.greenborderbutton)
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
                tuesday.setBackgroundResource(R.drawable.greenborderbutton)
            } else {
                tuesday.setBackgroundResource(R.drawable.greenbutton)
                tuesday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.greenborderbutton)
            everyDay.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekDays.setBackgroundResource(R.drawable.greenborderbutton)
            weekDays.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekEnds.setBackgroundResource(R.drawable.greenborderbutton)
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
                wednesday.setBackgroundResource(R.drawable.greenborderbutton)
            } else {
                wednesday.setBackgroundResource(R.drawable.greenbutton)
                wednesday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.greenborderbutton)
            everyDay.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekDays.setBackgroundResource(R.drawable.greenborderbutton)
            weekDays.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekEnds.setBackgroundResource(R.drawable.greenborderbutton)
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
                thursday.setBackgroundResource(R.drawable.greenborderbutton)
            } else {
                thursday.setBackgroundResource(R.drawable.greenbutton)
                thursday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.greenborderbutton)
            everyDay.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekDays.setBackgroundResource(R.drawable.greenborderbutton)
            weekDays.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekEnds.setBackgroundResource(R.drawable.greenborderbutton)
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
                friday.setBackgroundResource(R.drawable.greenborderbutton)
            } else {
                friday.setBackgroundResource(R.drawable.greenbutton)
                friday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.greenborderbutton)
            everyDay.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekDays.setBackgroundResource(R.drawable.greenborderbutton)
            weekDays.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekEnds.setBackgroundResource(R.drawable.greenborderbutton)
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
                saturday.setBackgroundResource(R.drawable.greenborderbutton)
            } else {
                saturday.setBackgroundResource(R.drawable.greenbutton)
                saturday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.greenborderbutton)
            everyDay.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekDays.setBackgroundResource(R.drawable.greenborderbutton)
            weekDays.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            weekEnds.setBackgroundResource(R.drawable.greenborderbutton)
            weekEnds.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        }
        binding.layoutList.addView(cricketerView)
    }

    @SuppressLint("SetTextI18n")
    private fun openDatePickerDialog(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)
        val calendarMinute = calendar.get(Calendar.MINUTE)
        val timepickerdialog = TimePickerDialog(
            this@ProviderSignUpThree,
            { view, hourOfDay, minute ->
                var format = ""
                var hour = hourOfDay
                if (hour == 0) {
                    hour += 12
                    format = "AM"
                } else if (hourOfDay == 12) {
                    format = "PM"
                } else if (hourOfDay > 12) {
                    hour -= 12
                    format = "PM"
                } else {
                    format = "AM"
                }
                editText.setText("$hour:$minute$format")
            }, calendarHour, calendarMinute, false
        )
        timepickerdialog.show()
    }

    private fun removeView(view: View) {
        binding.layoutList.removeView(view)
    }

    private fun validateSlots(): String {

        val slotList = ArrayList<ProviderTimeSlotModel>()

        for (i in 0 until binding.layoutList.childCount) {
            val slotView = binding.layoutList.getChildAt(i)

            val fromDate = slotView.findViewById<TextInputEditText>(R.id.fromDate)
            val toDate = slotView.findViewById<TextInputEditText>(R.id.toDate)

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
            var selectedFromDate = ""
            var selectedToDate = ""

            if (fromDate.text.toString().isNotEmpty()) {
                selectedFromDate = fromDate.text.toString().trim()
            }
            if (toDate.text.toString().isNotEmpty()) {
                selectedToDate = toDate.text.toString().trim()
            }
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
                daysList.add("Sunday")
            }
            if (monday.currentTextColor == Color.WHITE) {
                daysList.add("Monday")
            }
            if (tuesday.currentTextColor == Color.WHITE) {
                daysList.add("Tuesday")
            }
            if (wednesday.currentTextColor == Color.WHITE) {
                daysList.add("Wednesday")
            }
            if (thursday.currentTextColor == Color.WHITE) {
                daysList.add("Thursday")
            }
            if (friday.currentTextColor == Color.WHITE) {
                daysList.add("Friday")
            }
            if (saturday.currentTextColor == Color.WHITE) {
                daysList.add("Saturday")
            }

            slotList.add(ProviderTimeSlotModel(selectedFromDate, selectedToDate, dayType, daysList))
        }

        return Gson().toJson(slotList)
    }


}