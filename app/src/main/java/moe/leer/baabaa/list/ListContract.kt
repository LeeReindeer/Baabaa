package moe.leer.baabaa.list

import moe.leer.baabaa.base.BaseContract
import moe.leer.baabaa.model.BaaEvent
import moe.leer.baabaa.util.DateUtil

/**
 *
 * Created by leer on 8/15/18.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
interface ListContract {
  interface Presenter : BaseContract.Presenter {
    fun refresh()
  }

  interface View : BaseContract.View {
    fun onRefresh()

    fun viewVisibleChange(status: Int)

    fun viewEventTypeChange(event: BaaEvent, days: Int?, birthday: DateUtil.Birthday?)
  }
}