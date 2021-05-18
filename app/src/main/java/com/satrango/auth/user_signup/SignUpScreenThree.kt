package com.satrango.auth.user_signup

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.satrango.R
import com.satrango.databinding.ActivitySignUpScreenThreeBinding
import java.util.*

class SignUpScreenThree : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpScreenThreeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenThreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            email.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.toString().contains("@") || s.toString().contains(".co")) {
                        email.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_greencheck,
                            0
                        )
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })

            dateOfBirth.setOnClickListener {
                openDateOfBirthDialog()
            }

            termsAndConditions.setOnClickListener {
                startActivity(Intent(this@SignUpScreenThree, TermsAndConditionScreen::class.java))
            }

            nextBtn.setOnClickListener { startActivity(Intent(this@SignUpScreenThree, OTPVerificationScreen::class.java)) }

        }

    }

    @SuppressLint("SetTextI18n")
    private fun openDateOfBirthDialog() {
        val c = Calendar.getInstance()

        val mYear = c[Calendar.YEAR] // current year
        val mMonth = c[Calendar.MONTH] // current month
        val mDay = c[Calendar.DAY_OF_MONTH] // current day

        val datePickerDialog = DatePickerDialog(this@SignUpScreenThree, { _, year, monthOfYear, dayOfMonth ->
                binding.dateOfBirth.text = year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                binding.dateOfBirth.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_greencheck, 0)
                if (getAge(year, monthOfYear + 1, dayOfMonth) < 13) {
                    Toast.makeText(this, "Age must be greater than 13 years", Toast.LENGTH_SHORT).show()
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