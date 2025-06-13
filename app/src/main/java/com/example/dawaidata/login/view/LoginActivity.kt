package com.example.dawaidata.login.view

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.dawaidata.R
import com.example.dawaidata.navbar.view.NavBar

class LoginActivity : AppCompatActivity() {
    lateinit var loginUsername: EditText
    lateinit var loginPassword: EditText
    lateinit var loginButton: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginUsername = findViewById(R.id.loginUsername)
        loginPassword = findViewById(R.id.loginPassword)
        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val intent = Intent(this, NavBar::class.java)
            startActivity(intent)
        }


    }
}

