package river.exertion.kcop.sim.narrative.component

import com.badlogic.gdx.ai.msg.Telegram
import river.exertion.kcop.ecs.ECSPackage.EngineComponentBridge
import river.exertion.kcop.ecs.component.ImmersionTimerComponent
import river.exertion.kcop.ecs.entity.SubjectEntity
import river.exertion.kcop.ecs.messaging.EngineComponentMessage
import river.exertion.kcop.messaging.MessageChannelHandler
import river.exertion.kcop.sim.narrative.component.NarrativeComponentNavStatusHandler.inactivate
import river.exertion.kcop.sim.narrative.component.NarrativeComponentNavStatusHandler.next
import river.exertion.kcop.sim.narrative.component.NarrativeComponentNavStatusHandler.pause
import river.exertion.kcop.sim.narrative.component.NarrativeComponentNavStatusHandler.unpause
import river.exertion.kcop.sim.narrative.messaging.NarrativeFlagsMessage
import river.exertion.kcop.sim.narrative.messaging.NarrativeFlagsMessage.Companion.NarrativeFlagsBridge
import river.exertion.kcop.sim.narrative.messaging.NarrativeMediaMessage
import river.exertion.kcop.sim.narrative.messaging.NarrativeMediaMessage.Companion.NarrativeMediaBridge
import river.exertion.kcop.sim.narrative.messaging.NarrativeMessage
import river.exertion.kcop.sim.narrative.messaging.NarrativeMessage.Companion.NarrativeBridge
import river.exertion.kcop.sim.narrative.messaging.NarrativeStatusMessage
import river.exertion.kcop.sim.narrative.messaging.NarrativeStatusMessage.Companion.NarrativeStatusBridge
import river.exertion.kcop.sim.narrative.structure.ImmersionStatus
import river.exertion.kcop.sim.narrative.view.DVLayoutHandler
import river.exertion.kcop.view.layout.AudioView
import river.exertion.kcop.view.layout.StatusView

object NarrativeComponentMessageHandler {

