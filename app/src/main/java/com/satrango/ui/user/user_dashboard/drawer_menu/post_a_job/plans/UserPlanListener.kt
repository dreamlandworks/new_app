package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans

import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models.Data

interface UserPlanListener {

    fun loadPayment(amount: Data)

}