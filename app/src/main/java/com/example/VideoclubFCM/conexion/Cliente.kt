package com.example.VideoclubFCM.conexion

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Cliente {
    companion object{
        const val URL:String = "http://192.168.0.23:8000/api/"
        var retrofit: Retrofit?= null

        fun obtenerCliente(): Retrofit? {
            if(retrofit == null){
                retrofit = Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            }
            return retrofit
        }
    }
}