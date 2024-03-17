/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package oripa.swing.drawer.java2d;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

/**
 * @author OUCHI Koji
 *
 */
public class PixelDrawer {

	public PixelDrawer() {
	}

	public void draw(final Graphics2D g2d, final int[] pixels, final int width, final int height) {
		var renderImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		renderImage = fromPixelIntArray(pixels, width, height);
		g2d.drawImage(renderImage, 0, 0, null);

	}

	private BufferedImage fromPixelIntArray(final int[] pixels, final int width, final int height) {
		int[] bitMasks = new int[] { 0xFF0000, 0xFF00, 0xFF, 0xFF000000 };
		SinglePixelPackedSampleModel sm = new SinglePixelPackedSampleModel(
				DataBuffer.TYPE_INT, width, height, bitMasks);
		DataBufferInt db = new DataBufferInt(pixels, pixels.length);
		WritableRaster wr = Raster.createWritableRaster(sm, db, new Point());

		return new BufferedImage(ColorModel.getRGBdefault(), wr, false, null);
	}

}
