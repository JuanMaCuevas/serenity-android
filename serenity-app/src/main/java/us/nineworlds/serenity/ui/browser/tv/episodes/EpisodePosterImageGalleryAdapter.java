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

package us.nineworlds.serenity.ui.browser.tv.episodes;

import java.util.List;

import us.nineworlds.serenity.core.model.VideoContentInfo;
import us.nineworlds.serenity.core.services.EpisodeRetrievalIntentService;
import us.nineworlds.serenity.ui.adapters.AbstractPosterImageGalleryAdapter;
import us.nineworlds.serenity.ui.util.ImageUtils;
import us.nineworlds.serenity.widgets.SerenityGallery;

import us.nineworlds.serenity.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Implementation of the Poster Image Gallery class for TV Shows.
 * 
 * @author dcarver
 * 
 */
public class EpisodePosterImageGalleryAdapter extends
		AbstractPosterImageGalleryAdapter {

	private static EpisodePosterImageGalleryAdapter notifyAdapter;
	private static ProgressDialog pd;
	private static final float WATCHED_PERCENT = 0.98f;

	public EpisodePosterImageGalleryAdapter(Context c, String key) {
		super(c, key);
		notifyAdapter = this;
		shrink = AnimationUtils.loadAnimation(c, R.anim.shrink);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View galleryCellView = context.getLayoutInflater().inflate(
				R.layout.poster_indicator_view, null);

		VideoContentInfo pi = posterList.get(position);
		ImageView mpiv = (ImageView) galleryCellView
				.findViewById(R.id.posterImageView);
		mpiv.setBackgroundResource(R.drawable.gallery_item_background);
		mpiv.setScaleType(ImageView.ScaleType.FIT_XY);
		int width = ImageUtils.getDPI(300, context);
		int height = ImageUtils.getDPI(187, context);
		mpiv.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
		galleryCellView.setLayoutParams(new SerenityGallery.LayoutParams(width, height));

		shrinkPosterAnimation(mpiv, false);

		imageLoader.displayImage(pi.getImageURL(), mpiv);

		ImageView watchedView = (ImageView) galleryCellView
				.findViewById(R.id.posterWatchedIndicator);
		
		setWatchedStatus(galleryCellView, pi);
 
		return galleryCellView;
	}

	@Override
	protected void fetchDataFromService() {
		pd = ProgressDialog.show(context, "",
				context.getString(R.string.retrieving_episodes));
		handler = new EpisodeHandler();
		retrieveEpisodes();
		notifyAdapter = this;

	}

	/**
	 * 
	 */
	public void retrieveEpisodes() {
		Messenger messenger = new Messenger(handler);
		Intent intent = new Intent(context, EpisodeRetrievalIntentService.class);
		intent.putExtra("MESSENGER", messenger);
		intent.putExtra("key", key);

		context.startService(intent);
	}

	private static class EpisodeHandler extends Handler {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			posterList = (List<VideoContentInfo>) msg.obj;
			pd.dismiss();
			SerenityGallery gallery = (SerenityGallery) context
					.findViewById(R.id.moviePosterGallery);
			notifyAdapter.notifyDataSetChanged();
			gallery.requestFocus();
		}
	}
}
