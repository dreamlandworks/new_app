package com.satrango.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VM: ViewModel, VB: ViewBinding, R: BaseRepository>: Fragment() {

    protected lateinit var binding: VB
    protected lateinit var viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getFragmentBinding(layoutInflater, container)
        val factory = ViewModelFactory(getFragmentRepository())
        viewModel = ViewModelProvider(this, factory)[getFragmentViewModel()]
        return binding.root
    }

    abstract fun getFragmentViewModel(): Class<VM>

    abstract fun getFragmentBinding(layoutInflater: LayoutInflater, container: ViewGroup?): VB

    abstract fun getFragmentRepository(): R

}