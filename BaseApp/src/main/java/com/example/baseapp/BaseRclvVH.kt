package com.example.baseapp

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.baseapp.view.setSafeOnClickListener

abstract class BaseRclvVH<T>(itemView: View) : RecyclerView.ViewHolder(itemView), IOnBind<T> {

    abstract override fun onBind(data: T)

    override fun onBind(data: T, payload: List<Any>) {

    }

    fun clickOn(
        view: View,
        listener: View.OnClickListener?
    ) {
        if (listener != null) {
            view.setSafeOnClickListener {
                if (adapterPosition > -1) {
                    listener.onClick(view)
                }
            }
        }
    }

    fun longClickOn(
        view: View,
        listener: View.OnLongClickListener?
    ) {
        if (listener != null) {
            view.setOnLongClickListener {
                if (adapterPosition > -1) {
                    return@setOnLongClickListener listener.onLongClick(view)
                }
                true
            }
        }
    }
}