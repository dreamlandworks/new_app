package com.satrango.ui.auth.provider_signup

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.satrango.R
import com.satrango.databinding.ActivityProviderSignUpThreeBinding
import java.util.*

class ProviderSignUpThree : AppCompatActivity() {

    private lateinit var binding: ActivityProviderSignUpThreeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderSignUpThreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            val toolBar = findViewById<View>(R.id.toolBar)
            toolBar.findViewById<ImageView>(R.id.toolBarBackBtn)
                .setOnClickListener { onBackPressed() }
            toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
                .setOnClickListener { onBackPressed() }
            toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.profile)

            addSlot.setOnClickListener { addView() }

            nextBtn.setOnClickListener {
                startActivity(
                    Intent(
                        this@ProviderSignUpThree,
                        ProviderSignUpFour::class.java
                    )
                )
            }

            btnCancel.setOnClickListener { onBackPressed() }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun addView() {
        val cricketerView: View = layoutInflater.inflate(R.layout.row_slot_item, null, false)
        val txt_from = cricketerView.findViewById<TextInputEditText>(R.id.fromDate)
        val txt_to = cricketerView.findViewById<TextInputEditText>(R.id.toDate)
        val btn_everyday = cricketerView.findViewById<TextView>(R.id.btn_everyday)
        val btn_weekday = cricketerView.findViewById<TextView>(R.id.btn_weekday)
        val btn_wekend = cricketerView.findViewById<TextView>(R.id.btn_wekend)
        val sunday = cricketerView.findViewById<TextView>(R.id.sunday)
        val monday = cricketerView.findViewById<TextView>(R.id.monday)
        val tuesday = cricketerView.findViewById<TextView>(R.id.tuesday)
        val wednesday = cricketerView.findViewById<TextView>(R.id.wednesday)
        val thursday = cricketerView.findViewById<TextView>(R.id.thursday)
        val friday = cricketerView.findViewById<TextView>(R.id.friday)
        val saturday = cricketerView.findViewById<TextView>(R.id.saturday)

        val imageClose = cricketerView.findViewById<View>(R.id.image_remove) as ImageView
        imageClose.setOnClickListener { removeView(cricketerView) }

        btn_everyday.setOnClickListener {
            btn_everyday.setBackgroundResource(R.drawable.greenbutton)
            btn_everyday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            btn_weekday.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))
            btn_weekday.setBackgroundResource(R.drawable.greenborderbutton)
            btn_wekend.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))
            btn_wekend.setBackgroundResource(R.drawable.greenborderbutton)

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
        btn_weekday.setOnClickListener {
            btn_weekday.setBackgroundResource(R.drawable.greenbutton)
            btn_weekday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            btn_everyday.setBackgroundResource(R.drawable.greenborderbutton)
            btn_everyday.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))
            btn_wekend.setBackgroundResource(R.drawable.greenborderbutton)
            btn_wekend.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))

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

            saturday.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))
            saturday.setBackgroundResource(R.drawable.greenborderbutton)
            sunday.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))
            sunday.setBackgroundResource(R.drawable.greenborderbutton)
        }
        btn_wekend.setOnClickListener {
            btn_wekend.setBackgroundResource(R.drawable.greenbutton)
            btn_wekend.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            btn_weekday.setBackgroundResource(R.drawable.greenborderbutton)
            btn_weekday.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))
            btn_everyday.setBackgroundResource(R.drawable.greenborderbutton)
            btn_everyday.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))

            monday.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))
            monday.setBackgroundResource(R.drawable.greenborderbutton)
            tuesday.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))
            tuesday.setBackgroundResource(R.drawable.greenborderbutton)
            wednesday.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))
            wednesday.setBackgroundResource(R.drawable.greenborderbutton)
            thursday.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))
            thursday.setBackgroundResource(R.drawable.greenborderbutton)
            friday.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))
            friday.setBackgroundResource(R.drawable.greenborderbutton)

            saturday.setBackgroundResource(R.drawable.greenbutton)
            saturday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            sunday.setBackgroundResource(R.drawable.greenbutton)
            sunday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        }
        txt_from.setOnClickListener {
            openDatePickerDialog(txt_from)
        }
        txt_to.setOnClickListener {
            openDatePickerDialog(txt_to)
        }

        sunday.setOnClickListener {
            if (sunday.currentTextColor == Color.WHITE) {
                sunday.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))
                sunday.setBackgroundResource(R.drawable.greenborderbutton)
            } else {
                sunday.setBackgroundResource(R.drawable.greenbutton)
                sunday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
        }
        monday.setOnClickListener {
            if (monday.currentTextColor == Color.WHITE) {
                monday.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))
                monday.setBackgroundResource(R.drawable.greenborderbutton)
            } else {
                monday.setBackgroundResource(R.drawable.greenbutton)
                monday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
        }
        tuesday.setOnClickListener {
            if (tuesday.currentTextColor == Color.WHITE) {
                tuesday.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))
                tuesday.setBackgroundResource(R.drawable.greenborderbutton)
            } else {
                tuesday.setBackgroundResource(R.drawable.greenbutton)
                tuesday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
        }
        wednesday.setOnClickListener {
            if (wednesday.currentTextColor == Color.WHITE) {
                wednesday.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))
                wednesday.setBackgroundResource(R.drawable.greenborderbutton)
            } else {
                wednesday.setBackgroundResource(R.drawable.greenbutton)
                wednesday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
        }
        thursday.setOnClickListener {
            if (thursday.currentTextColor == Color.WHITE) {
                thursday.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))
                thursday.setBackgroundResource(R.drawable.greenborderbutton)
            } else {
                thursday.setBackgroundResource(R.drawable.greenbutton)
                thursday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
        }
        friday.setOnClickListener {
            if (friday.currentTextColor == Color.WHITE) {
                friday.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))
                friday.setBackgroundResource(R.drawable.greenborderbutton)
            } else {
                friday.setBackgroundResource(R.drawable.greenbutton)
                friday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
        }
        saturday.setOnClickListener {
            if (saturday.currentTextColor == Color.WHITE) {
                saturday.setTextColor(ContextCompat.getColor(applicationContext, R.color.vendorPrimaryColor))
                saturday.setBackgroundResource(R.drawable.greenborderbutton)
            } else {
                saturday.setBackgroundResource(R.drawable.greenbutton)
                saturday.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
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


}