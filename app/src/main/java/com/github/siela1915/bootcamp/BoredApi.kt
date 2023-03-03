package com.github.siela1915.bootcamp

import retrofit2.Call
import retrofit2.http.GET

interface BoredApi {
    @GET("activity")
    fun getActivity(): Call<BoredActivity>
}