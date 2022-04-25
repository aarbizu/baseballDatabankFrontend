package org.aarbizu.baseballDatabankFrontend.db

import java.io.File

interface CsvLoader {
    fun load(db: DBProvider, csvFile: File)
}
