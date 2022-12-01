package com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.databinding.UserSubCategoryRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowserSubCategoryModel
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.utils.UserUtils

class BrowseSubCategoriesAdapter(private val list: List<BrowserSubCategoryModel>) :
    RecyclerView.Adapter<BrowseSubCategoriesAdapter.ViewHolder>() {

    class ViewHolder(binding: UserSubCategoryRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        @SuppressLint("CheckResult")
        fun bind(data: BrowserSubCategoryModel) {
            Glide.with(binding.image).load(data.image).into(binding.image)
            binding.title.text = data.sub_name
            binding.root.setOnClickListener {
                SearchServiceProvidersScreen.FROM_POPULAR_SERVICES = true
                SearchServiceProvidersScreen.subCategoryId = data.id
                SearchServiceProvidersScreen.keyword = "0"
                binding.root.context.startActivity(Intent(binding.root.context, SearchServiceProvidersScreen::class.java))
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(UserSubCategoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}