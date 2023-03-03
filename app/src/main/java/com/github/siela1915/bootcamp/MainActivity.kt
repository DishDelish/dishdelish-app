package com.github.siela1915.bootcamp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var retro: Retrofit
    lateinit var api: BoredApi
    lateinit var dao: BoredDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        retro = Retrofit.Builder()
            .baseUrl("https://www.boredapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retro.create(BoredApi::class.java)
        dao = Room.databaseBuilder(application, BoredDatabase::class.java, BoredDatabase.NAME)
            .allowMainThreadQueries()
            .build()
            .boredDao()
    }

    fun buttonClicked(view: View?) {
        val userName = findViewById<View>(R.id.mainInputText) as EditText
        val greetingIntent = Intent(this, GreetingActivity::class.java)
        greetingIntent.putExtra("com.github.siela1915.bootcamp.userName", userName.text.toString())
        startActivity(greetingIntent)
    }

    fun buttonFetchApi(view: View?) {
        val dataView = findViewById<TextView>(R.id.apiDataView)

        api.getActivity().enqueue(object : Callback<BoredActivity> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<BoredActivity>, response: Response<BoredActivity>) {
                if(response.isSuccessful){
                    val temp = response.body() as BoredActivity
                    dataView.setText(temp.activity)
                    dao.insertAll(temp)
                }else{
                    dataView.setText("Fetch was unsuccessful :(")
                }

            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<BoredActivity>, t: Throwable) {
                val cached = dao.allActivities
                if(cached.isNotEmpty()){
                    val n = cached.size-1
                    val cachedString = "\n(cached response)"
                    dataView.text=(cached.get((0..n).random())
                        .activity + cachedString)
                }
            }
        })
    }
}