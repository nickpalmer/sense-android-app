package nl.sense_os.app.tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.sense_os.app.TwoLineListActivity;
import nl.sense_os.app.tags.Tags.TagInfo;
import nl.vu.lifetag.R;
import android.content.Intent;
import android.widget.TextView;

/**
 * This activity shows a list of tags made by the user.
 * 
 * @author Nick Palmer &lt;nick@sluggardy.net&gt;
 * 
 */
public class TagsActivity extends TwoLineListActivity {

	public static final String EXTRA_DATE_YEAR = "y";
	public static final String EXTRA_DATE_MONTH = "m";
	public static final String EXTRA_DATE_DAY = "d";

	private int mYear;
	private int mMonth;
	private int mDay;

	public TagsActivity() {
		super(R.string.label_tags, R.string.label_no_tags);
	}

	@Override
	public void onStart() {
		Intent launch = getIntent();
		mYear = launch.getIntExtra(EXTRA_DATE_YEAR, -1);
		mMonth = launch.getIntExtra(EXTRA_DATE_MONTH, -1);
		mDay = launch.getIntExtra(EXTRA_DATE_DAY, -1);

		if (mYear > 0) {
			TextView title = (TextView) findViewById(R.id.sense_banner);
			title.append(": " + mYear + "-" + mMonth + "-" + mDay);
		}

		super.onStart();
	}

	/**
	 * Return the list of tags.
	 */
	@Override
	protected List<Map<String, Object>> getDataList() {
		List<TagInfo> tags = TagDB.getAllTags(this, mYear, mMonth, mDay);

		List<Map<String, Object>> tagInfoList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < tags.size(); i++) {
			TagInfo tag = tags.get(i);
			tagInfoList.add(tag.asMap(this));
		}

		return tagInfoList;
	}

}
