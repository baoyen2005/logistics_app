package com.example.bettinalogistics.ui.fragment.ship.order_list

import android.view.View
import android.widget.TextView
import com.example.baseapp.BaseRclvAdapter
import com.example.baseapp.BaseRclvVH
import com.example.bettinalogistics.R
import com.example.bettinalogistics.model.Track

class TrackListOrderAdapter : BaseRclvAdapter() {

    override fun getLayoutResource(viewType: Int): Int = R.layout.item_tracking


    override fun onCreateVH(itemView: View, viewType: Int): BaseRclvVH<*> =
        TrackOrderViewHolder(itemView)

    inner class TrackOrderViewHolder(itemView: View) :
        BaseRclvVH<Track>(itemView) {
        private val tvTrackingDate: TextView = itemView.findViewById(R.id.tvTrackingDate)
        private val tvTrackingAddressOrderItem: TextView =
            itemView.findViewById(R.id.tvTrackingAddressOrderItem)
        private val tvTrackingStatusOrderItem: TextView =
            itemView.findViewById(R.id.tvTrackingStatusOrderItem)

        override fun onBind(data: Track) {
            tvTrackingDate.text = data.dateUpdate ?: ""
            tvTrackingAddressOrderItem.text = data.address ?: ""
            tvTrackingStatusOrderItem.text = data.status ?: ""
        }
    }
}