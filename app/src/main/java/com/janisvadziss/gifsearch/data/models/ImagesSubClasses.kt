package com.janisvadziss.gifsearch.data.models

import com.google.gson.annotations.SerializedName

data class Original(
    val height: String,
    val width: String,
    val size: String,
    val url: String,
    @SerializedName("mp4_size")
    val mp4Size: String,
    val mp4: String,
    @SerializedName("webp_size")
    val webpSize: String,
    val webp: String,
    val frames: String,
    val hash: String
)


data class FixedHeight(
    val height: String,
    val width: String,
    val size: String,
    val url: String,
    @SerializedName("mp4_size")
    val mp4Size: String,
    val mp4: String,
    @SerializedName("webp_size")
    val webpSize: String,
    val webp: String
)

data class FixedHeightDownsampled(
    val height: String,
    val width: String,
    val size: String,
    val url: String,
    @SerializedName("webp_size")
    val webpSize: String,
    val webp: String
)

data class FixedHeightSmall(
    val height: String,
    val width: String,
    val size: String,
    val url: String,
    @SerializedName("mp4_size")
    val mp4Size: String,
    val mp4: String,
    @SerializedName("webp_size")
    val webpSize: String,
    val webp: String
)

data class FixedWidth(
    val height: String,
    val width: String,
    val size: String,
    val url: String,
    @SerializedName("mp4_size")
    val mp4Size: String,
    val mp4: String,
    @SerializedName("webp_size")
    val webpSize: String,
    val webp: String
)

data class FixedWidthDownsampled(
    val height: String,
    val width: String,
    val size: String,
    val url: String,
    @SerializedName("webp_size")
    val webpSize: String,
    val webp: String
)

data class FixedWidthSmall(
    val height: String,
    val width: String,
    val size: String,
    val url: String,
    @SerializedName("mp4_size")
    val mp4Size: String,
    val mp4: String,
    @SerializedName("webp_size")
    val webpSize: String,
    val webp: String
)