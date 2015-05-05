/*
 * Copyright (C) 2015 Picon software
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package fr.piconsoft.eoit.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.TreeSet;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.model.ItemBean;
import fr.piconsoft.eoit.util.IconUtil;

import static fr.piconsoft.eoit.helper.PriceRepartitionHelper.getGreaterValues;
import static fr.piconsoft.eoit.helper.PriceRepartitionHelper.pollFirst;

/**
 * @author picon.software
 */
public class PriceRepartitionView extends View {

	private final static int TOP_MARGIN = 100;

	private TreeSet<ItemBean> items;
	private long totalPrice;

	private int circleWidth;
	private Point circleCenter;

	private float[] startAngles = new float[3], sweepAngles = new float[3];

	private Paint paint;

	private enum Orientation {
		NordWest,
		SouthWest,
		SouthEst,
		NorthEst
	}

	public PriceRepartitionView(Context context, AttributeSet attrs,
								int defStyle) {
		super(context, attrs, defStyle);
	}

	public PriceRepartitionView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PriceRepartitionView(Context context) {
		super(context);
	}

	public PriceRepartitionView(Context context, TreeSet<ItemBean> items) {
		super(context);
		this.items = items;
		for (ItemBean item : items) {
			totalPrice += Math.round(item.price * item.quantity);
		}

		startAngles[0] = (float) (360 * Math.random());
		sweepAngles[0] = (float) (180 + 90 * Math.random());
		startAngles[1] = (float) (startAngles[0] + 90 * Math.random());
		sweepAngles[1] = (float) (120 + 90 * Math.random());
		startAngles[2] = (float) (startAngles[1] + 45 * Math.random());
		sweepAngles[2] = (float) (45 + 45 * Math.random());

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1);
		paint.setAlpha(100);
		paint.setColor(Color.DKGRAY);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int width = canvas.getWidth();

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				width);
		setLayoutParams(lp);

		circleWidth = (int) Math.round(width * 0.4);
		int margin = (width - circleWidth) / 2;
		circleCenter = new Point(margin + circleWidth / 2, TOP_MARGIN + circleWidth / 2);

		TreeSet<ItemBean> greaterItems = getGreaterValues(items);
		ItemBean item1 = pollFirst(greaterItems);
		float angle1, angle2, angle3;

		if (item1 != null && !Double.isNaN(item1.price)) {
			angle1 = 360 * ((float) (item1.price * item1.quantity) / totalPrice);

			RectF r = new RectF(margin, TOP_MARGIN, circleWidth + margin, circleWidth + TOP_MARGIN);
			paint.setStrokeWidth(1);
			canvas.drawCircle(circleCenter.x, circleCenter.y, circleWidth * 0.425f - 3, paint);
			drawArcs(canvas, r);

			paint.setStyle(Paint.Style.FILL);
			paint.setShader(
					new RadialGradient(
							circleCenter.x, circleCenter.y,
							circleWidth * 0.45f - 6,
							0x00000000, Color.DKGRAY,
							Shader.TileMode.MIRROR)
			);
			canvas.drawCircle(circleCenter.x, circleCenter.y, circleWidth * 0.425f - 6, paint);

			paint.setAlpha(150);
			paint.setStyle(Paint.Style.STROKE);
			paint.setShader(
					new RadialGradient(
							circleCenter.x, circleCenter.y,
							circleWidth * 0.7f,
							0x05eb1313, getContext().getResources().getColor(R.color.red),
							Shader.TileMode.MIRROR)
			);
			paint.setStrokeWidth(circleWidth * 0.15f);
			canvas.drawArc(r, -90, angle1, false, paint);
			drawLegend(canvas, item1, -90 + angle1 / 2, angle1 / 360f,
					Color.GRAY, (int) (circleWidth * 1.025));
			//paint.setAlpha(125);
			ItemBean item2 = pollFirst(greaterItems);
			if (item2 != null && !Double.isNaN(item2.price)) {
				angle2 = 360 * ((float) (item2.price * item2.quantity) / totalPrice);
				paint.setAlpha(100);
				paint.setStrokeWidth(circleWidth * 0.3f + 140 - angle2 * 1.5f);
				paint.setShader(
						new RadialGradient(
								circleCenter.x, circleCenter.y,
								circleWidth * 0.6f,
								0x11888888, Color.LTGRAY,
								Shader.TileMode.MIRROR)
				);
				canvas.drawArc(r, (-90 + angle1 - 10), angle2, false, paint);
				drawLegend(canvas, item2, -90 + angle1 + (angle2) / 2 - 10, angle2 / 360f, Color.GRAY, (int) (circleWidth * 1.1));

				ItemBean item3 = pollFirst(greaterItems);
				if (item3 != null && !Double.isNaN(item3.price)) {
					angle3 = 360 * ((float) (item3.price * item3.quantity) / totalPrice);
					paint.setStrokeWidth(circleWidth * 0.3f + 140 - angle3 * 1.5f);
					paint.setShader(
							new RadialGradient(
									circleCenter.x, circleCenter.y,
									circleWidth * 0.6f,
									0x11888888, Color.GRAY,
									Shader.TileMode.MIRROR)
					);
					canvas.drawArc(r, (-90 + angle1 + angle2 - 20), angle3, false, paint);
					drawLegend(canvas, item3, -90 + angle1 + angle2 + (angle3) / 2 - 20, angle3 / 360f, Color.GRAY, (int) (circleWidth * 1.1));
				}
			}
		}
	}

	private void drawArcs(Canvas canvas, RectF r) {
		Paint paint = new Paint();
		paint.setAntiAlias(false);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		paint.setColor(Color.DKGRAY);
		paint.setAlpha(100);

		RectF rect = new RectF(r);
		rect.left -= circleWidth * 0.08f + 4;
		rect.top -= circleWidth * 0.08f + 4;
		rect.right += circleWidth * 0.08f + 4;
		rect.bottom += circleWidth * 0.08f + 4;

		paint.setStrokeWidth(5);
		for (int i = 0; i < 360; i += 5) {
			canvas.drawArc(rect, i, 1, false, paint);
		}
		paint.setStrokeWidth(5);
		rect.left -= 4;
		rect.top -= 4;
		rect.right += 4;
		rect.bottom += 4;
		canvas.drawArc(rect, 0, 360, false, paint);
		rect.left -= 3;
		rect.top -= 3;
		rect.right += 3;
		rect.bottom += 3;

		for (int i = 0; i < 3; i++) {
			canvas.drawArc(rect, startAngles[i], sweepAngles[i], false, paint);
			rect.left -= 3;
			rect.top -= 3;
			rect.right += 3;
			rect.bottom += 3;
		}
	}

	private void drawLegend(Canvas canvas, ItemBean item, float angle, float percent, int color, int circleWidth) {
		Point startPoint = new Point();
		startPoint.x = (int) Math.round(circleWidth * Math.cos(Math.toRadians(angle)) / 2) + circleCenter.x;
		startPoint.y = (int) Math.round(circleWidth * Math.sin(Math.toRadians(angle)) / 2) + circleCenter.y;

		Orientation orientation;
		if (angle >= -90 && angle <= 0) {
			orientation = Orientation.NordWest;
		} else if (angle >= 0 && angle <= 90) {
			orientation = Orientation.SouthWest;
		} else if (angle >= 90 && angle <= 180) {
			orientation = Orientation.SouthEst;
		} else if (angle >= 180 && angle <= 270) {
			orientation = Orientation.NorthEst;
		} else {
			orientation = Orientation.NordWest;
		}

		drawLegend(canvas, item, orientation, startPoint, percent, color);
	}

	private void drawLegend(Canvas canvas, ItemBean item,
							Orientation orientation, Point startPoint,
							float percent, int color) {

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeWidth(5);
		paint.setColor(color);
		//paint.setAlpha(100);

		Path path = new Path();
		path.moveTo(startPoint.x, startPoint.y);
		path.addCircle(startPoint.x, startPoint.y, 3, Direction.CW);
		path.moveTo(startPoint.x, startPoint.y);
		switch (orientation) {
			case NordWest:
				path.lineTo(startPoint.x + 40, startPoint.y - 40);
				path.lineTo(startPoint.x + 70, startPoint.y - 40);
				path.lineTo(startPoint.x + 40, startPoint.y - 40);
				break;

			case SouthWest:
				path.lineTo(startPoint.x + 40, startPoint.y + 40);
				path.lineTo(startPoint.x + 70, startPoint.y + 40);
				path.lineTo(startPoint.x + 40, startPoint.y + 40);
				break;

			case SouthEst:
				path.lineTo(startPoint.x - 40, startPoint.y + 40);
				path.lineTo(startPoint.x - 70, startPoint.y + 40);
				path.lineTo(startPoint.x - 40, startPoint.y + 40);
				break;

			case NorthEst:
				path.lineTo(startPoint.x - 40, startPoint.y - 40);
				path.lineTo(startPoint.x - 70, startPoint.y - 40);
				path.lineTo(startPoint.x - 40, startPoint.y - 40);
				break;
		}
		paint.setStrokeWidth(3);
		canvas.drawPath(path, paint);
		paint.setStrokeWidth(7);

		switch (orientation) {
			case NordWest:
				canvas.drawLine(startPoint.x + 70, startPoint.y - 38, startPoint.x + 130, startPoint.y - 38, paint);
				break;

			case SouthWest:
				canvas.drawLine(startPoint.x + 70, startPoint.y + 38, startPoint.x + 130, startPoint.y + 38, paint);
				break;

			case SouthEst:
				canvas.drawLine(startPoint.x - 70, startPoint.y + 38, startPoint.x - 130, startPoint.y + 38, paint);
				break;

			case NorthEst:
				canvas.drawLine(startPoint.x - 70, startPoint.y - 38, startPoint.x - 130, startPoint.y - 38, paint);
				break;
		}

		paint.setStrokeWidth(1);
		paint.setColor(Color.WHITE);
		NumberFormat nf = new DecimalFormat("##0.#%");
		paint.setTextSize(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				10, getResources().getDisplayMetrics()));
		switch (orientation) {
			case NordWest:
				canvas.drawText(nf.format(percent), startPoint.x + 85, startPoint.y - 21, paint);
				break;

			case SouthWest:
				canvas.drawText(nf.format(percent), startPoint.x + 85, startPoint.y + 55, paint);
				break;

			case SouthEst:
				canvas.drawText(nf.format(percent), startPoint.x - 115, startPoint.y + 55, paint);
				break;

			case NorthEst:
				canvas.drawText(nf.format(percent), startPoint.x - 115, startPoint.y - 21, paint);
				break;
		}

		float x = 0, y = 0;
		switch (orientation) {
			case NordWest:
				x = startPoint.x + 70;
				y = startPoint.y - 38 - 64;
				break;

			case SouthWest:
				x = startPoint.x + 70;
				y = startPoint.y + 38 - 64;
				break;

			case SouthEst:
				x = startPoint.x - 70 - 64;
				y = startPoint.y + 38 - 64;
				break;

			case NorthEst:
				x = startPoint.x - 70 - 64;
				y = startPoint.y - 38 - 64;
				break;
		}

		Target target = new BitmapInCanvas(canvas, x, y);
		Picasso.with(getContext()).load(IconUtil.getImageUrl(item.id, getContext())).into(target);
	}

	private class BitmapInCanvas implements Target {

		private Canvas canvas;
		float x, y;

		private BitmapInCanvas(Canvas canvas, float x, float y) {
			this.canvas = canvas;
			this.x = x;
			this.y = y;
		}

		@Override
		public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
			if(canvas != null) {
				canvas.drawBitmap(bitmap, x, y, null);
			}
		}

		@Override
		public void onBitmapFailed(Drawable drawable) {

		}

		@Override
		public void onPrepareLoad(Drawable drawable) {

		}
	}

}
