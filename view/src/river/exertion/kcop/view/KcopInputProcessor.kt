package river.exertion.kcop.view

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import river.exertion.kcop.view.layout.InputView
import river.exertion.kcop.view.layout.MenuView

object KcopInputProcessor : InputProcessor {

    override fun keyDown(keycode: Int): Boolean {
        InputView.keyEvent(Input.Keys.toString(keycode))

        if (keycode == Input.Keys.ESCAPE) MenuView.closeMenu()

        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        InputView.releaseEvent()
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        InputView.keyEvent(character.toString())
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        InputView.touchEvent(screenX, screenY, button)
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        InputView.releaseEvent()
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        InputView.touchEvent(screenX, screenY, -1)
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }
}