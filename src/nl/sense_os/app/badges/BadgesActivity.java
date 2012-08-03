package nl.sense_os.app.badges;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.sense_os.app.R;
import nl.sense_os.app.TwoLineListActivity;
import nl.sense_os.app.badges.Badges.BadgeInfo;

/**
 * This activity shows a list of badges earned by the user.
 *
 * @author Nick Palmer &lt;nick@sluggardy.net&gt;
 *
 */
public class BadgesActivity extends TwoLineListActivity {

	public BadgesActivity() {
		super(R.string.label_badges, R.string.label_no_badges);
	}

	protected List<Map<String, Object>> getDataList() {
		List<BadgeInfo> badges = BadgeDB.getAllEarnedBadges(this);

		List<Map<String, Object>> badgeInfoList = new ArrayList<Map<String, Object>>();
		for (BadgeInfo badge : badges) {
			badgeInfoList.add(badge.asMap(this));
		}

		return badgeInfoList;
	}


}
