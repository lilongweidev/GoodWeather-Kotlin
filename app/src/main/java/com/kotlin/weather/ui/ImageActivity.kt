package com.kotlin.weather.ui

import BaseActivity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.kotlin.library.util.Constant
import com.kotlin.library.util.SPUtils
import com.kotlin.library.util.StatusBarUtil
import com.kotlin.library.util.showToast
import com.kotlin.weather.R
import com.kotlin.weather.model.WallPaper
import kotlinx.android.synthetic.main.activity_image.*
import org.litepal.LitePal.findAll
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * 图片显示
 *
 * @author llw
 * @date 2021/2/20 16:40
 */
class ImageActivity : BaseActivity(), View.OnClickListener {

    var wallpaperUrl: String? = null

    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        //初始化
        initView()

    }

    /**
     * 页面初始化
     */
    private fun initView() {
        showLoadingDialog()
        //透明状态栏
        StatusBarUtil.transparencyBar(context)

        ivBack.setOnClickListener(this)
        btnSettingWallpaper.setOnClickListener(this)
        btnDownload.setOnClickListener(this)

        //获取位置
        val position = intent.getIntExtra("position", 0)

        var mList = findAll(WallPaper::class.java) as MutableList
        mList.removeAt(0)
        mList.removeAt(mList.size - 1)
        //配置适配器
        val adapter = WallPaperAdapter(R.layout.item_image_list, mList)
        //ViewPager2实现方式
        vp.adapter = adapter

        vp.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.d("position-->", "" + position)
                wallpaperUrl = mList[position].ImgUrl
                bitmap = getBitMap(wallpaperUrl)
            }
        })
        adapter.notifyDataSetChanged()
        vp.setCurrentItem(position, false)
        dismissLoadingDialog()
    }

    /**
     * 壁纸适配器
     */
    inner class WallPaperAdapter(layoutResId: Int, data: MutableList<WallPaper>) :
        BaseQuickAdapter<WallPaper, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: WallPaper) {
            Glide.with(mContext).load(item.ImgUrl)
                .into(helper.getView(R.id.wallpaper) as ShapeableImageView)
        }
    }

    /**
     * 保存图片到本地
     */
    private fun saveBitmapGallery(context: Context, bitmap: Bitmap): Boolean {
        val name = System.currentTimeMillis().toString()
        val photoPath = Environment.DIRECTORY_DCIM + "/Camera"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, photoPath)//保存路径
                put(MediaStore.MediaColumns.IS_PENDING, true)
            }
        }
        val insert = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: return false
        //这个打开了输出流  直接保存图片就好了
        context.contentResolver.openOutputStream(insert).use {
            it ?: return false
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, false)
        }
        return true
    }


    /**
     * Url转Bitmap
     *
     * @param url
     * @return
     */
    fun getBitMap(url: String?): Bitmap? {
        //新启一个线程进行转换
        Thread(Runnable {
            var imageurl: URL? = null
            try {
                imageurl = URL(url)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
            try {
                val conn = imageurl!!.openConnection() as HttpURLConnection
                conn.doInput = true
                conn.connect()
                val inputStream = conn.inputStream
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()
        return bitmap
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivBack -> finish()
            R.id.btnSettingWallpaper -> {
                //放入缓存
                SPUtils.putString(Constant.WALLPAPER_URL, wallpaperUrl, context)
                //壁纸列表
                SPUtils.putInt(Constant.WALLPAPER_TYPE, 1, context)
                "已设置".showToast()
            }
            R.id.btnDownload -> if(saveBitmapGallery(context, bitmap!!)) "图片保存成功".showToast() else "图片保存失败".showToast()
        }
    }
}
