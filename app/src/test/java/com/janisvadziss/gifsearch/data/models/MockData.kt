package com.janisvadziss.gifsearch.data.models

object MockData {

    private fun createMockMeta(
        status: Int = 200,
        msg: String = "OK",
        responseId: String = "mockResponseId"
    ): Meta {
        return Meta(
            status = status,
            msg = msg,
            responseId = responseId
        )
    }

    private fun createMockPagination(
        totalCount: Int = 100,
        count: Int = 25,
        offset: Int = 0
    ): Pagination {
        return Pagination(
            totalCount = totalCount,
            count = count,
            offset = offset
        )
    }

    fun createMockSingleGiphyResponse(
        data: GiphyData = createMockGiphyData(),
        meta: Meta = createMockMeta(),
        pagination: Pagination = createMockPagination()
    ): SingleGiphyResponse {
        return SingleGiphyResponse(
            data = data,
            meta = meta,
            pagination = pagination
        )
    }

    fun createMockGiphyListResponse(
        data: List<GiphyData> = listOf(),
        meta: Meta = createMockMeta(),
        pagination: Pagination = createMockPagination()
    ): GiphyListResponse {
        return GiphyListResponse(
            data = data,
            meta = meta,
            pagination = pagination
        )
    }

    private fun createMockGiphyData(
        type: String = "gif",
        id: String = "mockId",
        url: String = "https://giphy.com/gifs/mockId",
        slug: String = "mock-slug",
        bitlyGifUrl: String = "https://gph.is/mockId",
        bitlyUrl: String = "https://gph.is/mockId",
        embedUrl: String = "https://giphy.com/embed/mockId",
        username: String = "mockUsername",
        source: String = "mockSource",
        title: String = "Mock Title",
        rating: String = "g",
        contentUrl: String = "",
        sourceTld: String = "mocksourcetld.com",
        sourcePostUrl: String = "https://mocksourcetld.com/mock-post",
        isSticker: Int = 0,
        importDatetime: String = "2023-10-27 10:00:00",
        trendingDatetime: String = "2023-10-27 12:00:00",
        images: Images = createMockImages(),
        user: User? = null,
        analyticsResponsePayload: String = "mockAnalyticsResponsePayload",
        analytics: Analytics = createMockAnalytics(),
        altText: String = "Mock Alt Text"
    ): GiphyData {
        return GiphyData(
            type = type,
            id = id,
            url = url,
            slug = slug,
            bitlyGifUrl = bitlyGifUrl,
            bitlyUrl = bitlyUrl,
            embedUrl = embedUrl,
            username = username,
            source = source,
            title = title,
            rating = rating,
            contentUrl = contentUrl,
            sourceTld = sourceTld,
            sourcePostUrl = sourcePostUrl,
            isSticker = isSticker,
            importDatetime = importDatetime,
            trendingDatetime = trendingDatetime,
            images = images,
            user = user,
            analyticsResponsePayload = analyticsResponsePayload,
            analytics = analytics,
            altText = altText
        )
    }

    private fun createMockImages(
        original: Original = createMockOriginal(),
        fixedHeight: FixedHeight = createMockFixedHeight(),
        fixedHeightDownsampled: FixedHeightDownsampled = createMockFixedHeightDownsampled(),
        fixedHeightSmall: FixedHeightSmall = createMockFixedHeightSmall(),
        fixedWidth: FixedWidth = createMockFixedWidth(),
        fixedWidthDownsampled: FixedWidthDownsampled = createMockFixedWidthDownsampled(),
        fixedWidthSmall: FixedWidthSmall = createMockFixedWidthSmall()
    ): Images {
        return Images(
            original = original,
            fixedHeight = fixedHeight,
            fixedHeightDownsampled = fixedHeightDownsampled,
            fixedHeightSmall = fixedHeightSmall,
            fixedWidth = fixedWidth,
            fixedWidthDownsampled = fixedWidthDownsampled,
            fixedWidthSmall = fixedWidthSmall
        )
    }

    private fun createMockOriginal(
        height: String = "480",
        width: String = "640",
        size: String = "1000",
        url: String = "https://media.giphy.com/media/mockId/giphy.gif",
        mp4Size: String = "500",
        mp4: String = "https://media.giphy.com/media/mockId/giphy.mp4",
        webpSize: String = "250",
        webp: String = "https://media.giphy.com/media/mockId/giphy.webp",
        frames: String = "10",
        hash: String = "mockHash"
    ): Original {
        return Original(
            height = height,
            width = width,
            size = size,
            url = url,
            mp4Size = mp4Size,
            mp4 = mp4,
            webpSize = webpSize,
            webp = webp,
            frames = frames,
            hash = hash
        )
    }

