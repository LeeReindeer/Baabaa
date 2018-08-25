package moe.leer.baabaa.addedit

import moe.leer.baabaa.base.BaseContract
import moe.leer.baabaa.model.BaaEvent

/**
 *
 * Created by leer on 3/16/18.
 */
interface AddEventContract {

  interface Presenter : BaseContract.Presenter {
    fun saveEvent(event: BaaEvent, pos: Int)

    fun deleteEvent(pos: Int)
  }

  interface View : BaseContract.View {
    fun loadEvent(event: BaaEvent?)

    fun onSave(ok: Boolean)

    fun onDelete(ok: Boolean)

  }
}