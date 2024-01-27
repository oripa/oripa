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
package oripa.gui.view.foldability;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

import oripa.domain.fold.halfedge.OriVertex;
import oripa.gui.view.ScreenView;
import oripa.gui.view.creasepattern.PaintComponentGraphics;

/**
 * @author OUCHI Koji
 *
 */
public interface FoldabilityScreenView extends ScreenView {

	void updateCenterOfPaper(double x, double y);

	double getScale();

	void setViolatingVertices(Collection<OriVertex> vertices);

	Optional<OriVertex> getPickedViolatingVertex();

//	void setViolatingVertices(Collection<OriVertex> vertices);
//
//	void setViolatingFaces(Collection<OriFace> faces);
//
//	void setOverlappingLines(Collection<OriLine> lines);

	public void setPaintComponentListener(final Consumer<PaintComponentGraphics> listener);
}