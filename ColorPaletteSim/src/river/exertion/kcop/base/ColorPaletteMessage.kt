package river.exertion.kcop.base

data class ColorPaletteMessage(val colorPaletteMessageType: ColorPaletteMessageType, val baseColorName : String? = null, val baseCp: ColorPalette? = null, val modifyType: ColorPaletteModifyType? = null) {

    enum class ColorPaletteMessageType {
        SetBaseColor, ModifyBaseColor
    }

    enum class ColorPaletteModifyType {
        ColorBaseIncrR, ColorBaseIncrG, ColorBaseIncrB,
        ColorBaseDecrR, ColorBaseDecrG, ColorBaseDecrB,
        ColorBaseIncr, ColorBaseDecr,
        ColorSamplePrev, ColorSampleNext
    }
}