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
package oripa.renderer.estimation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import oripa.domain.fold.origeom.OverlapRelation;
import oripa.domain.fold.origeom.OverlapRelationValues;
import oripa.geom.RectangleDomain;

/**
 * @author OUCHI Koji
 *
 */
public class FoldedModelPixelRenderer {

	public static class Option {
		private boolean faceOrderFlipped;
		private boolean useColor;
		private boolean fillFaces;
		private boolean drawEdges;
		private boolean ambientOcclusion;
		private Color frontColor;
		private Color backColor;
		private Color singleColor;

		public Option setFaceOrderFlipped(final boolean faceOrderFlipped) {
			this.faceOrderFlipped = faceOrderFlipped;
			return this;
		}

		public Option setUseColor(final boolean b) {
			useColor = b;
			return this;
		}

		public Option setFillFace(final boolean bFillFace) {
			fillFaces = bFillFace;
			return this;
		}

		public Option setDrawEdges(final boolean drawEdges) {
			this.drawEdges = drawEdges;
			return this;
		}

		public Option setAmbientOcclusion(final boolean ambientOcclusion) {
			this.ambientOcclusion = ambientOcclusion;
			return this;
		}

		public Option setColors(final Color front, final Color back, final Color single) {
			frontColor = front;
			backColor = back;
			singleColor = single;

			return this;
		}
	}

	public final int width;
	public final int height;

	private final int pbuf[]; // 32bit pixel buffer
	private final int zbuf[]; // 32bit z buffer
	private final int min[];
	private final int max[];
	private final int minr[];
	private final int maxr[];
	private final int ming[];
	private final int maxg[];
	private final int minb[];
	private final int maxb[];
	private final double minu[];
	private final double maxu[];
	private final double minv[];
	private final double maxv[];

	private final boolean bUseTexture = false;
	private BufferedImage textureImage = null;

	/**
	 *
	 * Constructor
	 *
	 * @param width
	 *            of image
	 * @param height
	 *            of image
	 */
	public FoldedModelPixelRenderer(final int width, final int height) {

		this.width = width;
		this.height = height;

		pbuf = new int[width * height];
		zbuf = new int[width * height];
		min = new int[height];
		max = new int[height];
		minr = new int[height];
		maxr = new int[height];
		ming = new int[height];
		maxg = new int[height];
		minb = new int[height];
		maxb = new int[height];
		maxu = new double[height];
		maxv = new double[height];
		minu = new double[height];
		minv = new double[height];

		if (bUseTexture) {
			try {
				textureImage = ImageIO.read(new File("c:\\chiyo2-1024.bmp"));
			} catch (Exception e) {
				e.printStackTrace();
				textureImage = null;
			}
		}

	}

	private void clear() {
		for (int i = 0; i < width * height; i++) {
			pbuf[i] = 0xffffffff;
			zbuf[i] = -1;
		}
	}

	public void render(final List<Face> faces,
			final OverlapRelation overlapRelation,
			final RectangleDomain paperDomain,
			final Option option) {

		clear();

		faces.forEach(face -> drawFace(face, overlapRelation, option, paperDomain));

		if (option.drawEdges) {
			drawEdges();
		}

		if (option.ambientOcclusion) {
			applyAmbientOcculusion(overlapRelation, option);
		}

	}

	private void drawFace(final Face face, final OverlapRelation overlapRelation,
			final Option option,
			final RectangleDomain paperDomain) {
		if (option.useColor) {
			drawFace(face, option.frontColor, option.backColor, overlapRelation, option, paperDomain);
		} else {
			drawFace(face, option.singleColor, option.singleColor, overlapRelation, option, paperDomain);
		}
	}

