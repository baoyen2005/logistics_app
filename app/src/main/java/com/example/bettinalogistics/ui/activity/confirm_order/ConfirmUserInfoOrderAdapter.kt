package com.example.bettinalogistics.ui.activity.confirm_order

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.example.baseapp.BaseRclvAdapter
import com.example.baseapp.BaseRclvVH
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.ConfirmOrder


class ConfirmUserInfoOrderAdapter : BaseRclvAdapter() {
    companion object {
        const val TYPE_HEADER = 1
        const val TYPE_ITEM_USER = 2
        const val TYPE_ITEM_ORDER = 3
        const val TYPE_LINE = 4
    }

    override fun getLayoutResource(viewType: Int): Int =
        when (viewType) {
            TYPE_HEADER -> R.layout.item_order_confirm_header
            TYPE_LINE -> R.layout.item_order_confirm_line
            TYPE_ITEM_USER -> R.layout.item_user_info_confirm
            else -> R.layout.item_order_info_confirm
        }

    override fun onCreateVH(itemView: View, viewType: Int): BaseRclvVH<*> =
        when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(itemView)
            TYPE_LINE -> LineViewHolder(itemView)
            TYPE_ITEM_USER -> UserInforConfirmViewHolder(itemView)
            else -> OrderInforConfirmViewHolder(itemView)
        }

    override fun getItemViewType(position: Int): Int {
        if (mDataSet[position] is CommonEntity){
            return (mDataSet[position] as CommonEntity).getTypeLayout()
        }
        return super.getItemViewType(position)
    }

    inner class OrderInforConfirmViewHolder(itemView: View) :
        BaseRclvVH<ConfirmOrder>(itemView) {
        private val ivOrderImageItem : ImageView = itemView.findViewById(R.id.ivOrderImageItem)
        private val tvOrderNameConfirmItem: TextView =  itemView.findViewById(R.id.tvOrderNameConfirmItem)
        private val tvOrderTypeTransactionConfirmItem: TextView = itemView.findViewById(R.id.tvOrderTypeTransactionConfirmItem)
        private val tvOrderMethodTransactionConfirmItem: TextView = itemView.findViewById(R.id.tvOrderMethodTransactionConfirmItem)
        private val tvOrderQuantityTransactionConfirmItem: TextView = itemView.findViewById(R.id.tvOrderQuantityTransactionConfirmItem)
        private val tvOrderNoteConfirmItem: TextView = itemView.findViewById(R.id.tvOrderNoteConfirmItem)
        private val tvOrderStatusTransactionConfirmItem: TextView = itemView.findViewById(R.id.tvOrderStatusTransactionConfirmItem)

        override fun onBind(data: ConfirmOrder) {
            Glide.with(itemView.context).load(data.product?.imgUri ?: "").into(ivOrderImageItem)
            tvOrderNameConfirmItem.text = data.product?.productName ?: ""
            tvOrderTypeTransactionConfirmItem.text = data.transportType ?: ""
            tvOrderMethodTransactionConfirmItem.text = data.transportMethod ?: ""
            tvOrderQuantityTransactionConfirmItem.text = (data.amount ?: "").toString()
            //   tvOrderNoteConfirmItem.text = data.product?.note?:""
            tvOrderStatusTransactionConfirmItem.text = data.status
        }
    }

    inner class HeaderViewHolder(itemView: View) : BaseRclvVH<CommonEntity>(itemView) {
        private val ivHeadIconItem: AppCompatImageView = itemView.findViewById(R.id.ivHeadIconItem)
        private val tvHeaderTitleItem: TextView =  itemView.findViewById(R.id.tvHeaderTitleItem)
        private val tvOrderAmount: TextView =  itemView.findViewById(R.id.tvOrderAmount)

        override fun onBind(data: CommonEntity) {
            data.icon?.let { ivHeadIconItem.setImageResource(it) }
            tvHeaderTitleItem.text = data.getHeader()
            tvOrderAmount.text = data.getCounter().toString()
        }
    }

    inner class LineViewHolder(itemView: View) : BaseRclvVH<CommonEntity>(itemView) {
        override fun onBind(data: CommonEntity) {

        }
    }

    inner class UserInforConfirmViewHolder(itemView: View): BaseRclvVH<CommonEntity>(itemView){
        private val tvUserInfoConfirmTitleItem: TextView = itemView.findViewById(R.id.tvUserInfoConfirmTitleItem)
        private val tvUserInfoConfirmDescripItem: TextView = itemView.findViewById(R.id.tvUserInfoConfirmDescripItem)

        override fun onBind(data: CommonEntity) {
            tvUserInfoConfirmTitleItem.text = data.title
            tvUserInfoConfirmDescripItem.text = data.description
        }
    }
}