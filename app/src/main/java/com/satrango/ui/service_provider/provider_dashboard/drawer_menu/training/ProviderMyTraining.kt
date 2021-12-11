package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.satrango.R
import com.satrango.base.BaseFragment
import com.satrango.databinding.FragmentProviderMyTrainingBinding

class ProviderMyTraining : BaseFragment<ProviderMyTrainingViewModel, FragmentProviderMyTrainingBinding, ProviderMyTrainingRepository>() {

    override fun getFragmentViewModel(): Class<ProviderMyTrainingViewModel> = ProviderMyTrainingViewModel::class.java

    override fun getFragmentBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProviderMyTrainingBinding = FragmentProviderMyTrainingBinding.inflate(layoutInflater)

    override fun getFragmentRepository(): ProviderMyTrainingRepository = ProviderMyTrainingRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

}