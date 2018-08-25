package moe.leer.baabaa.addedit

import android.app.Activity
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import moe.leer.baabaa.base.BaseActivity
import moe.leer.baabaa.model.BaaEvent
import moe.leer.baabaa.util.SharedPreferencesUtil

/**
 *
 * Created by leer on 8/15/18.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class AddEventPresenter(val activity: Activity, val view: AddEventContract.View) : AddEventContract.Presenter {
  val TAG = "AddEventPresenter"

  override fun init() {
    val eventJson = activity.intent.getStringExtra(BaseActivity.EVENT_JSON_PARAM)
    if (eventJson.isNullOrBlank()) {
      view.loadEvent(null)
    } else {
      val event = Gson().fromJson<BaaEvent>(eventJson, object : TypeToken<BaaEvent>() {}.type)
      view.loadEvent(event)
    }
  }

  override fun saveEvent(event: BaaEvent, pos: Int) {
    val eventsJson = SharedPreferencesUtil.getStringFromSP(activity, BaseActivity.EVENT_SHARED, "")
    val gson = Gson()
    val eventList: MutableList<BaaEvent>
    if (eventsJson == "") { // the first event
      eventList = ArrayList()
    } else {
      eventList = gson.fromJson<MutableList<BaaEvent>>(eventsJson, object : TypeToken<MutableList<BaaEvent>>() {
      }.type)
      // set others to unpinned
      if (event.isPinned && eventList.isNotEmpty()) {
        eventList.forEach {
          it.isPinned = false
        }
      }
    }

    if (pos < 0) { // a new event
      eventList.add(event)
    } else {
      val oldEvent = eventList[pos]
      oldEvent.date = event.date
      oldEvent.content = event.content
      oldEvent.type = event.type
      oldEvent.picPath = event.picPath
      oldEvent.picType = event.picType
      oldEvent.isPinned = event.isPinned
    }
    //todo update weight

    Log.d(TAG, "saveEvent: ${gson.toJson(eventList)}")
    SharedPreferencesUtil.saveStringToSP(activity, BaseActivity.EVENT_SHARED, gson.toJson(eventList))

    // save success
    view.onSave(true)
  }

  override fun deleteEvent(pos: Int) {
    if (pos < 0) {
      view.onDelete(false)
    } else {
      val eventsJson = SharedPreferencesUtil.getStringFromSP(activity, BaseActivity.EVENT_SHARED, "")
      val gson = Gson()
      val eventList: MutableList<BaaEvent> = gson.fromJson<MutableList<BaaEvent>>(eventsJson, object : TypeToken<MutableList<BaaEvent>>() {
      }.type)
      eventList.removeAt(pos)

      SharedPreferencesUtil.saveStringToSP(activity, BaseActivity.EVENT_SHARED, gson.toJson(eventList))
      view.onDelete(true)
    }
  }
}