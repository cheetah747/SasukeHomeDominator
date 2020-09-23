package com.sibyl.screenshotlistener

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import com.sibyl.sasukehomeDominator.R
import com.sibyl.sasukehomeDominator.SasukeApplication
import com.sibyl.sasukehomeDominator.ScrShotSettingAct
import com.sibyl.sasukehomeDominator.util.FileData
import com.sibyl.sasukehomeDominator.util.PreferHelper
import com.sibyl.sasukehomeDominator.util.StaticVar
import java.io.*


/**
 * @author Sasuke on 2019-6-25 0025.
 */
class WaterMarker(val context: Context) {
    companion object{
        /**
         * 将bitmap宽适应屏幕
         */
         @JvmStatic
        fun trasBmp2Card(bitmap: Bitmap, heightFactor: Float): Bitmap{
            //比例
            val scaleFactor: Float = Resources.getSystem().displayMetrics.widthPixels / bitmap.width.toFloat()
//            val desWidth = Resources.getSystem().displayMetrics.widthPixels
//            val tempBmp = Bitmap.createBitmap(desWidth, (desWidth / heightFactor).toInt(), Bitmap.Config.RGB_565)

//            val canvas = Canvas(tempBmp)
//            canvas.scale(scaleFactor, scaleFactor)
//            canvas.drawBitmap(bitmap,null,Rect(0,0,bitmap.width, bitmap.height),Paint())
//            canvas.clipRect(0f, 0f, bitmap.width.toFloat(), bitmap.width.toFloat() / heightFactor)

//            return tempBmp
            // 取得想要缩放的matrix参数
//            val matrix = Matrix().apply { postScale(scaleFactor, scaleFactor) }
            // 得到新的图片
            // 得到新的图片
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, (bitmap.width / heightFactor).toInt(),null,true)
        }

    }

    var TEXT_PAINT_SIZE = 25.0f

    //行距
    var LINE_SPACE = 1.4f

