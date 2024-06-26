package com.example.ptamanah.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ptamanah.R
import com.example.ptamanah.data.response.DataItem
import com.example.ptamanah.databinding.ListEventsAdminBinding

class EventAdminAdapter : ListAdapter<DataItem, EventAdminAdapter.ViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallBack: OnItemClickCallBack

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ListEventsAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val  event = getItem(position)
        holder.binding.apply {
            namaTempat.text = event.namaTempat
            namaEvent.text = event.namaEvent
            alamat.text = event.alamat

            if (event.saleStatus == "end") {
                statusIV.setImageResource(R.drawable.status_border_end)
                tvStatus.text = "Selesai"
                tvStatus.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.disable
                    )
                )
                DetailBtn.setOnClickListener {
                    onItemClickCallBack.onItemClicked(event)
                }
            } else {
                tvStatus.text = "On-Going"
                DetailBtn.setOnClickListener {
                    onItemClickCallBack.onItemClicked(event)
                }
            }
        }
    }

    class ViewHolder(val binding: ListEventsAdminBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(
                oldItem: DataItem,
                newItem: DataItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DataItem,
                newItem: DataItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(user: DataItem)
    }
}