	private void drawFace(final Face face, final Color frontColor, final Color backColor,
			final OverlapRelation overlapRelation,
			final Option option,
			final RectangleDomain paperDomain) {

		List<Double> frontColorFactor;
		List<Double> backColorFactor;

		frontColorFactor = createColorFactor(frontColor);
		backColorFactor = createColorFactor(backColor);

		var vertexColorMapFactory = new VertexColorMapFactory();
		var colorMap = vertexColorMapFactory.createVertexColors(
				face.getOriginalFace(),
				frontColorFactor,
				backColorFactor,
				option.faceOrderFlipped);

		var triangleFactory = new TriangleFaceFactory();
		var triangles = triangleFactory.createAll(face);
		triangles.forEach(triangle -> triangle.prepareColor(colorMap, paperDomain));

		triangles.stream().forEach(tri -> {
			drawTriangle(tri, face.getFaceID(), overlapRelation, option);
		});

	}

	private List<Double> createColorFactor(final Color color) {
		return List.of(color.getRed(), color.getGreen(), color.getBlue()).stream()
				.map(c -> c / 255.0)
				.toList();
	}

	private int getIndex(final int x, final int y) {
		return y * width + x;
	}

	// --------------------------------------------------------------------
	// Polygon drawing
	//
	// --------------------------------------------------------------------
	private void drawTriangle(final TriangleFace tri, final int id, final OverlapRelation overlapRelation,
			final Option option) {

		// (For speed) set the range of use of the buffer
		int top = Integer.MAX_VALUE;
		int btm = Integer.MIN_VALUE;
		if (top > (int) tri.v[0].p.getY()) {
			top = (int) tri.v[0].p.getY();
		}
		if (top > (int) tri.v[1].p.getY()) {
			top = (int) tri.v[1].p.getY();
		}
		if (top > (int) tri.v[2].p.getY()) {
			top = (int) tri.v[2].p.getY();
		}
		if (btm < (int) tri.v[0].p.getY()) {
			btm = (int) tri.v[0].p.getY();
		}
		if (btm < (int) tri.v[1].p.getY()) {
			btm = (int) tri.v[1].p.getY();
		}
		if (btm < (int) tri.v[2].p.getY()) {
			btm = (int) tri.v[2].p.getY();
		}
		if (top < 0) {
			top = 0;
		}
		if (btm > height) {
			btm = height;
		}

		// Maximum and minimum buffer initialization
		for (int i = top; i < btm; i++) {
			min[i] = Integer.MAX_VALUE;
			max[i] = Integer.MIN_VALUE;
		}

		ScanEdge(tri.v[0], tri.v[1]);
		ScanEdge(tri.v[1], tri.v[2]);
		ScanEdge(tri.v[2], tri.v[0]);

		// To be drawn on the basis of the maximum and minimum buffer.
		for (int y = top; y < btm; y++) {

			// Skip if the buffer is not updated
			if (min[y] == Integer.MAX_VALUE) {
				continue;
			}

			int offset = y * width;

			// Increment calculation
			int l = (max[y] - min[y]) + 1;
			int addr = (maxr[y] - minr[y]) / l;
			int addg = (maxg[y] - ming[y]) / l;
			int addb = (maxb[y] - minb[y]) / l;
			double addu = (maxu[y] - minu[y]) / l;
			double addv = (maxv[y] - minv[y]) / l;

			int r = minr[y];
			int g = ming[y];
			int b = minb[y];
			double u = minu[y];
			double v = minv[y];

			for (int x = min[y]; x <= max[y]; x++, r += addr, g += addg, b += addb, u += addu, v += addv) {

				if (x < 0 || x >= width) {
					continue;
				}

				var faceOrderFlipped = option.faceOrderFlipped;
				// flattened pixel index
				int p = offset + x;

				byte renderFace = faceOrderFlipped ? OverlapRelationValues.UPPER
						: OverlapRelationValues.LOWER;

				if (zbuf[p] == -1 || overlapRelation.get(zbuf[p], id) == renderFace) {

					int tr = r >> 16;
					int tg = g >> 16;
					int tb = b >> 16;

					var fillFaces = option.fillFaces;

					if (!fillFaces) {
						pbuf[p] = 0xffffffff;

					} else {
						if (bUseTexture) {
							int tx = (int) (textureImage.getWidth() * u);
							int ty = (int) (textureImage.getHeight() * v);

							tx = tx % textureImage.getWidth();
							ty = ty % textureImage.getHeight();
							int textureColor = textureImage.getRGB(tx, ty);

							if (fillFaces && (tri.isFaceFront() ^ faceOrderFlipped)) {
								pbuf[p] = textureColor;
							} else {
								pbuf[p] = (tr << 16) | (tg << 8) | tb | 0xff000000;

							}
						} else {
							pbuf[p] = (tr << 16) | (tg << 8) | tb | 0xff000000;
						}
					}
					zbuf[p] = id;
				}
			}
		}
	}

