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

        val prefs = getSharedPreferences("peacepiece_prefs", MODE_PRIVATE)
        val openUrlEdit = findViewById<TextInputEditText>(R.id.openUrlEdit)
        val closeUrlEdit = findViewById<TextInputEditText>(R.id.closeUrlEdit)

        openUrlEdit.setText(prefs.getString("open_url", ""))
        closeUrlEdit.setText(prefs.getString("close_url", ""))

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            prefs.edit()
                .putString("open_url", openUrlEdit.text.toString().trim())
                .putString("close_url", closeUrlEdit.text.toString().trim())
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
