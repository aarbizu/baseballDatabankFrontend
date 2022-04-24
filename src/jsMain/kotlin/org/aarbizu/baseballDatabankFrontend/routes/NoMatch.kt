package org.aarbizu.baseballDatabankFrontend.routes

import mui.material.Box
import mui.material.Typography
import mui.material.styles.TypographyVariant
import react.VFC

val NoMatch = VFC {
    Box {
        Typography {
            variant = TypographyVariant.h3
            +"Nothing to see here"
        }
    }
}
