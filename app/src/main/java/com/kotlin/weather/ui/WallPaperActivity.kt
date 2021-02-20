package com.kotlin.weather.ui

import BaseActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kotlin.library.util.*
import com.kotlin.library.view.dialog.AlertDialog
import com.kotlin.weather.R
import com.kotlin.weather.adapter.WallPaperAdapter
import com.kotlin.weather.model.VerticalBean
import com.kotlin.weather.model.WallPaper
import com.kotlin.weather.viewmodel.WallpaperViewModel
import kotlinx.android.synthetic.main.activity_wall_paper.*
import org.litepal.LitePal.deleteAll

/**
 * 壁纸管理
 * @author llw
 * @date 2021/2/19 11:05
 */
class WallPaperActivity : BaseActivity() {

    /**
     * item高度列表
     */
    private val heightList: MutableList<Int> = ArrayList()

    /**
     * 必应的每日壁纸
     */
    private var biyingUrl: String? = null

    /**
     * 启动相册标识
     */
    val SELECT_PHOTO = 2

    private val wallpaperViewModel by lazy {
        ViewModelProviders.of(this).get(WallpaperViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wall_paper)
        //初始化
        initView()
        //网络请求
        networkRequestHelper()
    }

    /**
     * 初始化
     */
    private fun initView() {
        //高亮状态栏
        StatusBarUtil.lightMode(this)
        //左上角的返回
        Back(toolbar)
        //初始化列表item高度
        initItemHeight()
        //滑动监听
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                fabSetting.apply {
                    if (dy <= 0) show() else hide()
                }
            }
        })
    }

    /**
     * 网络请求
     */
    private fun networkRequestHelper() {
        wallpaperViewModel.apply {
            showLoadingDialog()
            biying()//必应每日一图
            wallpaper()//网络壁纸列表

            //观察必应壁纸返回
            biyingLiveData.observe(this@WallPaperActivity, Observer { result ->
                val biyingResponse = result.getOrNull()
                if (biyingResponse != null) {
                    biyingUrl = "http://cn.bing.com${biyingResponse.images[0].url}"
                    biyingUrl!!.LogD("biyingUrl")
                } else {
                    "获取不到必应壁纸".showToast()
                    result.exceptionOrNull()?.printStackTrace()
                }
            })

            //观察网络壁纸列表返回
            wallpaperLiveData.observe(this@WallPaperActivity, Observer { result ->
                val wallPaperResponse = result.getOrNull()
                if (wallPaperResponse != null) {
                    val topBean = VerticalBean("", "", "", "", "top")
                    val bottomBean = VerticalBean("", "", "", "", "bottom")
                    wallpaperBean += topBean
                    wallpaperBean += wallPaperResponse.res.vertical
                    wallpaperBean += bottomBean
                    //配置适配器
                    val wallPaperAdapter =
                        WallPaperAdapter(R.layout.item_wallpaper_list, wallpaperBean, heightList)
                    //列表布局管理器
                    val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    rv.layoutManager = manager
                    rv.adapter = wallPaperAdapter

                    wallPaperAdapter.setOnItemChildClickListener { _, _, position ->
                        //这里的列表数据实际上有32条，有两条假数据，就是首尾这两条，所以点击的时候要做判断处理
                        if (position === 0 || position === wallpaperBean.size - 1) { //是否为第一条或者最后一条
                            startActivity(Intent(context, AboutUsActivity::class.java))
                        } else {
                            val intent = Intent(context, ImageActivity::class.java)
                            intent.putExtra("position", position - 1)
                            startActivity(intent)
                        }
                    }
                    //删除数据库中的数据
                    deleteAll(WallPaper::class.java)
                    for (i in 0 until wallpaperBean.size) {
                        //添加数据进去数据库表
                        WallPaper(wallpaperBean[i].img).save()
                    }
                    dismissLoadingDialog()
                } else {
                    "获取不到网络壁纸列表数据".showToast()
                    result.exceptionOrNull()?.printStackTrace()
                }
            })
        }
    }

    /**
     * 初始化列表item高度
     */
    private fun initItemHeight() {
        heightList.add(100)
        for (i in 0 until 30) {
            heightList.add(300)
        }
        heightList.add(100)
    }

    /**
     * 壁纸底部弹窗弹窗
     */
    private fun showSettingDialog(type: Int) {
        var bottomSettingDialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
            .addDefaultAnimation()
            .setCancelable(true)
            .fromBottom(true)
            .setContentView(R.layout.dialog_bottom_wallpaper_setting)
            .setWidthAndHeight(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            .setOnClickListener(R.id.lay_wallpaper_list, View.OnClickListener {//壁纸列表
                val intent = Intent(context, ImageActivity::class.java)
                intent.putExtra("position", 0)
                startActivity(intent)
                bottomSettingDialog!!.dismiss()
            }).setOnClickListener(R.id.lay_everyday_wallpaper, View.OnClickListener {//每日一图
                "使用每日一图".showToast()
                SPUtils.putString(Constant.WALLPAPER_URL, biyingUrl, context)
                //壁纸列表
                SPUtils.putInt(Constant.WALLPAPER_TYPE, 2, context)
                bottomSettingDialog!!.dismiss()
            }).setOnClickListener(R.id.lay_upload_wallpaper, View.OnClickListener {//本地上传
                startActivityForResult(CameraUtils.getSelectPhotoIntent(), SELECT_PHOTO)
                "请选择图片".showToast()
                bottomSettingDialog!!.dismiss()
            }).setOnClickListener(R.id.lay_default_wallpaper, View.OnClickListener {//默认壁纸
                "使用默认壁纸".showToast()
                SPUtils.putInt(Constant.WALLPAPER_TYPE, 4, context) //使用默认壁纸
                SPUtils.putString(Constant.WALLPAPER_URL, null, context)
                bottomSettingDialog!!.dismiss()
            })
        bottomSettingDialog = builder.create()
        val ivWallpaperList = bottomSettingDialog.getView(R.id.iv_wallpaper_list) as ImageView?
        val ivEverydayWallpaper =
            bottomSettingDialog.getView(R.id.iv_everyday_wallpaper) as ImageView?
        val ivUploadWallpaper = bottomSettingDialog.getView(R.id.iv_upload_wallpaper) as ImageView?
        val ivDefaultWallpaper =
            bottomSettingDialog.getView(R.id.iv_default_wallpaper) as ImageView?
        when (type) {
            1 -> ivWallpaperList!!.visibility = View.VISIBLE
            2 -> ivEverydayWallpaper!!.visibility = View.VISIBLE
            3 -> ivUploadWallpaper!!.visibility = View.VISIBLE
            4 -> ivDefaultWallpaper!!.visibility = View.VISIBLE
            else -> ivDefaultWallpaper!!.visibility = View.GONE
        }
        bottomSettingDialog.show()
        //弹窗关闭监听
        bottomSettingDialog.setOnDismissListener { fabSetting.show() }
    }

    /**
     * 壁纸设置
     */
    fun wallpaperSetting(view: View) {
        fabSetting.hide()
        showSettingDialog(SPUtils.getInt(Constant.WALLPAPER_TYPE, 4, context))
    }

    /**
     * Activity返回结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECT_PHOTO -> {
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号
                    var imagePath: String? = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        //4.4及以上系统使用这个方法处理图片
                        data?.let { CameraUtils.getImgeOnKitKatPath(it, this) }
                    } else {
                        data?.let { CameraUtils.getImageBeforeKitKatPath(it, this) }
                    }
                    displayImage(imagePath)
                }
            }
            else -> "$requestCode + , + $resultCode + , + ${data.toString()}".LogD("result-->")
        }
    }

    /**
     * 从相册获取完图片(根据图片路径显示图片)
     */
    private fun displayImage(imagePath: String?) {
        if (!TextUtils.isEmpty(imagePath)) {
            //将本地上传选中的图片地址放入缓存,当手动定义开关打开时，取出缓存中的图片地址，显示为背景
            SPUtils.putInt(Constant.WALLPAPER_TYPE, 3, context)
            SPUtils.putString(Constant.WALLPAPER_URL, imagePath, context)
            "已更换为你选择的图片".showToast()
        } else {
            SPUtils.putInt(Constant.WALLPAPER_TYPE, 0, context)
            "图片获取失败".showToast()
        }
    }
}
