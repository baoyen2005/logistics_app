package com.example.bettinalogistics.ui.fragment.followtrask

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.baseapp.BaseRclvAdapter
import com.example.baseapp.BaseRclvVH
import com.example.baseapp.view.getAmountVND
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_STATUS_CANCEL
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_STATUS_PAYMENT_PAID
import com.example.bettinalogistics.utils.DataConstant.Companion.ORDER_STATUS_PAYMENT_WAITING

class FollowOrderTrackingAdapter : BaseRclvAdapter() {
    companion object {
        const val TYPE_DATE = 1
    }

    var onItemClickListener: ((Order) -> Unit)? = null

    override fun getLayoutResource(viewType: Int): Int =
        when (viewType) {
            TYPE_DATE -> R.layout.item_transaction_date
            else -> R.layout.item_order_follow
        }

    override fun onCreateVH(itemView: View, viewType: Int): BaseRclvVH<*> = when (viewType) {
        TYPE_DATE -> DateHistoryViewHolder(itemView)
        else -> FollowOrderViewHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        if (mDataSet[position] is CommonEntity)
            return TYPE_DATE
        return super.getItemViewType(position)
    }

    inner class FollowOrderViewHolder(itemView: View) : BaseRclvVH<Order>(itemView) {
        private val tvOrderCode: TextView by lazy { itemView.findViewById(R.id.tvOrderCode) }
        private val tvTransportType: TextView by lazy { itemView.findViewById(R.id.tvTransportType) }
        private val tvAmountOrderItem: TextView by lazy { itemView.findViewById(R.id.tvAmountOrderItem) }
        private val tvOrderStatusItem: TextView by lazy { itemView.findViewById(R.id.tvOrderStatusItem) }
        private val ivOrderStatusItem: ImageView by lazy { itemView.findViewById(R.id.ivOrderStatusItem) }

        init {
            itemView.setSafeOnClickListener {
                if (bindingAdapterPosition > -1) {
                    onItemClickListener?.invoke(mDataSet[bindingAdapterPosition] as Order)
                }
            }
        }

        override fun onBind(data: Order) {
            tvOrderCode.text = data.code ?: ""
            tvTransportType.text = data.typeTransportation ?: ""
            tvAmountOrderItem.text = data.amountBeforeDiscount.toString().getAmountVND()
            tvOrderStatusItem.text = data.statusOrder
            ivOrderStatusItem.setImageResource(getIconOrder(data.statusOrder))
        }
    }

    fun getIconOrder(status: String?): Int {
        val listSuccessPayment = listOf(DataConstant.ORDER_STATUS_DELIVERED, DataConstant.ORDER_STATUS_CONFIRM, ORDER_STATUS_PAYMENT_PAID)
        val listFailedPayment = listOf(ORDER_STATUS_CANCEL)
        val listProcessPayment = listOf(DataConstant.ORDER_STATUS_PENDING, ORDER_STATUS_PAYMENT_WAITING)
        return when {
            listSuccessPayment.contains(status) -> {
                R.drawable.ic_order_success
            }
            listFailedPayment.contains(status) -> {
                R.drawable.ic_order_fail
            }
            else -> {
                R.drawable.ic_order_process
            }
        }
    }

    inner class DateHistoryViewHolder(itemView: View) : BaseRclvVH<CommonEntity>(itemView) {
        private val tvDate: TextView by lazy { itemView.findViewById(R.id.tvDate) }
        private val tvAmount: TextView by lazy { itemView.findViewById(R.id.tvTranAmount) }

        override fun onBind(data: CommonEntity) {
            tvDate.text = data.title
            tvAmount.text = data.description
        }
    }
}

