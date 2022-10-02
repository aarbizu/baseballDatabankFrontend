package org.aarbizu.baseballDatabankFrontend

fun isSuperVocalic(input: String): Boolean {
    var aCount = 0
    var eCount = 0
    var iCount = 0
    var oCount = 0
    var uCount = 0
    input.forEach {
        when (it) {
            'A',
            'a' -> {
                aCount += 1
            }
            'E',
            'e' -> {
                eCount += 1
            }
            'I',
            'i' -> {
                iCount += 1
            }
            'O',
            'o' -> {
                oCount += 1
            }
            'U',
            'u' -> {
                uCount += 1
            }
        }
    }
    return aCount == 1 && eCount == 1 && iCount == 1 && oCount == 1 && uCount == 1
}
