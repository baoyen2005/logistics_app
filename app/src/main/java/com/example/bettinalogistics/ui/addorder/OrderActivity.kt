package com.example.bettinalogistics.ui.addorder

import android.os.Bundle
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityOrderBinding
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_STATUS_PENDING
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
            addNewProductBottomSheet.show(supportFragmentManager, "order")
        }
        addNewProductBottomSheet.mButtonAddListener =
            { uri, name, des, quantity, volume, mass, numberOfcarton, isOrderLcl ->
                showLoading()
                val order =
                    Order(
                        uri,
                        name,
                        des,
                        quantity?.toLong(),
                        volume?.toDouble(),
                        mass?.toDouble(),
                        numberOfcarton?.toLong(),
                        isOrderLcl,
                        status = PRODUCT_STATUS_PENDING
                    )
                if(viewModel.checkInvalidData(order, this)){
                    viewModel.addOrder(order)
                }
            }
    }

    private fun observerData() {
        viewModel.checkValidDataOrderLiveData.observe(this){
            confirm.newBuild().setNotice(it)
        }
        viewModel.addOrderLiveData.observe(this){
            if(it){
                confirm.newBuild().addButtonAgree(getString(R.string.str_add_order_success)){
                    addNewProductBottomSheet.dismiss()
                    hiddenLoading()
                    viewModel.getAllOrder()
                }
            }
            else{
                confirm.newBuild().addButtonAgree(getString(R.string.str_add_order_fail)){
                    addNewProductBottomSheet.dismiss()
                    hiddenLoading()
                }
            }
        }
        viewModel.getAllOrderLiveData.observe(this){
            if(it != null){
                addOrderAdapter.resetOrderList(it as ArrayList<Order>)
                hiddenLoading()
            }
            else{
                hiddenLoading()
                confirm.newBuild().setNotice(getString(R.string.str_get_all_order_fail))
            }
        }
    }
}