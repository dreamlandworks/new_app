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
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.chats.models.ChatMessageModel
import com.satrango.ui.user.user_dashboard.chats.models.ChatModel
import com.satrango.ui.user.user_dashboard.chats.models.ChatsModel
import com.satrango.utils.UserUtils
import com.satrango.utils.toast
import java.util.*

class ChatScreen : AppCompatActivity() {

    private lateinit var userData: ChatsModel
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

        userData = Gson().fromJson(UserUtils.getSelectedChat(this), ChatsModel::class.java)
        Glide.with(this).load(userData.profile_image).error(R.drawable.images).into(binding.toolBarImage)
        binding.toolBarBackBtn.setOnClickListener { onBackPressed() }
        binding.toolBarTitle.text = userData.username

        branch = if (UserUtils.getUserId(this).toInt() > userData.user_id.toInt()) {
            userData.user_id + "|" + UserUtils.getUserId(this@ChatScreen)
        } else {
            UserUtils.getUserId(this@ChatScreen) + "|" + userData.user_id
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
        val datetime = Date().time
        val lastMessageReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(getString(R.string.firebase_database_reference_url))
            .child(getString(R.string.users))
        val userChatReference = lastMessageReference.child(UserUtils.getUserId(this@ChatScreen))
            .child(getString(R.string.chat)).child(branch)
        userChatReference.child(getString(R.string.last_message)).setValue(message)
        userChatReference.child(getString(R.string.sent_by)).setValue(UserUtils.getUserId(this@ChatScreen))
        userChatReference.child(getString(R.string.date_time)).setValue(datetime)

        val spDatabaseReference = lastMessageReference.child(userData.user_id).child(getString(R.string.chat)).child(branch)
        spDatabaseReference.child(getString(R.string.last_message)).setValue(message)
        spDatabaseReference.child(getString(R.string.sent_by)).setValue(UserUtils.getUserId(this@ChatScreen))
        spDatabaseReference.child(getString(R.string.date_time)).setValue(datetime)

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
        val databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(getString(R.string.firebase_database_reference_url))
            .child(getString(R.string.users)).child(userData.user_id)
        userStatusValueEventListener = databaseReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userStatus = snapshot.child(getString(R.string.online_status)).value.toString()
                binding.toolBarSubTitle.text = userStatus
            }

            override fun onCancelled(error: DatabaseError) {
                toast(this@ChatScreen, error.message)
            }

        })
    }

    private fun loadChatMessages() {
        databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(getString(R.string.firebase_database_reference_url))
            .child(getString(R.string.chat)).child(branch)
        valueEventListener = databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount > 0) {
                    chats = ArrayList()
                    for (snap in snapshot.children) {
                        val dateTime = snap.key.toString()
                        val message = snap.child(getString(R.string.message_sm)).value.toString()
                        val sentBy = snap.child(getString(R.string.sent_by)).value.toString()
                        val type = snap.child(getString(R.string.message_type)).value.toString()
                        val unseen = snap.child(getString(R.string.unseen)).value.toString()
                        val chat = ChatMessageModel(message, dateTime, sentBy, type, unseen)
                        chats.add(chat)
                    }
                    val layoutManager = LinearLayoutManager(this@ChatScreen)
                    layoutManager.reverseLayout = true
                    binding.recyclerView.layoutManager = layoutManager
                    binding.recyclerView.adapter = ChatMessageAdapter(chats.reversed())
                    for (chat in chats) {
                        if (chat.sentBy != UserUtils.getUserId(this@ChatScreen) && chat.unseen == getString(R.string._0)) {
                            databaseReference.child(chat.datetime).child(getString(R.string.unseen)).setValue(getString(R.string.one))
                        }
                    }
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