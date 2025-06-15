package com.example.dawaidata.login.view

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dawaidata.R
import com.example.dawaidata.common.utility.showAlert
import com.example.dawaidata.navbar.view.NavBar
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var loginUsername: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        loginUsername = findViewById(R.id.loginUsername)
        loginPassword = findViewById(R.id.loginPassword)
        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = loginUsername.text.toString()
            val pass = loginPassword.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                showAlert(this@LoginActivity,"Empty Fields", "Please enter email and password")
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, NavBar::class.java))
                        finish()
                    } else {
                        showAlert(this@LoginActivity,"Invalid Email or Password", "Please enter the correct credentials")
                    }
                }
        }
    }
}
