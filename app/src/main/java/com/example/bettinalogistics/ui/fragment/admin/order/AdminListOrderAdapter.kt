package com.example.bettinalogistics.ui.fragment.admin.order

import android.view.View
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.example.baseapp.BaseRclvAdapter
import com.example.baseapp.BaseRclvVH
import com.example.baseapp.BaseVHData
import com.example.baseapp.utils.UtilsBase
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.utils.DataConstant

class AdminListOrderAdapter : BaseRclvAdapter(), Filterable {
    companion object {
        const val TYPE_DATE = 1
    }
    private val listFilter = mutableListOf<OrderVHData>()
    var onItemClickListener: ((Order) -> Unit)? = null
    var onSearchResult: ((Int) -> Unit)? = null

    override fun getItemCount(): Int {
        return listFilter.size
    }

    override fun getItemDataAtPosition(position: Int): Any {
        return listFilter[position]
    }

    fun setData(dataList: List<Any>) {
        val datas = mutableListOf<OrderVHData>()
        dataList.forEachIndexed {index, data->
            datas.add(OrderVHData(data))
        }
        listFilter.clear()
        listFilter.addAll(datas)
        reset(listFilter)
    }

    override fun getLayoutResource(viewType: Int): Int = when (viewType) {
        TYPE_DATE -> R.layout.item_admin_order_list_date
        else -> R.layout.item_admin_order_list_item
    }

    override fun onCreateVH(itemView: View, viewType: Int): BaseRclvVH<*> =
        when (viewType) {
            TYPE_DATE -> DateOrderViewHolder(itemView)
            else -> OrderListViewHolder(itemView)
        }

    override fun getItemViewType(position: Int): Int {
        if (listFilter[position].realData is CommonEntity)
            return TYPE_DATE
        return super.getItemViewType(position)
    }
    class OrderVHData(data: Any) : BaseVHData<Any>(data){

    }
    inner class OrderListViewHolder(itemView: View) : BaseRclvVH<OrderVHData>(itemView) {
        private val tvAdminTypeTransport: TextView by lazy { itemView.findViewById(R.id.tvAdminTypeTransport) }
        private val tvAdminOrderCode: TextView by lazy { itemView.findViewById(R.id.tvAdminOrderCode) }
        private val tvAdminOrderAmountItem: TextView by lazy { itemView.findViewById(R.id.tvAdminOrderAmountItem) }
        private val tvAdminStatusOrderItem: TextView by lazy { itemView.findViewById(R.id.tvAdminStatusOrderItem) }
        private val ivImageIcon: ImageView by lazy { itemView.findViewById(R.id.ivImageIcon) }
        private val llViewDetail: LinearLayoutCompat by lazy { itemView.findViewById(R.id.llViewDetail) }
        private val colorSuccess: Int by lazy {
            itemView.resources.getColor(R.color.merchant_color_004a9c)
        }
        private val colorProcess: Int by lazy {
            itemView.resources.getColor(R.color.merchant_color_ffbe21)
        }
        private val colorFailed: Int by lazy {
            itemView.resources.getColor(R.color.merchant_color_ea5b5b)
        }

        init {
            llViewDetail.setSafeOnClickListener {
                if(bindingAdapterPosition > -1){
                    val data = listFilter[bindingAdapterPosition]
                    data.realData?.let {
                        onItemClickListener?.invoke(it as Order)
                    }
                }
            }
        }

        override fun onBind(orderVHData: OrderVHData) {
            val data = orderVHData.realData as Order
            tvAdminTypeTransport.text = data.typeTransportation ?: ""
            tvAdminOrderAmountItem.text =
                UtilsBase.g().getDotMoneyHasCcy(data.amountBeforeDiscount.toString(), "đ")
            tvAdminOrderCode.text = data.orderCode ?: ""
            tvAdminStatusOrderItem.text = data.statusOrder ?: ""
            ivImageIcon.setImageResource(getIconTransaction(data.statusOrder))

            when (data.statusOrder) {
                DataConstant.ORDER_STATUS_CONFIRM,
                DataConstant.ORDER_STATUS_DELIVERED -> {
                    tvAdminStatusOrderItem.setTextColor(colorSuccess)
                    tvAdminOrderAmountItem.setTextColor(colorSuccess)
                }
                DataConstant.ORDER_STATUS_CANCEL -> {
                    tvAdminStatusOrderItem.setTextColor(colorFailed)
                    tvAdminOrderAmountItem.setTextColor(colorFailed)
                }
                DataConstant.ORDER_STATUS_PENDING -> {
                    tvAdminStatusOrderItem.setTextColor(colorProcess)
                    tvAdminOrderAmountItem.setTextColor(colorProcess)
                }
            }
        }

    }

    fun getIconTransaction(status: String?): Int {
        val listSuccess =
            listOf(DataConstant.ORDER_STATUS_DELIVERED, DataConstant.ORDER_STATUS_PAYMENT_PAID)
        val listFaild = listOf(DataConstant.ORDER_STATUS_CANCEL)
        val listProcess =
            listOf(DataConstant.ORDER_STATUS_PENDING, DataConstant.ORDER_STATUS_PAYMENT_WAITING)
        return when {
            listSuccess.contains(status) -> {
                R.drawable.ic_order_success
            }
            listFaild.contains(status) -> {
                R.drawable.ic_order_fail
            }
            else -> {
                R.drawable.ic_order_process
            }
        }
    }

    inner class DateOrderViewHolder(itemView: View) : BaseRclvVH<OrderVHData>(itemView) {
        private val tvDate: TextView by lazy { itemView.findViewById(R.id.tvAdminOrderListDateItem) }
        private val tvAdminOrderAmountItem: TextView by lazy { itemView.findViewById(R.id.tvAdminOrderListAmountItem) }

        override fun onBind(data: OrderVHData) {
            tvDate.text = (data.realData as CommonEntity).title
            tvAdminOrderAmountItem.text =  (data.realData as CommonEntity).description
        }
    }

    interface OnItemClickListener {
        fun onClick(item: Order)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString().trim()
                if (charString.isEmpty()) {
                    return null
                } else {
                    val filteredList: MutableList<Any> = ArrayList()
                    val results = FilterResults()
                    val key = normalization(charString)
                    mDataSet.forEach { data ->
                        if (data is OrderVHData && data.realData is Order && normalization(((data.realData as Order).orderCode?.lowercase())).contains(key.lowercase())
                            ||data is OrderVHData && data.realData is Order && normalization((data.realData as Order).typeTransportation?.lowercase()).contains(key.lowercase())
                        ) {
                            filteredList.add(data)
                        }
                    }
                    results.values = filteredList
                    return results
                }
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults?) {
                listFilter.clear()
                if (filterResults?.values == null) {
                    listFilter.addAll(mDataSet as ArrayList<OrderVHData>)
                } else {
                    listFilter.addAll(filterResults.values as ArrayList<OrderVHData>)
                }
                onSearchResult?.invoke(listFilter.size)
                notifyDataSetChanged()
            }
        }
    }
}

