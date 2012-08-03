package nl.sense_os.app.tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.sense_os.app.R;
import nl.sense_os.app.TwoLineListActivity;
import nl.sense_os.app.tags.Tags.TagInfo;

/**
 * This activity shows a list of tags made by the user.
 *
 * @author Nick Palmer &lt;nick@sluggardy.net&gt;
 *
 */
public class TagsActivity extends TwoLineListActivity {

	public TagsActivity() {
		super(R.string.label_tags, R.string.label_no_tags);
	}

	/**
	 * Return the list of tags.
	 */
	@Override
	protected List<Map<String, Object>> getDataList() {
		List<TagInfo> tags = TagDB.getAllTags(this);

		List<Map<String, Object>> tagInfoList = new ArrayList<Map<String, Object>>();
		for (TagInfo tag : tags) {
			tagInfoList.add(tag.asMap(this));
		}

		return tagInfoList;
	}

}
