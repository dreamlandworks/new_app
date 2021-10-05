package com.satrango.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.satrango.ui.auth.forgot_password.ForgotPwdRepository
import com.satrango.ui.auth.forgot_password.ForgotPwdViewModel
import com.satrango.ui.auth.login_screen.LoginRepository
import com.satrango.ui.auth.login_screen.LoginViewModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_five.ProviderSignUpFiveRepository
import com.satrango.ui.auth.provider_signup.provider_sign_up_five.ProviderSignUpFiveViewModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.ProviderSignUpFourRepository
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.ProviderSignUpFourViewModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.ProviderSignUpOneRepository
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.ProviderSignUpOneViewModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_two.ProviderSignUpTwoRepository
import com.satrango.ui.auth.provider_signup.provider_sign_up_two.ProviderSignUpTwoViewModel
import com.satrango.ui.auth.user_signup.otp_verification.OTPVerificationRepository
import com.satrango.ui.auth.user_signup.otp_verification.OTPVerificationViewModel
import com.satrango.ui.auth.user_signup.set_password.SetPasswordRepository
import com.satrango.ui.auth.user_signup.set_password.SetPasswordViewModel
import com.satrango.ui.service_provider.provider_dashboard.ProviderDashboardRepository
import com.satrango.ui.service_provider.provider_dashboard.ProviderDashboardViewModel
import com.satrango.ui.service_provider.provider_dashboard.alerts.ProviderAlertRepository
import com.satrango.ui.service_provider.provider_dashboard.alerts.ProviderAlertsViewModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.ProviderMyAccountRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.ProviderMyAccountViewModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.ProviderMyBidsRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.ProviderMyBidsViewModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderBookingViewModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.ProviderProfileRepository
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.ProviderProfileViewModel
import com.satrango.ui.service_provider.provider_dashboard.offers.ProviderOfferRepository
import com.satrango.ui.service_provider.provider_dashboard.offers.ProviderOfferViewModel
import com.satrango.ui.service_provider.provider_dashboard.plans.ProviderPlansRepository
import com.satrango.ui.service_provider.provider_dashboard.plans.ProviderPlansViewModel
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.MyAccountRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.MyAccountViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.MyBookingsRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.MyBookingsViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.faqs.UserFAQRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.faqs.UserFAQViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.SettingsRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.SettingsViewModel
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProviderRepository
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProviderViewModel
import com.satrango.ui.user.user_dashboard.user_alerts.UserAlertsRepository
import com.satrango.ui.user.user_dashboard.user_alerts.UserAlertsViewModel
import com.satrango.ui.user.user_dashboard.user_home_screen.UserHomeRepository
import com.satrango.ui.user.user_dashboard.user_home_screen.UserHomeViewModel
import com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.UserLocationChangeRepository
import com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.UserLocationChangeViewModel

