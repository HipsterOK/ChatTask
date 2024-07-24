package ru.porcupine.chattask.domain.usecase

import java.util.Locale

class CurrentRegionUseCase {
    fun getCurrentRegion(): String {
        return Locale.getDefault().country
    }
}