    @Suppress("NewApi")
    fun NarrativeComponent.messageHandler(msg: Telegram?): Boolean {
        if (msg != null) {
            if (MessageChannelHandler.isType(NarrativeBridge, msg.message) && isInitialized ) {
                val narrativeMessage: NarrativeMessage = MessageChannelHandler.receiveMessage(NarrativeBridge, msg.extraInfo)

                when (narrativeMessage.narrativeMessageType) {
                    NarrativeMessage.NarrativeMessageType.UpdateNarrativeImmersion -> {
                        if (narrativeMessage.narrativeImmersion != null) {
                            narrativeImmersion = narrativeMessage.narrativeImmersion
                        }
                    }
                    NarrativeMessage.NarrativeMessageType.ReplaceCumlTimer -> {
                        MessageChannelHandler.send(EngineComponentBridge, EngineComponentMessage(
                                EngineComponentMessage.EngineComponentMessageType.ReplaceComponent,
                                SubjectEntity.entityName, ImmersionTimerComponent::class.java, this.timerPair)
                        )
                    }
                    NarrativeMessage.NarrativeMessageType.Pause -> pause()
                    NarrativeMessage.NarrativeMessageType.Unpause -> unpause()
                    NarrativeMessage.NarrativeMessageType.Inactivate -> inactivate()
                    NarrativeMessage.NarrativeMessageType.Next -> if (narrativeMessage.promptNext != null) next(narrativeMessage.promptNext)
                }
                return true
            }
            if (MessageChannelHandler.isType(NarrativeStatusBridge, msg.message) && isInitialized ) {
                val narrativeStatusMessage: NarrativeStatusMessage = MessageChannelHandler.receiveMessage(NarrativeStatusBridge, msg.extraInfo)

                when (narrativeStatusMessage.narrativeStatusMessageType) {
                    NarrativeStatusMessage.NarrativeStatusMessageType.AddStatus -> {
                        //completion status
                        if (narrativeStatusMessage.key == null) {
                            StatusView.addOrUpdateStatus(sequentialStatusKey(), seqNarrativeProgress())
                        }
                    }
                    NarrativeStatusMessage.NarrativeStatusMessageType.RemoveStatus -> {
                        if (narrativeStatusMessage.key == null) {
                            StatusView.removeStatus(sequentialStatusKey())
                        }
                    }
                }
                return true
            }
            if (MessageChannelHandler.isType(NarrativeFlagsBridge, msg.message) && isInitialized ) {
                val narrativeFlagsMessage: NarrativeFlagsMessage = MessageChannelHandler.receiveMessage(NarrativeFlagsBridge, msg.extraInfo)

                when (narrativeFlagsMessage.narrativeFlagsMessageType) {
                    NarrativeFlagsMessage.NarrativeFlagsMessageType.SetPersistFlag -> {
                        if (narrativeImmersion!!.flags.any { it.key == narrativeFlagsMessage.key }) {
                            narrativeImmersion!!.flags.first { it.key == narrativeFlagsMessage.key }.value = narrativeFlagsMessage.value
                        } else {
                            narrativeImmersion!!.flags.add(ImmersionStatus(narrativeFlagsMessage.key, narrativeFlagsMessage.value))
                        }
                    }
                    NarrativeFlagsMessage.NarrativeFlagsMessageType.SetBlockFlag -> {
                        if (blockFlags.any { it.key == narrativeFlagsMessage.key }) {
                            blockFlags.first { it.key == narrativeFlagsMessage.key }.value = narrativeFlagsMessage.value
                        } else {
                            blockFlags.add(ImmersionStatus(narrativeFlagsMessage.key, narrativeFlagsMessage.value))
                        }
                    }
                    NarrativeFlagsMessage.NarrativeFlagsMessageType.AddToCounter -> {
                        if (narrativeImmersion!!.flags.any { it.key == narrativeFlagsMessage.key && (it.value?.toIntOrNull() is Int) }) {
                            narrativeImmersion!!.flags.first { it.key == narrativeFlagsMessage.key }.value =
                                    (narrativeImmersion!!.flags.first { it.key == narrativeFlagsMessage.key }.value!!.toInt() + (narrativeFlagsMessage.value?.toIntOrNull() ?: 0)).toString()
                        } else {
                            narrativeImmersion!!.flags.add(ImmersionStatus(narrativeFlagsMessage.key, narrativeFlagsMessage.value))
                        }
                    }
                    NarrativeFlagsMessage.NarrativeFlagsMessageType.UnsetPersistFlag -> {
                        narrativeImmersion!!.flags.removeIf { it.key == narrativeFlagsMessage.key }
                    }
                }
                return true
            }
            if (MessageChannelHandler.isType(NarrativeMediaBridge, msg.message) && isInitialized ) {
                val narrativeMediaMessage: NarrativeMediaMessage = MessageChannelHandler.receiveMessage(NarrativeMediaBridge, msg.extraInfo)

                when (narrativeMediaMessage.narrativeMediaMessageType) {
                    NarrativeMediaMessage.NarrativeMediaMessageType.PlaySound -> {
                        if ( narrative!!.sounds.keys.contains(narrativeMediaMessage.assetFilename) ) {
                            AudioView.playSound(narrative!!.sounds[narrativeMediaMessage.assetFilename]!!.asset)
                        }
                    }
                    NarrativeMediaMessage.NarrativeMediaMessageType.PlayMusic -> {
                        if ( narrative!!.music.keys.contains(narrativeMediaMessage.assetFilename) ) {
                            AudioView.playMusic(narrative!!.music[narrativeMediaMessage.assetFilename]!!.asset)
                        }
                    }
                    NarrativeMediaMessage.NarrativeMediaMessageType.StopMusic -> {
                        if ( narrative!!.music.keys.contains(narrativeMediaMessage.assetFilename) ) {
                            AudioView.stopMusic()
                        }
                    }
                    NarrativeMediaMessage.NarrativeMediaMessageType.FadeInMusic -> {
                        if ( narrative!!.music.keys.contains(narrativeMediaMessage.assetFilename) ) {
                            AudioView.fadeInMusic(narrative!!.music[narrativeMediaMessage.assetFilename]!!.asset)
                        }
                    }
                    NarrativeMediaMessage.NarrativeMediaMessageType.FadeOutMusic -> {
                        if ( narrative!!.music.keys.contains(narrativeMediaMessage.assetFilename) ) {
                            AudioView.fadeOutMusic()
                        }
                    }
                    NarrativeMediaMessage.NarrativeMediaMessageType.CrossFadeMusic -> {
                        if ( narrative!!.music.keys.contains(narrativeMediaMessage.assetFilename) ) {
                            AudioView.crossFadeMusic(narrative!!.music[narrativeMediaMessage.assetFilename]!!.asset)
                        }
                    }
                    NarrativeMediaMessage.NarrativeMediaMessageType.ShowImage -> {
                        if ( narrative!!.textures.keys.contains(narrativeMediaMessage.assetFilename) ) {
                            DVLayoutHandler.setImagePaneContent(narrativeMediaMessage.imageLayoutPaneIdx?.toInt()!!, narrative!!.textures[narrativeMediaMessage.assetFilename]!!.asset)
                        }
                    }
                    NarrativeMediaMessage.NarrativeMediaMessageType.HideImage -> {
                        if ( narrative!!.textures.keys.contains(narrativeMediaMessage.assetFilename) ) {
                            DVLayoutHandler.setImagePaneContent(narrativeMediaMessage.imageLayoutPaneIdx?.toInt()!!, null)
                        }
                    }
                    NarrativeMediaMessage.NarrativeMediaMessageType.FadeInImage -> {
                        if ( narrative!!.textures.keys.contains(narrativeMediaMessage.assetFilename) ) {
                            DVLayoutHandler.fadeImageIn(narrativeMediaMessage.imageLayoutPaneIdx?.toInt()!!, narrative!!.textures[narrativeMediaMessage.assetFilename]!!.asset)
                        }
                    }
                    NarrativeMediaMessage.NarrativeMediaMessageType.FadeOutImage -> {
                        if ( narrative!!.textures.keys.contains(narrativeMediaMessage.assetFilename) ) {
                            DVLayoutHandler.fadeImageOut(narrativeMediaMessage.imageLayoutPaneIdx?.toInt()!!, narrative!!.textures[narrativeMediaMessage.assetFilename]!!.asset)
                        }
                    }
                    NarrativeMediaMessage.NarrativeMediaMessageType.CrossFadeImage -> {
                        if ( narrative!!.textures.keys.contains(narrativeMediaMessage.assetFilename) ) {
                            DVLayoutHandler.setImagePaneContent(narrativeMediaMessage.imageLayoutPaneIdx?.toInt()!!, narrative!!.textures[narrativeMediaMessage.assetFilename]!!.asset)
                        }
                    }
                }
                return true
            }
        }

        return false
    }
}