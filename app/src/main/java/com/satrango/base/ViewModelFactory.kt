package com.satrango.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.satrango.ui.auth.forgot_password.ForgotPwdRepository
import com.satrango.ui.auth.forgot_password.ForgotPwdViewModel
import com.satrango.ui.auth.loginscreen.LoginRepository
import com.satrango.ui.auth.loginscreen.LoginViewModel
import com.satrango.ui.auth.user_signup.otp_verification.OTPVerificationRepository
import com.satrango.ui.auth.user_signup.otp_verification.OTPVerificationViewModel
import com.satrango.ui.auth.user_signup.set_password.SetPasswordRepository
import com.satrango.ui.auth.user_signup.set_password.SetPasswordViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesViewModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileViewModel
import com.satrango.ui.user.user_dashboard.search_service_providers.SearchServiceProviderRepository
import com.satrango.ui.user.user_dashboard.search_service_providers.SearchServiceProviderViewModel
import com.satrango.ui.user.user_dashboard.user_alerts.UserAlertsRepository
import com.satrango.ui.user.user_dashboard.user_alerts.UserAlertsViewModel
import com.satrango.ui.user.user_dashboard.user_home_screen.UserHomeRepository
import com.satrango.ui.user.user_dashboard.user_home_screen.UserHomeViewModel

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
        }
        return super.create(modelClass)
    }

}