package com.example.mysocialnetworkproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class UserFeedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_feed)

        val bundle: Bundle? = intent.extras
        val userId = bundle?.getString("userId")
    }
}