package com.example.bettinalogistics.di

import com.example.baseapp.di.AndroidResourceProvider
import com.example.baseapp.di.ResourceProvider
import com.example.baseapp.di.dialogs
import com.example.bettinalogistics.data.*
import com.example.bettinalogistics.ui.activity.add_new_order.AddNewProductViewModel
import com.example.bettinalogistics.ui.activity.addorder.OrderViewModel
import com.example.bettinalogistics.ui.activity.confirm_order.ConfirmOrderViewModel
import com.example.bettinalogistics.ui.activity.gg_map.GoogleMapViewmodel
import com.example.bettinalogistics.ui.activity.login.LoginViewModel
import com.example.bettinalogistics.ui.activity.main.MainViewModel
import com.example.bettinalogistics.ui.activity.signup.SignUpViewModel
import com.example.bettinalogistics.ui.activity.splash.SplashViewModel
import com.example.bettinalogistics.ui.fragment.admin.detail_order.DetailAdminOrderViewModel
import com.example.bettinalogistics.ui.fragment.admin.order.AdminOrderListViewModel
import com.example.bettinalogistics.ui.fragment.admin.person.AdminAccountViewModel
import com.example.bettinalogistics.ui.fragment.ship.order_list.ShipOrderViewModel
import com.example.bettinalogistics.ui.fragment.ship.person.ShipPersonViewModel
import com.example.bettinalogistics.ui.fragment.user.detail_order.DetailUserOrderViewModel
import com.example.bettinalogistics.ui.fragment.user.followtrask.UserFollowTrackingViewModel
import com.example.bettinalogistics.ui.fragment.user.home.UserHomeViewModel
import com.example.bettinalogistics.ui.fragment.user.notification.NotificationViewModel
import com.example.bettinalogistics.ui.fragment.user.person.UserPersonViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val models = module {
    viewModel{
        SplashViewModel()
    }
    viewModel {
        LoginViewModel(get())
    }
    viewModel {
        SignUpViewModel(get())
    }
    viewModel {
        MainViewModel(get())
    }
    viewModel {
        UserHomeViewModel()
    }
    viewModel {
        OrderViewModel(get())
    }
    viewModel {
        AddNewProductViewModel(get())
    }
    viewModel {
        GoogleMapViewmodel(get())
    }
    viewModel {
        ConfirmOrderViewModel(get(),get())
    }
    viewModel {
        UserFollowTrackingViewModel(get())
    }
    viewModel {
        AdminOrderListViewModel(get())
    }
    viewModel {
        NotificationViewModel(get())
    }
    viewModel {
        DetailUserOrderViewModel(get(),get(),get(),get())
    }
    viewModel {
        UserPersonViewModel(get(),get(),get())
    }
    viewModel {
        ShipOrderViewModel(get(),get(), get())
    }
    viewModel {
        ShipPersonViewModel(get(),get())
    }
    viewModel {
        DetailAdminOrderViewModel(get(),get(),get())
    }
    viewModel {
        AdminAccountViewModel(get())
    }
}

val impls = module {
    single<AuthenticationRepository>  {
        AuthenticationRepositoryImpl()
    }

    single<OrderRepository>  {
        OrderRepositoryImpl()
    }

    single<GoogleMapRepo>  {
        GoogleMapRepoImpl()
    }

    single<AdminOrShipperOrderRepo>  {
        AdminOrShipperOrderRepoImpl()
    }

    single<FollowTrackingRepo>  {
        FollowTrackingRepoImpl()
    }

    single<OTTFirebaseRepo>  {
        OttFirebaseRepoImpl()
    }

    single<CardRepository>  {
        CardRepoImpl()
    }
}

val utilities = module{
    single<ResourceProvider> { AndroidResourceProvider(get()) }
}

val modules = listOf(utilities, models,dialogs, impls)