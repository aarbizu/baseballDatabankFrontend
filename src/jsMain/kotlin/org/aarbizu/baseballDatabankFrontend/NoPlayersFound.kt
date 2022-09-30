package org.aarbizu.baseballDatabankFrontend

import csstype.TextAlign
import csstype.pct
import mui.material.Paper
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.VFC

val NoPlayersFound =
    VFC() {
        Paper {
            elevation = 0
            square = true
            sx {
                width = 100.pct
                textAlign = TextAlign.center
            }
            Typography {
                variant = TypographyVariant.h3
                sx { color = myAppTheme.palette.text.secondary }
                +"No results"
            }
        }
    }
