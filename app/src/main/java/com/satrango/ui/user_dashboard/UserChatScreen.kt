package com.satrango.ui.user_dashboard

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.FragmentUserChatScreenBinding
import com.satrango.utils.UserUtils
import de.hdodenhof.circleimageview.CircleImageView

class UserChatScreen : Fragment() {

    private lateinit var binding: FragmentUserChatScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserChatScreenBinding.inflate(layoutInflater, container, false)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { activity!!.onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { activity!!.onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.chats)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        Glide.with(profilePic).load(UserUtils.getUserProfilePic(requireContext())).into(profilePic)

        binding.apply {

            bothBtn.setOnClickListener {
                bothBtn.setTextColor(Color.parseColor("#ffffff"))
                bothBtn.setBackgroundResource(R.drawable.category_bg)
                onlineBtn.setTextColor(Color.parseColor("#000000"))
                onlineBtn.setBackgroundResource(0)
                offlineBtn.setTextColor(Color.parseColor("#000000"))
                offlineBtn.setBackgroundResource(0)
            }
            onlineBtn.setOnClickListener {
                onlineBtn.setTextColor(Color.parseColor("#ffffff"))
                onlineBtn.setBackgroundResource(R.drawable.category_bg)
                bothBtn.setTextColor(Color.parseColor("#000000"))
                bothBtn.setBackgroundResource(0)
                offlineBtn.setTextColor(Color.parseColor("#000000"))
                offlineBtn.setBackgroundResource(0)
            }
            offlineBtn.setOnClickListener {
                offlineBtn.setTextColor(Color.parseColor("#ffffff"))
                offlineBtn.setBackgroundResource(R.drawable.category_bg)
                bothBtn.setTextColor(Color.parseColor("#000000"))
                bothBtn.setBackgroundResource(0)
                onlineBtn.setTextColor(Color.parseColor("#000000"))
                onlineBtn.setBackgroundResource(0)
            }


        }

        return binding.root
    }

}