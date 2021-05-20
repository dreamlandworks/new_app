package com.satrango.ui.auth.provider_signup

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.satrango.R
import com.satrango.databinding.ActivityProviderSignUpOneBinding
import java.util.*

class ProviderSignUpOne : AppCompatActivity() {

    private lateinit var binding: ActivityProviderSignUpOneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderSignUpOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.profile)

        binding.apply {

            nextBtn.setOnClickListener {
                startActivity(
                    Intent(
                        this@ProviderSignUpOne,
                        ProviderSignUpTwo::class.java
                    )
                )
            }

            cancelBtn.setOnClickListener { onBackPressed() }

            dateOfBirth.setOnClickListener { openDateOfBirthDialog() }

            maleImg.setOnClickListener {
                maleImg.setBackgroundColor(Color.parseColor("#003286"))
                femaleImg.setBackgroundColor(Color.parseColor("#ffffff"))
                transGenderImg.setBackgroundColor(Color.parseColor("#ffffff"))
            }
            femaleImg.setOnClickListener {
                femaleImg.setBackgroundColor(Color.parseColor("#003286"))
                maleImg.setBackgroundColor(Color.parseColor("#ffffff"))
                transGenderImg.setBackgroundColor(Color.parseColor("#ffffff"))
            }
            transGenderImg.setOnClickListener {
                transGenderImg.setBackgroundColor(Color.parseColor("#003286"))
                maleImg.setBackgroundColor(Color.parseColor("#ffffff"))
                femaleImg.setBackgroundColor(Color.parseColor("#ffffff"))
            }

        }

    }

    @SuppressLint("SetTextI18n")
    private fun openDateOfBirthDialog() {
        val c = Calendar.getInstance()

        val mYear = c[Calendar.YEAR]
        val mMonth = c[Calendar.MONTH]
        val mDay = c[Calendar.DAY_OF_MONTH]

        val datePickerDialog = DatePickerDialog(
            this@ProviderSignUpOne, { _, year, monthOfYear, dayOfMonth ->
                binding.dateOfBirth.text =
                    year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                binding.dateOfBirth.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_greencheck,
                    0
                )
                if (getAge(year, monthOfYear + 1, dayOfMonth) < 13) {
                    Toast.makeText(this, "Age must be greater than 13 years", Toast.LENGTH_SHORT)
                        .show()
                }
            }, mYear, mMonth, mDay
        )
        datePickerDialog.datePicker.maxDate = Date().time
        datePickerDialog.show()
    }

    private fun getAge(year: Int, month: Int, day: Int): Int {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob[year, month] = day
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        return age
    }

}