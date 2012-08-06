package nl.sense_os.app;

import java.util.List;
import java.util.Map;

import nl.vu.lifetag.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * This activity is a base class for activities showing a list of time stamped items with images.
 * 
 * @author Nick Palmer &lt;nick@sluggardy.net&gt;
 * 
 */
public abstract class TwoLineListActivity extends FragmentActivity {

	private final int mEmptyLabel;
	private final int mTitle;

	/** Construct the fragment */
	public TwoLineListActivity(int title, int emptyLabel) {
		super();
		mTitle = title;
		mEmptyLabel = emptyLabel;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_list);

		TextView title = (TextView) findViewById(R.id.sense_banner);
		title.setText(mTitle);

		ListView listView = (ListView) findViewById(R.id.items_list);
		setEmptyView(listView, mEmptyLabel);
	}

	@Override
	public void onStart() {
		ListView listView = (ListView) findViewById(R.id.items_list);
		setListAdapter(listView, getDataList());
		super.onStart();
	}

	/**
	 * The data maps should have keys "i" with a drawable id, "t" with text, and "ts" with a
	 * timestamp text.
	 * 
	 * @return the list of data.
	 */
	protected abstract List<Map<String, Object>> getDataList();

	private void setListAdapter(ListView listView, List<Map<String, Object>> data) {
		SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item_list_twoline_item,
				new String[] { "i", "t", "ts" }, new int[] { R.id.item_twoline_image,
						R.id.item_twoline_label, R.id.item_twoline_timestamp }) {

			// Disable everything in the list.
			public boolean isEnabled(int position) {
				return false;
			}
		};

		listView.setAdapter(adapter);
	}

	/**
	 * Sets up the empty view for the list.
	 * 
	 * @param listView
	 *            the list view to add the empty view to
	 * @param emptyTextId
	 *            the text to show when the list is empty
	 */
	private void setEmptyView(ListView listView, int emptyTextId) {
		TextView emptyView = new TextView(this);

		emptyView.setText(emptyTextId);
		emptyView.setGravity(Gravity.CENTER);
		emptyView.setPadding(5, 100, 5, 0);
		emptyView.setId(android.R.id.empty);
		listView.setEmptyView(emptyView);

		// Work around bug in setEmptyView which should add the view to the parent but doesn't.
		((ViewGroup) listView.getParent()).addView(emptyView);
	}
}