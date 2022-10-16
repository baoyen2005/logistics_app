package com.example.baseapp.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.text.util.Linkify
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.example.baseapp.R

class ConfirmDialog( val mContext: Context) : Dialog(mContext){
    private var mContent: String? = null
    private var mLeftTitle: String? = null
    private var mRightTitle: String? = null
    private var mTitle: String? = null
    private var mAlertIcon = -1
    private  var mBtCancel: TextView?=null
    private  var mBtAgree: TextView?=null
    private  var mTvContent: TextView?=null
    private  var mTvTitle: TextView?=null
    private  var mIvAlert: ImageView?=null
    private var needActionAll = false
    private var cancelOnClick: () -> Unit? = {}
    private var agreeOnClick: () -> Unit? = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_notice)
        mTvTitle = findViewById(R.id.tv_notice_title_dal)
        mTvContent = findViewById(R.id.tv_notice_content_dal)
        mBtAgree = findViewById(R.id.bt_agree)
        mBtCancel = findViewById(R.id.bt_cancel)
        mIvAlert = findViewById(R.id.ivNoticeAlertDal)
        mBtAgree!!.setOnClickListener {
            agreeOnClick()
            dismiss()
        }
        mBtCancel!!.setOnClickListener {
            cancelOnClick()
            dismiss()
        }

        if (mLeftTitle != null) {
            if (mRightTitle != null) mBtAgree!!.text = mRightTitle
            if (mBtCancel!!.visibility != View.VISIBLE) mBtCancel!!.visibility = View.VISIBLE
            mBtCancel!!.text = mLeftTitle
        } else {
            mBtCancel!!.visibility = View.GONE
            if (mRightTitle != null) mBtAgree!!.setText(mRightTitle)
        }
        if (mTitle != null) {
            mTvTitle!!.text = mTitle
            mTvTitle!!.visibility = View.VISIBLE
        }
        if (mAlertIcon != -1) {
            mIvAlert!!.setImageDrawable(ContextCompat.getDrawable(mContext, mAlertIcon))
            mIvAlert!!.visibility = View.VISIBLE
        }
        setContent(mContent)
        setTouchArea(true)
        setOnCancelListener { actionDialog() }
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(listener)
        //actionDialog();
    }

    private fun actionDialog() {
        if (needActionAll
        ) {
            if (mBtCancel != null && mBtCancel!!.visibility == View.VISIBLE) cancelOnClick()
            else
                agreeOnClick()
        }
    }

    fun show(content: String?) {
        if (mTvContent == null) {
            mContent = content
        } else {
            mTvContent!!.text = content
        }
        setTouchArea(true)
        if (!isShowing) show()
    }

    override fun dismiss() {
        super.dismiss()
        newBuild()
    }

    fun setAction(needActionAll: Boolean) {
        this.needActionAll = needActionAll
    }

    fun setTouchArea(b: Boolean) {
        setCancelable(b)
        setCanceledOnTouchOutside(b)
    }

    fun newBuild(): ConfirmDialog {
        if (mBtCancel != null) {
            mBtCancel!!.visibility = View.GONE
        }
        if (mTvTitle != null) mTvTitle!!.visibility = View.GONE
        needActionAll = false
        cancelOnClick = {  }
        agreeOnClick = {  }
        mLeftTitle = null
        mRightTitle = context.getString(R.string.agree)
        if (mBtAgree != null) {
            mBtAgree!!.text = mRightTitle
        }
        if (mTvTitle != null) {
            mTitle = mContext.getString(R.string.notice)
            mTvTitle!!.visibility = View.GONE
        }
        if (mIvAlert != null) {
            mAlertIcon = -1
            mIvAlert!!.visibility = View.GONE
        }
        return this
    }

    fun setNotice(content: String?): ConfirmDialog {
        try {
            setContent(content)
            if (!isShowing) show()
        } catch (e: Exception) {
        }
        return this
    }

    private fun setContent(content: String?) {
        mContent = content
        if (mTvContent != null) {
            if (mContent!!.contains("<strong>") || mContent!!.contains("</a>")) mTvContent!!.text =
                Html.fromHtml(mContent) else {
                mTvContent!!.text = mContent
                try {
                    Linkify.addLinks(mTvContent!!, Linkify.ALL)
                } catch (e: Exception) {
                    Log.wtf("EXX", e)
                }
            }
        }
    }

    fun setNotice(@StringRes idContent: Int): ConfirmDialog {
        mContent = context.getString(idContent)
        setContent(mContent)
        try {
            if (!isShowing) show()
        } catch (e: Exception) {
            e.message
        }
        return this
    }

    fun addButtonCancel(@StringRes id: Int): ConfirmDialog {
        return addButtonCancel(context.getString(id))
    }

    fun addButtonCancel(onLeftOnClick: View.OnClickListener?): ConfirmDialog {
        if (mBtCancel != null) {
            mBtCancel!!.visibility = View.VISIBLE
            mBtCancel!!.text = context.getString(R.string.agree)
        } else {
            mLeftTitle = context.getString(R.string.cancel)
        }
        return this
    }

    fun addButtonCancel(title: String?): ConfirmDialog {
        if (mBtCancel != null) {
            mBtCancel!!.visibility = View.VISIBLE
            mBtCancel!!.text = title
        } else {
            mLeftTitle = title
        }
        return this
    }

    fun addButtonCancel(title: Int, onLeftOnClick:() -> Unit): ConfirmDialog {
        addButtonCancel(context.getString(title), onLeftOnClick)
        return this
    }

    fun addButtonCancel(title: String?, onLeftOnClick:() -> Unit): ConfirmDialog {
        cancelOnClick = onLeftOnClick
        if (mBtCancel != null) {
            mBtCancel!!.visibility = View.VISIBLE
            mBtCancel!!.text = title
        } else {
            mLeftTitle = title
        }
        return this
    }

    fun addButtonAgree(onRightClick:  () -> Unit): ConfirmDialog {
        agreeOnClick = onRightClick
        if (mBtAgree != null) {
            mBtAgree!!.setText(R.string.agree)
        } else {
            mRightTitle = context.getString(R.string.agree)
        }
        return this
    }

    fun addButtonAgree(title: Int, onRightClick:  () -> Unit): ConfirmDialog {
        addButtonAgree(context.getString(title), onRightClick)
        return this
    }

    fun addButtonAgree(title: Int): ConfirmDialog {
        addButtonAgree(context.getString(title), {})
        return this
    }

    fun addButtonAgree(title: String?, onRightClick: () -> Unit): ConfirmDialog {
        agreeOnClick = onRightClick
        if (mBtAgree != null) {
            mBtAgree!!.text = title
        } else {
            mRightTitle = title
        }
        return this
    }

    fun setIcon(icon: Int): ConfirmDialog {
        if (mIvAlert != null) {
            mIvAlert!!.setImageDrawable(ContextCompat.getDrawable(mContext, icon))
            mIvAlert!!.visibility = View.VISIBLE
        } else {
            mAlertIcon = icon
        }
        return this
    }

    fun setNoticeTitle(idTitle: Int): ConfirmDialog {
        setNoticeTitle(context.getString(idTitle))
        return this
    }

    fun setNoticeTitle(title: String?): ConfirmDialog {
        if (mTvTitle != null) {
            mTvTitle!!.text = title
            mTvTitle!!.visibility = View.VISIBLE
        } else {
            mTitle = title
        }
        return this
    }

    fun removeActionAll() {
        needActionAll = false
    }


    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}