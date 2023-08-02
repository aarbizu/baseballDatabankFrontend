package org.aarbizu.baseballDatabankFrontend.db

interface CsvLoader {
    fun load(db: DBProvider, csvFile: DataLoader.CsvFile)
}
