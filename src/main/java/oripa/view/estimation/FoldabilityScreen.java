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

package oripa.view.estimation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.vecmath.Vector2d;

import oripa.Config;
import oripa.domain.fold.OriFace;
import oripa.domain.fold.OriVertex;
import oripa.domain.fold.OrigamiModel;
import oripa.domain.fold.rule.FoldabilityChecker;
import oripa.domain.paint.CreasePatternGraphicDrawer;
import oripa.value.OriLine;

/**
 * A screen to show whether Maekawa theorem and Kawasaki theorem holds.
 * 
 * @author Koji
 *
 */
public class FoldabilityScreen extends JPanel
		implements ComponentListener {

	private final boolean bDrawFaceID = false;
	private Image bufferImage;
	private Graphics2D bufferg;
	private final double scale;
	private double transX;
	private double transY;

	// Affine transformation information
	private Dimension preSize;
	private final AffineTransform affineTransform = new AffineTransform();
	private final ArrayList<Vector2d> crossPoints = new ArrayList<>();
	private final JPopupMenu popup = new JPopupMenu();
	private final JMenuItem popupItem_DivideFace = new JMenuItem("Face division");
	private final JMenuItem popupItem_FlipFace = new JMenuItem("Face Inversion");

	private OrigamiModel origamiModel = null;
	private Collection<OriLine> creasePattern = null;
	// private FoldedModelInfo foldedModelInfo = null;

	FoldabilityScreen() {

		addComponentListener(this);

		scale = 1.5;
		setBackground(Color.white);

		popup.add(popupItem_DivideFace);
		popup.add(popupItem_FlipFace);
		preSize = getSize();
	}

	private Collection<OriVertex> violatingVertices = new ArrayList<>();
	private Collection<OriFace> violatingFaces = new ArrayList<>();

	public void showModel(
			final OrigamiModel origamiModel,
			final Collection<OriLine> creasePattern // , FoldedModelInfo
													// foldedModelInfo
	) {
		this.origamiModel = origamiModel;
		this.creasePattern = creasePattern;
		// this.foldedModelInfo = foldedModelInfo;

		FoldabilityChecker foldabilityChecker = new FoldabilityChecker();
		violatingVertices = foldabilityChecker.findViolatingVertices(
				origamiModel.getVertices());

		violatingFaces = foldabilityChecker.findViolatingFaces(
				origamiModel.getFaces());

		this.setVisible(true);
	}

	public void drawFoldablity(final Graphics2D g2d) {
		if (origamiModel == null) {
			return;
		}

		List<OriFace> faces = origamiModel.getFaces();
		List<OriVertex> vertices = origamiModel.getVertices();

		for (OriFace face : faces) {
			g2d.setColor(new Color(255, 210, 210));
			g2d.fill(face.preOutline);
		}

		g2d.setColor(Color.RED);
		for (OriVertex v : violatingVertices) {
			g2d.fill(new Rectangle2D.Double(v.preP.x - 8.0 / scale,
					v.preP.y - 8.0 / scale, 16.0 / scale, 16.0 / scale));
		}

		if (bDrawFaceID) {
			g2d.setColor(Color.BLACK);
			for (OriFace face : faces) {
				g2d.drawString("" + face.tmpInt, (int) face.getCenter().x,
						(int) face.getCenter().y);
			}
		}

		if (Config.FOR_STUDY) {
			paintForStudy(g2d, faces, vertices);
		}
	}

	private void paintForStudy(final Graphics2D g2d, final Collection<OriFace> faces,
			final Collection<OriVertex> vertices) {
		g2d.setColor(new Color(255, 210, 220));
		for (OriFace face : faces) {
			if (face.tmpInt2 == 0) {
				g2d.setColor(Color.RED);
				g2d.fill(face.preOutline);
			} else {
				g2d.setColor(face.color);
			}

			if (violatingFaces.contains(face)) {
				g2d.setColor(Color.RED);
			} else {
				if (face.faceFront) {
					g2d.setColor(new Color(255, 200, 200));
				} else {
					g2d.setColor(new Color(200, 200, 255));
				}
			}

			g2d.fill(face.preOutline);
		}

		g2d.setColor(Color.BLACK);
		for (OriFace face : faces) {
			g2d.drawString("" + face.z_order, (int) face.getCenter().x,
					(int) face.getCenter().y);
		}

	}

	// To update the current AffineTransform
	private void updateAffineTransform() {
		affineTransform.setToIdentity();
		affineTransform.translate(getWidth() * 0.5, getHeight() * 0.5);
		affineTransform.scale(scale, scale);
		affineTransform.translate(transX, transY);
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);

		if (bufferImage == null) {
			bufferImage = createImage(getWidth(), getHeight());
			bufferg = (Graphics2D) bufferImage.getGraphics();
			updateAffineTransform();
			preSize = getSize();
		}

		bufferg.setTransform(new AffineTransform());

		bufferg.setColor(Color.WHITE);
		bufferg.fillRect(0, 0, getWidth(), getHeight());

		bufferg.setTransform(affineTransform);

		Graphics2D g2d = bufferg;

		CreasePatternGraphicDrawer drawer = new CreasePatternGraphicDrawer();
		drawer.drawAllLines(g2d, creasePattern);
		drawer.drawCreaseVertices(g2d, creasePattern, scale);

		for (Vector2d v : crossPoints) {
			g2d.setColor(Color.RED);
			g2d.fill(new Rectangle2D.Double(v.x - 5.0 / scale, v.y - 5.0 / scale, 10.0 / scale,
					10.0 / scale));
		}

		// Line connecting the pair of unsetled faces
		// if (Config.FOR_STUDY) {
		// List<OriFace> faces = origamiModel.getFaces();
		//
		// int[][] overlapRelation = foldedModelInfo.getOverlapRelation();
		//
		// if (overlapRelation != null) {
		// g2d.setStroke(LineSetting.STROKE_RIDGE);
		// g2d.setColor(Color.MAGENTA);
		// int size = faces.size();
		// for (int i = 0; i < size; i++) {
		// for (int j = i + 1; j < size; j++) {
		// if (overlapRelation[i][j] == Doc.UNDEFINED) {
		// Vector2d v0 = faces.get(i).getCenter();
		// Vector2d v1 = faces.get(j).getCenter();
		// g2d.draw(new Line2D.Double(v0.x, v0.y, v1.x, v1.y));
		//
		// }
		// }
		// }
		// }
		// }

		drawFoldablity(g2d);

		g.drawImage(bufferImage, 0, 0, this);
	}

	@Override
	public void componentResized(final ComponentEvent arg0) {
		if (getWidth() <= 0 || getHeight() <= 0) {
			return;
		}
		preSize = getSize();

		// Update of the logical coordinates of the center of the screen
		transX = transX - preSize.width * 0.5 + getWidth() * 0.5;
		transY = transY - preSize.height * 0.5 + getHeight() * 0.5;

		// Updating the image buffer
		bufferImage = createImage(getWidth(), getHeight());
		bufferg = (Graphics2D) bufferImage.getGraphics();

		updateAffineTransform();
		repaint();

	}

	@Override
	public void componentMoved(final ComponentEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void componentShown(final ComponentEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void componentHidden(final ComponentEvent arg0) {
		// TODO Auto-generated method stub
	}
}
