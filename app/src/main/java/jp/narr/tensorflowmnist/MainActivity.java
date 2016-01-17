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

import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
	private static final String TAG = "MainActivity";

	private static final int PIXEL_WIDTH = 28;

	private TextView mResultText;

	private float mLastX;
	private float mLastY;

	private DrawModel mModel;
	private DrawView mDrawView;

	private PointF mTmpPiont = new PointF();

	private DigitDetector mDetector = new DigitDetector();


	@SuppressWarnings("SuspiciousNameCombination")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		boolean ret = mDetector.setup(this);
		if( !ret ) {
			Log.i(TAG, "Detector setup failed");
			return;
		}

		mModel = new DrawModel(PIXEL_WIDTH, PIXEL_WIDTH);

		mDrawView = (DrawView) findViewById(R.id.view_draw);
		mDrawView.setModel(mModel);
		mDrawView.setOnTouchListener(this);

		View detectButton = findViewById(R.id.button_detect);
		detectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onDetectClicked();
			}
		});

		View clearButton = findViewById(R.id.button_clear);
		clearButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClearClicked();
			}
		});

		mResultText = (TextView)findViewById(R.id.text_result);
	}

	@Override
	protected void onResume() {
		mDrawView.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		mDrawView.onPause();
		super.onPause();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;

		if (action == MotionEvent.ACTION_DOWN) {
			processTouchDown(event);
			return true;

		} else if (action == MotionEvent.ACTION_MOVE) {
			processTouchMove(event);
			return true;

		} else if (action == MotionEvent.ACTION_UP) {
			processTouchUp();
			return true;
		}
		return false;
	}

	private void processTouchDown(MotionEvent event) {
		mLastX = event.getX();
		mLastY = event.getY();
		mDrawView.calcPos(mLastX, mLastY, mTmpPiont);
		float lastConvX = mTmpPiont.x;
		float lastConvY = mTmpPiont.y;
		mModel.startLine(lastConvX, lastConvY);
	}

	private void processTouchMove(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		mDrawView.calcPos(x, y, mTmpPiont);
		float newConvX = mTmpPiont.x;
		float newConvY = mTmpPiont.y;
		mModel.addLineElem(newConvX, newConvY);

		mLastX = x;
		mLastY = y;
		mDrawView.invalidate();
	}

	private void processTouchUp() {
		mModel.endLine();
	}

	private void onDetectClicked() {
		int pixels[] = mDrawView.getPixelData();
		int digit = mDetector.detectDigit(pixels);

		Log.i(TAG, "digit =" + digit);

		mResultText.setText("Detected = " + digit);
	}

	private void onClearClicked() {
		mModel.clear();
		mDrawView.reset();
		mDrawView.invalidate();

		mResultText.setText("");
	}
}
