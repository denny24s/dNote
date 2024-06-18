package com.example.dnote

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PinCodeActivity : AppCompatActivity() {

    private lateinit var confirmTextView: TextView
    private lateinit var pinEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    private var isConfirmingPin = false
    private var firstPin: String? = null
    private var currentPin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_code)

        confirmTextView = findViewById(R.id.confirmTextView)
        pinEditText = findViewById(R.id.pinEditText)
        submitButton = findViewById(R.id.submitButton)
        sharedPreferences = getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)

        currentPin = sharedPreferences.getString("pin_code", null)
        val isChangingPin = intent.getBooleanExtra("isChangingPin", false)

        if (currentPin != null && !isChangingPin) {
            confirmTextView.setText(R.string.enter_pin)
        } else if (isChangingPin) {
            confirmTextView.setText(R.string.enter_current_pin)
        } else {
            confirmTextView.setText(R.string.set_pin)
        }

        submitButton.setOnClickListener {
            val enteredPin = pinEditText.text.toString()

            if (currentPin != null && !isChangingPin) {
                if (enteredPin == currentPin) {
                    finish()
                } else {
                    Toast.makeText(this, R.string.incorrect_pin, Toast.LENGTH_SHORT).show()
                }
            } else if (isChangingPin) {
                if (!isConfirmingPin) {
                    if (enteredPin == currentPin) {
                        confirmTextView.setText(R.string.set_new_pin)
                        pinEditText.text.clear()
                        isConfirmingPin = true
                    } else {
                        Toast.makeText(this, R.string.incorrect_pin, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (firstPin == null) {
                        firstPin = enteredPin
                        confirmTextView.setText(R.string.confirm_new_pin)
                        pinEditText.text.clear()
                    } else {
                        if (enteredPin == firstPin) {
                            sharedPreferences.edit().putString("pin_code", enteredPin).apply()
                            Toast.makeText(this, R.string.pin_changed, Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, R.string.pins_do_not_match, Toast.LENGTH_SHORT).show()
                            pinEditText.text.clear()
                        }
                    }
                }
            } else {
                if (!isConfirmingPin) {
                    firstPin = enteredPin
                    confirmTextView.setText(R.string.confirm_pin)
                    pinEditText.text.clear()
                    isConfirmingPin = true
                } else {
                    if (enteredPin == firstPin) {
                        sharedPreferences.edit().putString("pin_code", enteredPin).apply()
                        Toast.makeText(this, R.string.pin_set, Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, R.string.pins_do_not_match, Toast.LENGTH_SHORT).show()
                        pinEditText.text.clear()
                    }
                }
            }
        }
    }

    companion object {
        fun createPin(context: Context) {
            val intent = Intent(context, PinCodeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        fun changePin(context: Context) {
            val intent = Intent(context, PinCodeActivity::class.java).apply {
                putExtra("isChangingPin", true)
            }
            context.startActivity(intent)
        }

        fun verifyPin(context: Context) {
            val intent = Intent(context, PinCodeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}
