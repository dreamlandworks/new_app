package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.BaseFragment
import com.satrango.databinding.FragmentTariffTimingsProfileScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.TimeslotResponse
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.PreferredTimeSlot
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.update_tariff.UpdateTariffReqModel
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class TariffTimingsProfileScreen :
    BaseFragment<ProviderProfileViewModel, FragmentTariffTimingsProfileScreenBinding, ProviderProfileRepository>() {

    override fun getFragmentViewModel(): Class<ProviderProfileViewModel> =
        ProviderProfileViewModel::class.java

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTariffTimingsProfileScreenBinding =
        FragmentTariffTimingsProfileScreenBinding.inflate(layoutInflater)

    override fun getFragmentRepository(): ProviderProfileRepository = ProviderProfileRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ProviderProfileScreen.professionalDetails != null) {

            val data = ProviderProfileScreen.professionalDetails

            if (data.preferred_time_slots.isNotEmpty()) {
                for (timeSlot in data.preferred_time_slots) {
                    addView(timeSlot)
                }
            }
            ProviderProfileScreen.progressDialog.dismiss()
        }

        binding.addSlot.setOnClickListener {
            addView(null)
        }

        binding.updateBtn.setOnClickListener {
            validateFields()
        }

    }

    private fun validateFields() {
        when {
            JSONArray(validateSlots()).length() == 0 -> {
                snackBar(binding.addSlot, "Add Slots")
            }
            else -> {
                val requestBody = UpdateTariffReqModel(
                    RetrofitBuilder.PROVIDER_KEY,
                    validateSlots(),
                    UserUtils.getUserId(requireContext())
                )
                Log.e("JSON", Gson().toJson(requestBody))
                viewModel.updateTariff(requireContext(), requestBody).observe(requireActivity(), {
                    when (it) {
                        is NetworkResponse.Loading -> {
                            ProviderProfileScreen.progressDialog.show()
                        }
                        is NetworkResponse.Success -> {
                            snackBar(binding.addSlot, JSONObject(it.data!!.string()).getString("message"))
                            Handler().postDelayed({
                                startActivity(requireActivity().intent)
                            }, 3000)
                            ProviderProfileScreen.progressDialog.dismiss()
                        }
                        is NetworkResponse.Failure -> {
                            ProviderProfileScreen.progressDialog.dismiss()
                            snackBar(binding.addSlot, it.message!!)
                        }
                    }
                })

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addView(timeSlot: PreferredTimeSlot?) {
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

        if (timeSlot != null) {
            fromDate.setText(timeSlot.time_slot_from)
            toDate.setText(timeSlot.time_slot_to)

            when (timeSlot.day_slot) {
                "1" -> {
                    sunday.setBackgroundResource(R.drawable.provider_btn_bg)
                    sunday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                "2" -> {
                    monday.setBackgroundResource(R.drawable.provider_btn_bg)
                    monday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                "3" -> {
                    tuesday.setBackgroundResource(R.drawable.provider_btn_bg)
                    tuesday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                "4" -> {
                    wednesday.setBackgroundResource(R.drawable.provider_btn_bg)
                    wednesday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                "5" -> {
                    thursday.setBackgroundResource(R.drawable.provider_btn_bg)
                    thursday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                "6" -> {
                    friday.setBackgroundResource(R.drawable.provider_btn_bg)
                    friday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                "7" -> {
                    saturday.setBackgroundResource(R.drawable.provider_btn_bg)
                    saturday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
            }

        }

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
            everyDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            weekDays.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple_500
                )
            )
            weekDays.setBackgroundResource(R.drawable.purple_out_line)
            weekEnds.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple_500
                )
            )
            weekEnds.setBackgroundResource(R.drawable.purple_out_line)

            sunday.setBackgroundResource(R.drawable.provider_btn_bg)
            sunday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            monday.setBackgroundResource(R.drawable.provider_btn_bg)
            monday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            tuesday.setBackgroundResource(R.drawable.provider_btn_bg)
            tuesday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            wednesday.setBackgroundResource(R.drawable.provider_btn_bg)
            wednesday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            thursday.setBackgroundResource(R.drawable.provider_btn_bg)
            thursday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            friday.setBackgroundResource(R.drawable.provider_btn_bg)
            friday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            saturday.setBackgroundResource(R.drawable.provider_btn_bg)
            saturday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        weekDays.setOnClickListener {
            weekDays.setBackgroundResource(R.drawable.provider_btn_bg)
            weekDays.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            everyDay.setBackgroundResource(R.drawable.purple_out_line)
            everyDay.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple_500
                )
            )
            weekEnds.setBackgroundResource(R.drawable.purple_out_line)
            weekEnds.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple_500
                )
            )

            monday.setBackgroundResource(R.drawable.provider_btn_bg)
            monday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            tuesday.setBackgroundResource(R.drawable.provider_btn_bg)
            tuesday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            wednesday.setBackgroundResource(R.drawable.provider_btn_bg)
            wednesday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            thursday.setBackgroundResource(R.drawable.provider_btn_bg)
            thursday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            friday.setBackgroundResource(R.drawable.provider_btn_bg)
            friday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            saturday.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple_500
                )
            )
            saturday.setBackgroundResource(R.drawable.purple_out_line)
            sunday.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple_500
                )
            )
            sunday.setBackgroundResource(R.drawable.purple_out_line)
        }
        weekEnds.setOnClickListener {
            weekEnds.setBackgroundResource(R.drawable.provider_btn_bg)
            weekEnds.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            weekDays.setBackgroundResource(R.drawable.purple_out_line)
            weekDays.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple_500
                )
            )
            everyDay.setBackgroundResource(R.drawable.purple_out_line)
            everyDay.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple_500
                )
            )

            monday.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple_500
                )
            )
            monday.setBackgroundResource(R.drawable.purple_out_line)
            tuesday.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple_500
                )
            )
            tuesday.setBackgroundResource(R.drawable.purple_out_line)
            wednesday.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple_500
                )
            )
            wednesday.setBackgroundResource(R.drawable.purple_out_line)
            thursday.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple_500
                )
            )
            thursday.setBackgroundResource(R.drawable.purple_out_line)
            friday.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple_500
                )
            )
            friday.setBackgroundResource(R.drawable.purple_out_line)

            saturday.setBackgroundResource(R.drawable.provider_btn_bg)
            saturday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            sunday.setBackgroundResource(R.drawable.provider_btn_bg)
            sunday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

        sunday.setOnClickListener {
            if (sunday.currentTextColor == Color.WHITE) {
                sunday.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_500
                    )
                )
                sunday.setBackgroundResource(R.drawable.purple_out_line)
            } else {
                sunday.setBackgroundResource(R.drawable.provider_btn_bg)
                sunday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purple_out_line)
            everyDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
            weekDays.setBackgroundResource(R.drawable.purple_out_line)
            weekDays.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
            weekEnds.setBackgroundResource(R.drawable.purple_out_line)
            weekEnds.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
        }
        monday.setOnClickListener {
            if (monday.currentTextColor == Color.WHITE) {
                monday.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_500
                    )
                )
                monday.setBackgroundResource(R.drawable.purple_out_line)
            } else {
                monday.setBackgroundResource(R.drawable.provider_btn_bg)
                monday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purple_out_line)
            everyDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
            weekDays.setBackgroundResource(R.drawable.purple_out_line)
            weekDays.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
            weekEnds.setBackgroundResource(R.drawable.purple_out_line)
            weekEnds.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
        }
        tuesday.setOnClickListener {
            if (tuesday.currentTextColor == Color.WHITE) {
                tuesday.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_500
                    )
                )
                tuesday.setBackgroundResource(R.drawable.purple_out_line)
            } else {
                tuesday.setBackgroundResource(R.drawable.provider_btn_bg)
                tuesday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purple_out_line)
            everyDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
            weekDays.setBackgroundResource(R.drawable.purple_out_line)
            weekDays.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
            weekEnds.setBackgroundResource(R.drawable.purple_out_line)
            weekEnds.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
        }
        wednesday.setOnClickListener {
            if (wednesday.currentTextColor == Color.WHITE) {
                wednesday.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_500
                    )
                )
                wednesday.setBackgroundResource(R.drawable.purple_out_line)
            } else {
                wednesday.setBackgroundResource(R.drawable.provider_btn_bg)
                wednesday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purple_out_line)
            everyDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
            weekDays.setBackgroundResource(R.drawable.purple_out_line)
            weekDays.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
            weekEnds.setBackgroundResource(R.drawable.purple_out_line)
            weekEnds.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
        }
        thursday.setOnClickListener {
            if (thursday.currentTextColor == Color.WHITE) {
                thursday.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_500
                    )
                )
                thursday.setBackgroundResource(R.drawable.purple_out_line)
            } else {
                thursday.setBackgroundResource(R.drawable.provider_btn_bg)
                thursday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purple_out_line)
            everyDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
            weekDays.setBackgroundResource(R.drawable.purple_out_line)
            weekDays.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
            weekEnds.setBackgroundResource(R.drawable.purple_out_line)
            weekEnds.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
        }
        friday.setOnClickListener {
            if (friday.currentTextColor == Color.WHITE) {
                friday.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_500
                    )
                )
                friday.setBackgroundResource(R.drawable.purple_out_line)
            } else {
                friday.setBackgroundResource(R.drawable.provider_btn_bg)
                friday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purple_out_line)
            everyDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
            weekDays.setBackgroundResource(R.drawable.purple_out_line)
            weekDays.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
            weekEnds.setBackgroundResource(R.drawable.purple_out_line)
            weekEnds.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
        }
        saturday.setOnClickListener {
            if (saturday.currentTextColor == Color.WHITE) {
                saturday.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_500
                    )
                )
                saturday.setBackgroundResource(R.drawable.purple_out_line)
            } else {
                saturday.setBackgroundResource(R.drawable.provider_btn_bg)
                saturday.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            everyDay.setBackgroundResource(R.drawable.purple_out_line)
            everyDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
            weekDays.setBackgroundResource(R.drawable.purple_out_line)
            weekDays.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
            weekEnds.setBackgroundResource(R.drawable.purple_out_line)
            weekEnds.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
        }
        binding.layoutList.addView(cricketerView)
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
                toast(requireContext(), "Select From Time")
                return emptyList()
            }
            if (toTime.text.toString().isNotEmpty()) {
                selectedToTime = toTime.text.toString().trim()
            } else {
                toast(requireContext(), "Select To Time")
                return emptyList()
            }

            val from = selectedFromTime.split(":")[0].toInt()
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
                    toast(requireContext(), "Select Days")
                    return emptyList()
                } else {
                    slotList.add(
                        TimeslotResponse(
                            TextUtils.join(",", daysList),
                            "$time:00:00",
                            "${time + 1}:00:00"
                        )
                    )
                }
            }
        }

        return slotList
    }

    private fun removeView(view: View) {
        binding.layoutList.removeView(view)
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun openDatePickerDialog(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)
        val calendarMinute = calendar.get(Calendar.MINUTE)
        val timepickerdialog = TimePickerDialog(
            requireContext(),
            { view, hourOfDay, minute ->
//                editText.setText("${String.format("%02d", hourOfDay)}:${String.format("%02d", minute)}")
                editText.setText("${hourOfDay}:${minute}")
            }, calendarHour, calendarMinute, false
        )
        timepickerdialog.show()
    }

}