package com.example.css545

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.css545.ui.theme.CSS545Theme
import com.example.css545.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "options")

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Set click listener for the button
        binding.buttonGoToSecond.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        CoroutineScope(Dispatchers.IO).launch {
            val savedSelection = loadSelection()
            runOnUiThread {
                val radioButton = findViewById<RadioButton>(savedSelection ?: R.id.io_option)
                if (radioButton != null) {
                    radioButton.isChecked = true
                }
            }
        }

        findViewById<Button>(R.id.save_button).setOnClickListener {
            val selectedId = findViewById<RadioGroup>(R.id.radio_group).checkedRadioButtonId
            CoroutineScope(Dispatchers.IO).launch {
                saveSelection(selectedId)
            }
        }
    }

    private suspend fun loadSelection(): Int? {
        val key = stringPreferencesKey("selected_option")
        val preferences = dataStore.data.first()
        val savedSelection = preferences[key]?.toInt()
        Log.d("Settings", "Loaded selection: $savedSelection")
        return savedSelection
    }

    private suspend fun saveSelection(selectedId: Int) {
        val key = stringPreferencesKey("selected_option")
        dataStore.edit { preferences -> preferences[key] = selectedId.toString() }
        Log.d("Settings", "Saved Selection: $selectedId")
    }
}