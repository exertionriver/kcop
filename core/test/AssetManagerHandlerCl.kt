import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import kotlinx.serialization.json.Json
import AssetManagerMessageHandler.messageHandler
import river.exertion.kcop.system.ecs.component.NarrativeComponent
import river.exertion.kcop.system.ecs.component.ProfileComponent
import river.exertion.kcop.system.messaging.Switchboard

class AssetManagerHandlerCl : Telegraph {

    val assets = AssetManager()
    val lfhr = LocalFileHandleResolver()
    val ifhr = InternalFileHandleResolver()

    var profileAssets = ProfileAssets()
    var narrativeAssets = NarrativeAssets()
    var narrativeImmersionAssets = NarrativeImmersionAssets()

    var selectedProfileAsset : ProfileAsset? = null
    var selectedNarrativeAsset : NarrativeAsset? = null
    var selectedImmersionAsset : NarrativeImmersionAsset? = null

    var currentProfileComponent : ProfileComponent? = null
    var currentImmersionComponent : NarrativeComponent? = null

    init {
        assets.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(ifhr))
        assets.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(ifhr))
        FreeTypeFontAssets.values().forEach { assets.load(it) }
        TextureAssets.values().forEach { assets.load(it) }
        SkinAssets.values().forEach { assets.load(it) }
        SoundAssets.values().forEach { assets.load(it) }

        assets.setLoader(DisplayViewLayoutAsset::class.java, DisplayViewLayoutAssetLoader(lfhr))
        DisplayViewLayoutAssets.values().forEach { assets.load(it) }

        assets.finishLoading()

        assets.setLoader(NarrativeAsset::class.java, NarrativeAssetLoader(lfhr))
        assets.setLoader(NarrativeImmersionAsset::class.java, NarrativeImmersionAssetLoader(lfhr))

        reloadProfileAssets()
        reloadNarrativeAssets()
        reloadNarrativeImmersionAssets()

        MessageChannelEnum.AMH_LOAD_BRIDGE.enableReceive(this)
        MessageChannelEnum.AMH_SAVE_BRIDGE.enableReceive(this)
    }


    fun reloadNarrativeAssets() {
        narrativeAssets.values = reloadAssets<NarrativeAsset>(NarrativeAssets.narrativeAssetLocation).toMutableList()
    }

    fun reloadNarrativeImmersionAssets() {
        narrativeImmersionAssets.values = reloadAssets<NarrativeImmersionAsset>(NarrativeImmersionAssets.narrativeImmersionAssetLocation).toMutableList()
    }

    fun loadedProfileAssetTitles() : List<String> {
        return if (profileAssets.values.isNotEmpty()) profileAssets.values.map { it.assetTitle() } else listOf("<no profiles found>")
    }

    fun loadedNarrativeAssetTitles() : List<String> {
        return if (narrativeAssets.values.isNotEmpty()) narrativeAssets.values.map { it.assetTitle() } else listOf("<no narratives found>")
    }

    fun initSelectedProfile() {
        if (ProfileAsset.isValid(selectedProfileAsset)) {

            //init selected profile
            ProfileComponent.ecsInit(selectedProfileAsset!!.profile!!)

            //reload in prep to init
            reloadNarrativeAssets()
            selectedNarrativeAsset = narrativeAssets.byId(selectedProfileAsset!!.profile!!.currentImmersionId)

            //init current narrative immersion
            initSelectedNarrative()
        } else {
            Switchboard.noloadProfile()
        }

    }

    fun initSelectedNarrative() {
        if (NarrativeAsset.isValid(selectedNarrativeAsset) ) {

            if (ProfileAsset.isValid(selectedProfileAsset)) {
                //reload in prep to init
                reloadNarrativeImmersionAssets()
                selectedImmersionAsset = narrativeImmersionAssets.byIds(selectedProfileAsset!!.assetId(), selectedNarrativeAsset!!.assetId())
            } else {
                selectedProfileAsset = ProfileAsset.new(currentProfileComponent!!, currentImmersionComponent)
            }

            //init selected narrative
            NarrativeComponent.ecsInit(selectedProfileAsset!!.profile!!, selectedNarrativeAsset!!.narrative!!, selectedImmersionAsset?.narrativeImmersion)
        } else {
            Switchboard.noloadNarrative()
        }
    }

    override fun handleMessage(msg: Telegram?): Boolean = this.messageHandler(msg)

    companion object {
        val json = Json { ignoreUnknownKeys = true; encodeDefaults = true; isLenient = true }

        const val NoProfileLoaded = "No Profile Loaded"
        const val NoNarrativeLoaded = "No Narrative Loaded"
    }

    fun dispose() {
        assets.dispose()
    }
}