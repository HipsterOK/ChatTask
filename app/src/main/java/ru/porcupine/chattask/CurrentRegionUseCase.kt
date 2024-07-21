package ru.porcupine.chattask

import java.util.Locale

class CurrentRegionUseCase {
    fun getCurrentRegion(): String {
        return Locale.getDefault().country
    }
}
