package com.example.bettinalogistics.ui.fragment.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.CommonEntity

class HomeTransportTypeAdapter(var listCommonEntity: List<CommonEntity>) :
    RecyclerView.Adapter<HomeTransportTypeAdapter.ViewHolder>()   {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_transport_type_item, parent, false)
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
            icon = view.findViewById<ImageView>(R.id.ivHomeTransportTypeIconItem)
            title = view.findViewById<TextView>(R.id.tvHomeTransportTypeTitleItem)
        }
        fun bind(commonEntity: CommonEntity) {
            commonEntity.icon?.let { icon.setImageResource(it) }
            title.text = commonEntity.title
        }
    }
}