package com.example.miguel.weatherapp.domain.mappers

import com.example.miguel.weatherapp.data.Forecast
import com.example.miguel.weatherapp.domain.model.Forecast as ModelForecast
import com.example.miguel.weatherapp.data.ForecastResult
import com.example.miguel.weatherapp.domain.model.ForecastList
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by miguel on 1/11/17.
 */
class ForecastDataMaper {

    fun convertFromDataModel(forecast : ForecastResult) : ForecastList = ForecastList(forecast.city.name,forecast.city.country,convertForecastListToDomain(forecast.list))

    private fun convertForecastListToDomain(list: List<Forecast>): List<ModelForecast> {
        return list.mapIndexed{i,forecast ->
            val dt = Calendar.getInstance().timeInMillis +TimeUnit.DAYS.toMillis(i.toLong())
            convertForecastItemToDomain(forecast.copy(dt = dt))
        }
    }

    private fun convertForecastItemToDomain(forecast: Forecast): ModelForecast {
        return ModelForecast(convertDate(forecast.dt),forecast.weather[0].description,forecast.temp.max.toInt(),forecast.temp.min.toInt())
    }

    private fun convertDate(date: Long): String {
        val df = DateFormat.getDateInstance(DateFormat.MEDIUM,Locale.getDefault())
        return df.format(date)
    }

}