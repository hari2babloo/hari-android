package io.scal.ambi.extensions.view

import android.content.Context
import android.content.res.XmlResourceParser
import android.support.annotation.IntRange
import android.support.annotation.XmlRes
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

/**
 * Created by chandra on 20-07-2018.
 */

class ResourceParser{

    var parser: XmlResourceParser? = null
    var context: Context? = null
    private val tabs: MutableList<TabResourceItem>? = mutableListOf<TabResourceItem>()
    private val TAB_TAG = "tab"
    private val RESOURCE_NOT_FOUND = 0

    constructor(context: Context, @XmlRes tabsXmlResId: Int){
        this.context = context
        this.parser = context.getResources().getXml(tabsXmlResId);
    }


    fun parseTabs(): MutableList<TabResourceItem>? {
        try {
            var eventType: Int
            do {
                eventType = parser!!.next()
                if (eventType == XmlResourceParser.START_TAG && TAB_TAG == parser!!.getName()) {
                    val bottomBarTab = parseNewTab(parser!!, tabs!!.size)
                    tabs.add(bottomBarTab)
                }
            } while (eventType != XmlResourceParser.END_DOCUMENT)
        } catch (e: IOException) {
            e.printStackTrace()
            throw Error("Resource parsing failed")
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
            throw Error("Resource parsing failed")
        }
        return tabs
    }


    private fun parseNewTab(parser: XmlResourceParser, @IntRange(from = 0) containerPosition: Int): TabResourceItem {
        val workingTab = TabResourceItem()
        val numberOfAttributes = parser.attributeCount
        for (i in 0 until numberOfAttributes) {
            val attrName = parser.getAttributeName(i)
            when (attrName) {
                "id" -> workingTab.itemid = parser.getIdAttributeResourceValue(i)
                "icon" -> workingTab.drawableId = (parser.getAttributeResourceValue(i, RESOURCE_NOT_FOUND))
                "title" -> workingTab.label = (getTitleValue(parser, i))
            }
        }
        return workingTab
    }

    private fun getTitleValue(parser: XmlResourceParser, @IntRange(from = 0) attrIndex: Int): String {
        val titleResource = parser.getAttributeResourceValue(attrIndex, 0)
        return if (titleResource == RESOURCE_NOT_FOUND)
            parser.getAttributeValue(attrIndex)
        else
            context!!.getString(titleResource)
    }

}