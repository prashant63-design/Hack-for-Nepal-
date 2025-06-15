package com.example.dawaidata.signup.view

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dawaidata.R
import com.example.dawaidata.common.utility.showAlert
import com.example.dawaidata.login.view.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fullName: EditText
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        fullName = findViewById(R.id.signupFullName)
        username = findViewById(R.id.signupUsername)
        password = findViewById(R.id.signupPassword)
        confirmPassword = findViewById(R.id.signupConfirmPassword)
        signupButton = findViewById(R.id.signupButton)

        val textLogin = findViewById<TextView>(R.id.textLogin)

        textLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        signupButton.setOnClickListener {
            val email = username.text.toString()
            val pass = password.text.toString()
            val confirmPass = confirmPassword.text.toString()


            if (email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                showAlert(this@SignUpActivity,"Empty Fields", "Please enter email and password")
                return@setOnClickListener
            }

            if (pass != confirmPass) {
                showAlert(this@SignUpActivity,"Password doesnot match", "Please enter the same password")
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        showAlert(this@SignUpActivity,"Success", "Signup successful")
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        showAlert(this@SignUpActivity,"Signup Failed", task.exception?.message ?: "Unknown error")
                    }
                }

        }
    }
}
