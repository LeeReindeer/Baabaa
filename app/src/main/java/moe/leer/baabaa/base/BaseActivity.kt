package moe.leer.baabaa.base

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.os.bundleOf
import moe.leer.baabaa.R

/**
 *
 * Created by leer on 8/15/18.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
abstract class BaseActivity : AppCompatActivity() {

  val TAG = "BaseActivity"

  abstract fun initView()

  abstract fun initData()

  companion object {
    const val EVENT_JSON_PARAM = "events_json"
    const val EVENT_POST_PARAM = "event_pos"
    const val EVENT_SHARED = "events"
  }

  inline fun <reified T : Activity> Activity.startActivity(options: Bundle?, vararg args: Pair<String, Any>) {
    val intent = Intent(this, T::class.java)
    intent.putExtras(bundleOf(*args))
    startActivity(intent, options)
  }

  inline fun <reified T : Activity> Activity.startActivityForResult(options: Bundle?, requestCode: Int, vararg args: Pair<String, Any>) {
    val intent = Intent(this, T::class.java)
    intent.putExtras(bundleOf(*args))
    startActivityForResult(intent, requestCode, options)
  }

  protected fun getTransAnimSetting(): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_key_trans", true)
  }

  protected fun getDoublePressSetting(): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_key_double_Press", true)
  }

  protected fun makeViewSquare(view: View) {
    Handler().post {
      val viewParams = view.layoutParams
      viewParams.height = view.width
      view.layoutParams = viewParams
    }
  }

  protected fun setFullScreen() {
    this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      val window = this.window
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
      window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      // set statusBar transparent
      window.statusBarColor = this.resources.getColor(R.color.transparent)
    }
  }
}