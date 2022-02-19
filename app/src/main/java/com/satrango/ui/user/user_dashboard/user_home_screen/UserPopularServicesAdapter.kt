package com.satrango.ui.user.user_dashboard.user_home_screen

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.databinding.UserPopularServicesRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowserSubCategoryModel
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.utils.UserUtils

class UserPopularServicesAdapter(private val list: List<BrowserSubCategoryModel>) :
    RecyclerView.Adapter<UserPopularServicesAdapter.ViewHolder>() {

    class ViewHolder(view: UserPopularServicesRowBinding) : RecyclerView.ViewHolder(view.root) {
        val binding = view

        fun bindData(model: BrowserSubCategoryModel) {
            Glide.with(binding.rowImage).load(RetrofitBuilder.BASE_URL + model.image)
                .into(binding.rowImage)
            binding.rowTitle.text = model.sub_name
            binding.root.setOnClickListener {
                SearchServiceProvidersScreen.subCategoryId = model.id
                SearchServiceProvidersScreen.keyword = "0"
                SearchServiceProvidersScreen.FROM_POPULAR_SERVICES = true
                UserUtils.saveSearchFilter(binding.root.context, "")
                UserUtils.saveSelectedAllSPDetails(binding.root.context, "")
                UserUtils.saveSelectedKeywordCategoryId(binding.root.context, model.category_id)
                binding.root.context.startActivity(Intent(binding.root.context, SearchServiceProvidersScreen::class.java))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(UserPopularServicesRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}