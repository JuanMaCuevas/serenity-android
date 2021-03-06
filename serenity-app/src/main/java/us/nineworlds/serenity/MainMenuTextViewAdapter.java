/**
 * The MIT License (MIT)
 * Copyright (c) 2012 David Carver
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package us.nineworlds.serenity;

import java.util.ArrayList;

import us.nineworlds.serenity.core.menus.MenuItem;
import us.nineworlds.serenity.core.services.MainMenuIntentService;
import us.nineworlds.serenity.ui.views.MainMenuTextView;

import us.nineworlds.serenity.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Toast;

public class MainMenuTextViewAdapter extends BaseAdapter {

	/** The parent context */
	private static Context myContext;

	private static Handler menuItemhandler;

	private static MainMenuTextViewAdapter notifyAdapter;

	public static ArrayList<MenuItem> menuItems;

	/** Simple Constructor saving the 'parent' context. */
	public MainMenuTextViewAdapter(Context c) {
		myContext = c;
	}

	public MainMenuTextViewAdapter(Context c, View v) {
		myContext = c;
		menuItems = new ArrayList<MenuItem>();
		fetchData();
	}

	protected void fetchData() {
		menuItemhandler = new MenuItemHandler();
		Messenger messenger = new Messenger(menuItemhandler);
		Intent intent = new Intent(myContext, MainMenuIntentService.class);
		intent.putExtra("MESSENGER", messenger);
		myContext.startService(intent);
		notifyAdapter = this;
	}

	@Override
	public int getCount() {
		return menuItems.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MenuItem menuItem = menuItems.get(position);

		MainMenuTextView v = createView(menuItem);
		setDefaults(menuItem.getTitle(), v);

		return v;
	}

	/**
	 * Create a Main Menu item view for the corresponding MenuItem. If an
	 * appropriate type can not be found a default MainMenuTextView will be
	 * created.
	 * 
	 * @param v
	 * @param menuItem
	 * @return
	 */
	MainMenuTextView createView(MenuItem menuItem) {
		MainMenuTextView v = null;
		if ("movie".equals(menuItem.getType())) {
			v = new MainMenuTextView(myContext, R.drawable.movies);
			v.setLibraryKey(menuItem.getSection());
			v.setActivityType(menuItem.getType());
			return v;
		}

		if ("show".equals(menuItem.getType())) {
			v = new MainMenuTextView(myContext, R.drawable.tvshows);
			v.setLibraryKey(menuItem.getSection());
			v.setActivityType(menuItem.getType());
			return v;
		}

		if ("artist".equals(menuItem.getType())) {
			v = new MainMenuTextView(myContext, R.drawable.music);
			v.setLibraryKey(menuItem.getSection());
			v.setActivityType(menuItem.getType());
			return v;
		}

		if ("settings".equals(menuItem.getType())) {
			v = new MainMenuTextView(myContext, R.drawable.settings);
			v.setLibraryKey("0");
			v.setActivityType(menuItem.getType());
			return v;
		}

		if ("options".equals(menuItem.getType())) {
			v = new MainMenuTextView(myContext, R.drawable.settings);
			v.setLibraryKey("0");
			v.setActivityType(menuItem.getType());
			return v;
		}

		if ("search".equals(menuItem.getType())) {
			v = new MainMenuTextView(myContext, R.drawable.search);
			v.setLibraryKey("0");
			v.setActivityType(menuItem.getType());
			return v;
		}

		return new MainMenuTextView(myContext, R.drawable.serenity_bonsai_logo);
	}

	/**
	 * Sets the default values for the view passed to it.
	 * 
	 * @param position
	 * @param v
	 */
	void setDefaults(String title, MainMenuTextView v) {
		v.setText(title);
		v.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);
		v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
		v.setTextColor(Color.parseColor("#414141"));
		v.setGravity(Gravity.CENTER_VERTICAL);
		v.setLines(1);
		v.setHorizontallyScrolling(true);
		v.setEllipsize(TruncateAt.MARQUEE);
		v.setLayoutParams(new Gallery.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
	}

	private static class MenuItemHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			if (msg.obj != null) {
				menuItems = (ArrayList<MenuItem>) msg.obj;
			}
			if (menuItems.size() == 1) {
				Toast.makeText(myContext, R.string.no_libraries_found,
						Toast.LENGTH_LONG).show();
			}
			notifyAdapter.notifyDataSetChanged();
			Activity c = (Activity)myContext;
			c.findViewById(R.id.mainGalleryMenu).requestFocus();
			
		}

	}

}
