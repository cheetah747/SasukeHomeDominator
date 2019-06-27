package com.sibyl.screenshotlistener

import android.content.Context
import android.graphics.*
import java.io.*



/**
 * @author Sasuke on 2019-6-25 0025.
 */
class WaterMarker(val context: Context) {

    var TEXT_PAINT_SIZE = 25.0f

    //行距
    var LINE_SPACE = 1.4f

//    val bottomCard by lazy {
//        BitmapFactory.decodeResource(context.resources, R.mipmap.feedback_card)
//    }

    /**
     * 用于自定义card背景图片资源ID
     */
    var defaultCardResId = com.sibyl.sasukehomeDominator.R.drawable.ic_launcher_background

    /**
     * 把需要显示的信息画到底部卡片上
     */
    fun drawWaterMark(scrShotPath: String?, vararg infos: String): Bitmap {
//        val bottomCard = readBitmapRes(context, defaultCardResId)
        var shotBmp: Bitmap? = BitmapFactory.decodeFile(scrShotPath, BitmapFactory.Options().apply {
            val opt = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(scrShotPath, opt)
            //前面都是铺垫，这个才是目的
            this.inJustDecodeBounds = false
            this.inSampleSize = if (Math.min(opt.outWidth ,opt.outHeight)>=  2160) 2 else 1//凡是大于1080的全特么给我缩小！
            inPreferredConfig = Bitmap.Config.ARGB_8888
        })

        //字的大小，改成图片最高高度的60分之一
        shotBmp?.let { TEXT_PAINT_SIZE = Math.max(it.width,it.height).toFloat() / 60 }

        val newBitmap = Bitmap.createBitmap(shotBmp?.width ?:0, shotBmp?.height ?:0, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        val bitmapPaint = Paint().apply {
            isDither = true
            isFilterBitmap = true
        }
        val src = Rect(0, 0, shotBmp?.width ?:0, shotBmp?.height ?:0)
        val dst = Rect(0, 0, shotBmp?.width ?:0, shotBmp?.height ?:0)
        canvas.drawBitmap(shotBmp, src, dst, bitmapPaint)

        //截图已经画上画布了，接下来把字写上去。
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DEV_KERN_TEXT_FLAG)
        textPaint.apply {
            textSize = TEXT_PAINT_SIZE
            typeface = Typeface.DEFAULT
            color = Color.WHITE
            setShadowLayer(4f, 1f, 1f,Color.BLACK)
        }

        val heightUnit = TEXT_PAINT_SIZE * LINE_SPACE //newBitmap.height / infos.size.toFloat()//每行字的高度
        //绘制文字的起始水平线高度
        var textStartY = (shotBmp?.height?.toFloat() ?:1920f) - (infos.size - 0.6)* heightUnit

        infos.forEach {
            val textLength = Rect().apply { textPaint.getTextBounds(it, 0, it?.length, this) }.width()
            val textStartX = newBitmap.width - textLength.toFloat() - TEXT_PAINT_SIZE * 0.8
            canvas.drawText(it, textStartX.toFloat(), textStartY.toFloat(), textPaint)
            textStartY += TEXT_PAINT_SIZE * LINE_SPACE
        }
        canvas.save()
        canvas.restore()
        return newBitmap
    }


