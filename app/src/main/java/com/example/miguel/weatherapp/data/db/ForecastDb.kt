package com.example.miguel.weatherapp.data.db


import com.example.miguel.weatherapp.domain.model.ForecastList
import com.example.miguel.weatherapp.extensions.clear
import com.example.miguel.weatherapp.extensions.parseList
import com.example.miguel.weatherapp.extensions.parseOpt
import com.example.miguel.weatherapp.extensions.toVarargArray
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select


class ForecastDb (
        private val forecastDbHelper: ForecastDbHelper = ForecastDbHelper.instance,
        private val dataMapper:DbDataMapper = DbDataMapper()){

    fun forecastByZipCode(zipCode:Long,date:Long) = forecastDbHelper.use {

        val dailyRequest = "${DayForecastTable.CITY_ID} = ? " + "AND ${DayForecastTable.DATE} >= ?"

        val dailyForecast = select(DayForecastTable.NAME) .whereSimple(dailyRequest, zipCode.toString(), date.toString()).parseList { DayForecast(HashMap(it)) }

        val city  = select(CityForecastTable.NAME)
        .whereSimple("${CityForecastTable.ID} = ?", zipCode.toString())
            .parseOpt { CityForecast(HashMap(it), dailyForecast) }
        if (city != null) dataMapper.convertToDomain(city) else null
    }

    fun saveForecast(forecast: ForecastList) = forecastDbHelper.use {

        clear(CityForecastTable.NAME)
        clear(DayForecastTable.NAME)

        with(dataMapper.convertFromDomain(forecast)) {
            insert(CityForecastTable.NAME, *map.toVarargArray())
            dailyForecast.forEach { insert(DayForecastTable.NAME, *it.map.toVarargArray()) }
        }
    }

}