package com.example.css545

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.ComponentActivity
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.css545.MainActivity
import com.example.css545.databinding.ActivitySecondBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class SecondActivity : ComponentActivity() {
    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set click listeners for buttons
        binding.buttonGoToFirst.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.buttonLoadImage.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val selectedOption = loadSelectedOption()
                runOnUiThread {
                    displayImage(selectedOption)
                }
            }
        }

        binding.buttonSaveImage.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val selectedOption = loadSelectedOption()
                runOnUiThread {
                    saveImage(selectedOption)
                }
            }
        }
    }

    private suspend fun loadSelectedOption(): Int? {
        val key = stringPreferencesKey("selected_option") // Use the same key as in MainActivity.kt
        val preferences = applicationContext.dataStore.data.first()
        return preferences[key]?.toInt()
    }

    private fun displayImage(option: Int?) {
        val imageView = findViewById<ImageView>(R.id.imageView)
        val imageResId = when (option) {
            R.id.io_option -> R.drawable.io
            R.id.europa_option -> R.drawable.europa
            R.id.ganymede_option -> R.drawable.ganymede
            R.id.callisto_option -> R.drawable.callisto
            else -> R.drawable.jupiter // Default image if option not found
        }
        imageView.setImageDrawable(ContextCompat.getDrawable(this, imageResId))
    }

    private fun saveImage(selectedOption: Int?) {
        val imageView = findViewById<ImageView>(R.id.imageView)
        val bitmap = (imageView.drawable).toBitmap()

        val imageName = when (selectedOption) {
            R.id.io_option -> "io.jpg"
            R.id.europa_option -> "europa.jpg"
            R.id.ganymede_option -> "ganymede.jpg"
            R.id.callisto_option -> "callisto.jpg"
            else -> null
        }

        if (imageName != null) {
            val saveDir = getExternalFilesDir(null)
            var fileName = imageName

            var index = 0
            while (File(saveDir, fileName).exists()) {
                index++
                val nameWithoutExtension = imageName.substringBeforeLast(".")
                val extension = imageName.substringAfterLast(".")
                fileName = "$nameWithoutExtension-$index.$extension"
            }
            val file = File(saveDir, fileName)
            val outStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            outStream.flush()
            outStream.close()

            Toast.makeText(this, "Image saved as $fileName", Toast.LENGTH_SHORT).show()
        }
    }
}