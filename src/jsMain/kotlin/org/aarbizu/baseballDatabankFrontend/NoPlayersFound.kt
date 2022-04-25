package org.aarbizu.baseballDatabankFrontend

import csstype.Auto
import csstype.em
import csstype.pct
import mui.material.Box
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.VFC
import react.dom.html.ReactHTML

val NoPlayersFound =
    VFC() {
        Box {
            component = ReactHTML.div
            Typography {
                sx {
                    padding = 0.2.em
                    marginLeft = Auto.auto
                    marginRight = Auto.auto
                    width = 70.pct
                }
                variant = TypographyVariant.body1
                +"No players found"
            }
        }
    }
