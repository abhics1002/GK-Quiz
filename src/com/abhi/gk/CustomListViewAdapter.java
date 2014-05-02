package com.abhi.gk;
 
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListViewAdapter extends ArrayAdapter<RowItem> {

	Context context;

	public CustomListViewAdapter(Context context, int resourceId,
			List<RowItem> items) {
		super(context, resourceId, items);
		this.context = context;
	}

	/*private view holder class*/
	private class ViewHolder {
		ImageView imageView;
		TextView txtQuestion;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		RowItem rowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.answer_list_item, null);
			holder = new ViewHolder();

			holder.txtQuestion = (TextView) convertView.findViewById(R.id.question);
			holder.imageView = (ImageView) convertView.findViewById(R.id.icon);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();


		holder.txtQuestion.setText(rowItem.getquestion());
		holder.imageView.setImageResource(rowItem.getImageId());


		// for displaying animation on the the answer page
		Animation animation = null;
		animation = AnimationUtils.loadAnimation(context, R.anim.alpha);

		animation.setDuration(500);
		convertView.startAnimation(animation);
		animation = null;

		return convertView;
	}
}