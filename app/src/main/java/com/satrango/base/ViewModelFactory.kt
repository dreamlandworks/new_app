package com.satrango.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.satrango.ui.user_dashboard.user_alerts.UserAlertsRepository
import com.satrango.ui.user_dashboard.user_alerts.UserAlertsViewModel
import com.satrango.ui.user_dashboard.user_home_screen.UserHomeRepository
import com.satrango.ui.user_dashboard.user_home_screen.UserHomeViewModel

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
        }
        return super.create(modelClass)
    }

}