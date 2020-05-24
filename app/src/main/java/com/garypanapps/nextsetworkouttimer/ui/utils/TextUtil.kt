package com.garypanapps.nextsetworkouttimer.ui.utils

import android.os.Build
import android.text.Html
import android.text.Spanned

class TextUtil {
    companion object {
        fun getColoredText(toBeColoredText: String, color: String): Spanned {
            val finalText: Spanned
            var colorText = ""

            if (color == "red") {
                colorText = "<font color='#EE0000'>$toBeColoredText</font>"
            } else if (color == "green") {
                colorText = "<font color='#18a033'>$toBeColoredText</font>"
            }

            val html = "<b></b>"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                finalText = Html.fromHtml(html + colorText, Html.FROM_HTML_MODE_LEGACY)
            } else{
                finalText = Html.fromHtml(html + colorText)
            }
            return finalText
        }
    }
}