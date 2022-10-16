package com.example.bettinalogistics.ui.addorder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baseapp.view.Textview
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.Order


class AddOrderAdapter : RecyclerView.Adapter<AddOrderAdapter.ViewHolder>() {
    private lateinit var context: Context
    private var orderList = ArrayList<Order>()

    var itemExpandOnClick : ((Order?) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(
            R.layout.add_order_item_layout,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orderList[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    fun resetOrderList(orders: ArrayList<Order>) {
        orderList.clear()
        orderList.addAll(orders)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAddOrderImageItem = itemView.findViewById<ImageView>(R.id.ivAddOrderImageItem)
        private val addOrderNameItem = itemView.findViewById<Textview>(R.id.addOrderNameItem)
        private val addOrderTypeItem = itemView.findViewById<Textview>(R.id.addOrderTypeItem)
        private val ivAddOrderExpandItem =
            itemView.findViewById<ImageView>(R.id.ivAddOrderExpandItem)
        private val viewDeviceAddOrderItem =
            itemView.findViewById<View>(R.id.viewDeviceAddOrderItem)

        init {
            ivAddOrderExpandItem.setOnClickListener {
                itemExpandOnClick?.invoke(orderList[adapterPosition])
            }
        }

        fun bind(order: Order) {
            Glide.with(context).load(order.imgUri).into(ivAddOrderImageItem)
            addOrderNameItem.text = order.productName
            addOrderTypeItem.text =
                if (order.isOrderLCL) context.getString(R.string.str_order_lcl) else context.getString(
                    R.string.str_order_fcl
                )
            if (adapterPosition != orderList.size - 1) {
                viewDeviceAddOrderItem.visibility = View.VISIBLE
            } else viewDeviceAddOrderItem.visibility = View.GONE
        }
    }
}