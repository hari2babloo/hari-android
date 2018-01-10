package io.scal.ambi.entity.feed

import io.scal.ambi.R

enum class AnnouncementType(val titleId: Int, val titleSmallId: Int, val iconId: Int, val colorId: Int) {

    SAFETY(R.string.news_feed_announcement_safety,
           R.string.news_feed_announcement_safety_small,
           R.drawable.ic_announcement_safety,
           R.color.news_feed_announcement_safety),
    GOOD_NEWS(R.string.news_feed_announcement_good_news,
              R.string.news_feed_announcement_good_news_small,
              R.drawable.ic_announcement_good_news,
              R.color.news_feed_announcement_good_news),
    GENERAL(R.string.news_feed_announcement_general,
            R.string.news_feed_announcement_general_small,
            R.drawable.ic_announcement_general,
            R.color.news_feed_announcement_general),
    TRAGEDY(R.string.news_feed_announcement_tragedy,
            R.string.news_feed_announcement_tragedy_small,
            R.drawable.ic_announcement_tragedy,
            R.color.news_feed_announcement_tragedy),
    EVENT(R.string.news_feed_announcement_event,
          R.string.news_feed_announcement_event_small,
          R.drawable.ic_announcement_event,
          R.color.news_feed_announcement_event);

}