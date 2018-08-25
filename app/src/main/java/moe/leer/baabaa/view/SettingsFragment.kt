package moe.leer.baabaa.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.ListView
import es.dmoral.toasty.Toasty
import moe.leer.baabaa.R
import moe.leer.baabaa.util.FileUtil
import java.io.File
import java.net.URI

/**
 *
 * Created by leer on 8/24/18.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class SettingsFragment : PreferenceFragment() {

  private val PERMISSON_CODE = 11
  val TAG = "SettingsFragment"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.preferences)

    val backupPref = findPreference("pref_key_backup")
    val restorePref = findPreference("pref_key_restore")

    backupPref.setOnPreferenceClickListener {
      if (requestPermission()) {

        val filePath = FileUtil.backupToMyAppDir(activity)
        if (filePath != null) {
          Toasty.success(activity, "Save in $filePath").show()
          backupPref.summary = filePath
        } else {
          Toasty.error(activity, "Can't backup now").show()
        }
      }
      return@setOnPreferenceClickListener true
    }

    restorePref.setOnPreferenceClickListener {
      if (requestPermission()) {
        val intent = Intent()
            .setType("file/json")
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, getString(R.string.title_select)), 128)
      }
      return@setOnPreferenceClickListener true
    }
  }

  private fun requestPermission(): Boolean {
    val hasPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    if (!hasPermission) {
      ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSON_CODE)
    }
    return hasPermission
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
    if (requestCode == PERMISSON_CODE) {
      if (grantResults == null || grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED) {
        Toasty.error(activity, getString(R.string.toast_no_permisson)).show()
      }
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
  }

  /**
   * @see https://stackoverflow.com/questions/27750901/how-to-manage-dividers-in-a-preferencefragment
   */
  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    //remove dividers
    val rootView = view
    val list = rootView!!.findViewById(android.R.id.list) as ListView
    list.divider = null
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 128 && resultCode == RESULT_OK) {
      // fixme
      val ok = FileUtil.restoreFromFile(this.activity, File(URI(data?.data!!.toString())))
      if (ok) {
        Toasty.success(activity, "Restore data succeed").show()
      } else {
        Toasty.error(activity, "Restore data failed").show()
      }
    }
  }
}