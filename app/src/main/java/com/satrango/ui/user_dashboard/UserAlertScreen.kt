package com.satrango.ui.user_dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.FragmentUserAlertScreenBinding
import com.satrango.utils.UserUtils
import de.hdodenhof.circleimageview.CircleImageView

class UserAlertScreen : Fragment() {

    private lateinit var binding: FragmentUserAlertScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAlertScreenBinding.inflate(layoutInflater, container, false)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn)
            .setOnClickListener { activity!!.onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
            .setOnClickListener { activity!!.onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.alerts)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        Glide.with(profilePic).load(UserUtils.getUserProfilePic(requireContext())).into(profilePic)

        binding.apply {

            actionNeededBtn.setOnClickListener {
                actionNeededBtn.setBackgroundResource(R.drawable.category_bg)
                actionNeededBtn.setTextColor(Color.parseColor("#ffffff"))
                regularBtn.setTextColor(Color.parseColor("#000000"))
                regularBtn.setBackgroundResource(R.drawable.blue_out_line)
            }

            regularBtn.setOnClickListener {
                regularBtn.setBackgroundResource(R.drawable.category_bg)
                regularBtn.setTextColor(Color.parseColor("#ffffff"))
                actionNeededBtn.setTextColor(Color.parseColor("#000000"))
                actionNeededBtn.setBackgroundResource(R.drawable.blue_out_line)
            }

//            radioGroup.setOnCheckedChangeListener { group, checkedId ->
//                when (checkedId) {
//                    R.id.regularBtn -> {
//                        regularBtn.setTextColor(Color.parseColor("#ffffff"))
//                        actionNeededBtn.setTextColor(Color.parseColor("#000000"))
//                    }
//                    R.id.actionNeededBtn -> {
//                        actionNeededBtn.setTextColor(Color.parseColor("#ffffff"))
//                        regularBtn.setTextColor(Color.parseColor("#000000"))
//                    }
//                }
//            }

        }

        return binding.root
    }

}