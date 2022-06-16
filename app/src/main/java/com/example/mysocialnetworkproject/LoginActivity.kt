package com.example.mysocialnetworkproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Initialize UI bindings
        val loginButton: Button = findViewById(R.id.login_button)
        val registerButton: Button = findViewById(R.id.register_button)
        val emailInput: EditText = findViewById(R.id.email_input)
        val pwdInput: EditText = findViewById(R.id.pwd_input)
        // Get intent extras
        val bundle: Bundle? = intent.extras
        val newlyRegisteredEmail = bundle?.getString("email").toString()
        // Autofill email input if user just registered
        if(newlyRegisteredEmail != "null") {
            Log.d("LoginActivity", "Email is $newlyRegisteredEmail")
            emailInput.setText(newlyRegisteredEmail)
        }

        loginButton.setOnClickListener {
        when {
            // Check if email and password are empty
            TextUtils.isEmpty(emailInput.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@LoginActivity,
                    "Please enter email",
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(pwdInput.text.toString().trim {  it <= ' '}) -> {
                Toast.makeText(
                    this@LoginActivity,
                    "Please enter password",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                // Trim email and password to remove unwanted spaces
                val email: String = emailInput.text.toString().trim { it <= ' ' }
                val password: String = pwdInput.text.toString().trim { it <= ' ' }
                // Sign in user in Firebase
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Login successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Go to main activity
                            val intent =
                                Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            // Forward user uid to main activity [unused]
                            intent.putExtra(
                                "uid",
                                FirebaseAuth.getInstance().currentUser!!.uid
                            )
                            intent.putExtra("email", email)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
        // Register button to launch register activity
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}