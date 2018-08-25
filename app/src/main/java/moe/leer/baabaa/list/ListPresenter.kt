package moe.leer.baabaa.list

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import moe.leer.baabaa.base.BaseActivity
import moe.leer.baabaa.model.BaaEvent
import moe.leer.baabaa.util.DateUtil
import moe.leer.baabaa.util.SharedPreferencesUtil

/**
 *
 * Created by leer on 8/15/18.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class ListPresenter(val context: Context, val view: ListContract.View) : ListContract.Presenter {
  val TAG = "ListPresenter"

  var eventList: MutableList<BaaEvent> = ArrayList()
  private var pinnedEvent: BaaEvent? = null

  override fun init() {
    val eventsJson = SharedPreferencesUtil.getStringFromSP(context, BaseActivity.EVENT_SHARED, "")
    val gson = Gson()
    if (eventsJson.isNullOrBlank()) {
      eventList.clear()
      view.viewVisibleChange(0)
    } else {
      eventList.clear()
      eventList.addAll(gson.fromJson<MutableList<BaaEvent>>(eventsJson, object : TypeToken<MutableList<BaaEvent>>() {
      }.type))
      // show pinned event
      pinnedEvent = eventList.findLast { it.isPinned }
      if (pinnedEvent == null) {
        view.viewVisibleChange(1)

      } else {
        val days: Int
        val birthday: DateUtil.Birthday
        view.viewVisibleChange(2)

        when (pinnedEvent!!.type) {
          BaaEvent.COMMEMORATION -> {
            days = DateUtil.daysBeforeNow(pinnedEvent!!.date)
            view.viewEventTypeChange(pinnedEvent!!, days, null)
//            eventTV.text = pinnedEvent!!.content
//            eventMainTV.text = pinnedEvent!!.content + days + "天"
//            daysTV.text = days.toString(10)
//            startTimeTV.text = pinnedEvent!!.date
          }
          BaaEvent.CONTDAYS -> {
            days = DateUtil.daysAfterNow(pinnedEvent!!.date)
            view.viewEventTypeChange(pinnedEvent!!, days, null)
//            startTimeTV.text = pinnedEvent!!.date
//            if (days < 0) {
//              eventTV.text = pinnedEvent!!.content + "已过去"
//              daysTV.text = "${-days}天"
//              eventMainTV.text = "${pinnedEvent!!.content}已过去${-days}天"
//            } else {
//              eventTV.text = "离${pinnedEvent!!.content}还有"
//              daysTV.text = "${-days}天"
//              eventMainTV.text = "${eventTV.text}${-days}天"
//            }
          }
          BaaEvent.BIRTHDAY -> {
            birthday = DateUtil.birthday(pinnedEvent!!.date)
            view.viewEventTypeChange(pinnedEvent!!, null, birthday)
//            eventTV.text = "离${pinnedEvent!!.content}${birthday.years}岁生日还有"
//            daysTV.text = "${birthday.days}天"
//            eventMainTV.text = "${eventTV.text}${birthday.days}天"
//            startTimeTV.text = pinnedEvent!!.date
          }
          BaaEvent.LUNNARBIRTHDAY -> {
            birthday = DateUtil.lunarBirthday(pinnedEvent!!.date)
            view.viewEventTypeChange(pinnedEvent!!, null, birthday)
//            eventTV.text = "离${pinnedEvent!!.content}${birthday.years}农历岁生日还有"
//            daysTV.text = "${birthday.days}天"
//            eventMainTV.text = "${eventTV.text}${birthday.days}天"
//            startTimeTV.text = "下个农历生日${birthday.solar!!}"
          }
        }
      }
    }
  }

  override fun refresh() {
    Log.d(TAG, "refresh")
    init()
  }
}