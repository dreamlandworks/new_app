package com.satrango.ui.splash

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.R
import com.satrango.databinding.IntroRowBinding
import com.satrango.ui.auth.LoginScreen
import com.satrango.utils.AuthUtils


class IntroAdapter(private val list: MutableList<IntroModel>, private val introInterface: IntroInterface): RecyclerView.Adapter<IntroAdapter.ViewHolder>() {

    class ViewHolder(binding: IntroRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        fun bind(model: IntroModel) {
            binding.introRowImage.setImageResource(model.image)
            binding.introRowText.text = model.note
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(IntroRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        when(position) {
            0 -> {
                holder.binding.introRowPrevBtn.visibility = View.GONE
                holder.binding.indicatorOne.setBackgroundResource(R.drawable.category_unselected_bg)
            }
            1 -> {
                holder.binding.indicatorTwo.setBackgroundResource(R.drawable.category_unselected_bg)
            }
            2 -> {
                holder.binding.indicatorThree.setBackgroundResource(R.drawable.category_unselected_bg)
            }
            3 -> {
                holder.binding.indicatorFour.setBackgroundResource(R.drawable.category_unselected_bg)
            }
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
        holder.binding.skip.setOnClickListener {
            AuthUtils.setFirstTimeLaunch(holder.binding.skip.context)
            holder.binding.skip.context.startActivity(Intent(holder.binding.skip.context, LoginScreen::class.java))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}