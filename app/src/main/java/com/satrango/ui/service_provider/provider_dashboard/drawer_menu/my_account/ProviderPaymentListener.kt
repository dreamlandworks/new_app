package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account
import com.satrango.ui.service_provider.provider_dashboard.plans.models.Data

interface ProviderPaymentListener {

    fun loadPayment(data: Data)

}