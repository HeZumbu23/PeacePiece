package io.github.hezumbu23.peacepiece

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val prefs        = getSharedPreferences("peacepiece_prefs", MODE_PRIVATE)
        val baseUrlEdit  = findViewById<TextInputEditText>(R.id.baseUrlEdit)
        val entityIdEdit = findViewById<TextInputEditText>(R.id.entityIdEdit)
        val usernameEdit = findViewById<TextInputEditText>(R.id.usernameEdit)
        val passwordEdit = findViewById<TextInputEditText>(R.id.passwordEdit)

        baseUrlEdit.setText(prefs.getString("base_url",   ""))
        entityIdEdit.setText(prefs.getString("entity_id", ""))
        usernameEdit.setText(prefs.getString("username",  ""))
        passwordEdit.setText(prefs.getString("password",  ""))

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            prefs.edit()
                .putString("base_url",   baseUrlEdit.text.toString().trim())
                .putString("entity_id",  entityIdEdit.text.toString().trim())
                .putString("username",   usernameEdit.text.toString())
                .putString("password",   passwordEdit.text.toString())
                .apply()
            Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
