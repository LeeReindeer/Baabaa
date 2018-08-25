package moe.leer.baabaa.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

/**
 *
 * Created by leer on 8/15/18.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
object SharedPreferencesUtil {

  fun saveStringToSP(context: Context, key: String, str: String) {
    context.getSharedPreferences("config", MODE_PRIVATE)
        .edit {
          putString(key, str)
        }
  }

  fun getStringFromSP(context: Context, key: String, defaultString: String): String? {
    val sp = context.getSharedPreferences("config", MODE_PRIVATE)
    return sp.getString(key, defaultString)
  }

  fun saveBooleanToSP(context: Context, key: String, t: Boolean) {
    val sp = context.getSharedPreferences("config", MODE_PRIVATE)
    val editor = sp.edit {
      putBoolean(key, t)
    }
  }

  fun getBooleanFromSP(context: Context, key: String): Boolean {
    val sp = context.getSharedPreferences("config", MODE_PRIVATE)
    return sp.getBoolean(key, true)
  }

  fun getBooleanFromSP(context: Context, key: String, defaultBoolean: Boolean): Boolean {
    val sp = context.getSharedPreferences("config", MODE_PRIVATE)
    return sp.getBoolean(key, defaultBoolean)
  }

  fun saveIntToSP(context: Context, key: String, i: Int) {
    val sp = context.getSharedPreferences("config", MODE_PRIVATE)
    val editor = sp.edit {
      putInt(key, i)
    }
  }

  fun getIntFromSP(context: Context, key: String): Int {
    val sp = context.getSharedPreferences("config", MODE_PRIVATE)
    return sp.getInt(key, 0)
  }

  fun getIntFromSP(context: Context, key: String, defaultInt: Int): Int {
    val sp = context.getSharedPreferences("config", MODE_PRIVATE)
    return sp.getInt(key, defaultInt)
  }

  fun clean(context: Context) {
    val sp = context.getSharedPreferences("config", MODE_PRIVATE).edit {
      clear()
    }
  }
}