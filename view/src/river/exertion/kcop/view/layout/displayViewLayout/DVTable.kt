package river.exertion.kcop.view.layout.displayViewLayout

import kotlinx.serialization.Serializable

@Serializable
data class DVTable(
    val tableIdx : String,
    override var cellType: DVLCellTypes = DVLCellTypes.TABLE,
    val colspan : String? = null,
    val panes : MutableList<DVLayoutCell> = mutableListOf()
) : DVLayoutCell() {

    fun colspan() = colspan?.toIntOrNull() ?: 1

}