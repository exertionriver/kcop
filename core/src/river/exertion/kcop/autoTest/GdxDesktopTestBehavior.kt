import river.exertion.kcop.automation.AutoUserTest
import river.exertion.kcop.automation.AutoUserTestHandler
import river.exertion.kcop.automation.btree.behavior.ClickDisplayModeButtonBehavior
import river.exertion.kcop.view.layout.LogView

object GdxDesktopTestBehavior {

    fun testBehavior() {

        AutoUserTestHandler.currentTest = AutoUserTest()
        AutoUserTestHandler.currentTest.testBehaviors.add(ClickDisplayModeButtonBehavior())

    }
}