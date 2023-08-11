package com.atakan.mainclient.presentation.currency.screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(isBound: Boolean) : ViewModel() {

    private val _isServiceConnected = MutableLiveData<Boolean>(isBound)

    val isServiceConnected: LiveData<Boolean> = _isServiceConnected

    fun toggleServiceConnection() {
        _isServiceConnected.postValue(!_isServiceConnected.value!!)
    }
}
