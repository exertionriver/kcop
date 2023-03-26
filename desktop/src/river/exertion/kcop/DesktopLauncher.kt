package river.exertion.kcop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

object DesktopLauncher {

    @JvmStatic
    fun main(arg: Array<String>) {
        val config = Lwjgl3ApplicationConfiguration().apply {
            this.setTitle(Kcop.title)
            this.setWindowedMode(Kcop.initViewportWidth.toInt(), Kcop.initViewportHeight.toInt())
            this.setForegroundFPS(60)
            setBackBufferConfig(8, 8, 8, 8, 24, 0, 8)
        }

        Lwjgl3Application(Kcop(), config).logLevel = Kcop.loglevel
    }
}