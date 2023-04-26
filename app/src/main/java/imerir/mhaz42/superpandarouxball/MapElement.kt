package imerir.mhaz42.superpandarouxball

import android.graphics.Rect
import kotlinx.serialization.Serializable

@Serializable

class MapElement(private val rectElement: List<Int>, private val isFinishLine: Boolean) {
    fun getRect(): Rect {
        return Rect(rectElement[0], rectElement[1], rectElement[2], rectElement[3])
    }

    fun isFinishLine(): Boolean {
        return isFinishLine
    }

    fun contains(x: Float, y: Float): Boolean {
        val toRet = Rect(rectElement[0], rectElement[1], rectElement[2], rectElement[3])
        return toRet.contains(x.toInt(), y.toInt())
    }
}