package com.satrango.ui.auth.provider_signup

import android.R
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.hootsuite.nachos.validator.ChipifyingNachoValidator
import com.satrango.databinding.ActivityProviderSignUpOneBinding
import java.util.*


class ProviderSignUpOne : AppCompatActivity() {

    private lateinit var binding: ActivityProviderSignUpOneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderSignUpOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            val languagesList = arrayOf("Telugu", "Hindi", "English", "Kanada", "Marati", "Bengali", "Oddisa")
            val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_dropdown_item_1line, languagesList)
            languages.setAdapter(adapter)
            languages.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
            languages.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
            languages.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
            languages.addChipTerminator(';', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
            languages.setNachoValidator(ChipifyingNachoValidator())
            languages.enableEditChipOnTouch(true, true)
            languages.setOnChipClickListener { chip, motionEvent ->
                Log.d("TAG", "onChipClick: " + chip.text)
            }

            val experienceList = arrayOf("0-1 Years", "2-3 Years", "3-4 Years", "4-5 Years", "5-10 Years")
            val experienceAdapter = ArrayAdapter(applicationContext, R.layout.select_dialog_item, experienceList)
            experience.setAdapter(experienceAdapter)
            experience.threshold = 1
            experience.setAdapter(adapter)

//            for (chip in languages.allChips) {
//                val text = chip.text
//                languag_list.add(text.toString())
//                all_Languages = TextUtils.join(",", languag_list)
//            }


            nextBtn.setOnClickListener {

                startActivity(Intent(this@ProviderSignUpOne, ProviderSignUpTwo::class.java))
            }

            resetBtn.setOnClickListener {
                finish()
                startActivity(intent)
            }

        }

    }

}