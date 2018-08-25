package moe.leer.baabaa.model

/**
 *
 * Created by leer on 8/15/18.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
data class Solar(var solarYear: Int, var solarMonth: Int, var solarDay: Int) {
  constructor() : this(0, 0, 0)

  constructor(dateStr: String) : this() {
    val list = dateStr.split("-")
    solarYear = list[0].toInt()
    solarMonth = list[1].toInt()
    solarDay = list[2].toInt()
  }

  override fun toString(): String {
    return "$solarYear-$solarMonth-$solarDay"
  }
}