package com.example.bettinalogistics.ui.fragment.ship.order_list

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.baseapp.BaseRclvAdapter
import com.example.baseapp.BaseRclvVH
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.Track

class TrackListOrderAdapter : BaseRclvAdapter() {
    var onShowDetailTrack: ((Track, View) -> Unit)? = null
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
        private val ivShowDetailTracking: ImageView =
            itemView.findViewById(R.id.ivShowDetailTracking)

        init {
            if(!ivShowDetailTracking.isVisible)
            {
               itemView.setSafeOnClickListener {
                   val position = bindingAdapterPosition
                   if (position > -1) {
                       onShowDetailTrack?.invoke(mDataSet[position] as Track, it)
                   }
               }
            }
            else{
                ivShowDetailTracking.setSafeOnClickListener {
                    val position = bindingAdapterPosition
                    if (position > -1) {
                        onShowDetailTrack?.invoke(mDataSet[position] as Track, it)
                    }
                }
            }
        }

        override fun onBind(data: Track) {
            tvTrackingDate.text = data.dateUpdate ?: ""
            tvTrackingAddressOrderItem.text = data.address ?: ""
            tvTrackingStatusOrderItem.text = data.status ?: ""
            ivShowDetailTracking.isVisible = AppData.g().currentUser?.role == "ship"
        }
    }
}