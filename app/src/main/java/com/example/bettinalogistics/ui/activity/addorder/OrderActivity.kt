package com.example.bettinalogistics.ui.activity.addorder

import android.content.Intent
import android.os.Bundle
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityOrderBinding
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.ui.activity.add_new_order.AddNewOrderActivity
import com.example.bettinalogistics.ui.activity.login.LoginActivity
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_STATUS_PENDING
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderActivity : BaseActivity() {
    private lateinit var addNewProductBottomSheet: AddNewProductBottomSheet
    private lateinit var addOrderAdapter: AddOrderAdapter

    override val layoutId: Int
        get() = R.layout.activity_order
    override val viewModel: OrderViewModel by viewModel()

    override val binding: ActivityOrderBinding by lazy {
        ActivityOrderBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initListener()
        observerData()
    }

    private fun initView() {
        addNewProductBottomSheet = AddNewProductBottomSheet()
        addOrderAdapter = AddOrderAdapter()
        binding.layoutHeaderOrder.tvHeaderTitle.text = getString(R.string.header_order)
        binding.rvOrderList.adapter = addOrderAdapter
        showLoading()
        viewModel.getAllOrder()
    }

    private fun initListener() {
        binding.layoutHeaderOrder.ivHeaderBack.setOnClickListener {
            finish()
        }
        binding.btnAddOrderProduct.setOnClickListener {
            startActivity(Intent(this, AddNewOrderActivity::class.java))
        }
    }

    private fun observerData() {
        viewModel.getAllOrderLiveData.observe(this){
            if(it != null){
                addOrderAdapter.resetOrderList(it as ArrayList<Order>)
                hiddenLoading()
            }
            else{
                hiddenLoading()
                confirm.newBuild().setNotice(getString(R.string.str_get_all_order_fail))
                    .addButtonAgree(getString(R.string.str_agree)) {
                        confirm.dismiss()
                        startActivity(Intent(this, AddNewOrderActivity::class.java))
                    }.setAction(true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllOrder()
    }
}