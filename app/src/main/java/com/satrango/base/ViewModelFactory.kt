package com.satrango.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

open class ViewModelFactory(private val baseRepository: BaseRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        when {
//            modelClass.isAssignableFrom()
//        }
        return super.create(modelClass)
    }

}