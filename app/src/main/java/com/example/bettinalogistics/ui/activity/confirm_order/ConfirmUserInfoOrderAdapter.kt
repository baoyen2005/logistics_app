package com.example.bettinalogistics.ui.activity.confirm_order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.baseapp.view.Textview
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.CommonEntity


class ConfirmUserInfoOrderAdapter : RecyclerView.Adapter<ConfirmUserInfoOrderAdapter.ViewHolder>() {
    private lateinit var context: Context
    private var commonEntityList = ArrayList<CommonEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(
            R.layout.confirm_user_order_transportation_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val commonEntity = commonEntityList[position]
        holder.bind(commonEntity)
    }

    override fun getItemCount(): Int {
        return commonEntityList.size
    }

    fun resetCommonEntityList(list: ArrayList<CommonEntity>) {
        commonEntityList.clear()
        commonEntityList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvConfirmOrderTitle = itemView.findViewById<Textview>(R.id.tvConfirmOrderTitle)
        private val tvConfirmOrderContent = itemView.findViewById<Textview>(R.id.tvConfirmOrderContent)

        init {

        }

        fun bind(commonEntity: CommonEntity) {
            tvConfirmOrderTitle.text = commonEntity.title
            tvConfirmOrderContent.text = commonEntity.description
        }
    }
}