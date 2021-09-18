package com.satrango.ui.user.bookings.view_booking_details.installments_request

import com.satrango.ui.user.bookings.view_booking_details.installments_request.models.GoalsInstallmentsDetail

interface UserInstallmentsRequestInterface {

    fun acceptInstallment(data: GoalsInstallmentsDetail)

    fun rejectInstallment(data: GoalsInstallmentsDetail)

}
