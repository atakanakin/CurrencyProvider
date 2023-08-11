package com.atakan.mainclient.di

import android.app.Service
import com.atakan.mainclient.common.Constants
import com.atakan.mainclient.data.remote.CurrencyApi
import com.atakan.mainclient.data.repository.CurrencyRepositoryImpl
import com.atakan.mainclient.domain.repository.CurrencyRepo
import com.atakan.mainclient.presentation.currency.CurrencyViewModel
import com.atakan.mainclient.presentation.currency.screen.AIDL.AIDLService
import com.atakan.mainclient.presentation.currency.screen.ServiceViewModel
import com.atakan.mainclient.service.AIDLService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCurrencyApi() : CurrencyApi{
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDuckRepository(api : CurrencyApi) : CurrencyRepo{
        return CurrencyRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideViewModel() : CurrencyViewModel{
        return CurrencyViewModel()
    }

    @Provides
    @Singleton
    fun provideClicker() : ServiceViewModel{
        return ServiceViewModel(false)
    }
}