/*
   Copyright 2016 Narrative Nights Inc. All Rights Reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package jp.narr.tensorflowmnist;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class DrawModel {

	public static class LineElem {
		public float x;
		public float y;

		private LineElem(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

	public static class Line {
		public int color;
		public float width;
		private List<LineElem> elems = new ArrayList<>();

		private Line(int color, float width) {
			this.color = color;
			this.width = width;
		}

		private void addElem(LineElem elem) {
			elems.add(elem);
		}

		public int getElemSize() {
			return elems.size();
		}

		public LineElem getElem(int index) {
			return elems.get(index);
		}
	}

	private int mColor;
	private float mStrokeWidth;
	private Line mCurrentLine;

	private int mWidth;
	private int mHeight;

	private List<Line> mLines = new ArrayList<>();

	public DrawModel(int width, int height) {
		this.mWidth = width;
		this.mHeight = height;
		mColor = Color.BLACK;
	}

	public int getWidth() {
		return mWidth;
	}

	public int getHeight() {
		return mHeight;
	}

	public void setStrokeWidth(float strokeWidth) {
		this.mStrokeWidth = strokeWidth;
	}

	public void startLine(float x, float y) {
		mCurrentLine = new Line(mColor, mStrokeWidth);
		mCurrentLine.addElem(new LineElem(x, y));
		mLines.add(mCurrentLine);
	}

	public void endLine() {
		mCurrentLine = null;
	}

	public void addLineElem(float x, float y) {
		if (mCurrentLine != null) {
			mCurrentLine.addElem(new LineElem(x, y));
		}
	}

	public int getLineSize() {
		return mLines.size();
	}

	public Line getLine(int index) {
		return mLines.get(index);
	}

	public void clear() {
		mLines.clear();
	}
}
