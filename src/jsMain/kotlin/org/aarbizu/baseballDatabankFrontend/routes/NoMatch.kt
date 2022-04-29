package org.aarbizu.baseballDatabankFrontend.routes

import csstype.Auto
import csstype.pct
import mui.material.Box
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.VFC

val NoMatch = VFC {
    Box {
        sx {
            width = 90.pct
            margin = Auto.auto
        }
        Typography {
            variant = TypographyVariant.h6
            +"Nothing to see here"
        }
    }
}
