/*
   Copyright 2016 Narrative Nithts Inc. All Rights Reserved.

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

import android.graphics.Canvas;
import android.graphics.Paint;

public class DrawRenderer {
	public void renderModel(Canvas canvas, DrawModel model, Paint paint,
	                        int startLineIndex, int offX, int offY) {
		paint.setAntiAlias(true);

		int lineSize = model.getLineSize();
		for (int i = startLineIndex; i < lineSize; ++i) {
			DrawModel.Line line = model.getLine(i);
			paint.setColor(line.color);
			paint.setStrokeWidth(line.width);
			int elemSize = line.getElemSize();
			if (elemSize < 1) {
				continue;
			}
			DrawModel.LineElem elem = line.getElem(0);
			float lastX = elem.x;
			float lastY = elem.y;

			float radius = line.width / 2;
			canvas.drawCircle(offX + lastX, offX + lastY, radius, paint);

			for (int j = 0; j < elemSize; ++j) {
				elem = line.getElem(j);
				float x = elem.x;
				float y = elem.y;
				canvas.drawCircle(offX + x, offY + y, radius, paint);
				canvas.drawLine(offX + lastX, offY + lastY, offX + x, offX + y, paint);
				lastX = x;
				lastY = y;
			}
		}
	}
}
