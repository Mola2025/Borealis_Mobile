package com.example.borealis_mobile.core.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlIndexParser {
    public List<String> extractContentFileUrls(InputStream inputStream) throws Exception{
        List<String> fileUrls = new ArrayList<>();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(inputStream, "UTF-8");

        int eventType = parser.getEventType();
        boolean insideFilesBlock = false;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String tagName = parser.getName();

                if (tagName.equals("files")) {
                    insideFilesBlock = true;
                } else if (insideFilesBlock && tagName.equals("file")) {
                    String fileUrl = parser.getAttributeValue(null, "url");
                    if (fileUrl != null) {
                        fileUrls.add(fileUrl);
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("files")) {
                    insideFilesBlock = false;
                }
                if (parser.getName().equals("index")) {
                    break;
                }
            }
            eventType = parser.next();
        }
        return fileUrls;
    }
}
