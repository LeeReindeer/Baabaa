package moe.leer.baabaa.util

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import moe.leer.baabaa.base.BaseActivity
import moe.leer.baabaa.model.BaaEvent
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.util.*

/**
 *
 * Created by leer on 8/24/18.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
object FileUtil {

  val TAG = "FileUtil"
  /* Checks if external storage is available for read and write */
  fun isExternalStorageWritable(): Boolean {
    return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
  }

  /* Checks if external storage is available to at least read */
  fun isExternalStorageReadable(): Boolean {
    return Environment.getExternalStorageState() in
        setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
  }

  fun backupToMyAppDir(context: Context): String? {
    val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
    val appDir = File(root + "/BaabaaBackup")

    if (!isExternalStorageWritable()) {
      return null
    }

    if (!appDir.exists()) {
      if (!appDir.mkdirs()) {
        Log.e(TAG, "backupToMyAppDir: can't mkdir ${appDir.path}")
        return null
      }
    }
    val gson = GsonBuilder().setPrettyPrinting().create()
    val eventsJson = SharedPreferencesUtil.getStringFromSP(context, BaseActivity.EVENT_SHARED, "")
    val eventList = gson.fromJson<MutableList<BaaEvent>>(eventsJson, object : TypeToken<MutableList<BaaEvent>>() {
    }.type)
    val prettyJson = gson.toJson(eventList)
    val jsonFile = File(appDir, "Baabaa-${System.currentTimeMillis()}.json")
    return try {
      val out = PrintWriter(jsonFile)
      Log.d(TAG, "backupToMyAppDir: $eventsJson")
      out.println(prettyJson)
      out.flush()
      out.close()

      jsonFile.path
    } catch (e: IOException) {
      e.printStackTrace()
      null
    }
  }

  /**
   * @see https://stackoverflow.com/questions/3402735/what-is-simplest-way-to-read-a-file-into-string
   */
  fun restoreFromFile(context: Context, file: File?): Boolean {
    if (file == null || !isExternalStorageReadable()) {
      return false
    }

    val jsonString = Scanner(file).useDelimiter("\\Z").next()
    Log.d(TAG, "restoreFromFile: $jsonString")

    try {
      val eventList = Gson().fromJson<MutableList<BaaEvent>>(jsonString, object : TypeToken<MutableList<BaaEvent>>() {
      }.type)
      if (eventList.isEmpty()) {
        return false
      }
      SharedPreferencesUtil.saveStringToSP(context, BaseActivity.EVENT_SHARED, jsonString)
    } catch (e: Exception) {
      e.printStackTrace()
      return false
    }
    return true
  }
}