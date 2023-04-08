package river.exertion.kcop.simulation

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.KtxScreen
import river.exertion.kcop.assets.*
import river.exertion.kcop.assets.NarrativeAsset.Companion.get
import river.exertion.kcop.simulation.view.ViewLayout
import river.exertion.kcop.system.ecs.EngineHandler
import river.exertion.kcop.system.ecs.component.NarrativeComponent
import river.exertion.kcop.system.ecs.component.NarrativeComponentNavStatusHandler.inactivate
import river.exertion.kcop.system.ecs.entity.ProfileEntity
import river.exertion.kcop.system.messaging.MessageChannel
import river.exertion.kcop.system.messaging.messages.DisplayViewTextureMessage
import river.exertion.kcop.system.messaging.messages.DisplayViewTextureMessageType
import river.exertion.kcop.system.view.ViewInputProcessor


class NarrativeSimulator(private val stage: Stage,
                         private val engineHandler: EngineHandler,
                         private val assetManagerHandler: AssetManagerHandler,
                         private val orthoCamera: OrthographicCamera) : KtxScreen {

    val viewLayout = ViewLayout(orthoCamera.viewportWidth, orthoCamera.viewportHeight)

    val profileEntity = engineHandler.instantiateEntity(ProfileEntity::class.java)

    var narrativesIdx = 0
    lateinit var narrativesBlock : NarrativeAssets

    @Suppress("NewApi")
    override fun render(delta: Float) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        stage.act(Gdx.graphics.deltaTime)
        stage.draw()

        engineHandler.engine.update(delta)

        when {
            Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> {
                val prevNarrativesIdx = narrativesIdx
                narrativesIdx = (narrativesIdx - 1).coerceAtLeast(0)
                if (prevNarrativesIdx != narrativesIdx) {
                    NarrativeComponent.getFor(profileEntity)!!.inactivate()
                    narrativesBlock.values[narrativesIdx].initNarrative()
                }
            }
            Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> {
                val prevNarrativesIdx = narrativesIdx
                narrativesIdx = (narrativesIdx + 1).coerceAtMost(narrativesBlock.values.size - 1)
                if (prevNarrativesIdx != narrativesIdx) {
                    NarrativeComponent.getFor(profileEntity)!!.inactivate()
                    narrativesBlock.values[narrativesIdx].initNarrative()
                }
            }
        }
    }

    override fun hide() {
    }

    override fun show() {
        val multiplexer = InputMultiplexer()
        multiplexer.addProcessor(ViewInputProcessor())
        multiplexer.addProcessor(stage)
        Gdx.input.inputProcessor = multiplexer

        viewLayout.build(stage, assetManagerHandler)

        narrativesBlock = assetManagerHandler.narrativeAssets
        narrativesBlock.values[narrativesIdx].initNarrative()
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
        orthoCamera.viewportWidth = width.toFloat()
        orthoCamera.viewportHeight = height.toFloat()
        stage.viewport.update(width, height)
    }

    override fun dispose() {
        assetManagerHandler.dispose()
        viewLayout.dispose()
    }
}