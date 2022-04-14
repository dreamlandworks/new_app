package com.satrango.ui.user.user_dashboard.chats

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.gson.Gson
import com.satrango.R
import com.satrango.databinding.FragmentUserChatScreenBinding
import com.satrango.ui.user.user_dashboard.chats.models.ChatMessageModel
import com.satrango.ui.user.user_dashboard.chats.models.ChatModel
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UserChatScreen : Fragment() {

    private lateinit var chatModel: ChatModel
    private lateinit var adapter: ChatAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var chats: java.util.ArrayList<ChatModel>
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

    override fun onPause() {
        super.onPause()
        databaseReference.removeEventListener(valueEventListener)
    }

    override fun onResume() {
        super.onResume()
        loadChats()
    }

    private fun loadChats() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_database_reference_url))
        valueEventListener = databaseReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chats = ArrayList()
                for (snap in snapshot.child(getString(R.string.users)).children) {
                    val mobileNo = snap.key
                    if (mobileNo != UserUtils.getPhoneNo(requireContext())) {
                        val userName = snap.child(getString(R.string.user_name)).value.toString()
                        val userId = snap.child(getString(R.string.users_id)).value.toString()
                        val profileImage = snap.child(getString(R.string.profile_image)).value.toString()
                        var lastMessage = ""
                        var unseen = 0
                        var sentBy = ""
                        val branch = if (userId.toInt() < UserUtils.getUserId(requireContext()).toInt()) {
                            userId + "|" + UserUtils.getUserId(requireContext())
                        } else {
                            UserUtils.getUserId(requireContext()) + "|" + userId
                        }
//                        val databaseRef = databaseReference.child(getString(R.string.chat)).child(branch).orderByKey().limitToLast(1)
                        val chatsList = ArrayList<ChatMessageModel>()
                        val databaseRef = databaseReference.child(getString(R.string.chat)).child(branch)
                        databaseRef.addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(chat_snapshot: DataSnapshot) {
                                for (nSnap in chat_snapshot.children) {
                                    val datetime = nSnap.key.toString()
                                    lastMessage = nSnap.child(getString(R.string.message_sm)).value.toString()
                                    sentBy = nSnap.child(getString(R.string.sent_by)).value.toString()
                                    unseen = nSnap.child(getString(R.string.unseen)).value.toString().toInt()
                                    val type = nSnap.child(getString(R.string.message_type)).value.toString()
                                    chatsList.add(ChatMessageModel(lastMessage, datetime, sentBy, type))
                                }
                                chatModel = ChatModel(userName, mobileNo.toString(), lastMessage, profileImage, unseen, userId, sentBy, chatsList)
                                chats.add(chatModel)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                toast(requireContext(), error.message)
                            }

                        })
                    }
                }
                toast(requireContext(), "Chats Loading...")
                binding.recyclerView.adapter = ChatAdapter(chats)
//                adapter.updateChats(chats)
                toast(requireContext(), "Chats Loaded")
            }

            override fun onCancelled(error: DatabaseError) {
                toast(requireContext(), error.message)
            }

        })
    }

}