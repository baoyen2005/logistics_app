package com.example.bettinalogistics.ui.fragment.user.notification

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.baseapp.BaseRclvAdapter
import com.example.baseapp.BaseRclvVH
import com.example.baseapp.UtilsBase
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.Notification
import com.example.bettinalogistics.ui.fragment.admin.order.AdminListOrderAdapter.Companion.TYPE_DATE
import com.example.bettinalogistics.utils.Utils_Date
import com.example.bettinalogistics.utils.Utils_Date.DATE_PATTERN_DD_MM_YYYY_HH_MM_SS
import com.example.bettinalogistics.utils.Utils_Date.DATE_PATTERN_ddMMYYYY
import com.example.bettinalogistics.utils.toDate

class NotificationAdapter : BaseRclvAdapter() {

    companion object {

    }

    override fun getLayoutResource(viewType: Int): Int = when (viewType) {
        TYPE_DATE -> R.layout.item_transaction_date
        else -> R.layout.item_notification
    }

    override fun onCreateVH(itemView: View, viewType: Int): BaseRclvVH<*> =
        when (viewType) {
            TYPE_DATE -> DateNotifyViewHolder(itemView)
            else -> NotifyViewHolder(itemView)
        }

    override fun getItemViewType(position: Int): Int {
        if (mDataSet[position] is CommonEntity)
            return TYPE_DATE
        return super.getItemViewType(position)
    }

    inner class NotifyViewHolder(itemView: View) : BaseRclvVH<Notification>(itemView) {
        private val tvTitle: TextView by lazy { itemView.findViewById(R.id.tvNotifyTitle) }
        private val ivIcon: ImageView by lazy { itemView.findViewById(R.id.ivIcon) }
        private val tvTime: TextView by lazy { itemView.findViewById(R.id.tvTime) }
        private val tvNotifyDes: TextView by lazy { itemView.findViewById(R.id.tvNotifyDes) }
        private val rvInfo: RecyclerView by lazy { itemView.findViewById(R.id.rvInfoOTT) }

        init {
            itemView.setSafeOnClickListener {

            }
        }

        override fun onBind(data: Notification) {
            tvTitle.text = data.notificationType ?: ""
            tvNotifyDes.text = data.contentNoti ?: ""
            tvNotifyDes.visibility = View.VISIBLE
            tvTime.text = data.confirmDate?.toDate(DATE_PATTERN_DD_MM_YYYY_HH_MM_SS)?.let {
                Utils_Date.getCurrentTimeByFormat(
                    Utils_Date.DATE_PATTERN_DD_MM_YYYY_HH_MM_SS,
                    it.time
                )
            }
            data.contentNoti?.let { setColorContent(it, data, itemView) }
            ivIcon.setImageResource(R.drawable.ic_order_success)
            rvInfo.visibility = View.GONE
        }
    }

    private fun setColorContent(
        content: String,
        notification: Notification,
        itemView: View
    ): SpannableString {
        val resultContent = SpannableString(content)
        val customer = notification.order?.company?.name ?: ""
        val transId = notification.order?.orderCode ?: ""
        if (resultContent.contains(customer)) {
            val startIndex = resultContent.indexOf(customer)
            val endIndex = startIndex + customer.length
            resultContent.setSpan(
                ForegroundColorSpan(
                    itemView.context.resources.getColor(R.color.merchant_color_4a4a4a)
                ), startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            UtilsBase.g().getFont(2, itemView.context)?.let {
                resultContent.setSpan(
                    StyleSpan(it.style),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        if (resultContent.contains(transId)) {
            val startIndex = resultContent.indexOf(transId)
            val endIndex = startIndex + transId.length
            resultContent.setSpan(
                ForegroundColorSpan(
                    itemView.context.resources.getColor(R.color.merchant_color_4a4a4a)
                ), startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            UtilsBase.g().getFont(2, itemView.context)?.let {
                resultContent.setSpan(
                    StyleSpan(it.style),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        return resultContent
    }

    inner class DateNotifyViewHolder(itemView: View) : BaseRclvVH<CommonEntity>(itemView) {
        private val tvDate: TextView by lazy { itemView.findViewById(R.id.tvDate) }
        private val tvAmount: TextView by lazy { itemView.findViewById(R.id.tvTranAmount) }

        override fun onBind(data: CommonEntity) {
            tvDate.text = data.title
            tvAmount.text = data.description
        }
    }
}