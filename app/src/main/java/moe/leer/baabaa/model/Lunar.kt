package moe.leer.baabaa.model

/**
 *
 * Created by leer on 8/15/18.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
data class Lunar(var lunarYear: Int, var lunarMonth: Int, var lunarDay: Int, var isLeap: Boolean) {
  constructor() : this(0, 0, 0, false)

  constructor(dateStr: String) : this() {
    val list = dateStr.split("-")
    lunarYear = list[0].toInt()
    lunarMonth = list[1].toInt()
    lunarDay = list[2].toInt()
  }

  override fun toString(): String {
    return "$lunarYear-$lunarMonth-$lunarDay"
  }
}