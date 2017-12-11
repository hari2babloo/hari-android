package io.scal.ambi.entity.feed

import io.scal.ambi.R

enum class Audience(val titleId: Int, val iconId: Int) {

    COLLEGE_UPDATE(R.string.news_feed_audience_college_update, R.drawable.ic_audience_college_update),
    STUDENTS(R.string.news_feed_audience_students, R.drawable.ic_audience_students),
    FACULTY(R.string.news_feed_audience_faculty, R.drawable.ic_audience_faculty),
    STAFF(R.string.news_feed_audience_staff, R.drawable.ic_audience_staff),
    GROUPS(R.string.news_feed_audience_groups, R.drawable.ic_audience_groups),
    CLASSES(R.string.news_feed_audience_classes, R.drawable.ic_audience_classes),
    COMMUNITIES(R.string.news_feed_audience_communities, R.drawable.ic_audience_communities),
    NEWS(R.string.news_feed_audience_news, R.drawable.ic_audience_news)
}