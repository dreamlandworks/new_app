package com.satrango.auth.intro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.R
import com.satrango.databinding.IntroRowBinding


class IntroAdapter(private val list: List<Int>, private val introInterface: IntroInterface): RecyclerView.Adapter<IntroAdapter.ViewHolder>() {

    class ViewHolder(binding: IntroRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        fun bind(image: Int) {
            binding.introRowImage.setImageResource(image)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(IntroRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        if (position == 0) {
            holder.binding.introRowPrevBtn.visibility = View.GONE
        }
        if (position == list.size - 1) {
            holder.binding.introRowNextBtn.text = holder.binding.root.context.resources.getString(R.string.finish)
        }
        holder.binding.introRowPrevBtn.setOnClickListener {
            introInterface.prevClick(position)
        }
        holder.binding.introRowNextBtn.setOnClickListener {
            introInterface.nextClick(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}