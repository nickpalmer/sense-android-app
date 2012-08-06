package nl.sense_os.app.tags;

import interdroid.util.view.CalendarClickListener;
import interdroid.util.view.CalendarView;
import interdroid.util.view.LayoutUtil.LayoutParameters;
import nl.vu.lifetag.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

public class TagsCalendar extends Activity {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.tag_calendar);
		LinearLayout layout = (LinearLayout) this.findViewById(R.id.calendar_layout);
		CalendarView calendar = new CalendarView(this);
		LayoutParameters.setLinearLayoutParams(LayoutParameters.W_FILL_H_FILL, 1, calendar);
		calendar.setOnCalendarClickListener(new CalendarClickListener() {

			@Override
			public void onCalendarClicked(int day, int month, int year) {
				startDayActivity(day, month, year);
			}

		});
		layout.addView(calendar);
	}

	/**
	 * Start the day view activity.
	 * 
	 * @param day
	 *            the day to view
	 * @param month
	 *            the month to view
	 * @param year
	 *            the year to view
	 */
	private void startDayActivity(final int day, final int month, final int year) {

		Intent dayIntent = new Intent(this, TagsActivity.class);
		dayIntent.putExtra(TagsActivity.EXTRA_DATE_YEAR, year);
		dayIntent.putExtra(TagsActivity.EXTRA_DATE_MONTH, month + 1);
		dayIntent.putExtra(TagsActivity.EXTRA_DATE_DAY, day);
		startActivity(dayIntent);
	}

}
