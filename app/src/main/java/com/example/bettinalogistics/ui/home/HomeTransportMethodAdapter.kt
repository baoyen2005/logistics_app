package com.example.bettinalogistics.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.CommonEntity

class HomeTransportMethodAdapter(var listCommonEntity: List<CommonEntity>) :
    RecyclerView.Adapter<HomeTransportMethodAdapter.ViewHolder>()   {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_transport_method_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val commonEntity = listCommonEntity[position]
        holder.bind(commonEntity)
    }

    override fun getItemCount(): Int {
        return listCommonEntity.size
    }

    fun resetList(data : List<CommonEntity>){
        listCommonEntity = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var icon: ImageView
        var title: TextView
        init {
            icon = view.findViewById<ImageView>(R.id.ivHomeTransportMethodIconItem)
            title = view.findViewById<TextView>(R.id.tvHomeTransportMethodTitleItem)
        }
        fun bind(commonEntity: CommonEntity) {
            commonEntity.icon?.let { icon.setImageResource(it) }
            title.text = commonEntity.title
        }
    }
}