package com.elyric.bredio.view.component.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment

open class BaseDialogFragment:DialogFragment() {
    protected open fun initViews(){

    }
    protected open fun initDatum(){

    }
    protected open fun initListeners(){

    }
    fun <T : View> findViewById(@IdRes id: Int): T {
        return requireView().findViewById<T>(id)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initDatum()
        initListeners()
    }

}