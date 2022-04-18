package org.aarbizu.baseballDatabankFrontend

import kotlinx.css.CssBuilder
import kotlinx.css.body
import kotlinx.css.borderBottom
import kotlinx.css.margin
import kotlinx.css.nav
import kotlinx.css.padding
import kotlinx.css.paddingBottom
import kotlinx.css.px
import kotlinx.css.rem

val styles = CssBuilder(allowClasses = false).apply {
    body {
        margin(10.px)
        padding(10.px)
    }

    nav {
        borderBottom = "solid 1px"
        paddingBottom = 1.rem
    }
}