    private fun createMockFixedHeight(
        height: String = "200",
        width: String = "300",
        size: String = "400",
        url: String = "https://media.giphy.com/media/mockId/200.gif",
        mp4Size: String = "200",
        mp4: String = "https://media.giphy.com/media/mockId/200.mp4",
        webpSize: String = "100",
        webp: String = "https://media.giphy.com/media/mockId/200.webp"
    ): FixedHeight {
        return FixedHeight(
            height = height,
            width = width,
            size = size,
            url = url,
            mp4Size = mp4Size,
            mp4 = mp4,
            webpSize = webpSize,
            webp = webp
        )
    }

    private fun createMockFixedHeightDownsampled(
        height: String = "150",
        width: String = "250",
        size: String = "150",
        url: String = "https://media.giphy.com/media/mockId/150.gif",
        webpSize: String = "75",
        webp: String = "https://media.giphy.com/media/mockId/150.webp"
    ): FixedHeightDownsampled {
        return FixedHeightDownsampled(
            height = height,
            width = width,
            size = size,
            url = url,
            webpSize = webpSize,
            webp = webp
        )
    }

    private fun createMockFixedHeightSmall(
        height: String = "100",
        width: String = "150",
        size: String = "100",
        url: String = "https://media.giphy.com/media/mockId/100.gif",
        mp4Size: String = "50",
        mp4: String = "https://media.giphy.com/media/mockId/100.mp4",
        webpSize: String = "25",
        webp: String = "https://media.giphy.com/media/mockId/100.webp"
    ): FixedHeightSmall {
        return FixedHeightSmall(
            height = height,
            width = width,
            size = size,
            url = url,
            mp4Size = mp4Size,
            mp4 = mp4,
            webpSize = webpSize,
            webp = webp
        )
    }

    private fun createMockFixedWidth(
        height: String = "300",
        width: String = "200",
        size: String = "400",
        url: String = "https://media.giphy.com/media/mockId/300.gif",
        mp4Size: String = "200",
        mp4: String = "https://media.giphy.com/media/mockId/300.mp4",
        webpSize: String = "100",
        webp: String = "https://media.giphy.com/media/mockId/300.webp"
    ): FixedWidth {
        return FixedWidth(
            height = height,
            width = width,
            size = size,
            url = url,
            mp4Size = mp4Size,
            mp4 = mp4,
            webpSize = webpSize,
            webp = webp
        )
    }

    private fun createMockFixedWidthDownsampled(
        height: String = "250",
        width: String = "150",
        size: String = "150",
        url: String = "https://media.giphy.com/media/mockId/250.gif",
        webpSize: String = "75",
        webp: String = "https://media.giphy.com/media/mockId/250.webp"
    ): FixedWidthDownsampled {
        return FixedWidthDownsampled(
            height = height,
            width = width,
            size = size,
            url = url,
            webpSize = webpSize,
            webp = webp
        )
    }

    private fun createMockFixedWidthSmall(
        height: String = "150",
        width: String = "100",
        size: String = "100",
        url: String = "https://media.giphy.com/media/mockId/150.gif",
        mp4Size: String = "50",
        mp4: String = "https://media.giphy.com/media/mockId/150.mp4",
        webpSize: String = "25",
        webp: String = "https://media.giphy.com/media/mockId/150.webp"
    ): FixedWidthSmall {
        return FixedWidthSmall(
            height = height,
            width = width,
            size = size,
            url = url,
            mp4Size = mp4Size,
            mp4 = mp4,
            webpSize = webpSize,
            webp = webp
        )
    }

    private fun createMockAnalytics(
        onload: Onload = createMockOnload(),
        onclick: Onclick = createMockOnclick(),
        onsent: Onsent = createMockOnsent()
    ): Analytics {
        return Analytics(
            onload = onload,
            onclick = onclick,
            onsent = onsent
        )
    }

    private fun createMockOnload(
        url: String = "https://giphy.com/analytics/onload"
    ): Onload {
        return Onload(
            url = url
        )
    }

    private fun createMockOnclick(
        url: String = "https://giphy.com/analytics/onclick"
    ): Onclick {
        return Onclick(
            url = url
        )
    }

    private fun createMockOnsent(
        url: String = "https://giphy.com/analytics/onsent"
    ): Onsent {
        return Onsent(
            url = url
        )
    }
}