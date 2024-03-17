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
package oripa.gui.view.estimation;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.origeom.OverlapRelation;
import oripa.gui.view.View;
import oripa.renderer.estimation.DistortionMethod;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public interface EstimationResultUIView extends View {

	/**
	 * Set Model to be displayed and update index label
	 *
	 * @param foldedModel
	 *            {@code FoldedModel} to be displayed
	 */
	void setModel(FoldedModel foldedModel);

	FoldedModel getModel();

	OverlapRelation getOverlapRelation();

	int getOverlapRelationIndex();

	boolean isFaceOrderFlipped();

	boolean isFaceShade();

	boolean isDrawEdges();

	boolean isFillFace();

	boolean isUseColor();

	double getRotateAngle();

	double getEps();

	DistortionMethod getDistortionMethod();

	Vector2d getDistortionParameter();

	Map<OriVertex, Integer> getVertexDepths();

	double getSVGFaceStrokeWidth();

	void setSVGFaceStrokeWidth(double strokeWidth);

	double getSVGPrecreaseStrokeWidth();

	void setSVGPrecreaseStrokeWidth(double strokeWidth);

	Color getFrontColor();

	Color getBackColor();

	void addExportButtonListener(Runnable listener);

	void addSaveSVGConfigButtonListener(Runnable listener);

	/**
	 * @param listener
	 *            Output type: < index of subface, list< overlap relation
	 *            indices > >
	 */
	void setFilterInitializationListener(Function<FoldedModel, Map<Integer, List<Set<Integer>>>> listener);

	void showExportErrorMessage(Exception e);

	void showErrorMessage(Exception e);
}
