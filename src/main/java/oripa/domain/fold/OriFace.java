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

package oripa.domain.fold;

import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import oripa.domain.fold.rule.Condition3;
import oripa.domain.fold.rule.Condition4;
import oripa.value.OriLine;

public class OriFace {

	public ArrayList<OriHalfedge> halfedges = new ArrayList<>();

	// FIXME: GeneralPath is legacy.
	public GeneralPath outline = new GeneralPath();

	/**
	 * For drawing foldability-check face
	 */
	// FIXME: GeneralPath is legacy.
	public GeneralPath preOutline = new GeneralPath();

	public ArrayList<OriLine> precreases = new ArrayList<>();

	public boolean selected = false;
	public boolean faceFront = true;
	public Color color;
	public boolean tmpFlg = false;
	public int z_order = 0;
	public int tmpInt2 = 0;
	public int tmpInt = 0;
	public boolean hasProblem = false; // TODO delete this variable and use
										// bucket approach using ConjunctionLoop
	public boolean alreadyStacked = false;
	public ArrayList<TriangleFace> triangles = new ArrayList<>();
	public int intColor;
	public ArrayList<Condition4> condition4s = new ArrayList<>();
	public ArrayList<Condition3> condition3s = new ArrayList<>();
	public ArrayList<Integer> condition2s = new ArrayList<>();

	public OriFace() {
		int r = (int) (Math.random() * 255);
		int g = (int) (Math.random() * 255);
		int b = (int) (Math.random() * 255);
		color = new Color(r, g, b);
	}

	public void trianglateAndSetColor(final boolean bUseColor, final boolean bFlip,
			final double paperSize) {
		triangles.clear();

		double min_x = Double.MAX_VALUE;
		double max_x = -Double.MAX_VALUE;
		double min_y = Double.MAX_VALUE;
		double max_y = -Double.MAX_VALUE;

		for (OriHalfedge he : halfedges) {
			min_x = Math.min(min_x, he.vertex.p.x);
			max_x = Math.max(max_x, he.vertex.p.x);
			min_y = Math.min(min_y, he.vertex.p.y);
			max_y = Math.max(max_y, he.vertex.p.y);
		}

		double faceWidth = Math.sqrt((max_x - min_x) * (max_x - min_x)
				+ (max_y - min_y) * (max_y - min_y));

		for (OriHalfedge he : halfedges) {
			double val = 0;
			if (he.edge.type == OriLine.Type.RIDGE.toInt()) {
				val += 1;
			} else if (he.edge.type == OriLine.Type.VALLEY.toInt()) {
				val -= 1;
			}

			if (he.prev.edge.type == OriLine.Type.RIDGE.toInt()) {
				val += 1;
			} else if (he.prev.edge.type == OriLine.Type.VALLEY.toInt()) {
				val -= 1;
			}

			double vv = (val + 2) / 4.0;
			double v = (0.75 + vv * 0.25);

			v *= 0.9 + 0.15 * (Math.sqrt((he.vertex.p.x - min_x)
					* (he.vertex.p.x - min_x)
					+ (he.vertex.p.y - min_y)
							* (he.vertex.p.y - min_y))
					/ faceWidth);

			v = Math.min(1, v);

			if (bUseColor) {
				if (true) {
					if (faceFront ^ bFlip) {
						he.vertexColor.set(v * 0.7, v * 0.7, v);
					} else {
						he.vertexColor.set(v, v * 0.8, v * 0.7);
					}
//				} else {
//					if (faceFront ^ bFlip) {
//						he.vertexColor.set(v, v * 0.6, v * 0.6);
//					} else {
//						he.vertexColor.set(v, v, v * 0.95);
//					}
//
				}
			} else {
				he.vertexColor.set(v, v, v * 0.95);
			}
		}

		int heNum = halfedges.size();
		OriHalfedge startHe = halfedges.get(0);
		for (int i = 1; i < heNum - 1; i++) {
			TriangleFace tri = new TriangleFace(this);
			tri.v[0].p = new Vector2d(startHe.vertex.p);
			tri.v[1].p = new Vector2d(halfedges.get(i).vertex.p);
			tri.v[2].p = new Vector2d(halfedges.get(i + 1).vertex.p);

			tri.v[0].color = new Vector3d(startHe.vertexColor);
			tri.v[1].color = new Vector3d(halfedges.get(i).vertexColor);
			tri.v[2].color = new Vector3d(halfedges.get(i + 1).vertexColor);

			tri.v[0].uv = new Vector2d(startHe.vertex.preP.x / paperSize
					+ 0.5, startHe.vertex.preP.y / paperSize + 0.5);
			tri.v[1].uv = new Vector2d(halfedges.get(i).vertex.preP.x
					/ paperSize + 0.5,
					halfedges.get(i).vertex.preP.y
							/ paperSize + 0.5);
			tri.v[2].uv = new Vector2d(halfedges.get(i + 1).vertex.preP.x
					/ paperSize + 0.5,
					halfedges.get(i + 1).vertex.preP.y
							/ paperSize + 0.5);
			triangles.add(tri);
		}
	}

	public void makeHalfedgeLoop() {
		int heNum = halfedges.size();
		for (int i = 0; i < heNum; i++) {
			OriHalfedge pre_he = halfedges.get((i - 1 + heNum) % heNum);
			OriHalfedge he = halfedges.get(i);
			OriHalfedge nxt_he = halfedges.get((i + 1) % heNum);

			he.next = nxt_he;
			he.prev = pre_he;
		}
	}

	public void printInfo() {
		System.out.println("OriFace");
		for (OriHalfedge he : halfedges) {
			System.out.println(he.vertex.p);
		}
	}

	public void setOutline() {
		outline.reset();
		outline.moveTo((float) (halfedges.get(0).positionForDisplay.x),
				(float) (halfedges.get(0).positionForDisplay.y));
		for (int i = 1; i < halfedges.size(); i++) {
			outline.lineTo((float) (halfedges.get(i).positionForDisplay.x),
					(float) (halfedges.get(i).positionForDisplay.y));
		}
		outline.closePath();
	}

	public void setPreOutline() {
		preOutline.reset();
		Vector2d centerP = new Vector2d();
		for (OriHalfedge he : halfedges) {
			centerP.add(he.vertex.preP);
		}
		centerP.scale(1.0 / halfedges.size());
		double rate = 0.5;

		preOutline.moveTo(
				(float) (halfedges.get(0).vertex.preP.x * rate + centerP.x
						* (1.0 - rate)),
				(float) (halfedges.get(0).vertex.preP.y * rate + centerP.y
						* (1.0 - rate)));
		for (int i = 1; i < halfedges.size(); i++) {
			preOutline.lineTo(
					(float) (halfedges.get(i).vertex.preP.x * rate + centerP.x
							* (1.0 - rate)),
					(float) (halfedges.get(i).vertex.preP.y * rate + centerP.y
							* (1.0 - rate)));
		}
		preOutline.closePath();
	}

	public Vector2d getCenter() {
		Vector2d centerVec = new Vector2d();
		for (OriHalfedge he : halfedges) {
			centerVec.add(he.vertex.preP);
		}
		centerVec.scale(1.0 / halfedges.size());
		return centerVec;
	}
}
