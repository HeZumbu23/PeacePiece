package io.github.hezumbu23.peacepiece

import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class LogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val logContent = findViewById<TextView>(R.id.logContent)
        val logScroll  = findViewById<ScrollView>(R.id.logScroll)

        logContent.text = AppLog.getText().ifBlank { getString(R.string.log_empty) }
        logScroll.post { logScroll.fullScroll(ScrollView.FOCUS_DOWN) }

        findViewById<MaterialButton>(R.id.clearButton).setOnClickListener {
            AppLog.clear()
            logContent.text = getString(R.string.log_empty)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
