package ru.shtrm.gosport.gps;

import java.util.List;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import android.content.Context;
import android.widget.Toast;

public class TaskItemizedOverlay extends ItemizedIconOverlay<OverlayItem> {
	private Context mContext;

	protected TaskItemizedOverlay(final Context context,
								  final List<OverlayItem> aList) {
		super(context, aList, new OnItemGestureListener<OverlayItem>() {
			@Override
			public boolean onItemSingleTapUp(final int index,
					final OverlayItem item) {
				return false;
			}

			@Override
			public boolean onItemLongPress(final int index,
					final OverlayItem item) {
				return false;
			}
		});

		mContext = context;
	}

	@Override
	protected boolean onSingleTapUpHelper(final int index,
			final OverlayItem item, final MapView mapView) {
		Toast.makeText(mContext, item.getTitle(), Toast.LENGTH_SHORT).show();
		return true;
	}

}