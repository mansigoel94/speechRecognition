package com.example.mansigoel.speechrecognition

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_online.setOnClickListener {
            val intent = Intent(this@MainActivity, OnlineActivity::class.java)
            startActivity(intent)
        }

        tv_offline.setOnClickListener{
            val intent = Intent(this@MainActivity, OfflineActivity::class.java)
            startActivity(intent)
        }
    }
}
