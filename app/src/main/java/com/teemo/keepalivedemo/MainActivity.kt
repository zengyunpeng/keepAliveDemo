package com.teemo.keepalivedemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)//
        setContentView(R.layout.activity_main)
        App.setMainActivity(this)
        crash.setOnClickListener {
            val a: String? = null
            a!!.toString()

        }
        Log.i("tag", "MainActivity被启动了")
    }
}