	// --------------------------------------------------------------------
	// ScanEdge
	//
	// Vector v1 ...Starting point
	// Vector v2 ...Starting point
	// --------------------------------------------------------------------
	private void ScanEdge(final TriangleVertex v1, final TriangleVertex v2) {

		int l = Math.abs((int) (v2.p.getY() - v1.p.getY())) + 1;

		// Increment calculation
		int addx = (int) ((v2.p.getX() - v1.p.getX()) * 0xffff) / l;
		int addy = (int) ((v2.p.getY() - v1.p.getY()) * 0xffff) / l;

		int addr = (int) (255 * 0xffff * (v2.color.getR() - v1.color.getR()) / l);
		int addg = (int) (255 * 0xffff * (v2.color.getG() - v1.color.getG()) / l);
		int addb = (int) (255 * 0xffff * (v2.color.getB() - v1.color.getB()) / l);

		double addu = (v2.uv.getX() - v1.uv.getX()) / l;
		double addv = (v2.uv.getY() - v1.uv.getY()) / l;

		// Initial value setting
		int x = (int) (v1.p.getX() * 0xffff);
		int y = (int) (v1.p.getY() * 0xffff);
		int r = (int) (255 * 0xffff * v1.color.getR());
		int g = (int) (255 * 0xffff * v1.color.getG());
		int b = (int) (255 * 0xffff * v1.color.getB());
		double u = v1.uv.getX();
		double v = v1.uv.getY();

		// Scan
		for (int i = 0; i < l; i++, x += addx, y += addy, r += addr, g += addg, b += addb, u += addu, v += addv) {
			int py = y >> 16;
			int px = x >> 16;

			if (py < 0 || py >= height) {
				continue;
			}

			if (min[py] > px) {
				min[py] = px;
				minr[py] = r;
				ming[py] = g;
				minb[py] = b;
				minu[py] = u;
				minv[py] = v;
			}

			if (max[py] < px) {
				max[py] = px;
				maxr[py] = r;
				maxg[py] = g;
				maxb[py] = b;
				maxu[py] = u;
				maxv[py] = v;
			}
		}
	}

