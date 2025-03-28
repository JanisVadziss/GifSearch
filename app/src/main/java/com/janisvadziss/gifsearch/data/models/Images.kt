package com.janisvadziss.gifsearch.data.models

import com.google.gson.annotations.SerializedName

data class Images(
    val original: Original,
    @SerializedName("fixed_height")
    val fixedHeight: FixedHeight,
    @SerializedName("fixed_height_downsampled")
    val fixedHeightDownsampled: FixedHeightDownsampled,
    @SerializedName("fixed_height_small")
    val fixedHeightSmall: FixedHeightSmall,
    @SerializedName("fixed_width")
    val fixedWidth: FixedWidth,
    @SerializedName("fixed_width_downsampled")
    val fixedWidthDownsampled: FixedWidthDownsampled,
    @SerializedName("fixed_width_small")
    val fixedWidthSmall: FixedWidthSmall
)