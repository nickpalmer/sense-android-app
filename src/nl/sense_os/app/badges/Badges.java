/*
 * Copyright (c) 2008-2012 Vrije Universiteit, The Netherlands All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Vrije Universiteit nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package nl.sense_os.app.badges;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.sense_os.app.appwidget.TagWidgetRegistry;
import nl.sense_os.app.tags.Tags.TagId;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class handles the Badge reward system. It manages registration of available badges from the
 * various widget classes and manages the database of badges.
 *
 * @author Nick Palmer &gt;nick@sluggardy.net&lt;
 *
 */
public final class Badges {

	/**
	 * Interface for providers of badges.
	 */
	public static interface BadgeProvider {
		HashMap<BadgeId, BadgeInfo> getBadges();
	}

	// No construction
	private Badges() {

	}

	private static final DateFormat parser = new SimpleDateFormat("yyyy-M-d hh:mm:ss");
	private static final DateFormat formatter =
			DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

	/**
	 * The map from a badge to the info for that badge
	 */
	private static HashMap<BadgeId, BadgeInfo>sBadges = new HashMap<BadgeId,BadgeInfo>();

	public static final class BadgeInfo implements Parcelable {
		/**
		 *  The string resource for the name of this badge
		 */
		public final int name;
		/**
		 *  The drawable resource for the icon for this badge
		 */
		public final int drawable;
		/**
		 * The timestamp this badge was earned if any
		 */
		public String timestamp;

		/**
		 * Construct the information for a badge.
		 * @param name the id for the string resource with the name of the badge
		 * @param drawable the id for the drawable resource for the badge
		 */
		public BadgeInfo(int name, int drawable) {
			this.name = name;
			this.drawable = drawable;
		}

		/**
		 * Keys are i for image resource and t for string.
		 * @return a map for use in a list adapter
		 */
		public Map<String, Object> asMap(Context context) {
			HashMap<String, Object> badgeMap = new HashMap<String, Object>();
			badgeMap.put("i", drawable);
			badgeMap.put("t", context.getString(name));
			try {
				if (timestamp != null) {
					Date d = parser.parse(timestamp);
					badgeMap.put("ts", formatter.format(d));
				} else {
					badgeMap.put("ts", timestamp);
				}

			} catch (ParseException e) {
				badgeMap.put("ts", timestamp);
			}
			return badgeMap;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(name);
			dest.writeInt(drawable);
			dest.writeString(timestamp);
		}

		public static final Parcelable.Creator<BadgeInfo> CREATOR
		= new Parcelable.Creator<BadgeInfo>() {
			public BadgeInfo createFromParcel(Parcel in) {
				return new BadgeInfo(in);
			}

			public BadgeInfo[] newArray(int size) {
				return new BadgeInfo[size];
			}
		};

		private BadgeInfo(Parcel in) {
			name = in.readInt();
			drawable = in.readInt();
			timestamp = in.readString();
		}

	}

	/**
	 * Represents the identification for a badge.
	 * @author Nick Palmer &gt;nick@sluggardy.net&lt;
	 *
	 */
	public static final class BadgeId {
		/**
		 * The type for this badge.
		 */
		public final String type;
		/**
		 *  The if of the tag this badge is associated with or -1 if just a count badge
		 */
		public final int tag;
		/**
		 *  The count of this tag required for this badge
		 */
		public final int count;

		/**
		 * Construct a BadgeId.
		 * @param type the type for this badge or null if just a count badge.
		 * @param tag the tag for this badge or -1 if just a count or type/count badge
		 * @param count the count required for this badge
		 */
		public BadgeId(String type, int tag, int count) {
			this.type = type;
			this.tag = tag;
			this.count = count;
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof BadgeId) {
				BadgeId b = (BadgeId) other;
				if ((b.type == null && type == null)
						|| (type != null && type.equals(b.type))) {
					return b.tag == tag && b.count == count;
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return (17 * (type == null ? 0 : type.hashCode()))
					+ (53 * tag)
					+ (101 * count);

		}

		@Override
		public String toString() {
			return "Badge: " + type + ":" + tag + ":" + count;
		}
	}

	/**
	 * @param id the id of the badge to get information on
	 * @return the information for the requested badge
	 */
	public static synchronized BadgeInfo getBadgeInfo(BadgeId id) {
		return sBadges.get(id);
	}

	/**
	 * Returns the badges earned for a particular tag addition.
	 * @param tag the tag being made
	 * @return context the context we are working in
	 * @return a list of badges earned for this tag or null if no badges were earned.
	 */
	public static synchronized List<BadgeInfo> getEarnedBadges(Context context, TagId tag) {
		// Store off that we added this new tag and get IDs for possible new badges.
		BadgeId[] badges = BadgeDB.updateBadgeCounts(context, tag);

		ArrayList<BadgeInfo> list = null;

		for (BadgeId badgeId : badges) {
			if (sBadges.containsKey(badgeId)) {
				if (list  == null) {
					list = new ArrayList<BadgeInfo>();
				}
				BadgeInfo badge = sBadges.get(badgeId);
				list.add(badge);
				BadgeDB.earnedBadge(context, badgeId);
			}
		}

		return list;
	}

	/**
	 * Adds the badges available from the given badge provider.
	 * @param config the provider with the badges, typically a tag widget configuration.
	 */
	public static void addBadgeProvider(BadgeProvider config) {
		sBadges.putAll(config.getBadges());
	}

	/* Make sure the widgets are registered. This should come last. */
	static {
		TagWidgetRegistry.registerWidgets();
	}
}
