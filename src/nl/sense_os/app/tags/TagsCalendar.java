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
