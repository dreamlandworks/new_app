package com.satrango.ui.user.user_dashboard.user_alerts

interface AlertsInterface {

    fun rescheduleUserStatusCancelDialog(bookingId: Int, categoryId: Int, userId: Int, rescheduleId: Int, description: String)

    fun rescheduleUserAcceptRejectDialog(bookingId: Int, categoryId: Int, userId: Int, rescheduleId: Int, description: String)

    fun rescheduleSPStatusCancelDialog(bookingId: Int, categoryId: Int, userId: Int, rescheduleId: Int, description: String)

    fun rescheduleSPAcceptRejectDialog(bookingId: Int, categoryId: Int, userId: Int, rescheduleId: Int, description: String)

    fun extraDemandDialog(bookingId: Int, categoryId: Int, userId: Int)

    fun divertToInstallmentsScreen(bookingId: String, postJobId: Int)

    fun divertToViewBidDetailsScreen(bookingId: String, spId: Int, bidId: Int)

    fun divertToOfferScreen()

}