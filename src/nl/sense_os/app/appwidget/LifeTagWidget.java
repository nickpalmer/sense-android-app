package nl.sense_os.app.appwidget;

import java.util.HashMap;

import nl.sense_os.app.badges.Badges.BadgeId;
import nl.sense_os.app.badges.Badges.BadgeInfo;
import nl.sense_os.app.tags.Tags.TagId;
import nl.sense_os.app.tags.Tags.TagInfo;
import nl.vu.lifetag.R;

/**
 * This is a life tag widget that allows you to tag your daily activities.
 * 
 * @author Nick Palmer &lt;nick@sluggardy.net&gt;
 * 
 */
public final class LifeTagWidget {

	/** The name of this sensor. */
	private static final String sSensorName = "life_tag";

	/* The tags for this widget. */
	public static final TagId cooking = new TagId(sSensorName, "cooking", 0);
	public static final TagId eating = new TagId(sSensorName, "eating", 1);
	public static final TagId sleeping = new TagId(sSensorName, "sleeping", 2);
	public static final TagId traveling = new TagId(sSensorName, "traveling", 3);
	public static final TagId working = new TagId(sSensorName, "working", 4);
	public static final TagId shopping = new TagId(sSensorName, "shopping", 5);
	public static final TagId watching = new TagId(sSensorName, "watching", 6);
	public static final TagId showering = new TagId(sSensorName, "showering", 7);
	public static final TagId reading = new TagId(sSensorName, "reading", 8);
	public static final TagId sporting = new TagId(sSensorName, "sporting", 9);
	public static final TagId meeting = new TagId(sSensorName, "meeting", 10);
	public static final TagId socializing = new TagId(sSensorName, "socializing", 11);
	public static final TagId sex = new TagId(sSensorName, "sex", 12);
	public static final TagId unknown = new TagId(sSensorName, "unknown", 13);

	/** The array of tags for this widget. */
	public static final TagId[] sTags = { cooking, eating, sleeping, traveling, working, shopping,
			watching, showering, reading, sporting, meeting, socializing, sex, unknown };

	/** The provider class for this widget. */
	public static class Provider extends BaseTagProvider {
		public Provider() {
			super(sConfig);
		}
	}

	/** The receiver class for this widget. */
	public static class Receiver extends BaseTagReceiver {
		public Receiver() {
			super(sConfig);
		}
	}

