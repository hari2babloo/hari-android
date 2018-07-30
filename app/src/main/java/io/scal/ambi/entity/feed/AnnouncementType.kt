package io.scal.ambi.entity.feed

import com.ambi.work.R

enum class AnnouncementType(val titleId: Int, val titleSmallId: Int, val iconId: Int, val colorId: Int) {

    SAFETY(R.string.news_feed_announcement_safety,
            R.string.news_feed_announcement_safety_small,
            R.drawable.ic_announcement_safety,
            R.drawable.announcement_header_background_safety),
    GOOD_NEWS(R.string.news_feed_announcement_good_news,
            R.string.news_feed_announcement_good_news_small,
            R.drawable.ic_announcement_good_news,
            R.drawable.announcement_header_background_good_news),
    GENERAL(R.string.news_feed_announcement_general,
            R.string.news_feed_announcement_general_small,
            R.drawable.ic_announcement_general,
            R.drawable.announcement_header_background_general),
    TRAGEDY(R.string.news_feed_announcement_tragedy,
            R.string.news_feed_announcement_tragedy_small,
            R.drawable.ic_announcement_tragedy,
            R.drawable.announcement_header_background_tragedy),
    EVENT(R.string.news_feed_announcement_event,
            R.string.news_feed_announcement_event_small,
            R.drawable.ic_announcement_event,
            R.drawable.announcement_header_background_event),
    NONE(R.string.news_feed_announcement_event,
            R.string.news_feed_announcement_event_small,
            R.drawable.ic_announcement_event,
            R.drawable.announcement_header_background_event)
}