	private void drawEdges() {
		// apply Sobel filter
		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				int val_h = -1 * zbuf[getIndex(x - 1, y - 1)]
						+ zbuf[getIndex(x + 1, y - 1)]
						+ -2 * zbuf[getIndex(x - 1, y)]
						+ 2 * zbuf[getIndex(x + 1, y)]
						+ -1 * zbuf[getIndex(x - 1, y + 1)]
						+ zbuf[getIndex(x + 1, y + 1)];
				int val_v = -1 * zbuf[getIndex(x - 1, y - 1)]
						+ zbuf[getIndex(x - 1, y + 1)]
						+ -2 * zbuf[getIndex(x, y - 1)]
						+ 2 * zbuf[getIndex(x, y + 1)]
						+ -1 * zbuf[getIndex(x + 1, y - 1)]
						+ zbuf[getIndex(x + 1, y + 1)];

				if (val_h != 0 || val_v != 0) {
					pbuf[getIndex(x, y)] = 0xff888888;
				}
			}
		}
	}

	private void applyAmbientOcculusion(final OverlapRelation overlapRelation, final Option option) {
		byte renderFace = option.faceOrderFlipped ? OverlapRelationValues.UPPER
				: OverlapRelationValues.LOWER;
		int r = 10;
		int s = (int) (r * r * Math.PI);
		// For every pixel
		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				int f_id = zbuf[getIndex(x, y)];

				// Within a circle of radius r, Count the pixels of the
				// surface
				// that is above their own
				int cnt = 0;
				for (int dy = -r; dy <= r; dy++) {
					for (int dx = -r; dx <= r; dx++) {
						if (dx * dx + dy * dy > r * r) {
							continue;
						}
						if (y + dy < 0 || y + dy > height - 1) {
							continue;
						}
						if (x + dx < 0 || x + dx > width - 1) {
							continue;
						}
						int f_id2 = zbuf[getIndex(x + dx, y + dy)];

						if (f_id == -1 && f_id2 != -1) {
							cnt++;
						} else {
							if (f_id2 != -1 && overlapRelation.get(f_id, f_id2) == renderFace) {
								cnt++;
							}
						}
					}
				}

				if (cnt > 0) {
					int prev = pbuf[getIndex(x, y)];
					double ratio = 1.0 - ((double) cnt) / s;
					int p_r = (int) Math.max(0, ((prev & 0x00ff0000) >> 16) * ratio);
					int p_g = (int) Math.max(0, ((prev & 0x0000ff00) >> 8) * ratio);
					int p_b = (int) Math.max(0, (prev & 0x000000ff) * ratio);

					pbuf[getIndex(x, y)] = (p_r << 16) | (p_g << 8) | p_b | 0xff000000;
				}
			}
		}
	}

	public int[] getPixels() {
		return pbuf;
	}

	/*
	 * Convenience method that returns a scaled instance of the provided {@code
	 * BufferedImage}.
	 *
	 * @param img the original image to be scaled
	 *
	 * @param targetWidth the desired width of the scaled instance, in pixels
	 *
	 * @param targetHeight the desired height of the scaled instance, in pixels
	 *
	 * @param hint one of the rendering hints that corresponds to {@code
	 * RenderingHints.KEY_INTERPOLATION} (e.g. {@code
	 * RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR}, {@code
	 * RenderingHints.VALUE_INTERPOLATION_BILINEAR}, {@code
	 * RenderingHints.VALUE_INTERPOLATION_BICUBIC})
	 *
	 * @param higherQuality if true, this method will use a multi-step scaling
	 * technique that provides higher quality than the usual one-step technique
	 * (only useful in downscaling cases, where {@code targetWidth} or {@code
	 * targetHeight} is smaller than the original dimensions, and generally only
	 * when the {@code BILINEAR} hint is specified)
	 *
	 * @return a scaled version of the original {@code BufferedImage}
	 */
//	public BufferedImage getScaledInstance(final BufferedImage img,
//			final int targetWidth,
//			final int targetHeight,
//			final Object hint,
//			final boolean higherQuality) {
//		int type = (img.getTransparency() == Transparency.OPAQUE)
//				? BufferedImage.TYPE_INT_RGB
//				: BufferedImage.TYPE_INT_ARGB;
//		BufferedImage ret = img;
//		int w, h;
//		if (higherQuality) {
//			// Use multi-step technique: start with original size, then
//			// scale down in multiple passes with drawImage()
//			// until the target size is reached
//			w = img.getWidth();
//			h = img.getHeight();
//			if (w < targetWidth) {
//				w = targetWidth;
//			}
//			if (h < targetHeight) {
//				h = targetHeight;
//			}
//		} else {
//			// Use one-step technique: scale directly from original
//			// size to target size with a single drawImage() call
//			w = targetWidth;
//			h = targetHeight;
//		}
//
//		do {
//			if (higherQuality && w > targetWidth) {
//				w /= 2;
//				if (w < targetWidth) {
//					w = targetWidth;
//				}
//			}
//
//			if (higherQuality && h > targetHeight) {
//				h /= 2;
//				if (h < targetHeight) {
//					h = targetHeight;
//				}
//			}
//
//			BufferedImage tmp = new BufferedImage(w, h, type);
//			Graphics2D g2 = tmp.createGraphics();
//			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
//			g2.drawImage(ret, 0, 0, w, h, null);
//			g2.dispose();
//
//			ret = tmp;
//		} while (w != targetWidth || h != targetHeight);
//
//		return ret;
//	}

}
