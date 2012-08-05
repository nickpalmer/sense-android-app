package nl.sense_os.app.tags;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import nl.sense_os.app.appwidget.TagWidgetRegistry;

import android.content.Context;

/**
 * This class holds information about all the tags available in the system.
 * @author Nick Palmer &lt;nick@sluggardy.net&gt;
 *
 */
public class Tags {

	/**
	 * Interface for a provider of tag information.
	 */
	public static interface TagProvider {
		HashMap<TagId, TagInfo> getTags();
	}

	/** No construction. */
	private Tags() {

	}

	private static HashMap<TagId, TagInfo> sTags = new HashMap<TagId, TagInfo>();

	/**
	 * Information about a tag.
	 */
	public static class TagInfo {
		public final int mDrawable;
		public final int mDescription;

		public String mTimestamp;


		public TagInfo(int drawable, int description) {
			mDrawable = drawable;
			mDescription = description;
		}

		public TagInfo(TagInfo info, String timestamp) {
			mDrawable = info.mDrawable;
			mDescription = info.mDescription;
			mTimestamp = timestamp;
		}

		/**
		 * Keys are i for image resource and t for string.
		 * @return a map for use in a list adapter
		 */
		public Map<String, Object> asMap(Context context) {
			HashMap<String, Object> tagMap = new HashMap<String, Object>();
			tagMap.put("i", mDrawable);
			tagMap.put("t", context.getString(mDescription));
			tagMap.put("ts", mTimestamp);
			return tagMap;
		}

		@Override
		public String toString() {
			return mDescription + ":" + mDrawable + ":" + mTimestamp;
		}
	}

	/**
	 * Identification for a given tag.
	 */
	public static class TagId implements Serializable {

		private static final long serialVersionUID = 254510334898408212L;

		public final String mSensor;
		public final int mId;
		public final String mName;

		public TagId(String sensor, String name, int id) {
			this.mSensor = sensor;
			this.mName = name;
			this.mId = id;

		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof TagId) {
				TagId b = (TagId) other;
				if (b.mSensor.equals(mSensor) && b.mName.equals(mName) && b.mId == mId) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return (17 * (mSensor == null ? 0 : mSensor.hashCode()))
					+ (53 * (mName == null ? 0 : mName.hashCode()))
					+ (101 * mId);
		}

		@Override
		public String toString() {
			return "Tag: " + mSensor + ":" + mName + ":" + mId;
		}

		/**
		 * @return a JSON string for this tag.
		 */
		public String getJson() {
			return "{id:" + mId + ", desc:'" + mName +"'}";
		}

	}

	/**
	 * Adds a tag provider to the system
	 * @param config the TagProvider to get tags from.
	 */
	public static void addTagProvider(TagProvider config) {
		sTags.putAll(config.getTags());
	}

	/**
	 *
	 * @param tagId the id of the tag being requested
	 * @return information for the requested tag
	 */
	public static TagInfo getTagInfo(TagId tagId) {
		return sTags.get(tagId);
	}

	/* Make sure the widgets are registered. This should come last. */
	static {
		TagWidgetRegistry.registerWidgets();
	}

	/**
	 * Returns a TagInfo info with the timestamp set.
	 * @param id the id for the requested info
	 * @param timestamp the timestamp to set
	 * @return a TagInfo with the timestamp set
	 */
	public static TagInfo getTagInfo(TagId id, String timestamp) {
		TagInfo info = getTagInfo(id);
		if (info != null) {
			info = new TagInfo(info, timestamp);
		}
		return info;
	}
}
