package dev.abdujabbor.chatappsimple.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dev.abdujabbor.chatappsimple.adapters.ChatAdapter
import dev.abdujabbor.chatappsimple.databinding.DialogForDeleteBinding
import dev.abdujabbor.chatappsimple.databinding.FragmentChatBinding
import dev.abdujabbor.chatappsimple.models.ChatMessage
import dev.abdujabbor.chatappsimple.models.MyPerson
import dev.abdujabbor.chatappsimple.utils.MyData
import java.util.EventListener


class ChatFragment : Fragment() ,ChatAdapter.Rvclicks{
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
lateinit var dialogDelete:AlertDialog
    private lateinit var chatAdapter: ChatAdapter
    private var chatMessages = mutableListOf<ChatMessage>()

    val binding by lazy { FragmentChatBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding.icBack.setOnClickListener {
            findNavController().popBackStack()
        }
        Glide.with(requireActivity()).load(MyData.userall.photoUrl).into(binding.userimage)
        binding.username.text=MyData.userall.displayName
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        chatAdapter = ChatAdapter(chatMessages, MyPerson(auth.uid,auth.currentUser?.photoUrl.toString(),auth.currentUser?.displayName),this)
        binding.recyclerViewChat.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        binding.buttonSend.setOnClickListener {
            val message = binding.editTextMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                binding.editTextMessage.text.clear()
                chatAdapter.notifyDataSetChanged()
                binding.recyclerViewChat.scrollToPosition(chatMessages.size-1)
            } else {
                Toast.makeText(requireContext(), "Please enter a message", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        listenForMessages()

        return binding.root
    }

    private fun listenForMessages() {
        val chatMessagesRef = database.child("messege").child(MyData.usernameiu)
        chatMessagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                chatMessage?.let {
                    chatMessages.add(it)
                    chatAdapter.notifyDataSetChanged()
                    binding.recyclerViewChat.scrollToPosition(chatMessages.size-1   )
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // not used
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // not used
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // not used
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to read value.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendMessage(message: String) {
        val chatMessage = ChatMessage(
            auth.currentUser?.displayName!!,
            auth.currentUser?.uid ?: "",MyData.recieveruid,
            message,
            Calendar.getInstance().timeInMillis
        )
        database.child("messege").child(MyData.usernameiu).push().setValue(chatMessage)
        database.child("messege").child(MyData.iuusernAme).push().setValue(chatMessage)
        sendPushNotification(auth.currentUser?.displayName,message)
    }

    private fun sendPushNotification(senderName: String?, message: String) {
        val notificationRef = database.child("notifications").child(MyData.recieveruid!!).push()

        val notification = HashMap<String, String>()
        notification["senderName"] = senderName ?: "Unknown"
        notification["message"] = message

        notificationRef.setValue(notification)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun delete(messege: ChatMessage) {
val alertDialog=AlertDialog.Builder(activity,dev.abdujabbor.chatappsimple.R.style.MyMenuDialogTheme)
     val bindingDelete =DialogForDeleteBinding.inflate(layoutInflater)

        alertDialog.setView(bindingDelete.root)
        bindingDelete.tvDelete.setOnClickListener {
            if (bindingDelete.checkBox.isChecked){
                database.child(MyData.usernameiu).removeValue()
                database.child(MyData.iuusernAme).removeValue()
            }else{
                database.child(MyData.usernameiu).removeValue()
            }
            dialogDelete.cancel()


        }
        bindingDelete.tvCancel.setOnClickListener {
            dialogDelete.cancel()
        }
        dialogDelete=alertDialog.create()

        dialogDelete.window!!.attributes.windowAnimations = dev.abdujabbor.chatappsimple.R.style.MyAnimation
        dialogDelete.show()

    }



}