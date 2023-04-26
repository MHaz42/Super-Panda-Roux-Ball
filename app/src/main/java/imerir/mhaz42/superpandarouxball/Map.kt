package imerir.mhaz42.superpandarouxball

import kotlinx.serialization.Serializable

@Serializable
class Map(private val mapElements: ArrayList<MapElement> = ArrayList()) {
    fun addMapElement(nMapElement: MapElement) {
        mapElements.add(nMapElement)
    }

    fun getMapElements(): ArrayList<MapElement> {
        return mapElements
    }
}