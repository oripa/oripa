/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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

package oripa.gui.view.estimation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.vecmath.Vector2d;

import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.OverlapRelationList;
import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.origeom.OverlapRelationValues;
import oripa.geom.RectangleDomain;
import oripa.gui.view.util.MouseUtility;

/**
 * A screen to show the folded state of origami.
 *
 * @author Koji
 *
 */
public class FoldedModelScreen extends JPanel
		implements MouseListener, MouseMotionListener, MouseWheelListener {

	private BufferedImage bufferImage;
	private Graphics2D bufferg;
	private final int pbuf[]; // 32bit pixel buffer
	private final int zbuf[]; // 32bit z buffer
	private final int BUFFERW; // width
	private final int BUFFERH; // height
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
	private boolean useColor = true;
	private boolean fillFaces = true;
	private boolean ambientOcclusion = false;
	private boolean faceOrderFlip = false;
	private final double rotAngle = 0;
	private final double scaleRate = 0.8;
	private boolean drawEdges = true;
	private Image renderImage;
	double rotateAngle;
	double scale;
	double transX;
	double transY;
	private Point2D preMousePoint;
	private final AffineTransform affineTransform;
	private BufferedImage textureImage = null;
	private final boolean bUseTexture = false;

	private OrigamiModel origamiModel = null;
	private OverlapRelationList overlapRelationList = null;
	private RectangleDomain domain;

	public FoldedModelScreen() {
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);

		BUFFERW = 600;
		BUFFERH = 600;

		pbuf = new int[BUFFERW * BUFFERH];
		zbuf = new int[BUFFERW * BUFFERH];
		min = new int[BUFFERH];
		max = new int[BUFFERH];
		minr = new int[BUFFERH];
		maxr = new int[BUFFERH];
		ming = new int[BUFFERH];
		maxg = new int[BUFFERH];
		minb = new int[BUFFERH];
		maxb = new int[BUFFERH];
		maxu = new double[BUFFERH];
		maxv = new double[BUFFERH];
		minu = new double[BUFFERH];
		minv = new double[BUFFERH];

		clear();
		drawOrigami();
		rotateAngle = 0;
		scale = 1.0;
		affineTransform = new AffineTransform();
		updateAffineTransform();

		if (bUseTexture) {
			try {
				textureImage = ImageIO.read(new File("c:\\chiyo2-1024.bmp"));
			} catch (Exception e) {
				e.printStackTrace();
				textureImage = null;
			}
		}

	}

	private void resetViewMatrix() {
		rotateAngle = 0;
		scale = 1;

		domain = origamiModel.createDomainOfFoldedModel();

		updateAffineTransform();
		redrawOrigami();
	}

	public void redrawOrigami() {
		clear();
		drawOrigami();
		repaint();
	}

	public void setUseColor(final boolean b) {
		useColor = b;
		redrawOrigami();
	}

	public void setFillFace(final boolean bFillFace) {
		fillFaces = bFillFace;
		redrawOrigami();
	}

	public void drawEdge(final boolean bEdge) {
		drawEdges = bEdge;
		redrawOrigami();
	}

	public void flipFaces(final boolean bFlip) {
		this.faceOrderFlip = bFlip;
		redrawOrigami();
	}

	public void shadeFaces(final boolean bShade) {
		ambientOcclusion = bShade;
		redrawOrigami();
	}

	private int getIndex(final int x, final int y) {
		return y * BUFFERW + x;
	}

	public void clear() {
		for (int i = 0; i < BUFFERW * BUFFERH; i++) {
			pbuf[i] = 0xffffffff;
			zbuf[i] = -1;
		}
	}

	private void updateAffineTransform() {
		affineTransform.setToIdentity();
		affineTransform.translate(getWidth() * 0.5, getHeight() * 0.5);
		affineTransform.scale(scale, -scale);
		affineTransform.translate(transX, -transY);
		affineTransform.rotate(rotateAngle);
		affineTransform.translate(-getWidth() * 0.5, -getHeight() * 0.5);
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

	public void setModel(final FoldedModel foldedModel) {
		this.origamiModel = foldedModel.getOrigamiModel();
		this.overlapRelationList = foldedModel.getOverlapRelationList();

		resetViewMatrix();
		redrawOrigami();
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);

		if (bufferImage == null) {
			bufferImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
			bufferg = (Graphics2D) bufferImage.getGraphics();

			updateAffineTransform();
		}
		bufferg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		bufferg.setTransform(new AffineTransform());

		// Clear image
		bufferg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		bufferg.setColor(Color.WHITE);
		bufferg.fillRect(0, 0, getWidth(), getHeight());

		bufferg.setTransform(affineTransform);

		Graphics2D g2d = bufferg;

		if (renderImage != null) {
			g2d.drawImage(renderImage, 0, 0, null);
		}
		g.drawImage(bufferImage, 0, 0, this);
	}

	public void drawOrigami() {
		if (origamiModel == null || overlapRelationList == null) {
			return;
		}

		if (!origamiModel.isFolded()) {
			return;
		}
		long time0 = System.currentTimeMillis();

		Vector2d center = new Vector2d(domain.getCenterX(), domain.getCenterY());
		final double localScale = scaleRate * Math.min(
				BUFFERW / (domain.getWidth()),
				BUFFERH / (domain.getHeight())) * 0.95;
		final double angle = rotAngle * Math.PI / 180;

		List<OriFace> faces = origamiModel.getFaces();

		var paperDomain = origamiModel.createPaperDomain();

		for (OriFace face : faces) {
			if (useColor) {
				var frontColorFactor = List.of(0.7, 0.7, 1.0);
				var backColorFactor = List.of(1.0, 0.8, 0.7);
				face.setVertexColor(frontColorFactor, backColorFactor, isFaceOrderFlipped());
			} else {
				var white = List.of(1.0, 1.0, 0.95);
				face.setVertexColor(white, white, isFaceOrderFlipped());
			}

			var triangleFactory = new TriangleFaceFactory();
			var triangles = triangleFactory.create(face);
			triangles.forEach(triangle -> triangle.prepareColor(paperDomain));

			triangles.stream().forEach(tri -> {
				for (int i = 0; i < 3; i++) {
					var pos = tri.getPosition(i);
					double x = (pos.x - center.x) * localScale;
					double y = (pos.y - center.y) * localScale;

					double rotX = x * Math.cos(angle) + y * Math.sin(angle) + BUFFERW * 0.5;
					double rotY = x * Math.sin(angle) - y * Math.cos(angle) + BUFFERW * 0.5;

					tri.setPosition(i, rotX, rotY);
				}
				drawTriangle(tri, face.getFaceID());
			});
		}

		if (drawEdges) {
			// apply Sobel filter
			for (int y = 1; y < BUFFERH - 1; y++) {
				for (int x = 1; x < BUFFERW - 1; x++) {
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

		if (ambientOcclusion) {
			byte renderFace = isFaceOrderFlipped() ? OverlapRelationValues.UPPER
					: OverlapRelationValues.LOWER;
			int r = 10;
			int s = (int) (r * r * Math.PI);
			// For every pixel
			for (int y = 1; y < BUFFERH - 1; y++) {
				for (int x = 1; x < BUFFERW - 1; x++) {
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
							if (y + dy < 0 || y + dy > BUFFERH - 1) {
								continue;
							}
							if (x + dx < 0 || x + dx > BUFFERW - 1) {
								continue;
							}
							int f_id2 = zbuf[getIndex(x + dx, y + dy)];

							if (f_id == -1 && f_id2 != -1) {
								cnt++;
							} else {
								var overlapRelation = overlapRelationList.getOverlapRelation();

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
		long time1 = System.currentTimeMillis();

		System.out.println("render time = " + (time1 - time0) + "ms");

		renderImage = createImage(new MemoryImageSource(BUFFERW, BUFFERH, pbuf, 0, BUFFERW));

	}

	// --------------------------------------------------------------------
	// Polygon drawing
	//
	// --------------------------------------------------------------------
	private void drawTriangle(final TriangleFace tri, final int id) {

		// (For speed) set the range of use of the buffer
		int top = Integer.MAX_VALUE; // Integer.MAX_VALUE;
		int btm = Integer.MIN_VALUE;// Integer.MIN_VALUE;
		if (top > (int) tri.v[0].p.y) {
			top = (int) tri.v[0].p.y;
		}
		if (top > (int) tri.v[1].p.y) {
			top = (int) tri.v[1].p.y;
		}
		if (top > (int) tri.v[2].p.y) {
			top = (int) tri.v[2].p.y;
		}
		if (btm < (int) tri.v[0].p.y) {
			btm = (int) tri.v[0].p.y;
		}
		if (btm < (int) tri.v[1].p.y) {
			btm = (int) tri.v[1].p.y;
		}
		if (btm < (int) tri.v[2].p.y) {
			btm = (int) tri.v[2].p.y;
		}
		if (top < 0) {
			top = 0;
		}
		if (btm > BUFFERH) {
			btm = BUFFERH;
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

			int offset = y * BUFFERW;

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

				if (x < 0 || x >= BUFFERW) {
					continue;
				}

				// flattened pixel index
				int p = offset + x;

				byte renderFace = isFaceOrderFlipped() ? OverlapRelationValues.UPPER
						: OverlapRelationValues.LOWER;

				var overlapRelation = overlapRelationList.getOverlapRelation();

				if (zbuf[p] == -1 || overlapRelation.get(zbuf[p], id) == renderFace) {

					int tr = r >> 16;
					int tg = g >> 16;
					int tb = b >> 16;

					if (!fillFaces) {
						pbuf[p] = 0xffffffff;

					} else {
						if (bUseTexture) {
							int tx = (int) (textureImage.getWidth() * u);
							int ty = (int) (textureImage.getHeight() * v);

							tx = tx % textureImage.getWidth();
							ty = ty % textureImage.getHeight();
							int textureColor = textureImage.getRGB(tx, ty);

							if (fillFaces && (tri.isFaceFront() ^ isFaceOrderFlipped())) {
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

		int l = Math.abs((int) (v2.p.y - v1.p.y)) + 1;

		// Increment calculation
		int addx = (int) ((v2.p.x - v1.p.x) * 0xffff) / l;
		int addy = (int) ((v2.p.y - v1.p.y) * 0xffff) / l;

		int addr = (int) (255 * 0xffff * (v2.color.x - v1.color.x) / l);
		int addg = (int) (255 * 0xffff * (v2.color.y - v1.color.y) / l);
		int addb = (int) (255 * 0xffff * (v2.color.z - v1.color.z) / l);

		double addu = (v2.uv.x - v1.uv.x) / l;
		double addv = (v2.uv.y - v1.uv.y) / l;

		// Initial value setting
		int x = (int) (v1.p.x * 0xffff);
		int y = (int) (v1.p.y * 0xffff);
		int r = (int) (255 * 0xffff * v1.color.x);
		int g = (int) (255 * 0xffff * v1.color.y);
		int b = (int) (255 * 0xffff * v1.color.z);
		double u = v1.uv.x;
		double v = v1.uv.y;

		// Scan
		for (int i = 0; i < l; i++, x += addx, y += addy, r += addr, g += addg, b += addb, u += addu, v += addv) {
			int py = y >> 16;
			int px = x >> 16;

			if (py < 0 || py >= BUFFERH) {
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

	public void setScale(final double newScale) {
		scale = newScale;
	}

	@Override
	public void mouseClicked(final MouseEvent e) {

	}

	@Override
	public void mouseEntered(final MouseEvent e) {

	}

	@Override
	public void mouseExited(final MouseEvent e) {

	}

	@Override
	public void mousePressed(final MouseEvent e) {
		preMousePoint = e.getPoint();
	}

	@Override
	public void mouseReleased(final MouseEvent e) {

	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		if (MouseUtility.isLeftButtonDown(e)) {
			rotateAngle -= (e.getX() - preMousePoint.getX()) / 100.0;
			preMousePoint = e.getPoint();
			updateAffineTransform();
			repaint();
		} else if (MouseUtility.isRightButtonDown(e)) {
			transX += (e.getX() - preMousePoint.getX()) / scale;
			transY += (e.getY() - preMousePoint.getY()) / scale;
			preMousePoint = e.getPoint();
			updateAffineTransform();
			repaint();
		}
	}

	@Override
	public void mouseMoved(final MouseEvent e) {

	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		double scale_ = (100.0 - e.getWheelRotation() * 5) / 100.0;
		scale *= scale_;
		updateAffineTransform();
		repaint();
	}

	public AffineTransform getAffineTransform() {
		return affineTransform;
	}

	public BufferedImage getBufferImage() {
		return bufferImage;
	}

	public boolean isFaceOrderFlipped() {
		return faceOrderFlip;
	}
}
