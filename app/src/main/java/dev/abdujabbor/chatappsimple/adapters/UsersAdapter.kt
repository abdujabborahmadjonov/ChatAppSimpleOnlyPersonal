package dev.abdujabbor.chatappsimple.adapters

import com.bumptech.glide.Glide
import dev.abdujabbor.chatappsimple.databinding.ItemRvUserBinding
import dev.abdujabbor.chatappsimple.models.MyPerson
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class UsersAdapter(var list: ArrayList<MyPerson>, var context:Context,var rvClick: RvClick) :
    RecyclerView.Adapter<UsersAdapter.VH>() {
    inner class VH(var itemViewBinding: ItemRvUserBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(list: MyPerson, position: Int) {
            Glide.with(context).load(list.photoUrl).into(itemViewBinding.imageofusers)
            itemViewBinding.textofuser.text = list.displayName
            itemViewBinding.root.setOnClickListener {
                rvClick.click(list,position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        return VH(ItemRvUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        return holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

interface RvClick {
    fun click(moview: MyPerson, position: Int)
}