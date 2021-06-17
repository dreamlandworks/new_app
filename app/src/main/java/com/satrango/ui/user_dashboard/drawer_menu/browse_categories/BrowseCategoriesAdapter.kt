package com.satrango.ui.user_dashboard.drawer_menu.browse_categories

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.UserCategoryRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.models.BrowserCategoryModel

class BrowseCategoriesAdapter(
    private val list: List<BrowserCategoryModel>,
    private val browseCategoriesInterface: BrowseCategoriesInterface
): RecyclerView.Adapter<BrowseCategoriesAdapter.ViewHolder>() {

    class ViewHolder(binding: UserCategoryRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        @SuppressLint("CheckResult")
        fun bind(data: BrowserCategoryModel) {
//            Glide.with(binding.image).load(RetrofitBuilder.BASE_URL + data.image).into(binding.image)
            binding.title.text = data.category
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(UserCategoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        if (list[position].selected) {
            holder.binding.rootLayout.setBackgroundResource(R.drawable.category_bg)
        } else {
            holder.binding.title.setTextColor(Color.parseColor("#000000"))
        }
        holder.itemView.setOnClickListener {
            browseCategoriesInterface.selectedCategory(list[position].id, position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}