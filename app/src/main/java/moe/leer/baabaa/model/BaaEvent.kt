package moe.leer.baabaa.model

import com.google.gson.Gson

/**
 *
 * Created by leer on 8/15/18.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */

data class BaaEvent(var date: String, var content: String, var type: Int,
                    var picPath: String, var picType: Int, var isPinned: Boolean) {

  override fun toString(): String {
    return Gson().toJson(this)
  }

  companion object {
    const val COMMEMORATION = 0
    const val CONTDAYS = 1
    const val BIRTHDAY = 2
    const val LUNNARBIRTHDAY = 3

    const val IMAGE_TYPE = 0
    const val COLOR_TYPE = 1
  }
}