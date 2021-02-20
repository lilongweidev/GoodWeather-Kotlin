@file:Suppress("DEPRECATION")

package com.kotlin.library.util

import android.annotation.TargetApi
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

/**
 * 相机、相册工具类
 *
 * @author llw
 * @date 2021/2/20 16:00
 */
object CameraUtils {

    fun getTakePhotoIntent(context: Context, outputImagePath: File): Intent? {
        //获取系統版本
        val currentApiVersion = Build.VERSION.SDK_INT
        // 激活相机
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            if (currentApiVersion < 24) {
                // 从文件中创建uri
                val uri = Uri.fromFile(outputImagePath)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            } else {
                //兼容android7.0 使用共享文件的形式
                val contentValues = ContentValues(1)
                contentValues.put(MediaStore.Images.Media.DATA, outputImagePath.absolutePath)
                val uri = context.applicationContext.contentResolver
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            }
        }
        return intent
    }

    fun getSelectPhotoIntent(): Intent? {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        return intent
    }

    /**
     * 判断sdcard是否被挂载
     */
    private fun hasSdcard(): Boolean {
        return Environment.getExternalStorageState() ==
                Environment.MEDIA_MOUNTED
    }

    /**
     * 4.4及以上系统处理图片的方法
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun getImgeOnKitKatPath(data: Intent, context: Context): String? {
        var imagePath: String? = null
        val uri = data.data
        Log.d("uri=intent.getData :", "" + uri)
        if (DocumentsContract.isDocumentUri(context, uri)) {
            val docId = DocumentsContract.getDocumentId(uri) //数据表里指定的行
            Log.d("getDocumentId(uri) :", "" + docId)
            Log.d("uri.getAuthority() :", "" + uri!!.authority)
            if ("com.android.providers.media.documents" == uri.authority) {
                val id = docId.split(":").toTypedArray()[1]
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath =
                    getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, context)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(docId)
                )
                imagePath = getImagePath(contentUri, null, context)
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            imagePath = getImagePath(uri, null, context)
        }
        return imagePath
    }

    /**
     * 通过uri和selection来获取真实的图片路径,从相册获取图片时要用
     */
    private fun getImagePath(
        uri: Uri?,
        selection: String?,
        context: Context
    ): String? {
        var path: String? = null
        val cursor =
            context.contentResolver.query(uri!!, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    /**
     * 改变拍完照后图片方向不正的问题
     */
    fun ImgUpdateDirection(filepath: String, orcBitmap: Bitmap?, iv: ImageView) {
        var orcBitmap =orcBitmap
        var angle: Int //图片旋转的角度
        //根据图片的URI获取图片的绝对路径
        Log.i("tag", ">>>>>>>>>>>>>开始")
        //String filepath = ImgUriDoString.getRealFilePath(getApplicationContext(), uri);
        Log.i("tag", "》》》》》》》》》》》》》》》$filepath")
        //根据图片的filepath获取到一个ExifInterface的对象
        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(filepath)
            Log.i("tag", "exif》》》》》》》》》》》》》》》$exif")

            // 读取图片中相机方向信息
            val ori = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            angle = when (ori) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
            //如果图片不为0
            if (angle != 0) {
                // 旋转图片
                val m = Matrix()
                m.postRotate(angle.toFloat())
                orcBitmap = Bitmap.createBitmap(
                    orcBitmap!!, 0, 0, orcBitmap.width,
                    orcBitmap.height, m, true
                )
            }
            if (orcBitmap != null) {
                iv.setImageBitmap(orcBitmap)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 4.4以下系统处理图片的方法
     */
    fun getImageBeforeKitKatPath(
        data: Intent,
        context: Context
    ): String? {
        val uri = data.data
        return getImagePath(uri, null, context)
    }

    //比例压缩
    fun comp(image: Bitmap): Bitmap? {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        if (baos.toByteArray().size / 1024 > 5120) { //判断如果图片大于5M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset() //重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos) //这里压缩50%，把压缩后的数据存放到baos中
        }
        var isBm: ByteArrayInputStream
        val newOpts = BitmapFactory.Options()
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true
        var bitmap: Bitmap?
        newOpts.inJustDecodeBounds = false
        val width = newOpts.outWidth
        val height = newOpts.outHeight
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        val heightResolution = 800f //这里设置高度为800f
        val widthResolution = 480f //这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        var be = 1 //be=1表示不缩放
        if (width > height && width > widthResolution) { //如果宽度大的话根据宽度固定大小缩放
            be = (newOpts.outWidth / widthResolution).toInt()
        } else if (width < height && height > heightResolution) { //如果高度高的话根据宽度固定大小缩放
            be = (newOpts.outHeight / heightResolution).toInt()
        }
        if (be <= 0) be = 1
        newOpts.inSampleSize = be //设置缩放比例
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565 //降低图片从ARGB888到RGB565
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = ByteArrayInputStream(baos.toByteArray())
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts)
        return bitmap //压缩好比例大小后再进行质量压缩
    }
}