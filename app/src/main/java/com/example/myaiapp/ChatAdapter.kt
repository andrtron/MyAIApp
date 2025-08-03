package com.example.myaiapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myaiapp.databinding.ItemMessageBinding

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.VH>() {
    private val items = mutableListOf<Pair<String, Boolean>>()

    fun add(userMsg: String, isUser: Boolean) {
        items.add(userMsg to isUser)
        notifyItemInserted(items.lastIndex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, pos: Int) {
        val (text, isUser) = items[pos]
        holder.binding.tvMessage.text = text
        holder.binding.tvMessage.textAlignment = if (isUser)
            android.view.View.TEXT_ALIGNMENT_TEXT_END
        else android.view.View.TEXT_ALIGNMENT_TEXT_START
    }

    override fun getItemCount() = items.size

    class VH(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)
}
