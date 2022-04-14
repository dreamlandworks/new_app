package com.satrango.ui.user.user_dashboard.chats

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.gson.Gson
import com.satrango.R
import com.satrango.databinding.ActivityChatScreenBinding
import com.satrango.ui.user.user_dashboard.chats.models.ChatMessageModel
import com.satrango.ui.user.user_dashboard.chats.models.ChatModel
import com.satrango.utils.UserUtils
import com.satrango.utils.toast
import java.util.*

class ChatScreen : AppCompatActivity() {

    private lateinit var userData: ChatModel
    private lateinit var userStatusValueEventListener: ValueEventListener
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: ActivityChatScreenBinding
    private lateinit var chats: ArrayList<ChatMessageModel>
    private lateinit var branch: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userData = Gson().fromJson(UserUtils.getSelectedChat(this), ChatModel::class.java)
        Glide.with(this).load(userData.profileImage).error(R.drawable.images).into(binding.toolBarImage)
        binding.toolBarBackBtn.setOnClickListener { onBackPressed() }
        binding.toolBarTitle.text = userData.userName

        branch = if (UserUtils.getUserId(this).toInt() > userData.userId.toInt()) {
            userData.userId + "|" + UserUtils.getUserId(this@ChatScreen)
        } else {
            UserUtils.getUserId(this@ChatScreen) + "|" + userData.userId
        }

        binding.sendBtn.setOnClickListener {
            sendMessage()
        }

    }

    private fun sendMessage() {
        binding.apply {
            val message = message.text.toString().trim()
            if (message.isNotEmpty()) {
                databaseReference.child(getString(R.string.chat))
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val timestamp = Date().time.toString()
                            sendToBranch(timestamp, message)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            toast(this@ChatScreen, error.message)
                        }

                    })
            }
        }
    }

    private fun sendToBranch(timestamp: String, message: String) {
        databaseReference.child(timestamp).child(getString(R.string.message_sm)).setValue(message)
        databaseReference.child(timestamp).child(getString(R.string.sent_by)).setValue(UserUtils.getUserId(this))
        databaseReference.child(timestamp).child(getString(R.string.message_type)).setValue(getString(R.string.text))
        databaseReference.child(timestamp).child(getString(R.string.unseen)).setValue(getString(R.string._0))
        binding.message.setText("")
    }

    override fun onPause() {
        super.onPause()
        databaseReference.removeEventListener(valueEventListener)
        databaseReference.removeEventListener(userStatusValueEventListener)
    }

    override fun onResume() {
        super.onResume()
        loadChatMessages()
        loadUserStatus()
    }

    private fun loadUserStatus() {
        val databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_database_reference_url)).child(getString(R.string.users)).child(userData.mobileNo)
        userStatusValueEventListener = databaseReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userStatus = snapshot.child(getString(R.string.online_status)).value.toString()
                if (userStatus == getString(R.string.online)) {
                    binding.toolBarSubTitle.text = userStatus
                } else {
                    binding.toolBarSubTitle.text = userStatus
                }

            }

            override fun onCancelled(error: DatabaseError) {
                toast(this@ChatScreen, error.message)
            }

        })
    }

    private fun loadChatMessages() {
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_database_reference_url)).child(getString(R.string.chat)).child(branch)
        valueEventListener = databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount > 0) {
                    chats = ArrayList()
                    for (snap in snapshot.children) {
                        val dateTime = snap.key.toString()
                        val message = snap.child(getString(R.string.message_sm)).value.toString()
                        val sentBy = snap.child(getString(R.string.sent_by)).value.toString()
                        val type = snap.child(getString(R.string.message_type)).value.toString()
                        val chat = ChatMessageModel(message, dateTime, sentBy, type)
                        chats.add(chat)
                    }
                    val layoutManager = LinearLayoutManager(this@ChatScreen)
                    layoutManager.reverseLayout = true
                    binding.recyclerView.layoutManager = layoutManager
                    binding.recyclerView.adapter = ChatMessageAdapter(chats.reversed())
                } else {
                    toast(this@ChatScreen, "Messages not Found!")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                toast(this@ChatScreen, error.details)
            }

        })
    }

}