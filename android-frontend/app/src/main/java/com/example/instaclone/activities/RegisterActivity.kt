package com.example.instaclone.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.instaclone.R
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val refBtn = findViewById<TextView>(R.id.refToLogIn)
        refBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish();
        }
        val btn = findViewById<Button>(R.id.signUpBtn)
        btn.setOnClickListener {
            processFormField()
        }
    }

    private fun processFormField() {
        val queue = Volley.newRequestQueue(this)
        val url = "http://localhost:8080/api/v1/user/register"

        val jsonBody = JSONObject()
        jsonBody.put("fullname", findViewById<EditText>(R.id.fullNameEdit).text.toString())
        jsonBody.put("username", findViewById<EditText>(R.id.usernameEdit).text.toString())
        jsonBody.put("email", findViewById<EditText>(R.id.emailEdit).text.toString())
        jsonBody.put("phoneNumber", findViewById<EditText>(R.id.mobilePhoneEdit).text.toString())
        jsonBody.put("password", findViewById<EditText>(R.id.passwordEdit).text.toString())

        val jsonRequest = JsonObjectRequest(Request.Method.POST, url, jsonBody,
            { response ->
                if (response != null && response.length() > 0) {
                    Toast.makeText(this, "Success: Registration completed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Empty response from server", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                when (error.networkResponse?.statusCode) {
                    400 -> {
                        Toast.makeText(this, "Bad Request: Registration failed", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        queue.add(jsonRequest)
    }
}