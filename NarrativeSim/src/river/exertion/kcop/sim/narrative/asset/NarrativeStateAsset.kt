package river.exertion.kcop.sim.narrative.asset

import river.exertion.kcop.asset.AssetManagerHandler
import river.exertion.kcop.asset.AssetStatus
import river.exertion.kcop.asset.IAsset
import river.exertion.kcop.asset.immersionTimer.ImmersionTimer
import river.exertion.kcop.profile.asset.ProfileAsset
import river.exertion.kcop.sim.narrative.asset.NarrativeAsset.Companion.isNarrativeLoaded
import river.exertion.kcop.sim.narrative.component.NarrativeComponent
import river.exertion.kcop.sim.narrative.structure.NarrativeState

class NarrativeStateAsset(var narrativeState : NarrativeState = NarrativeState()) : IAsset {

    override fun assetData() : Any = narrativeState

    override fun assetId() = narrativeState.id
    override fun assetName() = assetId()

    override fun assetPath() : String = newAssetFilename()
    override fun assetTitle() = assetPath()

    override var assetStatus : AssetStatus? = null

    override var persisted = false

    override fun newAssetFilename(): String {
        narrativeState.id = NarrativeState.genId(ProfileAsset.currentProfileAsset.assetId(), NarrativeAsset.currentNarrativeAsset.assetId())
        return NarrativeStateAssets.iAssetPath(assetId())
    }

    var cumlImmersionTimer : ImmersionTimer
        get() = narrativeState.cumlImmersionTimer
        set(value) { narrativeState.cumlImmersionTimer = value }

    var blockCumlImmersionTimers : MutableMap<String, ImmersionTimer>
        get() = narrativeState.blockCumlImmersionTimers
        set(value) { narrativeState.blockCumlImmersionTimers = value }

    override fun assetInfo() : List<String> {

        val returnList = mutableListOf<String>()

        returnList.add("cuml. time: ${narrativeState.cumlImmersionTimer.immersionTime()}")

        return returnList.toList()
    }

    override fun saveTyped(assetSaveLocation : String?) {
        persisted = AssetManagerHandler.saveAsset<NarrativeState>(this, assetSaveLocation).persisted
    }

    override fun save(assetSaveLocation : String?) {
        if (isNarrativeLoaded()) {
            //used to update serializable fields
            cumlImmersionTimer = cumlImmersionTimer
            blockCumlImmersionTimers = blockCumlImmersionTimers

            this.saveTyped(assetSaveLocation)
        }
    }

    companion object {
        var currentNarrativeStateAsset = NarrativeStateAsset()

        fun new(narrativeComponent: NarrativeComponent) : NarrativeStateAsset {
            return NarrativeStateAsset(narrativeComponent.narrativeState)
        }
    }
}