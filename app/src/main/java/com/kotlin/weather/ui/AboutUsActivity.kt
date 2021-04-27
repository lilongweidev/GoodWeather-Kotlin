package com.kotlin.weather.ui

import BaseActivity
import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import com.kotlin.library.util.APKVersionInfoUtils
import com.kotlin.library.util.SizeUtils
import com.kotlin.library.util.StatusBarUtil
import com.kotlin.library.util.showToast
import com.kotlin.library.view.dialog.AlertDialog
import com.kotlin.weather.R
import com.kotlin.weather.model.AppVersion
import kotlinx.android.synthetic.main.activity_about_us.*
import org.litepal.LitePal.find
import java.io.File

/**
 * 关于我们
 * @author llw
 * @date 2021/2/20 10:32
 */
class AboutUsActivity : BaseActivity(), View.OnClickListener {

    private var updateUrl: String? = null
    private var updateLog: String? = null
    private var is_update = false

    /**
     * 博客地址
     */
    private val CSDN_BLOG_URL = "https://blog.csdn.net/qq_38436214/category_9880722.html"

    /**
     * 源码地址
     */
    private val GITHUB_URL = "https://github.com/lilongweidev/GoodWeather"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        //初始化页面
        initView()
    }

    /**
     * 初始化页面
     */
    private fun initView() {
        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(context, R.color.about_bg_color)
        back(toolbar)
        layAppVersion.setOnClickListener(this)
        tvBlog.setOnClickListener(this)
        tvCode.setOnClickListener(this)
        tvCopyEmail.setOnClickListener(this)
        tvAuthor.setOnClickListener(this)

        var appVersion = find(AppVersion::class.java, 1)
        if (appVersion != null) {
            updateLog = appVersion.changelog

            //提示更新
            if (appVersion.versionShort != APKVersionInfoUtils.getVerName(context)) {
                is_update = true
                //显示红点
                vRed.visibility = View.VISIBLE
                updateUrl = appVersion.install_url
                updateLog = appVersion.changelog
            } else {
                //隐藏红点
                vRed.visibility = View.GONE
                is_update = false
            }
        }

    }

    /**
     * 点击事件
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.layAppVersion -> if (is_update) {
                showUpdateAppDialog(updateUrl, updateLog)
            } else {
                "当前已是最新版本".showToast()
            }
            R.id.tvBlog -> jumpUrl(CSDN_BLOG_URL)
            R.id.tvCode -> jumpUrl(GITHUB_URL)
            R.id.tvCopyEmail -> {
                val myClipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val myClip = ClipData.newPlainText("text", "lonelyholiday@qq.com")
                myClipboard.setPrimaryClip(myClip)
                "邮箱已复制".showToast()
            }
            R.id.tvAuthor -> "你为啥要点我呢？".showToast()
            else -> "点你咋的！".showToast()
        }
    }

    /**
     * 跳转URL
     *
     * @param url 地址
     */
    private fun jumpUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    /**
     * 更新弹窗
     *
     * @param downloadUrl 下载地址
     * @param updateLog   更新内容
     */
    private fun showUpdateAppDialog(
        downloadUrl: String?,
        updateLog: String?
    ) {
        var updateAppDialog: AlertDialog? = null

        val builder = AlertDialog.Builder(context)
            .addDefaultAnimation() //默认弹窗动画
            .setCancelable(true)
            .setText(R.id.tv_update_info, updateLog)
            .setContentView(R.layout.dialog_update_app_tip) //载入布局文件
            .setWidthAndHeight(//设置弹窗宽高
                SizeUtils.dp2px(context, 270F),
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).setOnClickListener(R.id.tv_cancel, View.OnClickListener { //取消
                updateAppDialog!!.dismiss()
            }).setOnClickListener(R.id.tv_fast_update, View.OnClickListener { //立即更新
                "正在后台下载，下载后会自动安装".showToast()
                if (downloadUrl != null) {
                    downloadApk(downloadUrl)
                }
                updateAppDialog!!.dismiss()
            })
        updateAppDialog = builder.create()
        updateAppDialog.show()
    }

    /**
     * 清除APK
     *
     * @param apkName
     * @return
     */
    private fun clearApk(apkName: String?): File? {
        val apkFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            apkName
        )
        if (apkFile.exists()) {
            apkFile.delete()
        }
        return apkFile
    }

    /**
     * 下载APK
     *
     * @param downloadUrl
     */
    private fun downloadApk(downloadUrl: String) {
        clearApk("GoodWeather.apk")
        //下载管理器 获取系统下载服务
        val downloadManager =
            getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val request =
            DownloadManager.Request(Uri.parse(downloadUrl))
        //设置运行使用的网络类型，移动网络或者Wifi都可以
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        //设置是否允许漫游
        request.setAllowedOverRoaming(true)
        //设置文件类型
        val mimeTypeMap = MimeTypeMap.getSingleton()
        val mimeString =
            mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(downloadUrl))
        request.setMimeType(mimeString)
        //设置下载时或者下载完成时，通知栏是否显示
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setTitle("下载新版本")
        request.setVisibleInDownloadsUi(true) //下载UI
        //sdcard目录下的download文件夹
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "GoodWeather.apk"
        )
        //将下载请求放入队列
        downloadManager.enqueue(request)
    }
}
