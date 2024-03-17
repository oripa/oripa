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
package oripa.persistence.entity.exporter;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import oripa.geom.RectangleDomain;
import oripa.persistence.entity.FoldedModelEntity;
import oripa.persistence.filetool.Exporter;
import oripa.renderer.estimation.DistortionFacade;
import oripa.renderer.estimation.FoldedModelPixelRenderer;
import oripa.swing.drawer.java2d.PixelDrawer;
import oripa.swing.view.util.AffineCamera;

/**
 * @author OUCHI Koji
 *
 */
public class FoldedModelPictureExporter implements Exporter<FoldedModelEntity> {
	private final int WIDTH = 600;
	private final int HEIGHT = 600;

	/**
	 * @param configObj
	 *            should be an instance of {@link FoldedModelPictureConfig}.
	 */
	@Override
	public boolean export(final FoldedModelEntity foldedModel, final String filePath, final Object configObj)
			throws IOException, IllegalArgumentException {

		if (configObj == null) {
			throw new IllegalArgumentException("configObj should not be null.");
		}

		var config = (FoldedModelPictureConfig) configObj;

		var origamiModel = foldedModel.getOrigamiModel();
		var overlapRelation = foldedModel.getOverlapRelation();
		var modelDomain = origamiModel.createDomainOfFoldedModel();

		var rendererOption = new FoldedModelPixelRenderer.Option()
				.setAmbientOcclusion(config.isAmbientOcclusion())
				.setDrawEdges(config.isDrawEdges())
				.setFaceOrderFlipped(config.isFaceOrderFlipped())
				.setFillFace(config.isFillFaces())
				.setColors(config.getFrontColor(), config.getBackColor());

		var pixelRenderer = new FoldedModelPixelRenderer(WIDTH, HEIGHT);

		var distortion = new DistortionFacade(modelDomain, WIDTH, HEIGHT);
		var converter = distortion.createCoordinateConverter(config.getDistortionMethod(),
				config.getDistortionParameter(),
				computeScale(modelDomain));

		var distortionResult = distortion.apply(origamiModel, overlapRelation,
				converter, config.getVertexDepths(), config.getEps());

		var faces = distortionResult.getFaces();
		var interpolatedOverlapRelation = distortionResult.getInterpolatedOverlapRelation();

		pixelRenderer.render(faces, interpolatedOverlapRelation, origamiModel.createPaperDomain(),
				rendererOption);

		var camera = new AffineCamera();

		camera.updateCameraPosition(WIDTH / 2, HEIGHT / 2);
		camera.updateCenterOfPaper(WIDTH / 2, HEIGHT / 2);
		camera.updateRotateAngle(config.getRotateAngle());
		camera.updateScale(1);

		var image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		var g = image.createGraphics();
		// Clear image
		g.setTransform(new AffineTransform());
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		g.setTransform(camera.getAffineTransform());

		var drawer = new PixelDrawer();

		drawer.draw(g, pixelRenderer.getPixels(), WIDTH, HEIGHT);

		File file = new File(filePath);
		ImageIO.write(image, filePath.substring(filePath.lastIndexOf(".") + 1),
				file);

		return true;
	}

	private double computeScale(final RectangleDomain modelDomain) {
		return Math.min(WIDTH / modelDomain.getWidth(), HEIGHT / modelDomain.getHeight()) / 1.5;
	}
}
