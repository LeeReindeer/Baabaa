package moe.leer.baabaa.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Display the fragment as the main content.
    fragmentManager.beginTransaction()
        .replace(android.R.id.content, SettingsFragment())
        .commit()
  }
}
