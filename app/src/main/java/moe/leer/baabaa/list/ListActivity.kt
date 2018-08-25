package moe.leer.baabaa.list

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.core.view.postDelayed
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_main.*
import moe.leer.baabaa.R
import moe.leer.baabaa.addedit.AddEventActivity
import moe.leer.baabaa.base.BaseActivity
import moe.leer.baabaa.model.BaaEvent
import moe.leer.baabaa.util.DateUtil
import moe.leer.baabaa.util.SharedPreferencesUtil
import moe.leer.baabaa.view.SettingsActivity

class ListActivity : BaseActivity(), ListContract.View {

  private val NEW_EVENT_CODE = 1
  private val EDIT_EVENT_CODE = 2

  private lateinit var presenter: ListPresenter
  private lateinit var adapter: EventAdapter

  private var alreadyRefreshed = false


  override fun onCreate(savedInstanceState: Bundle?) {
    // enter full screen mode
    setFullScreen()

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    initData()
    initView()
  }

  override fun onRefresh() {
  }

  override fun viewVisibleChange(status: Int) {
    adapter.notifyDataSetChanged()
    when (status) {
      0 -> {
        eventTV.text = getString(R.string.text_add_event)
        eventMainTV.text = getString(R.string.text_add_event)
        daysTV.visibility = View.GONE
        eventDayLabel.visibility = View.GONE
        startTimeTV.visibility = View.GONE
        Glide.with(this)
            .load(R.drawable.default_background)
            .into(backgroundIV)
        addFab.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))
      }
      1 -> {
        eventTV.text = getString(R.string.text_pin)
        eventMainTV.text = getString(R.string.text_pin)
        daysTV.visibility = View.GONE
        startTimeTV.visibility = View.GONE
        eventDayLabel.visibility = View.GONE
        Glide.with(this)
            .load(R.drawable.default_background)
            .into(backgroundIV)
        addFab.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))
      }
      2 -> {
        daysTV.visibility = View.VISIBLE
        startTimeTV.visibility = View.VISIBLE
        eventDayLabel.visibility = View.VISIBLE
      }
    }
  }

  @SuppressLint("SetTextI18n")
  override fun viewEventTypeChange(event: BaaEvent, days: Int?, birthday: DateUtil.Birthday?) {
    adapter.notifyDataSetChanged()
    when (event.type) {
      BaaEvent.COMMEMORATION -> {
        eventTV.text = event.content
        eventMainTV.text = event.content + days + "天"
        daysTV.setContent(days!!.toString(10))
        startTimeTV.text = event.date
      }
      BaaEvent.CONTDAYS -> {
        startTimeTV.text = event.date
        if (days!! < 0) {
          eventTV.text = event.content + "已过去"
          daysTV.setContent("${-days}")
          eventMainTV.text = "${event.content}已过去${-days}天"
        } else {
          eventTV.text = "离${event.content}还有"
          daysTV.setContent("${days}")
          eventMainTV.text = "${eventTV.text}${days}天"
        }
      }
      BaaEvent.BIRTHDAY -> {
        eventTV.text = "离${event.content}${birthday!!.years}岁生日还有"
        daysTV.setContent("${birthday.days}")
        eventMainTV.text = "${eventTV.text}${birthday.days}天"
        startTimeTV.text = event.date
      }
      BaaEvent.LUNNARBIRTHDAY -> {
        eventTV.text = "离${event.content}${birthday!!.years}农历岁生日还有"
        daysTV.setContent("${birthday.days}")
        eventMainTV.text = "${eventTV.text}${birthday.days}天"
        startTimeTV.text = "下个农历生日${birthday.solar!!}"
      }
    }

    if (event.picType == BaaEvent.IMAGE_TYPE) {
      Log.w(TAG, "change image: ")
      backgroundView.alpha = 0.618f
      val uri = event.picPath.toUri()
      // load image
      Glide.with(this)
          .load(uri)
          .listener(GlidePalette.with(event.picPath)
              .use(BitmapPalette.Profile.MUTED_DARK)
              .intoBackground(backgroundView)
              .use(BitmapPalette.Profile.VIBRANT)
              .intoCallBack {
                // set color of fab
                addFab.backgroundTintList = ColorStateList.valueOf(it!!.getVibrantColor(resources.getColor(R.color.colorAccent)))
              })
          .into(backgroundIV)
    } else if (event.picType == BaaEvent.COLOR_TYPE) {
      backgroundView.alpha = 0.5f

      val colorDrawable = ColorDrawable(event.picPath.toInt())
      Glide.with(this).load(colorDrawable).into(backgroundIV)
      addFab.backgroundTintList = ColorStateList.valueOf(event.picPath.toInt())
    }
  }

  @SuppressLint("RestrictedApi")
  override fun initView() {
    val layoutManager = LinearLayoutManager(this)
    eventsRV.layoutManager = layoutManager
    adapter.setOnItemClickListener { adapter, view, position ->
      val option = ActivityOptions.makeSceneTransitionAnimation(this, view.findViewById(R.id.cardPic), "image").toBundle()
      startActivityForResult<AddEventActivity>(if (getTransAnimSetting()) option else null, EDIT_EVENT_CODE,
          EVENT_JSON_PARAM to presenter.eventList[position].toString(),
          EVENT_POST_PARAM to position)
//      startActivityForResult(Intent(this, AddEventActivity::class.java).apply {
//        putExtra(EVENT_JSON_PARAM, presenter.eventList[position].toString())
//        putExtra(EVENT_POST_PARAM, position)
//      }, EDIT_EVENT_CODE, option)
    }
    eventsRV.adapter = adapter
    val itemDragAndSwipeCallback = ItemDragAndSwipeCallback(adapter)
    val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
    itemTouchHelper.attachToRecyclerView(eventsRV)
    val onItemDragListener = object : OnItemDragListener {
      var start = 0
      override fun onItemDragMoving(source: RecyclerView.ViewHolder?, from: Int, target: RecyclerView.ViewHolder?, to: Int) {
      }

      override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
        //todo Vibrator
        start = pos
      }

      override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
        //todo update weight
        SharedPreferencesUtil.saveStringToSP(this@ListActivity, BaseActivity.EVENT_SHARED, Gson().toJson(presenter.eventList))
      }
    }
    adapter.enableDragItem(itemTouchHelper)
    adapter.setOnItemDragListener(onItemDragListener)

    //make layout's height = width
    //fixme empty view
    //makeViewSquare(appbarLayout)
    appbarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
      val scrollRange = appBarLayout.totalScrollRange
      val alpha = 1 - (1.0f * Math.abs(verticalOffset) / scrollRange)
      infoLayout.alpha = alpha
      eventMainTV.alpha = 1 - alpha
    }

    menuButton.setOnClickListener {
      drawerLayout.openDrawer(GravityCompat.START)
    }

    addFab.setOnClickListener {
      startActivityForResult<AddEventActivity>(null, NEW_EVENT_CODE)
    }
    initNavMenu()
  }

  private fun initNavMenu() {
    navigationView.setNavigationItemSelectedListener { menuItem ->
      when (menuItem.itemId) {
        R.id.nav_about -> {
          drawerLayout.closeDrawer(GravityCompat.START)
          drawerLayout.postDelayed(360) {
            Toasty.success(this@ListActivity, getString(R.string.toast_about)).show()
          }
        }
        R.id.nav_update -> {
          drawerLayout.closeDrawer(GravityCompat.START)
          drawerLayout.postDelayed(500) {
            Toasty.success(this@ListActivity, getString(R.string.toast_new_version)).show()
          }
        }
        R.id.nav_settings -> {
          drawerLayout.closeDrawer(GravityCompat.START)
          drawerLayout.postDelayed(360) {
            startActivity<SettingsActivity>(null)
//            startActivity(Intent(this@ListActivity, SettingsActivity::class.java))
          }
        }
      }
      true
    }
  }

  override fun initData() {
    presenter = ListPresenter(this, this)
    adapter = EventAdapter(R.layout.item_event, presenter.eventList)
    presenter.init()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    Log.d(TAG, "onActivityResult: result")
    if (requestCode == NEW_EVENT_CODE || requestCode == EDIT_EVENT_CODE) {
      Log.d(TAG, "onActivityResult: result ok? $resultCode")
      if (resultCode == Activity.RESULT_OK) {
        Log.d(TAG, "onActivityResult: refresh from $requestCode")
        presenter.refresh()
        alreadyRefreshed = true
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data)
    }
  }

  override fun onResume() {
    super.onResume()
    Log.d(TAG, "onResume: ")
    if (!alreadyRefreshed) {
      Log.d(TAG, "onResume: refresh")
      presenter.refresh()
    }
    // reset
    alreadyRefreshed = false
  }

  override fun onBackPressed() {
    // close drawer first
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START)
    } else {
      super.onBackPressed()
    }
  }
}
