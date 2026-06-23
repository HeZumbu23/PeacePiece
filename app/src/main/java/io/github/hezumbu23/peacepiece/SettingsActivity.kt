package io.github.hezumbu23.peacepiece

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import org.json.JSONException
import org.json.JSONObject

class SettingsActivity : AppCompatActivity() {

    private lateinit var baseUrlEdit: TextInputEditText
    private lateinit var cfClientIdEdit: TextInputEditText
    private lateinit var cfClientSecretEdit: TextInputEditText
    private lateinit var haTokenEdit: TextInputEditText

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        val content = result.contents ?: return@registerForActivityResult
        try {
            val json = JSONObject(content)
            baseUrlEdit.setText(json.optString("base_url",         ""))
            cfClientIdEdit.setText(json.optString("cf_client_id",  ""))
            cfClientSecretEdit.setText(json.optString("cf_client_secret", ""))
            haTokenEdit.setText(json.optString("ha_token",         ""))
            Toast.makeText(this, R.string.qr_imported, Toast.LENGTH_LONG).show()
        } catch (e: JSONException) {
            Toast.makeText(this, R.string.qr_invalid, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val prefs = getSharedPreferences("peacepiece_prefs", MODE_PRIVATE)
        baseUrlEdit        = findViewById(R.id.baseUrlEdit)
        cfClientIdEdit     = findViewById(R.id.cfClientIdEdit)
        cfClientSecretEdit = findViewById(R.id.cfClientSecretEdit)
        haTokenEdit        = findViewById(R.id.haTokenEdit)

        baseUrlEdit.setText(prefs.getString("base_url",           ""))
        cfClientIdEdit.setText(prefs.getString("cf_client_id",    ""))
        cfClientSecretEdit.setText(prefs.getString("cf_client_secret", ""))
        haTokenEdit.setText(prefs.getString("ha_token",           ""))

        findViewById<Button>(R.id.qrScanButton).setOnClickListener {
            scanLauncher.launch(ScanOptions().apply {
                setPrompt(getString(R.string.qr_scan_button))
                setBeepEnabled(false)
                setOrientationLocked(false)
            })
        }

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            prefs.edit()
                .putString("base_url",         baseUrlEdit.text.toString().trim())
                .putString("cf_client_id",     cfClientIdEdit.text.toString().trim())
                .putString("cf_client_secret", cfClientSecretEdit.text.toString().trim())
                .putString("ha_token",         haTokenEdit.text.toString().trim())
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
