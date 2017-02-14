/*
 * Copyright (C) 2012 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.song.sjweibo.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.song.sjweibo.R;

public class UnderlinePageIndicator extends View {
	
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mCurrentPage;
    private float mPositionOffset;
    
	private final Runnable mTranslateRunnable = new Runnable() {
		@Override
		public void run() {

			invalidate();
		}
	};

    public UnderlinePageIndicator(Context context) {
        this(context, null);
    }

    public UnderlinePageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public UnderlinePageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) return;

        final Resources res = getResources();
        setSelectedColor(res.getColor(R.color.black));
    }

    public int getSelectedColor() {
        return mPaint.getColor();
    }

    public void setSelectedColor(int selectedColor) {
        mPaint.setColor(selectedColor);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int count = 3;

        if (mCurrentPage >= count) {
            setCurrentItem(count - 1);
            return;
        }

        final int paddingLeft = getPaddingLeft();
        final float pageWidth = (getWidth() - paddingLeft - getPaddingRight()) / (1f * count);
        final float left = paddingLeft + pageWidth * (mCurrentPage + mPositionOffset);
        final float right = left + pageWidth;
        final float top = getPaddingTop();
        final float bottom = getHeight() - getPaddingBottom();
        canvas.drawRect(left, top, right, bottom, mPaint);
    }

    public void setCurrentItem(int item) {
        mCurrentPage = item;
        invalidate();
    }
}