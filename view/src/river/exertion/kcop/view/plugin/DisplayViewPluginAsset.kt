package river.exertion.kcop.view.plugin

import river.exertion.kcop.asset.AssetStatus
import river.exertion.kcop.asset.IAsset
import river.exertion.kcop.view.klop.IDisplayViewKlop

class DisplayViewPluginAsset(var packageClass : Class<IDisplayViewKlop>? = null) : IAsset {

    override fun assetData() : Any? = packageClass?.kotlin?.objectInstance
    fun assetDataTyped() : IDisplayViewKlop = assetData() as IDisplayViewKlop

    override fun assetId() : String = packageClass?.name ?: ""
    override fun assetName() : String = (assetData() as IDisplayViewKlop?)?.tag ?: ""

    override fun assetPath() : String = newAssetFilename()
    override fun assetTitle() = assetPath()

    override var assetStatus : AssetStatus? = null

    override var persisted = false

    override fun newAssetFilename(): String = DisplayViewPluginAssets.iAssetPath(super.newAssetFilename())

    override fun assetInfo() : List<String> {

        val returnList = mutableListOf<String>()

        returnList.add("path: ${assetPath()}")
        returnList.addAll(listOf(assetId()))

        return returnList.toList()
    }
}