@Suppress("UNCHECKED_CAST")
open class ViewModelFactory(private val baseRepository: BaseRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        when {
            modelClass.isAssignableFrom(UserHomeViewModel::class.java) -> return UserHomeViewModel(
                baseRepository as UserHomeRepository
            ) as T
            modelClass.isAssignableFrom(UserAlertsViewModel::class.java) -> return UserAlertsViewModel(
                baseRepository as UserAlertsRepository
            ) as T
            modelClass.isAssignableFrom(BrowseCategoriesViewModel::class.java) -> return BrowseCategoriesViewModel(
                baseRepository as BrowseCategoriesRepository
            ) as T
            modelClass.isAssignableFrom(SetPasswordViewModel::class.java) -> return SetPasswordViewModel(
                baseRepository as SetPasswordRepository
            ) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> return LoginViewModel(
                baseRepository as LoginRepository
            ) as T
            modelClass.isAssignableFrom(OTPVerificationViewModel::class.java) -> return OTPVerificationViewModel(
                baseRepository as OTPVerificationRepository
            ) as T
            modelClass.isAssignableFrom(UserProfileViewModel::class.java) -> return UserProfileViewModel(
                baseRepository as UserProfileRepository
            ) as T
            modelClass.isAssignableFrom(ForgotPwdViewModel::class.java) -> return ForgotPwdViewModel(
                baseRepository as ForgotPwdRepository
            ) as T
            modelClass.isAssignableFrom(SearchServiceProviderViewModel::class.java) -> return SearchServiceProviderViewModel(
                baseRepository as SearchServiceProviderRepository
            ) as T
            modelClass.isAssignableFrom(ProviderSignUpOneViewModel::class.java) -> return ProviderSignUpOneViewModel(
                baseRepository as ProviderSignUpOneRepository
            ) as T
            modelClass.isAssignableFrom(ProviderSignUpTwoViewModel::class.java) -> return ProviderSignUpTwoViewModel(
                baseRepository as ProviderSignUpTwoRepository
            ) as T
            modelClass.isAssignableFrom(ProviderSignUpFourViewModel::class.java) -> return ProviderSignUpFourViewModel(
                baseRepository as ProviderSignUpFourRepository
            ) as T
            modelClass.isAssignableFrom(ProviderSignUpFiveViewModel::class.java) -> return ProviderSignUpFiveViewModel(
                baseRepository as ProviderSignUpFiveRepository
            ) as T
            modelClass.isAssignableFrom(ProviderDashboardViewModel::class.java) -> return ProviderDashboardViewModel(
                baseRepository as ProviderDashboardRepository
            ) as T
            modelClass.isAssignableFrom(UserFAQViewModel::class.java) -> return UserFAQViewModel(
                baseRepository as UserFAQRepository
            ) as T
            modelClass.isAssignableFrom(UserLocationChangeViewModel::class.java) -> return UserLocationChangeViewModel(
                baseRepository as UserLocationChangeRepository
            ) as T
            modelClass.isAssignableFrom(BookingViewModel::class.java) -> return BookingViewModel(
                baseRepository as BookingRepository
            ) as T
            modelClass.isAssignableFrom(MyBookingsViewModel::class.java) -> return MyBookingsViewModel(
                baseRepository as MyBookingsRepository
            ) as T
            modelClass.isAssignableFrom(MyAccountViewModel::class.java) -> return MyAccountViewModel(
                baseRepository as MyAccountRepository
            ) as T
            modelClass.isAssignableFrom(PostJobViewModel::class.java) -> return PostJobViewModel(
                baseRepository as PostJobRepository
            ) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> return SettingsViewModel(
                baseRepository as SettingsRepository
            ) as T
            modelClass.isAssignableFrom(ProviderBookingViewModel::class.java) -> return ProviderBookingViewModel(
                baseRepository as ProviderBookingRepository
            ) as T
            modelClass.isAssignableFrom(ProviderMyBidsViewModel::class.java) -> return ProviderMyBidsViewModel(
                baseRepository as ProviderMyBidsRepository
            ) as T
            modelClass.isAssignableFrom(ProviderAlertsViewModel::class.java) -> return ProviderAlertsViewModel(
                baseRepository as ProviderAlertRepository
            ) as T
            modelClass.isAssignableFrom(ProviderOfferViewModel::class.java) -> return ProviderOfferViewModel(
                baseRepository as ProviderOfferRepository
            ) as T
            modelClass.isAssignableFrom(ProviderProfileViewModel::class.java) -> return ProviderProfileViewModel(
                baseRepository as ProviderProfileRepository
            ) as T
            modelClass.isAssignableFrom(ProviderMyAccountViewModel::class.java) -> return ProviderMyAccountViewModel(
                baseRepository as ProviderMyAccountRepository
            ) as T
            modelClass.isAssignableFrom(ProviderPlansViewModel::class.java) -> return ProviderPlansViewModel(
                baseRepository as ProviderPlansRepository
            ) as T
        }
        return super.create(modelClass)
    }

}