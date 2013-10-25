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
package oripa.paint;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.paint.core.LineSetting;
import oripa.paint.core.PaintConfig;
import oripa.paint.util.ElementSelector;
import oripa.value.OriLine;

/**
 * This class provides a drawing method for crease pattern and some utilities.
 * @author Koji
 *
 */
public class CreasePatternGraphicDrawer {

	/**
	 * draws crease pattern according to the context of user interaction.
	 * 
	 * @param g2d
	 * @param context
	 * @param creasePattern
	 */
	public void draw(
			Graphics2D g2d,
			PaintContextInterface context, CreasePatternInterface creasePattern) {

		if (context.isGridVisible()) {

			drawGridLines(g2d, creasePattern.getPaperSize());
		}

		//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


		drawLines(g2d, creasePattern, null);


		// Drawing of the vertices
		if (PaintConfig.getMouseAction().getEditMode() == EditMode.VERTEX 
				|| PaintConfig.dispVertex) {
			drawVertexRectangles(g2d, creasePattern, context.getScale());
		}

	}
	/**
	 * draws given lines
	 * @param g2d
	 * @param lines
	 * @param pickedLines null if you don't wanna show selections.
	 */
	public void drawLines(
			Graphics2D g2d,
			Collection<OriLine> lines, Collection<OriLine> pickedLines) {

		ElementSelector selector = new ElementSelector();
		for (OriLine line : lines) {
			if (line.typeVal == OriLine.TYPE_NONE &&!PaintConfig.dispAuxLines) {
				continue;
			}

			if ((line.typeVal == OriLine.TYPE_RIDGE || line.typeVal == OriLine.TYPE_VALLEY)
					&& !PaintConfig.dispMVLines) {
				continue;
			}

			g2d.setColor(selector.selectColorByLineType(line.typeVal));
			g2d.setStroke(selector.selectStroke(line.typeVal));

			
			if(pickedLines == null || pickedLines.contains(line) == false){
				g2d.draw(new Line2D.Double(line.p0.x, line.p0.y, line.p1.x, line.p1.y));
			}

		}

	}

	public void drawVertexRectangles(
			Graphics2D g2d, Collection<OriLine> creasePattern, double scale){

		g2d.setColor(Color.BLACK);
		final double vertexDrawSize = 2.0;
		for (OriLine line : creasePattern) {
			if (!PaintConfig.dispAuxLines && line.typeVal == OriLine.TYPE_NONE) {
				continue;
			}
			if (!PaintConfig.dispMVLines && (line.typeVal == OriLine.TYPE_RIDGE
					|| line.typeVal == OriLine.TYPE_VALLEY)) {
				continue;
			}
			Vector2d v0 = line.p0;
			Vector2d v1 = line.p1;

			g2d.fill(new Rectangle2D.Double(v0.x - vertexDrawSize / scale,
					v0.y - vertexDrawSize / scale, vertexDrawSize * 2 / scale,
					vertexDrawSize * 2 / scale));
			g2d.fill(new Rectangle2D.Double(v1.x - vertexDrawSize / scale,
					v1.y - vertexDrawSize / scale, vertexDrawSize * 2 / scale,
					vertexDrawSize * 2 / scale));
		}

	}

	
	public void drawCandidatePositionString(Graphics2D g, Vector2d candidate){
		if(candidate != null){
			g.setColor(Color.BLACK);
			g.drawString("(" + candidate.x + 
					"," + candidate.y + ")", 0, 10);
		}	

	}

	public void drawGridLines(Graphics2D g2d, double paperSize) {
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.setStroke(LineSetting.STROKE_GRID);
		
		int lineNum = PaintConfig.gridDivNum;
		double step = paperSize / lineNum;

		for (int i = 1; i < lineNum; i++) {
			g2d.draw(new Line2D.Double(
					step * i - paperSize / 2.0, -paperSize / 2.0,
					step * i - paperSize / 2.0, paperSize / 2.0));

			g2d.draw(new Line2D.Double(
					-paperSize / 2.0, step * i - paperSize / 2.0,
					paperSize / 2.0, step * i - paperSize / 2.0));
		}
	}

}
