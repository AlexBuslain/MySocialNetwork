package com.example.mysocialnetworkproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val signInButton: Button = findViewById(R.id.sign_up_button)
        val firstnameInput: EditText = findViewById(R.id.firstname_input)
        val lastnameInput: EditText = findViewById(R.id.lastname_input)
        val emailInput: EditText = findViewById(R.id.email_input)
        val pwdInput: EditText = findViewById(R.id.pwd_input)


        signInButton.setOnClickListener {
            when {
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
                        .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->

                                // If registration is successful
                                if (task.isSuccessful){

                                    // Firebase registered user
                                    val firebaseUser: FirebaseUser = task.result!!.user!!
                                    val db = Firebase.firestore
                                    val user = hashMapOf(
                                        "uid" to firebaseUser.uid,
                                        "firstname" to firstname,
                                        "lastname" to lastname,
                                    )
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

                                    val intent =
                                        Intent(this@RegisterActivity, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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
                        )
                }
            }
        }
    }
}