package com.example.firebasesample.data.models

data class OverviewDataList(
    val data: List<OverviewData>
) {
    constructor() : this (
        data = listOf()
            )
}

data class OverviewData(
    val type: String,
    val title: String,
    val ranking: String,
    val season: String,
    val year: String,
    val order: Int
) {
    constructor() : this (
        type = "",
        title = "",
        ranking = "",
        season = "",
        year = "",
        order = 0
            )
}