//    val bottomCard by lazy {
//        BitmapFactory.decodeResource(context.resources, R.mipmap.feedback_card)
//    }

    /**
     * 用于自定义card背景图片资源ID
     */
    var defaultCardResId =R.mipmap.default_card_background

    val textPaintColor by lazy {
        //只有在启用水印卡片&&反色的时候，才用黑色字，否则用白色
        if(PreferHelper.getInstance().getBoolean(StaticVar.KEY_IS_SHOW_WATER_CARD, false)
            && PreferHelper.getInstance().getBoolean(StaticVar.KEY_WATER_TEXT_IS_BLACK, false)){
            Color.BLACK
        }else{
            Color.WHITE
        }
    }

    //文字画笔（普通模式）
    val textPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG or Paint.DEV_KERN_TEXT_FLAG).apply {
            textSize = TEXT_PAINT_SIZE
            typeface = context.resources.getFont(R.font.google_product_sans)
            color = Color.WHITE
            //如果不显示水印卡片，那就加阴影，否则不加
            setShadowLayer(4f, 1f, 1f,Color.BLACK)
        }
    }

    //文字画笔（画水印卡片）
    val textPaintInCard by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG or Paint.DEV_KERN_TEXT_FLAG).apply {
            textSize = TEXT_PAINT_SIZE
            typeface = context.resources.getFont(R.font.google_product_sans)
            color = textPaintColor
            //如果不显示水印卡片，那就加阴影，否则不加
//            if ( !PreferHelper.getInstance().getBoolean(StaticVar.KEY_IS_SHOW_WATER_CARD, false)){
            PreferHelper.getInstance().getBoolean(StaticVar.KEY_WATER_TEXT_IS_BLACK, false).run {
                setShadowLayer(1f, 1f, 1f,when(this){
                    true -> Color.WHITE
                    else -> Color.BLACK
                })
            }

//            }
        }
    }


    //图片画笔
    val bitmapPaint by lazy {
        Paint().apply {
            isDither = true
            isFilterBitmap = true
        }
    }

    //每行字的高度
    val heightUnit = TEXT_PAINT_SIZE * LINE_SPACE //newBitmap.height / infos.size.toFloat()//每行字的高度

    //水印位置（左，中，右）
    val waterPos by lazy { PreferHelper.getInstance().getString(StaticVar.KEY_POS_SELECT, StaticVar.RIGHT) }

    /**
     * 把需要显示的信息画到底部卡片上
     */
    fun drawWaterMark(scrShotPath: String?, vararg infos: String): Bitmap? {
        var shotBmp: Bitmap? = BitmapFactory.decodeFile(scrShotPath, BitmapFactory.Options().apply {
            val opt = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(scrShotPath, opt)
            //前面都是铺垫，这个才是目的
            this.inJustDecodeBounds = false
            this.inSampleSize = if (Math.min(opt.outWidth ,opt.outHeight)>  2160) 1 else 1//凡是大于1080的全特么给我缩小！(新：缩你麻痹，有时候给老子莫名其妙缩成一半模糊小图是怎么回事)
            inPreferredConfig = Bitmap.Config.RGB_565
        })

        //要画水印卡片的情况===============================
        //字的大小，改成图片窄边的30分之一
        shotBmp?.let { TEXT_PAINT_SIZE = Math.min(it.width,it.height).toFloat() / 30/*60*/
            //如果要使用水印卡片&&是竖屏截图
            if (PreferHelper.getInstance().getBoolean(StaticVar.KEY_IS_SHOW_WATER_CARD,false)
                     && shotBmp.width < shotBmp.height){
                    val cardFile = FileData.waterCardFile(SasukeApplication.app!!)
                    val bottomCard = if (cardFile.exists()) BitmapFactory.decodeFile(cardFile.absolutePath) else readBitmapRes(context, defaultCardResId)
                    //开始绘制
                    val desHeight = ((bottomCard?.width ?: 0) / ScrShotSettingAct.IMG_CARD_HEIGHT_FACTORS).toInt()
                    val newBitmap = Bitmap.createBitmap(bottomCard?.width ?:0, desHeight, Bitmap.Config.RGB_565)

                    val canvas = createBmpCanvas(bottomCard, newBitmap)
                    //绘制文字的起始水平线高度
                    var textStartY = desHeight / 2 - infos.size * heightUnit / 2 + heightUnit * 0.66
                    myDrawText(canvas, bottomCard.width, textStartY.toFloat(), true ,*infos)
                    bottomCard?.takeIf { !it.isRecycled }?.run { this.recycle()}

                    return mergeScrShot2BottomCard(shotBmp,newBitmap)
            }
        }

        //不画水印卡片的情况===============================
        val newBitmap = Bitmap.createBitmap(shotBmp?.width ?:0, shotBmp?.height ?:0, Bitmap.Config.RGB_565)
        val canvas = createBmpCanvas(shotBmp, newBitmap)
        //绘制文字的起始水平线高度
        var textStartY = (shotBmp?.height?.toFloat() ?:1920f) - (infos.size /*- 0.66*/)* heightUnit
        //水印位置
        myDrawText(canvas,newBitmap.width, textStartY,false, *infos)
        return newBitmap
    }


    /**
     * 用一个bitmap创建一个Canvas
     * 注： 把srcBitmap画到desBitmap里，这里的desBitmap一般是个空bitmap
     */
    fun createBmpCanvas(srcBitmap: Bitmap?,desBitmap: Bitmap?): Canvas{
        val canvas = Canvas(desBitmap)
        val src = Rect(0, 0, desBitmap?.width ?: 0, desBitmap?.height ?:0)
        val dst = Rect(0, 0, desBitmap?.width ?: 0, desBitmap?.height ?:0)
        srcBitmap?.let { canvas.drawBitmap(srcBitmap, src, dst, bitmapPaint) }
        return canvas
    }


    /**
     * 实际绘制水印文字
     * textStartY：文字绘制起始Y轴坐标
     * isInCard：是否绘制卡片模式
     */
    fun myDrawText(canvas: Canvas, bitmapWidth: Int, textStartY: Float, isInCard: Boolean,vararg infos: String) {
        var textPaintTemp = if (isInCard) textPaintInCard else textPaint
        var textStartYTemp = textStartY
        infos.forEach {
            val textLength = Rect().apply { textPaintTemp.getTextBounds(it, 0, it?.length, this) }.width()
//            val textStartX = TEXT_PAINT_SIZE * 0.8//写在左下角
//            val textStartX = newBitmap.width / 2 - textLength.toFloat() / 2 //写在中央
            val textStartX: Double = when(waterPos){
                StaticVar.RIGHT -> bitmapWidth - textLength.toFloat() - TEXT_PAINT_SIZE * 1.0/*0.8*/ //写在右下角（还是多减点吧，圆角屏，需要多空一点空间出来）
                StaticVar.CENTER -> bitmapWidth / 2.0 - textLength.toFloat() / 2.0 //写在中央
                else -> TEXT_PAINT_SIZE * 0.8//写在左下角
            }

            canvas.drawText(it, textStartX.toFloat(), textStartYTemp, textPaintTemp)
            textStartYTemp += TEXT_PAINT_SIZE * LINE_SPACE
        }
    }

    /**
     * 把截图与底部信息卡拼接起来
     */
    fun mergeScrShot2BottomCard(shotBmp: Bitmap?, bottomCard: Bitmap): Bitmap? {
        var resultBmp: Bitmap?
//        var shotBmp: Bitmap? = BitmapFactory.decodeFile(scrShotPath, BitmapFactory.Options().apply {
//            val opt = BitmapFactory.Options().apply {
//                inJustDecodeBounds = true
//            }
//            BitmapFactory.decodeFile(scrShotPath, opt)
//            //前面都是铺垫，这个才是目的
//            this.inJustDecodeBounds = false
//            this.inSampleSize = if (opt.outWidth > bottomCard.width) 2 else 1//凡是大于1080的全特么给我缩小！
//            inPreferredConfig = Bitmap.Config.RGB_565
//        })

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
        if (shotBmp == null) return null
        //如果截图宽 小于 底卡宽，那就缩小底卡
        if (shotBmp.width < bottomCard.width) {
            var newbottomCard =
                resizeBitmap(bottomCard, shotBmp.width, bottomCard.height * shotBmp.width / bottomCard.width)
            resultBmp =
                Bitmap.createBitmap(newbottomCard.width, shotBmp.height + newbottomCard.height, Bitmap.Config.RGB_565)
            val canvas = Canvas(resultBmp)
            canvas.drawBitmap(shotBmp, 0.toFloat(), 0.toFloat(), null)
            canvas.drawBitmap(newbottomCard, 0.toFloat(), shotBmp.height.toFloat(), null)
            //如果截图宽 大于 底卡宽，那就缩小截图
        } else if (shotBmp.width > bottomCard.width) {
            var shotBmpTemp = shotBmp
            shotBmpTemp = resizeBitmap(shotBmp, bottomCard.width, shotBmp.height * bottomCard.width / shotBmp.width)
            resultBmp = Bitmap.createBitmap(shotBmpTemp.width, shotBmpTemp.height + bottomCard.height, Bitmap.Config.RGB_565)
            val canvas = Canvas(resultBmp)
            canvas.drawBitmap(shotBmpTemp, 0.toFloat(), 0.toFloat(), null)
            canvas.drawBitmap(bottomCard, 0.toFloat(), shotBmpTemp.height.toFloat(), null)
            shotBmpTemp?.takeIf { !it.isRecycled }?.run { this.recycle()}
        } else {
            //两张图片宽度相等，则直接拼接。
            resultBmp = Bitmap.createBitmap(bottomCard.width, shotBmp.height + bottomCard.height, Bitmap.Config.RGB_565)
            val canvas = Canvas(resultBmp)
            canvas.drawBitmap(shotBmp, 0.toFloat(), 0.toFloat(), null)
            canvas.drawBitmap(bottomCard, 0.toFloat(), shotBmp.height.toFloat(), null)
        }

//        }
//        var saveResult = saveBmp2File(resultBmp, File(scrShotPath), Bitmap.CompressFormat.PNG)
        shotBmp?.takeIf { !it.isRecycled }?.run { this.recycle()}
        bottomCard?.takeIf { !it.isRecycled }?.run { this.recycle()}

//        resultBmp?.takeIf { !it.isRecycled }?.run { this.recycle(); resultBmp = null }

        return resultBmp
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