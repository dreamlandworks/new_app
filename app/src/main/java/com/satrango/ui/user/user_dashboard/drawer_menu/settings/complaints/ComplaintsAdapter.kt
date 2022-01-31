package com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.satrango.R
import com.satrango.databinding.RaiseComplaintRowBinding
import com.satrango.databinding.UserCategoryRowBinding
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesInterface
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowserCategoryModel

class ComplaintsAdapter(private val list: List<BrowserCategoryModel>, private val browseCategoriesInterface: BrowseCategoriesInterface) :
    RecyclerView.Adapter<ComplaintsAdapter.ViewHolder>() {

    class ViewHolder(binding: RaiseComplaintRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RaiseComplaintRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.title.text = list[position].category

        if (list[position].selected) {
            if (ComplaintScreen.FROM_PROVIDER) {
                holder.binding.title.setBackgroundResource(R.drawable.provider_btn_bg)
                holder.binding.title.setTextColor(holder.binding.title.context.getColor(R.color.white))
            } else {
                holder.binding.title.setBackgroundResource(R.drawable.category_bg)
                holder.binding.title.setTextColor(holder.binding.title.context.getColor(R.color.white))
            }
        } else {
            if (ComplaintScreen.FROM_PROVIDER) {
                holder.binding.title.setBackgroundResource(R.drawable.purple_out_line)
                holder.binding.title.setTextColor(holder.binding.title.context.getColor(R.color.purple_500))
            } else {
                holder.binding.title.setBackgroundResource(R.drawable.blue_out_line)
                holder.binding.title.setTextColor(holder.binding.title.context.getColor(R.color.blue))
            }
        }
        holder.binding.title.setOnClickListener {
            browseCategoriesInterface.selectedCategory(list[position].id, position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}