package com.example.bettinalogistics.ui.activity.addorder

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baseapp.view.Textview
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.Product


class AddOrderAdapter : RecyclerView.Adapter<AddOrderAdapter.ViewHolder>() {
    private lateinit var context: Context
    private var productList = ArrayList<Product>()

    var itemExpandOnClick: ((Product?) -> Unit)? = null

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
        val order = productList[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun resetOrderList(products: ArrayList<Product>) {
        productList.clear()
        productList.addAll(products)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAddOrderImageItem = itemView.findViewById<ImageView>(R.id.ivAddOrderImageItem)
        private val addOrderNameItem = itemView.findViewById<Textview>(R.id.addOrderNameItem)
        private val addOrderTypeItem = itemView.findViewById<Textview>(R.id.addOrderTypeItem)
        private val tvAddOrderQuantityItem =
            itemView.findViewById<Textview>(R.id.tvAddOrderQuantityItem)
        private val ivAddOrderExpandItem =
            itemView.findViewById<ImageView>(R.id.ivAddOrderExpandItem)
        private val viewDeviceAddOrderItem =
            itemView.findViewById<View>(R.id.viewDeviceAddOrderItem)

        init {
            ivAddOrderExpandItem.setOnClickListener {
                itemExpandOnClick?.invoke(productList[adapterPosition])
            }
        }

        fun bind(product: Product) {
            Glide.with(context).load(Uri.parse(product.imgUri)).into(ivAddOrderImageItem)
            addOrderNameItem.text = product.productName
            addOrderTypeItem.text =
                if (product.isOrderLCL) context.getString(R.string.str_order_lcl) else context.getString(
                    R.string.str_order_fcl
                )
            if (adapterPosition != productList.size - 1) {
                viewDeviceAddOrderItem.visibility = View.VISIBLE
            } else viewDeviceAddOrderItem.visibility = View.GONE
            tvAddOrderQuantityItem.text =
                if (!product.isOrderLCL) "${product.quantity} cái" else "${product.quantity} cont"
        }
    }
}