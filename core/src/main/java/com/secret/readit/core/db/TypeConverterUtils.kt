/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.secret.readit.core.data.articles.utils.Parser
import com.secret.readit.core.data.utils.isImageElement
import com.secret.readit.core.data.utils.isTextElement
import com.secret.readit.core.uimodels.ImageUiElement
import com.secret.readit.model.BaseElement
import com.secret.readit.model.Element
import timber.log.Timber
import java.io.ByteArrayOutputStream

class TypeConverterUtils {
    @TypeConverter
    fun baseElementToPrimitives(elements: List<BaseElement>): Array<*>{
        for (element in elements) {
            if(element.isImageElement){
                return imageElementToBytes(element as ImageUiElement)
            }
            if (element.isTextElement) {
                return arrayOf(textElementToString(element as Element))
            }
        }
        return emptyArray<BaseElement>()
    }

    /**
     * to convert imageElement to bytes we need to do two things:
     * first: encode the actual bitmap to byteArray
     * second: encode the path of bitmap to byteArray
     *
     * We do this, so when user intend to publish draft article we can get valid data and upload it to Firebase storage
     */
    private fun imageElementToBytes(element: ImageUiElement): Array<ByteArray>{
        //part 1
        val stream = ByteArrayOutputStream()
        element.bitmap?.compress(Bitmap.CompressFormat.WEBP, 90, stream) //TODO:(WEBP is deprecated) update when targeting Android 11
        val bitmapByteArray = stream.toByteArray()
        //Part 2
        val bitmapPathByteArray = element.imgPath.toByteArray() //Works well as long as the Charset is UTF-8 which is default on Android

        //Wrap all in one array
        return arrayOf(bitmapByteArray, bitmapPathByteArray)
    }

    private fun textElementToString(element: Element): String? {
        return if (element.imageUri == null) Parser.reverseParse(element) else null
    }
    //Reverse
    @Suppress("UNCHECKED_CAST")
    @TypeConverter
    fun primitivesToBaseElement(array: Array<*>): List<BaseElement>{
        val formattedElements =  mutableListOf<BaseElement>()
        for (item in array){
            if (item is String) formattedElements += stringToTextElement(item) else break
        }
        try {
            formattedElements += bytesToImageElement(array as Array<ByteArray>)
        } catch (ex: Exception) {
            Timber.e("Invalid ByteArray or maybe error in decoding, cause: ${ex.message}")
        }
        return formattedElements
    }

    private fun bytesToImageElement(bytes: Array<ByteArray>): ImageUiElement{
        val bitmap = BitmapFactory.decodeByteArray(bytes[0], 0, bytes.size)
        val imgPath = String(bytes[1], Charsets.UTF_8)
        return ImageUiElement(bitmap, imgPath)
    }

    private fun stringToTextElement(text: String): Element{
        return Parser.parse(text)
    }
}
