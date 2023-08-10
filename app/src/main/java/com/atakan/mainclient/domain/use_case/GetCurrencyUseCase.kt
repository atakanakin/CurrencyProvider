package com.atakan.mainclient.domain.use_case

import com.atakan.mainclient.common.Resource
import com.atakan.mainclient.domain.model.Currency
import com.atakan.mainclient.domain.repository.CurrencyRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetCurrencyUseCase @Inject constructor(
    private val repository: CurrencyRepo
) {
    operator fun invoke(): Flow<Resource<Currency>> = flow{
        try {
            emit(Resource.Loading())
            val duck = repository.getCurrency()
            emit(Resource.Success(duck))
        } catch (e : HttpException){
            emit(Resource.Error(e.localizedMessage ?: "Unexpected error occurred."))
        } catch (e : IOException){
            emit(Resource.Error("Could not reach server. Check your connection."))
        }
    }
}