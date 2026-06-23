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

        val prefs             = getSharedPreferences("peacepiece_prefs", MODE_PRIVATE)
        val baseUrlEdit       = findViewById<TextInputEditText>(R.id.baseUrlEdit)
        val cfClientIdEdit    = findViewById<TextInputEditText>(R.id.cfClientIdEdit)
        val cfClientSecretEdit = findViewById<TextInputEditText>(R.id.cfClientSecretEdit)
        val haTokenEdit       = findViewById<TextInputEditText>(R.id.haTokenEdit)

        baseUrlEdit.setText(prefs.getString("base_url",          ""))
        cfClientIdEdit.setText(prefs.getString("cf_client_id",   ""))
        cfClientSecretEdit.setText(prefs.getString("cf_client_secret", ""))
        haTokenEdit.setText(prefs.getString("ha_token",          ""))

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            prefs.edit()
                .putString("base_url",          baseUrlEdit.text.toString().trim())
                .putString("cf_client_id",      cfClientIdEdit.text.toString().trim())
                .putString("cf_client_secret",  cfClientSecretEdit.text.toString().trim())
                .putString("ha_token",          haTokenEdit.text.toString().trim())
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
