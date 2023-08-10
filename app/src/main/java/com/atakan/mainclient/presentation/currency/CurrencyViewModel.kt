package com.atakan.mainclient.presentation.currency

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atakan.mainclient.common.Resource
import com.atakan.mainclient.domain.model.Currency
import com.atakan.mainclient.domain.use_case.GetCurrencyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor() : ViewModel() {

    private val _currencyLiveData = MutableLiveData<Resource<Currency>>()
    val currencyLiveData: LiveData<Resource<Currency>> = _currencyLiveData


    fun refreshData(resource: Resource<Currency>){
        _currencyLiveData.postValue(resource)
    }
}