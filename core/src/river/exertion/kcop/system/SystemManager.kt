package river.exertion.kcop.system

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import river.exertion.kcop.system.system.PositionSystem
import river.exertion.kcop.system.system.TimeSystem

object SystemManager {

    fun init(pooledEngine: PooledEngine) {
        pooledEngine.addSystem(TimeSystem())
        pooledEngine.addSystem(PositionSystem())
    }

    fun logDebug(tag : String, message : String) {
        if (Gdx.app != null)
            Gdx.app.debug(tag, message)
        else
            println("$tag: $message")
    }
}