package com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.databinding.UserSubCategoryRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowserSubCategoryModel

class BrowseSubCategoriesAdapter(private val list: List<BrowserSubCategoryModel>) :
    RecyclerView.Adapter<BrowseSubCategoriesAdapter.ViewHolder>() {

    class ViewHolder(binding: UserSubCategoryRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        @SuppressLint("CheckResult")
        fun bind(data: BrowserSubCategoryModel) {
            Glide.with(binding.image).load(RetrofitBuilder.BASE_URL + data.image).into(binding.image)
            binding.title.text = data.sub_name
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            UserSubCategoryRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
//        if (position == list.size - 1) holder.binding.line.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return list.size
    }
}