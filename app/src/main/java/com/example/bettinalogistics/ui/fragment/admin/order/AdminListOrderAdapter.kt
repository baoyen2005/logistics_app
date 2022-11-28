package com.vnpay.merchant.ui.fragment.history

import android.view.View
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.example.baseapp.BaseRclvAdapter
import com.example.baseapp.BaseRclvVH
import com.example.baseapp.utils.UtilsBase
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.utils.DataConstant

class AdminListOrderAdapter : BaseRclvAdapter(), Filterable {
    companion object {
        const val TYPE_DATE = 1
        const val SUCCESS = "1"
        const val FAILED = "2"
        const val SUSPECT = "3"
        const val CANCEL_FAILED = "4"
        const val CANCEL_SUCCESS = "5"
        const val PROCESSING = "6"
        const val SUSPECT_CHEAT = "7"
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun getLayoutResource(viewType: Int): Int = when (viewType) {
        TYPE_DATE -> R.layout.item_admin_order_list_date
        else -> R.layout.item_admin_order_list_item
    }

    override fun onCreateVH(itemView: View, viewType: Int): BaseRclvVH<*> =
        when (viewType) {
            TYPE_DATE -> DateHistoryViewHolder(itemView)
            else -> OrderListViewHolder(itemView)
        }

    override fun getItemViewType(position: Int): Int {
        if (mDataSet[position] is CommonEntity)
            return TYPE_DATE
        return super.getItemViewType(position)
    }

    inner class OrderListViewHolder(itemView: View) : BaseRclvVH<Order>(itemView) {
        private val tvAdminTypeTransport: TextView by lazy { itemView.findViewById(R.id.tvAdminTypeTransport) }
        private val tvAdminOrderCode: TextView by lazy { itemView.findViewById(R.id.tvAdminOrderCode) }
        private val tvAdminOrderAmountItem: TextView by lazy { itemView.findViewById(R.id.tvAdminOrderAmountItem) }
        private val tvAdminStatusOrderItem: TextView by lazy { itemView.findViewById(R.id.tvAdminStatusOrderItem) }
        private val ivImageIcon: ImageView by lazy { itemView.findViewById(R.id.ivImageIcon) }
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
            itemView.setSafeOnClickListener {
                onItemClickListener?.onClick(mDataSet[bindingAdapterPosition] as Order)
            }
        }

        override fun onBind(data: Order) {
            tvAdminTypeTransport.text = data.typeTransportation ?: ""
            tvAdminOrderAmountItem.text =
                UtilsBase.g().getDotMoneyHasCcy(data.amountAfterDiscount.toString(), "đ")
            tvAdminOrderCode.text = data.code ?: ""
            tvAdminStatusOrderItem.text = data.statusOrder ?: ""
           // ivImageIcon.setImageResource(getIconTransaction())

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

    fun getIconTransaction(status: String?, paymentSource: Long?): Int {
        val listSuccess = listOf(SUCCESS, CANCEL_SUCCESS)
        val listFaild = listOf(FAILED, CANCEL_FAILED)
        val listProcess = listOf(SUSPECT, PROCESSING, SUSPECT_CHEAT)
        return R.drawable.icon_facebook

    }

    inner class DateHistoryViewHolder(itemView: View) : BaseRclvVH<CommonEntity>(itemView) {
        private val tvDate: TextView by lazy { itemView.findViewById(R.id.tvAdminOrderListDateItem) }
        private val tvAdminOrderAmountItem: TextView by lazy { itemView.findViewById(R.id.tvAdminOrderListAmountItem) }

        override fun onBind(data: CommonEntity) {
            tvDate.text = data.title
            tvAdminOrderAmountItem.text = data.description
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
                        val order = data as Order
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (normalization((order.code)).contains(key)
                            || normalization(order.typeTransportation).contains(key)
                        ) {
                            filteredList.add(data)
                        }
                    }
                    results.values = filteredList
                    return results
                }
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults?) {
                mDataSet.clear()
                if (filterResults?.values == null) {

                    mDataSet.addAll(mDataSet as ArrayList<Any>)
                } else {
                    mDataSet.addAll(filterResults.values as ArrayList<Any>)
                }
                notifyDataSetChanged()
            }
        }
    }
}

