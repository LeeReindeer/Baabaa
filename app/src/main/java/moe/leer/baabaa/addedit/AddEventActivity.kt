package moe.leer.baabaa.addedit

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_add_event.*
import moe.leer.baabaa.R
import moe.leer.baabaa.base.BaseActivity
import moe.leer.baabaa.model.BaaEvent
import moe.leer.baabaa.util.GlideApp
import moe.leer.baabaa.util.MatisseGlideEngine
import moe.leer.baabaa.view.DatePickerFragment

class AddEventActivity : BaseActivity(), AddEventContract.View, DatePickerDialog.OnDateSetListener, ColorPickerDialogListener {

  private val CHOOSE_PIC_CODE = 9
  private val PERMISSON_CODE = 10

  private lateinit var presenter: AddEventPresenter
  private var pos = -1
  private var pickedImage = false
  private lateinit var picPath: String
  private var eventType = BaaEvent.COMMEMORATION
  private var picType = BaaEvent.IMAGE_TYPE

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_add_event)

    setResult(Activity.RESULT_OK)
    initData()
    initView()
  }

  private inline fun pickImage() {
    Matisse.from(this)
        .choose(MimeType.ofAll())
        .countable(true)
        .maxSelectable(1)
        .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
        .thumbnailScale(0.85f)
        .imageEngine(MatisseGlideEngine())
        .forResult(CHOOSE_PIC_CODE)
  }

  override fun initView() {
    makeViewSquare(eventImage)

    radioGroup.setOnCheckedChangeListener { group, checkedId ->
      when (checkedId) {
        R.id.commemorationRadio -> eventType = BaaEvent.COMMEMORATION
        R.id.countRadio -> eventType = BaaEvent.CONTDAYS
        R.id.birthdayRadio -> eventType = BaaEvent.BIRTHDAY
        R.id.lunarBirthdayRadio -> eventType = BaaEvent.LUNNARBIRTHDAY
      }
    }

    if (picRadio.isChecked) {
      eventImage.setOnClickListener {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSON_CODE)
        } else {
          pickImage()
        }
      }
    } else if (colorRadio.isChecked) {
      picType = BaaEvent.COLOR_TYPE
      eventImage.setOnClickListener {
        ColorPickerDialog.newBuilder().setColor(resources.getColor(R.color.colorPrimary)).show(this)
      }
    }

    picTypeRadioGroup.setOnCheckedChangeListener { group, checkedId ->
      when (checkedId) {
        R.id.picRadio -> {
          Log.d(TAG, "initView: pick image")
          picType = BaaEvent.IMAGE_TYPE
          eventImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
              ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSON_CODE)
            } else {
              pickImage()
            }
          }
        }
        R.id.colorRadio -> {
          Log.d(TAG, "initView: pick color")
          picType = BaaEvent.COLOR_TYPE
          eventImage.setOnClickListener {
            ColorPickerDialog.newBuilder().setColor(resources.getColor(R.color.colorPrimary)).show(this)
          }
        }
      }
    }

    //todo image type
    dateLayout.setOnClickListener {
      val dateFragment = DatePickerFragment()
      dateFragment.show(fragmentManager, "datePicker")
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.addevent_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.ae_save -> {
        if (checkInput()) {
          presenter.saveEvent(BaaEvent(eventDateText.text.toString(), eventEdit.text.toString(), eventType, picPath, picType, isPinnedSwitch.isChecked), pos)
        }
// this.setResult(Activity.RESULT_OK, intent)
        Log.d(TAG, "onOptionsItemSelected: save")
      }
      R.id.ae_delete -> {
        presenter.deleteEvent(pos)
        if (parent == null) {
          setResult(Activity.RESULT_OK)
        } else {
          Log.d(TAG, "onOptionsItemSelected: parent")
          parent.setResult(Activity.RESULT_OK)
        }
        Log.d(TAG, "onOptionsItemSelected: delete")
      }
      android.R.id.home -> {
        super.onBackPressed()
      }
    }
    return true
  }

  private fun checkInput(): Boolean {
    return when {
      (!pickedImage) -> {
        Toasty.warning(this, getString(R.string.toast_selext_image)).show()
        false
      }
      eventEdit.text.isNullOrBlank() -> {
        Toasty.warning(this, getString(R.string.toast_write_some)).show()

        false
      }
      eventDateText.text.startsWith("请") -> {
        Toasty.warning(this, getString(R.string.toast_date)).show()
        false
      }
      else -> true
    }
  }

  override fun initData() {
    pos = intent.getIntExtra(EVENT_POST_PARAM, -1)
    presenter = AddEventPresenter(this, this)
    presenter.init()
  }

  /**
   * load an old event from ListActivity
   */
  override fun loadEvent(event: BaaEvent?) {
    Log.d(TAG, "loadEvent: ")
    if (event != null) {
      this.title = getString(R.string.title_edit)

      eventType = event.type
      picType = event.picType
      pickedImage = true
      eventEdit.setText(event.content)
      eventDateText.text = event.date
      isPinnedSwitch.isChecked = event.isPinned
      when (event.type) {
        BaaEvent.COMMEMORATION -> radioGroup.check(R.id.commemorationRadio)
        BaaEvent.CONTDAYS -> radioGroup.check(R.id.countRadio)
        BaaEvent.BIRTHDAY -> radioGroup.check(R.id.birthdayRadio)
        BaaEvent.LUNNARBIRTHDAY -> radioGroup.check(R.id.lunarBirthdayRadio)
      }

      when (event.picType) {
        BaaEvent.IMAGE_TYPE -> {
          picTypeRadioGroup.check(R.id.picRadio)
          picPath = event.picPath
          GlideApp.with(this).load(event.picPath.toUri()).into(eventImage)
        }
        BaaEvent.COLOR_TYPE -> {
          picTypeRadioGroup.check(R.id.colorRadio)
          picPath = event.picPath // color code
          Log.d(TAG, "loadEvent: load color: $picPath")
          Handler().post {
            Glide.with(this).load(ColorDrawable(picPath.toInt())).into(eventImage)
          }
        }
      }
    } else {
      this.title = getString(R.string.title_new)
    }
  }

  override fun onSave(ok: Boolean) {
    if (ok) {
      Toasty.success(this, getString(R.string.taost_saved)).show()
      finish()
    }
  }

  override fun onDelete(ok: Boolean) = if (ok) {
    Toasty.success(this, getString(R.string.toast_deleted)).show()
    finish()
  } else {
    Toasty.error(this, getString(R.string.toast_cant_delete)).show()
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    if (requestCode == PERMISSON_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      pickImage()
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == CHOOSE_PIC_CODE && resultCode == Activity.RESULT_OK) {
      val uri = Matisse.obtainResult(data)[0]
      GlideApp.with(this).load(uri).into(eventImage)
      picPath = uri.toString()
      pickedImage = true
    }
  }

  var lastPress: Long = 0

  override fun onBackPressed() {
    if (getDoublePressSetting()) {
      if (System.currentTimeMillis() - lastPress > 1500) {
        Toasty.warning(this, getString(R.string.toast_double_press)).show()
        lastPress = System.currentTimeMillis()
      } else {
        super.onBackPressed()
      }
    } else {
      super.onBackPressed()
    }
  }

  override fun onColorSelected(dialogId: Int, color: Int) {
    Log.w(TAG, "onColorSelected: picked :$color")
    picPath = color.toString(10)
    pickedImage = true
    GlideApp.with(this).load(ColorDrawable(color)).into(eventImage)
  }

  override fun onDialogDismissed(dialogId: Int) {
  }

  override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
    eventDateText.text = "$year-${month + 1}-$dayOfMonth"
  }
}
