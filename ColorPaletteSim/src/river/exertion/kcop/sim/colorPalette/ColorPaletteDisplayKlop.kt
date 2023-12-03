package river.exertion.kcop.sim.colorPalette

import river.exertion.kcop.base.Id
import river.exertion.kcop.sim.colorPalette.view.ColorPaletteInputProcessor
import river.exertion.kcop.sim.colorPalette.view.ColorPaletteLayoutHandler
import river.exertion.kcop.view.klop.IDisplayViewKlop

object ColorPaletteDisplayKlop : IDisplayViewKlop {

    override var id = Id.randomId()

    override var tag = "colorPalette"

    override fun load() { }

    override fun unload() { hideView() }

    override fun inputProcessor() = ColorPaletteInputProcessor

    override fun displayViewLayoutHandler() = ColorPaletteLayoutHandler

    override fun dispose() { }
}