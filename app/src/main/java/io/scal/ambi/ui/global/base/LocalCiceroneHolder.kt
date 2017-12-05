package io.scal.ambi.ui.global.base

import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router
import java.util.*

class LocalCiceroneHolder {

    private val containers: HashMap<String, Cicerone<Router>> = HashMap()

    fun getCicerone(containerTag: String): Cicerone<Router> {
        if (!containers.containsKey(containerTag)) {
            containers.put(containerTag, Cicerone.create())
        }
        return containers[containerTag]!!
    }
}