package river.exertion.kcop.system.messaging.messages

import river.exertion.kcop.assets.FontSize

data class TextViewMessage(val narrativeText : String, val displayText : String, val displayFontSize : FontSize, val prompts : List<String>, val param: String)