    /**
     * 把截图与底部信息卡拼接起来
     */
    fun mergeScrShot2BottomCard(scrShotPath: String, bottomCard: Bitmap): Boolean {
        var resultBmp: Bitmap?
        var shotBmp: Bitmap? = BitmapFactory.decodeFile(scrShotPath, BitmapFactory.Options().apply {
            val opt = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(scrShotPath, opt)
            //前面都是铺垫，这个才是目的
            this.inJustDecodeBounds = false
            this.inSampleSize = if (opt.outWidth > bottomCard.width) 2 else 1//凡是大于1080的全特么给我缩小！
            inPreferredConfig = Bitmap.Config.RGB_565
        })

//        val shotWidth = shotBmp.getWidth()
//        if (bottomCard.getWidth() !== shotWidth) {
//            //以第二张图片的宽度为标准，对第一张图片进行缩放。
//            val shotHeight = bottomCard.getHeight() * shotWidth / bottomCard.getWidth()
//            resultBmp = Bitmap.createBitmap(bottomCard.width, shotHeight + bottomCard.height, Bitmap.Config.RGB_565)
//            val canvas = Canvas(resultBmp)
//            val newSizeBmp2 = resizeBitmap(shotBmp, bottomCard.width, shotBmp.height)
//            canvas.drawBitmap(shotBmp,0.toFloat(),0.toFloat(),null)
//            canvas.drawBitmap(newSizeBmp2, 0.toFloat(), shotBmp.height.toFloat(), null)
//        } else {
        //以防万一，有时候会为空
        if (shotBmp == null) return false
        //如果截图宽 小于 底卡宽，那就缩小底卡
        if (shotBmp?.width < bottomCard.width) {
            var newbottomCard =
                resizeBitmap(bottomCard, shotBmp?.width, bottomCard.height * shotBmp?.width / bottomCard.width)
            resultBmp =
                Bitmap.createBitmap(newbottomCard.width, shotBmp.height + newbottomCard.height, Bitmap.Config.RGB_565)
            val canvas = Canvas(resultBmp)
            canvas.drawBitmap(shotBmp, 0.toFloat(), 0.toFloat(), null)
            canvas.drawBitmap(newbottomCard, 0.toFloat(), shotBmp.height.toFloat(), null)
            //如果截图宽 大于 底卡宽，那就缩小截图
        } else if (shotBmp?.width > bottomCard.width) {
            shotBmp = resizeBitmap(shotBmp, bottomCard.width, shotBmp.height * bottomCard.width / shotBmp.width)
            resultBmp = Bitmap.createBitmap(shotBmp?.width, shotBmp.height + bottomCard.height, Bitmap.Config.RGB_565)
            val canvas = Canvas(resultBmp)
            canvas.drawBitmap(shotBmp, 0.toFloat(), 0.toFloat(), null)
            canvas.drawBitmap(bottomCard, 0.toFloat(), shotBmp.height.toFloat(), null)
        } else {
            //两张图片宽度相等，则直接拼接。
            resultBmp = Bitmap.createBitmap(bottomCard.width, shotBmp.height + bottomCard.height, Bitmap.Config.RGB_565)
            val canvas = Canvas(resultBmp)
            canvas.drawBitmap(shotBmp, 0.toFloat(), 0.toFloat(), null)
            canvas.drawBitmap(bottomCard, 0.toFloat(), shotBmp.height.toFloat(), null)
        }

//        }
        var saveResult = saveBmp2File(resultBmp, File(scrShotPath), Bitmap.CompressFormat.JPEG)
        shotBmp?.takeIf { !it.isRecycled }?.run { this.recycle(); shotBmp = null }
        resultBmp?.takeIf { !it.isRecycled }?.run { this.recycle(); resultBmp = null }

        return saveResult
    }


    /**
     * 通过流的方式，可以使调用底层JNI来实现减少Java层的内存消耗。
     */
    fun readBitmapRes(context: Context, resId: Int): Bitmap {
        val opt = BitmapFactory.Options().apply {
            inPreferredConfig = Bitmap.Config.RGB_565
            inPurgeable = true
            inInputShareable = true
        }

        //获取资源图片
        val inputStream = context.resources.openRawResource(resId)
        var resBitmap = BitmapFactory.decodeStream(inputStream, null, opt)
        inputStream.close()
        return resBitmap
    }


    /**
     * 改变图片尺寸
     */
    fun resizeBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val scaleWidth = newWidth.toFloat() / bitmap.width
        val scaleHeight = newHeight.toFloat() / bitmap.height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


    /**
     * 保存图片到文件
     */
    fun saveBmp2File(src: Bitmap, file: File, format: Bitmap.CompressFormat/*, recycle: Boolean*/): Boolean {
        if (src == null || src.width == 0 || src.height == 0) {
            return false
        }

        var os: OutputStream? = null
        var ret = false
        try {
            os = BufferedOutputStream(FileOutputStream(file))
            ret = src.compress(format, 100, os)
            if ( !src.isRecycled)
                src.recycle()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } finally {
            os?.close()
        }
        //文件格式重命名。
//        file.run {
//            renameTo(File(name.replace(".png",when(format){
//                Bitmap.CompressFormat.JPEG -> ".jpg"
//                else -> ".png"
//            })))
//        }

        return ret
    }

}