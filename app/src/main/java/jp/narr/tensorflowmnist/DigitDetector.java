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

import android.content.Context;
import android.content.res.AssetManager;

/**
 * Handwritten digit detector.
 * <p/>
 * Created by miyoshi on 16/01/17.
 */
public class DigitDetector {
	static {
		System.loadLibrary("tensorflow_mnist");
	}

	private native int init(AssetManager assetManager, String model);

	/**
	 * draw pixels
	 */
	public native int detectDigit(int[] pixels);

	public boolean setup(Context context) {
		AssetManager assetManager = context.getAssets();

		// model from beginner tutorial
		//int ret = init(assetManager, "file:///android_asset/beginner-graph.pb");

		// model from expert tutorial
		int ret = init(assetManager, "file:///android_asset/expert-graph.pb");

		return ret >= 0;
	}
}
