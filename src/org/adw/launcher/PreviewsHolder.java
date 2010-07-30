/*
*    Copyright 2010 AnderWeb (Gustavo Claramunt) <anderweb@gmail.com>
*
*    This file is part of ADW.Launcher.
*
*    ADW.Launcher is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    ADW.Launcher is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with ADW.Launcher.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.adw.launcher;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class PreviewsHolder extends ViewGroup {
	private int[][] distro={{1},{2},{1,2},{2,2},{2,1,2},{2,2,2},{2,3,2}};
	private int maxPreviewWidth;
	private int maxPreviewHeight;
	public PreviewsHolder(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public PreviewsHolder(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public PreviewsHolder(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	private void init(){
		/*
		2
		2-1
		2-2
		2-1-2
		3-3
		3-1-3
		 */
	}
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        assert(MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);

        final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        final int count = getChildCount();
        int line_height = 0;

        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();
        int distro_set=getChildCount()-1;
        int childPos=0;
        for(int i=0;i<distro[distro_set].length;i++){
        	for(int j=0;j<distro[distro_set][i];j++){
        		if(childPos>getChildCount()-1) break;
        		final View child = getChildAt(childPos);
                if (child.getVisibility() != GONE) {
                    final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    child.measure(
                            MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));

                    final int childw = child.getMeasuredWidth();
                    line_height = Math.max(line_height, child.getMeasuredHeight() + lp.vertical_spacing);
                    xpos += childw + lp.horizontal_spacing;
                }
                childPos++;
        	}
            xpos = getPaddingLeft();
            ypos += line_height;
        }
        maxPreviewWidth=getChildAt(0).getMeasuredWidth();
        maxPreviewHeight=getChildAt(0).getMeasuredHeight();

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED){
            height = ypos + line_height;

        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST){
            if (ypos + line_height < height){
                height = ypos + line_height;
            }
        }
        setMeasuredDimension(width, height);
    }

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
        final int count = getChildCount();
        final int width = r - l;
        final int height = b-t;
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();
        
        int distro_set=count-1;
        int childPos=0;
        //TODO:ADW We nedd to find the "longer row" and get the best children width
        int maxItemsPerRow=0;
        for(int rows=0;rows<distro[distro_set].length;rows++){
        		if(distro[distro_set][rows]>maxItemsPerRow){
        			maxItemsPerRow=distro[distro_set][rows];
        		}
        }
        int childWidth=(width/maxItemsPerRow)-getPaddingLeft()-getPaddingRight();//-(horizontal_spacing*(maxItemsPerRow-1));
        if(childWidth>maxPreviewWidth)childWidth=maxPreviewWidth;
        final float scale = ((float)childWidth/(float)maxPreviewWidth);
        int childHeight = Math.round(maxPreviewHeight*scale);
        
        final int topMargin=(height/2)-((childHeight*distro[distro_set].length)/2);
        for(int rows=0;rows<distro[distro_set].length;rows++){
        	final int leftMargin=(width/2)-((childWidth*distro[distro_set][rows])/2);
        	for(int columns=0;columns<distro[distro_set][rows];columns++){
                if(childPos>getChildCount()-1) break;
        		final View child = getChildAt(childPos);
                if (child.getVisibility() != GONE) {
                    child.layout(leftMargin+xpos, topMargin+ypos, leftMargin+xpos + childWidth, topMargin+ypos + childHeight);
                    xpos += childWidth;
                }
                childPos++;
        	}
            xpos = getPaddingLeft();
            ypos += childHeight;
        }

        /*for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childw = child.getMeasuredWidth();
                final int childh = child.getMeasuredHeight();
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
                }
                child.layout(xpos, ypos, xpos + childw, ypos + childh);
                xpos += childw + lp.horizontal_spacing;
            }
        }*/

	}
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(1, 1); // default of 1px spacing
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams)
            return true;
        return false;
    }
	
    public static class LayoutParams extends ViewGroup.LayoutParams {
        public final int horizontal_spacing;
        public final int vertical_spacing;

        /**
         * @param horizontal_spacing Pixels between items, horizontally
         * @param vertical_spacing Pixels between items, vertically
         */
        public LayoutParams(int horizontal_spacing, int vertical_spacing) {
            super(0, 0);
            this.horizontal_spacing = horizontal_spacing;
            this.vertical_spacing = vertical_spacing;     
        }
    }

}
