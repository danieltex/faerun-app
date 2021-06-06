package com.github.danieltex.faerunapp.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class WaterPocketBatchDTO(
    @get:JsonProperty("water-pockets")
    val waterPockets: List<WaterPocketDTO>
)