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
package oripa.persistent.doc.exporter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.paint.CreasePatternGraphicDrawer;
import oripa.persistent.doc.doc.Doc;

/**
 * @author Koji
 *
 */
public class PictureExporter implements DocExporter {
	private static final Logger logger = LoggerFactory
			.getLogger(PictureExporter.class);

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.persistent.doc.exporter.Exporter#export(java.lang.Object,
	 * java.lang.String)
	 */
	@Override
	public boolean export(final Doc doc, final String filePath) throws IOException {
		CreasePatternInterface creasePattern = doc.getCreasePattern();
		double gWidth = creasePattern.getPaperSize();
		double gHeight = creasePattern.getPaperSize();

		logger.info("save as image, size(w,h) = " + gWidth + ", " + gHeight);

		BufferedImage image = new BufferedImage((int) gWidth, (int) gHeight,
				BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = (Graphics2D) image.getGraphics();

		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, image.getWidth(), image.getHeight());

		g2d.setTransform(new AffineTransform());
		g2d.translate(gWidth / 2, gHeight / 2);
		g2d.scale(0.9, 0.9);

		CreasePatternGraphicDrawer drawer = new CreasePatternGraphicDrawer();
		drawer.drawAllLines(g2d, creasePattern);

		File file = new File(filePath);
		ImageIO.write(image, filePath.substring(filePath.lastIndexOf(".") + 1),
				file);

		return true;
	}
}
