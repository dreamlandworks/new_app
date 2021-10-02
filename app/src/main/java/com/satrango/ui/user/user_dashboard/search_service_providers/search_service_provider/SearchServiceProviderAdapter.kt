package com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.satrango.R
import com.satrango.databinding.SearchServiceProviderRowBinding
import com.satrango.ui.user.bookings.booking_address.BookingAddressScreen
import com.satrango.ui.user.bookings.booking_attachments.BookingAttachmentsScreen
import com.satrango.ui.user.bookings.booking_date_time.BookingDateAndTimeScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.UserSearchViewProfileScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.utils.UserUtils
import java.util.*

class SearchServiceProviderAdapter(
    private val list: List<Data>
) :
    RecyclerView.Adapter<SearchServiceProviderAdapter.ViewHolder>() {

    class ViewHolder(binding: SearchServiceProviderRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val binding = binding
        @SuppressLint("SetTextI18n")
        fun bindValues(data: Data) {
            Glide.with(binding.profilePic).load(data.profile_pic).into(binding.profilePic)
            binding.userRating.text = data.id
            binding.userName.text = data.fname
            binding.userOccupation.text = data.profession
            binding.userDescription.text = data.about_me
            binding.costPerHour.text = data.per_hour
            binding.userDistance.text = "${UserUtils.distance(UserUtils.getLatitude(binding.profilePic.context).toDouble(), UserUtils.getLongitude(binding.profilePic.context).toDouble(), data.latitude.toDouble(), data.longitude.toDouble())} Kms"

            val spDetails = Gson().fromJson(UserUtils.getSelectedSPDetails(binding.below.context), SearchServiceProviderResModel::class.java)
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
                val intent = Intent(Intent(binding.root.context, UserSearchViewProfileScreen::class.java))
                intent.putExtra(binding.root.context.getString(R.string.service_provider), data)
                binding.root.context.startActivity(intent)
            }
            binding.bookLaterBtn.setOnClickListener {
                val intent = Intent(Intent(binding.root.context, BookingDateAndTimeScreen::class.java))
                intent.putExtra(binding.root.context.getString(R.string.service_provider), data)
                binding.root.context.startActivity(intent)
            }
            binding.bookNowBtn.setOnClickListener {
                if (data.category_id == "3") {
                    val intent = Intent(binding.root.context, BookingAddressScreen::class.java)
                    intent.putExtra(binding.root.context.getString(R.string.service_provider), data)
                    binding.root.context.startActivity(intent)
                } else {
                    val intent = Intent(binding.root.context, BookingAttachmentsScreen::class.java)
                    intent.putExtra(binding.root.context.getString(R.string.service_provider), data)
                    binding.root.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(SearchServiceProviderRowBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindValues(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


}