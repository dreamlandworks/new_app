package com.satrango.ui.user.user_dashboard.chats

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.satrango.R
import com.satrango.databinding.FragmentUserChatScreenBinding
import com.satrango.ui.user.user_dashboard.chats.models.ChatModel
import com.satrango.ui.user.user_dashboard.chats.models.ChatsModel
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception


class UserChatScreen : Fragment() {

    private lateinit var adapter: ChatAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var binding: FragmentUserChatScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserChatScreenBinding.inflate(layoutInflater, container, false)
        initializeToolBar()

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
        loadChats()
        return binding.root
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn)
            .setOnClickListener { activity?.onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn)
            .setOnClickListener { activity?.onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.chats)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(profilePic)
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onPause() {
        super.onPause()
        adapter.stopListening()
    }

    private fun loadChats() {
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_database_reference_url))
        val options: FirebaseRecyclerOptions<ChatsModel> = FirebaseRecyclerOptions.Builder<ChatsModel>()
                .setQuery(databaseReference.child(getString(R.string.users)).child(UserUtils.getUserId(requireContext())).child(getString(R.string.chat)), ChatsModel::class.java)
                .build()
        adapter = ChatAdapter(options)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.adapter = adapter
    }

}