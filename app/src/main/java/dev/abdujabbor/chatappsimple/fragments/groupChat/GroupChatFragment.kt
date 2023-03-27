package dev.abdujabbor.chatappsimple.fragments.groupChat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.abdujabbor.chatappsimple.R
import dev.abdujabbor.chatappsimple.databinding.FragmentGroupChatBinding

class GroupChatFragment : Fragment() {

    val binding by lazy { FragmentGroupChatBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_chat, container, false)
    }
}