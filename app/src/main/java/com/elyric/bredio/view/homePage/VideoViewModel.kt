package com.elyric.bredio.view.homePage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory

class VideoViewModel: ViewModel() {
    val adapter = VideoAdapter()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
        }
    }
}