	/** The configuration for this widget. */
	public static final TagWidgetConfiguration sConfig = new TagWidgetConfiguration(Provider.class,
			Receiver.class, sSensorName, "json", "Life Tag", "Daily Activities",
			R.string.dialog_lifetag, sTags, unknown) {

		@Override
		public HashMap<TagId, TagInfo> getTags() {
			final HashMap<TagId, TagInfo> tags = new HashMap<TagId, TagInfo>();

			tags.put(cooking, new TagInfo(R.drawable.lifetag_cooking, R.string.tag_lifetag_cooking));
			tags.put(eating, new TagInfo(R.drawable.lifetag_eating, R.string.tag_lifetag_eating));
			tags.put(sleeping, new TagInfo(R.drawable.lifetag_sleeping,
					R.string.tag_lifetag_sleeping));
			tags.put(traveling, new TagInfo(R.drawable.lifetag_traveling,
					R.string.tag_lifetag_traveling));
			tags.put(working, new TagInfo(R.drawable.lifetag_working, R.string.tag_lifetag_working));
			tags.put(shopping, new TagInfo(R.drawable.lifetag_shopping,
					R.string.tag_lifetag_shopping));
			tags.put(watching, new TagInfo(R.drawable.lifetag_watching,
					R.string.tag_lifetag_watching));
			tags.put(showering, new TagInfo(R.drawable.lifetag_showering,
					R.string.tag_lifetag_showering));
			tags.put(reading, new TagInfo(R.drawable.lifetag_reading, R.string.tag_lifetag_reading));
			tags.put(sporting, new TagInfo(R.drawable.lifetag_sporting,
					R.string.tag_lifetag_sporting));
			tags.put(meeting, new TagInfo(R.drawable.lifetag_meeting, R.string.tag_lifetag_meeting));
			tags.put(socializing, new TagInfo(R.drawable.lifetag_socializing,
					R.string.tag_lifetag_socializing));
			tags.put(sex, new TagInfo(R.drawable.lifetag_sex, R.string.tag_lifetag_sexing));
			tags.put(unknown, new TagInfo(R.drawable.lifetag_unknown, R.string.tag_lifetag_other));

			return tags;
		}

		@Override
		public HashMap<BadgeId, BadgeInfo> getBadges() {
			final HashMap<BadgeId, BadgeInfo> badges = new HashMap<BadgeId, BadgeInfo>();

			badges.put(new BadgeId(sSensorName, -1, 1), new BadgeInfo(R.string.badge_count_first,
					R.drawable.badge_tag_first));
			badges.put(new BadgeId(sSensorName, -1, 20), new BadgeInfo(R.string.badge_count_twenty,
					R.drawable.badge_tag_twenty));
			badges.put(new BadgeId(sSensorName, -1, 50), new BadgeInfo(R.string.badge_count_fifty,
					R.drawable.badge_tag_fifty));
			badges.put(new BadgeId(sSensorName, -1, 100), new BadgeInfo(
					R.string.badge_count_hundred, R.drawable.badge_tag_hundred));
			badges.put(new BadgeId(sSensorName, -1, 200), new BadgeInfo(
					R.string.badge_count_twohundred, R.drawable.badge_tag_twohundred));
			badges.put(new BadgeId(sSensorName, -1, 300), new BadgeInfo(
					R.string.badge_count_threehundred, R.drawable.badge_tag_threehundred));
			badges.put(new BadgeId(sSensorName, -1, 400), new BadgeInfo(
					R.string.badge_count_fourhundred, R.drawable.badge_tag_fourhundred));
			badges.put(new BadgeId(sSensorName, -1, 500), new BadgeInfo(
					R.string.badge_count_fivehundred, R.drawable.badge_tag_fivehundred));
			badges.put(new BadgeId(sSensorName, -1, 1000), new BadgeInfo(
					R.string.badge_count_onethousand, R.drawable.badge_tag_1k));
			badges.put(new BadgeId(sSensorName, -1, 2000), new BadgeInfo(
					R.string.badge_count_twothousand, R.drawable.badge_tag_2k));
			badges.put(new BadgeId(sSensorName, -1, 3000), new BadgeInfo(
					R.string.badge_count_threethousand, R.drawable.badge_tag_3k));
			badges.put(new BadgeId(sSensorName, -1, 4000), new BadgeInfo(
					R.string.badge_count_fourthousand, R.drawable.badge_tag_4k));
			badges.put(new BadgeId(sSensorName, -1, 5000), new BadgeInfo(
					R.string.badge_count_fivethousand, R.drawable.badge_tag_5k));
			badges.put(new BadgeId(sSensorName, -1, 6000), new BadgeInfo(
					R.string.badge_count_sixthousand, R.drawable.badge_tag_6k));
			badges.put(new BadgeId(sSensorName, -1, 7000), new BadgeInfo(
					R.string.badge_count_seventhousand, R.drawable.badge_tag_7k));
			badges.put(new BadgeId(sSensorName, -1, 8000), new BadgeInfo(
					R.string.badge_count_eightthousand, R.drawable.badge_tag_8k));
			badges.put(new BadgeId(sSensorName, -1, 9000), new BadgeInfo(
					R.string.badge_count_ninethousand, R.drawable.badge_tag_9k));
			badges.put(new BadgeId(sSensorName, -1, 10000), new BadgeInfo(
					R.string.badge_count_tenthousand, R.drawable.badge_tag_10k));

			// <!-- Cooking -->
			badges.put(new BadgeId(sSensorName, cooking.mId, 1), new BadgeInfo(
					R.string.badge_cooking_one, R.drawable.badge_cooking_one));
			badges.put(new BadgeId(sSensorName, cooking.mId, 7), new BadgeInfo(
					R.string.badge_cooking_line, R.drawable.badge_cooking_line));
			badges.put(new BadgeId(sSensorName, cooking.mId, 14), new BadgeInfo(
					R.string.badge_cooking_sous, R.drawable.badge_cooking_sous));
			badges.put(new BadgeId(sSensorName, cooking.mId, 28), new BadgeInfo(
					R.string.badge_cooking_executive, R.drawable.badge_cooking_executive));
			badges.put(new BadgeId(sSensorName, cooking.mId, 56), new BadgeInfo(
					R.string.badge_cooking_top, R.drawable.badge_cooking_top));
			badges.put(new BadgeId(sSensorName, cooking.mId, 112), new BadgeInfo(
					R.string.badge_cooking_master, R.drawable.badge_cooking_master));
			// <!-- Sleeping -->
			badges.put(new BadgeId(sSensorName, sleeping.mId, 1), new BadgeInfo(
					R.string.badge_sleeping_one, R.drawable.badge_sleeping_first));
			badges.put(new BadgeId(sSensorName, sleeping.mId, 7), new BadgeInfo(
					R.string.badge_sleeping_week, R.drawable.badge_sleeping_week));
			badges.put(new BadgeId(sSensorName, sleeping.mId, 14), new BadgeInfo(
					R.string.badge_sleeping_twoweeks, R.drawable.badge_sleeping_twoweeks));
			badges.put(new BadgeId(sSensorName, sleeping.mId, 30), new BadgeInfo(
					R.string.badge_sleeping_vanwinkle, R.drawable.badge_sleeping_vanwinkle));
			// <!-- Eating -->
			badges.put(new BadgeId(sSensorName, eating.mId, 3), new BadgeInfo(
					R.string.badge_eating_threesquare, R.drawable.badge_eating_threesquare));
			badges.put(new BadgeId(sSensorName, eating.mId, 21), new BadgeInfo(
					R.string.badge_eating_regular, R.drawable.badge_eating_regular));
			badges.put(new BadgeId(sSensorName, eating.mId, 100), new BadgeInfo(
					R.string.badge_eating_lots, R.drawable.badge_eating_alot));
			// <!-- Travel -->
			badges.put(new BadgeId(sSensorName, traveling.mId, 5), new BadgeInfo(
					R.string.badge_travel_onthemove, R.drawable.badge_travel_onthemove));
			badges.put(new BadgeId(sSensorName, traveling.mId, 10), new BadgeInfo(
					R.string.badge_travel_commuter, R.drawable.badge_travel_commuter));
			badges.put(new BadgeId(sSensorName, traveling.mId, 50), new BadgeInfo(
					R.string.badge_travel_tripper, R.drawable.badge_travel_tripper));
			// <!-- Shower -->
			badges.put(new BadgeId(sSensorName, showering.mId, 1), new BadgeInfo(
					R.string.badge_shower_first, R.drawable.badge_showering_first));
			badges.put(new BadgeId(sSensorName, showering.mId, 7), new BadgeInfo(
					R.string.badge_shower_good, R.drawable.badge_showering_good));
			badges.put(new BadgeId(sSensorName, showering.mId, 30), new BadgeInfo(
					R.string.badge_shower_soclean, R.drawable.badge_showering_soclean));
			// <!-- Shopping -->
			badges.put(new BadgeId(sSensorName, shopping.mId, 1), new BadgeInfo(
					R.string.badge_shopping_first, R.drawable.badge_shopping_first));
			badges.put(new BadgeId(sSensorName, shopping.mId, 7), new BadgeInfo(
					R.string.badge_shopping_lots, R.drawable.badge_shopping_lots));
			badges.put(new BadgeId(sSensorName, shopping.mId, 50), new BadgeInfo(
					R.string.badge_shopping_shopaholic, R.drawable.badge_shopping_shopaholic));
			// <!-- Read -->
			badges.put(new BadgeId(sSensorName, reading.mId, 1), new BadgeInfo(
					R.string.badge_read_first, R.drawable.badge_reading_first));
			badges.put(new BadgeId(sSensorName, reading.mId, 14), new BadgeInfo(
					R.string.badge_read_avidreader, R.drawable.badge_reading_avid));
			badges.put(new BadgeId(sSensorName, reading.mId, 50), new BadgeInfo(
					R.string.badge_read_bookworm, R.drawable.badge_reading_bookworm));
			// <!-- Watch -->
			badges.put(new BadgeId(sSensorName, watching.mId, 10), new BadgeInfo(
					R.string.badge_watch_viewer, R.drawable.badge_watch_viewer));
			badges.put(new BadgeId(sSensorName, watching.mId, 25), new BadgeInfo(
					R.string.badge_watch_lounger, R.drawable.badge_watch_lounger));
			badges.put(new BadgeId(sSensorName, watching.mId, 50), new BadgeInfo(
					R.string.badge_watch_couchpotato, R.drawable.badge_watch_couchpotato));
			// <!-- Work -->
			badges.put(new BadgeId(sSensorName, working.mId, 1), new BadgeInfo(
					R.string.badge_work_first, R.drawable.badge_work_first));
			badges.put(new BadgeId(sSensorName, working.mId, 14), new BadgeInfo(
					R.string.badge_work_hard, R.drawable.badge_work_hard));
			badges.put(new BadgeId(sSensorName, working.mId, 24), new BadgeInfo(
					R.string.badge_work_workaholic, R.drawable.badge_work_workaholic));
			// <!-- Sport -->
			badges.put(new BadgeId(sSensorName, sporting.mId, 1), new BadgeInfo(
					R.string.badge_sport_first, R.drawable.badge_sporting_first));
			badges.put(new BadgeId(sSensorName, sporting.mId, 6), new BadgeInfo(
					R.string.badge_sport_regular, R.drawable.badge_sporting_regular));
			badges.put(new BadgeId(sSensorName, sporting.mId, 12), new BadgeInfo(
					R.string.badge_sport_fit, R.drawable.badge_sporting_fit));
			badges.put(new BadgeId(sSensorName, sporting.mId, 24), new BadgeInfo(
					R.string.badge_sport_athelete, R.drawable.badge_sporting_athelete));
			badges.put(new BadgeId(sSensorName, sporting.mId, 48), new BadgeInfo(
					R.string.badge_sport_top, R.drawable.badge_sporting_top));
			badges.put(new BadgeId(sSensorName, sporting.mId, 100), new BadgeInfo(
					R.string.badge_sport_olympic, R.drawable.badge_sporting_olympic));
			// <!-- Meeting -->
			badges.put(new BadgeId(sSensorName, meeting.mId, 1), new BadgeInfo(
					R.string.badge_meet_first, R.drawable.badge_meet_first));
			badges.put(new BadgeId(sSensorName, meeting.mId, 7), new BadgeInfo(
					R.string.badge_meet_several, R.drawable.badge_meet_several));
			badges.put(new BadgeId(sSensorName, meeting.mId, 14), new BadgeInfo(
					R.string.badge_meet_lots, R.drawable.badge_meet_lots));
			badges.put(new BadgeId(sSensorName, meeting.mId, 28), new BadgeInfo(
					R.string.badge_meet_loads, R.drawable.badge_meet_loads));
			badges.put(new BadgeId(sSensorName, meeting.mId, 50), new BadgeInfo(
					R.string.badge_meet_toomany, R.drawable.badge_meet_toomany));
			badges.put(new BadgeId(sSensorName, meeting.mId, 100), new BadgeInfo(
					R.string.badge_meet_meetaholic, R.drawable.badge_meet_meetaholic));
			// <!-- Social -->
			badges.put(new BadgeId(sSensorName, socializing.mId, 1), new BadgeInfo(
					R.string.badge_social_friends, R.drawable.badge_social_friends));
			badges.put(new BadgeId(sSensorName, socializing.mId, 6), new BadgeInfo(
					R.string.badge_social_healthy, R.drawable.badge_social_healthy));
			badges.put(new BadgeId(sSensorName, socializing.mId, 25), new BadgeInfo(
					R.string.badge_social_butterfly, R.drawable.badge_social_butterfly));
			// <!-- Sex -->
			badges.put(new BadgeId(sSensorName, sex.mId, 1), new BadgeInfo(
					R.string.badge_sex_gotlucky, R.drawable.badge_sex_gotlucky));
			badges.put(new BadgeId(sSensorName, sex.mId, 1), new BadgeInfo(
					R.string.badge_sex_hattrick, R.drawable.badge_sex_hattrick));
			badges.put(new BadgeId(sSensorName, sex.mId, 7), new BadgeInfo(
					R.string.badge_sex_steady, R.drawable.badge_sex_steady));
			badges.put(new BadgeId(sSensorName, sex.mId, 30), new BadgeInfo(
					R.string.badge_sex_libido, R.drawable.badge_sex_libido));
			badges.put(new BadgeId(sSensorName, sex.mId, 100), new BadgeInfo(
					R.string.badge_sex_sexaholic, R.drawable.badge_sex_sexaholic));

			return badges;
		}
	};

}
