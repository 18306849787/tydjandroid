package com.typartybuilding.activity.quanminlangdu.fragment.dummy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 25;

//    static {
//        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createDummyItem(i));
//        }
//    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.readId, item);
    }

//    private static DummyItem createDummyItem(int position) {
//        return new DummyItem(String.valueOf(position), "Item " + position, makeDetails(position));
//    }



    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem implements Serializable {
        public final String readId;
        public final String readTitle;
        public final String readAuthor;
        public final String readNumber;
        public final String readCover;
        public final String readProfile;
        public final String readDetail;
        public final String readFrequency;
        public final String publishDate;
        public final String updateTime;
        public  int readType;
        public DummyItem(String readId, String readTitle, String readAuthor, String readNumber, String readCover, String readProfile, String readDetail,
                         String readFrequency, String publishDate, String updateTime,int readType) {
            this.readId = readId;
            this.readTitle = readTitle;
            this.readAuthor = readAuthor;
            this.readNumber = readNumber;
            this.readCover = readCover;
            this.readProfile = readProfile;
            this.readDetail = readDetail;
            this.readFrequency = readFrequency;
            this.publishDate = publishDate;
            this.updateTime = updateTime;
            this.readType =  readType;
        }

        @Override
        public String toString() {
            return this.readId;
        }
    }
}
