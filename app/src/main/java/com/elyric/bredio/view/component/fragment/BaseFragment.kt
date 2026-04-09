package com.elyric.bredio.view.component.fragment


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {
    protected open fun initViews(){

    }
    protected open fun initDatum(){

    }
    protected open fun initListeners(){

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initDatum()
        initListeners()
    }
}
