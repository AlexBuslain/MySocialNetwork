package com.example.mysocialnetworkproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        // Initialize UI bindings
        val signInButton: Button = findViewById(R.id.sign_up_button)
        val firstnameInput: EditText = findViewById(R.id.firstname_input)
        val lastnameInput: EditText = findViewById(R.id.lastname_input)
        val emailInput: EditText = findViewById(R.id.email_input)
        val pwdInput: EditText = findViewById(R.id.pwd_input)

        signInButton.setOnClickListener {
            when {
                // Check if all fields are filled in
                TextUtils.isEmpty(firstnameInput.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter your first name",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(lastnameInput.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter your last name",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(emailInput.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(pwdInput.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val firstname = firstnameInput.text.toString()
                    val lastname = lastnameInput.text.toString()
                    val email: String = emailInput.text.toString().trim { it <= ' ' }
                    val password: String = pwdInput.text.toString().trim { it <= ' ' }

                    // Create instance and register a user with email & password
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            // If registration is successful
                            if (task.isSuccessful){

                                // Firebase registered user
                                val firebaseUser: FirebaseUser = task.result!!.user!!
                                // Instantiate Firestore
                                val db = Firebase.firestore
                                // Create a new user document
                                val user = hashMapOf(
                                    // "uid" can be seen as a primary key in the database
                                    "uid" to firebaseUser.uid,
                                    "firstname" to firstname,
                                    "lastname" to lastname,
                                )
                                // Add the user to the database
                                db.collection("users")
                                    .add(user)
                                    .addOnSuccessListener {}
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "Error: $e",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Sign up successful, please login with your new account",
                                    Toast.LENGTH_LONG
                                ).show()
                                // Go to login activity
                                val intent =
                                    Intent(this@RegisterActivity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                // Forward email to login activity in order to autofill the email field
                                intent.putExtra("email", email)
                                startActivity(intent)
                                finish()
                            } else {
                                // If registering not successful, show err message
                                Toast.makeText(
                                    this@RegisterActivity,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
    }
}