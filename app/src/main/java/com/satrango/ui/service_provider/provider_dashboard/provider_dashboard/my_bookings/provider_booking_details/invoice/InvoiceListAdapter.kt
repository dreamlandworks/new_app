package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.invoice

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.InvoiceListViewRowBinding
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.invoice.model.BookingPaidTransaction

class InvoiceListAdapter(private val list: List<BookingPaidTransaction>):
    RecyclerView.Adapter<InvoiceListAdapter.ViewHolder>() {
    class ViewHolder(binding: InvoiceListViewRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(InvoiceListViewRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.bookingCharges.text = list[position].amount
        holder.binding.transactionName.text = list[position].transaction_name
    }

    override fun getItemCount(): Int {
        return list.size
    }


}