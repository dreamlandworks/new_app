package com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.satrango.R
import com.satrango.databinding.SearchServiceProviderRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.booking_address.BookingAddressScreen
import com.satrango.ui.user.bookings.booking_attachments.BookingAttachmentsScreen
import com.satrango.ui.user.bookings.booking_attachments.BookingMultiMoveAddressScreen
import com.satrango.ui.user.bookings.booking_attachments.models.MultiMoveReqModel
import com.satrango.ui.user.bookings.booking_date_time.BookingDateAndTimeScreen
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.UserSearchViewProfileScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Charges
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen.Companion.FROM_POPULAR_SERVICES
import com.satrango.utils.UserUtils
import kotlin.math.ceil
import kotlin.math.round

class SearchServiceProviderAdapter(
    private val list: List<Data>,
    private val activity: Activity,
) : RecyclerView.Adapter<SearchServiceProviderAdapter.ViewHolder>() {

    class ViewHolder(binding: SearchServiceProviderRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        fun TextView.showStrikeThrough(show: Boolean) {
            paintFlags =
                if (show) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        @SuppressLint("SetTextI18n")
        fun bindValues(data: Data) {
            Glide.with(binding.profilePic).load(RetrofitBuilder.BASE_URL + data.profile_pic).into(binding.profilePic)
            binding.userRating.text = data.id
            binding.userName.text = data.fname
            binding.userOccupation.text = data.profession
            binding.userDescription.text = data.about_me
            binding.userRating.text = data.rating.toString()
            binding.userRank.text = data.rank.toString()
            binding.costPerHour.text = "Rs. ${round(data.final_amount.toDouble()).toInt()}/-"
            if (SearchServiceProvidersScreen.offerId != 0) {
                binding.actualCost.text = "Rs. ${round(data.actual_amount.toDouble()).toInt()}/-"
                binding.actualCost.showStrikeThrough(true)
            } else {
                binding.actualCost.visibility = View.GONE
            }
            binding.userDistance.text = "${UserUtils.roundOffDecimal(data.distance_kms.toDouble())} Kms away"

            val spDetails = Gson().fromJson(UserUtils.getSelectedAllSPDetails(binding.profilePic.context), SearchServiceProviderResModel::class.java)
            for (sp in spDetails.slots_data) {
                if (data.users_id == sp.user_id) {
                    for (booking in sp.blocked_time_slots) {
                        if (UserUtils.getComingHour() == booking.time_slot_from.split(":")[0].toInt()) {
                            binding.bookNowBtn.visibility = View.GONE
                        }
                    }
                }
            }

            binding.root.setOnClickListener {
                FROM_POPULAR_SERVICES = false
                UserUtils.saveSelectedSPDetails(binding.root.context, Gson().toJson(data))
                val intent = Intent(Intent(binding.root.context, UserSearchViewProfileScreen::class.java))
                binding.root.context.startActivity(intent)
            }
            binding.bookLaterBtn.setOnClickListener {
                ViewUserBookingDetailsScreen.RESCHEDULE = false
                UserUtils.saveSelectedSPDetails(binding.root.context, Gson().toJson(data))
                UserUtils.saveFromInstantBooking(binding.root.context, false)
                val intent = Intent(Intent(binding.root.context, BookingDateAndTimeScreen::class.java))
                binding.root.context.startActivity(intent)
            }
            binding.bookNowBtn.setOnClickListener {
                UserUtils.saveFromInstantBooking(binding.root.context, true)
                UserUtils.saveSelectedSPDetails(binding.root.context, Gson().toJson(data))
                if (data.category_id == "3") {
                    val intent = Intent(binding.root.context, BookingAddressScreen::class.java)
                    binding.root.context.startActivity(intent)
                } else {

                    val database = Firebase.database
                    database.getReference(UserUtils.getFCMToken(binding.root.context)).removeValue()
                    val intent = Intent(binding.root.context, BookingAttachmentsScreen::class.java)
                    binding.root.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(SearchServiceProviderRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindValues(list[position])
        holder.binding.userViewMore.setOnClickListener {
            displayPopupWindow(holder.binding.userViewMore, activity, list[position])
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayPopupWindow(anchorView: View, activity: Activity, charges: Data) {
        val popup = PopupWindow(anchorView.context)
        val layout: View = activity.layoutInflater.inflate(R.layout.popup_content, null)
        val cgstText = layout.findViewById<TextView>(R.id.cgstText)
        val cgstCost = layout.findViewById<TextView>(R.id.cgstCost)
        val sgstText = layout.findViewById<TextView>(R.id.sgstText)
        val sgstCost = layout.findViewById<TextView>(R.id.sgstCost)
        val cpkmsText = layout.findViewById<TextView>(R.id.cpkmText)
        val cpkmsCost = layout.findViewById<TextView>(R.id.cpkmCost)
        val minChargesText = layout.findViewById<TextView>(R.id.minChargesText)
        val minChargesCost = layout.findViewById<TextView>(R.id.minChargesCost)

        cgstText.text = "CGST(${charges.CGST_percentage}%): "
        cgstCost.text = "Rs ${UserUtils.roundOffDecimal(charges.CGST_amount.toDouble())}/-"
        sgstText.text = "SGST(${charges.SGST_percentage}%): "
        sgstCost.text = "Rs ${UserUtils.roundOffDecimal(charges.SGST_amount.toDouble())}/-"
        minChargesText.text = "Min Charges: "
        minChargesCost.text = "Rs. ${round(charges.min_charges.toDouble()).toInt()}/-"
        if (charges.category_id == "3") {
            cpkmsText.text = "Distance Per Kms:"
            cpkmsCost.text = "Rs" + UserUtils.roundOffDecimal(charges.distance_kms.toDouble()).toString()
        } else {
            cpkmsText.visibility = View.GONE
            cpkmsCost.visibility = View.GONE
        }

        popup.contentView = layout
        popup.height = WindowManager.LayoutParams.WRAP_CONTENT
        popup.width = WindowManager.LayoutParams.WRAP_CONTENT
        popup.isOutsideTouchable = true
        popup.isFocusable = true
        popup.setBackgroundDrawable(BitmapDrawable())
        popup.showAsDropDown(anchorView)
    }

    override fun getItemCount(): Int {
        return list.size
    }


}