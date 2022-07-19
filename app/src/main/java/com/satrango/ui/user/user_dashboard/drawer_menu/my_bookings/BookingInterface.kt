package com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings

import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.BookingDetail

interface BookingInterface {

    fun startUserMessaging(bookingDetails: BookingDetail)

    fun startServiceProviderMessaging(bookingDetails: com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.BookingDetail)

}