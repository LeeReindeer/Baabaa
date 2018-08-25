package moe.leer.baabaa.list

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import jp.wasabeef.glide.transformations.BlurTransformation
import moe.leer.baabaa.R
import moe.leer.baabaa.model.BaaEvent
import moe.leer.baabaa.model.Solar
import moe.leer.baabaa.util.DateUtil
import moe.leer.baabaa.util.LunarSolarConverter
import java.util.*

/**
 *
 * Created by leer on 8/15/18.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class EventAdapter(layoutResId: Int, data: List<BaaEvent>?) : BaseItemDraggableAdapter<BaaEvent, BaseViewHolder>(layoutResId, data) {

  val TAG = "EventAdapter"

  override fun convert(holder: BaseViewHolder, item: BaaEvent?) {
    when (item!!.type) {
      BaaEvent.COMMEMORATION -> {
        holder.setText(R.id.cardContent, "「${item.content}」${DateUtil.daysBeforeNow(item.date)}天")
        holder.setText(R.id.cardDate, "从${item.date}")
      }
      BaaEvent.CONTDAYS -> {
        val days = DateUtil.daysAfterNow(item.date)
        if (days < 0) {
          holder.setText(R.id.cardContent, "${item.content}已过去${-days}天")
        } else {
          holder.setText(R.id.cardContent, "离${item.content}还有${days}天")
        }
        holder.setText(R.id.cardDate, "时间：${item.date}")
      }
      BaaEvent.BIRTHDAY -> {
        val todayTime = DateUtil.format(Date())!! // 2018-08-15, 1998-01-28, birthday in this year: 2018-01-28
        var years = Integer.parseInt(todayTime.substring(0, 4)) - Integer.parseInt(item.date.substring(0, 4)) //2018 - 1998
        var days = DateUtil.daysAfterNow(todayTime.substring(0, 4) + item.date.substring(4)) // 2018-01-28
        if (days < 0) { // your birthday is already passed
          years += 1
          days = DateUtil.daysAfterNow((Integer.parseInt(todayTime.substring(0, 4)) + 1).toString() + item.date.substring(4))
        }
        holder.setText(R.id.cardContent, "离${item.content}${years}岁生日还有${days}天")
        holder.setText(R.id.cardDate, "生日：${item.date}")
      }
      BaaEvent.LUNNARBIRTHDAY -> {
        val todayTime = DateUtil.format(Date())!! // 2018-08-15, 1998-01-28, birthday in this year: 2018-01-28
        val nowSolar = Solar(todayTime)
        val nowLunar = LunarSolarConverter.solarToLunar(nowSolar)
        val lunarBirthday = LunarSolarConverter.solarToLunar(Solar(item.date))
        val thisLunarBirthday = lunarBirthday.copy(lunarYear = nowLunar.lunarYear)

        var years = nowLunar.lunarYear - lunarBirthday.lunarYear
        var days = DateUtil.daysAfterNow(LunarSolarConverter.lunarToSolar(thisLunarBirthday).toString())
        if (days < 0) {
          years += 1
          thisLunarBirthday.lunarYear += 1
          days = DateUtil.daysAfterNow(LunarSolarConverter.lunarToSolar(thisLunarBirthday).toString())
        }
        holder.setText(R.id.cardContent, "离${item.content}${years}岁农历生日还有${days}天")
        holder.setText(R.id.cardDate, "下个农历生日(显示为阳历)：${LunarSolarConverter.lunarToSolar(thisLunarBirthday)}")
      }
    }

    when (item.picType) {
      BaaEvent.IMAGE_TYPE -> {
        // load image
        val uri = Uri.parse(item.picPath)
        Glide.with(mContext).load(uri).into(holder.getView(R.id.cardPic) as ImageView)

        Glide.with(mContext).load(uri).apply(bitmapTransform(BlurTransformation(25))).listener(GlidePalette
            .with(uri.toString())
            .use(BitmapPalette.Profile.VIBRANT_LIGHT).intoBackground(holder.getView(R.id.cardBackgroundView) as View)
        ).into(holder.getView(R.id.cardBackgroundIV) as ImageView)
      }
      BaaEvent.COLOR_TYPE -> {
        // load color
        val colorDrawable = ColorDrawable(item.picPath.toInt())
        Glide.with(mContext).load(colorDrawable).into(holder.getView(R.id.cardPic) as ImageView)

        Glide.with(mContext).load(colorDrawable).apply(bitmapTransform(BlurTransformation(25)))
            .into(holder.getView(R.id.cardBackgroundIV) as ImageView)
      }
    }


  }

}