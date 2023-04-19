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
package oripa.domain.fold;

import java.util.List;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OrigamiModelFactory;
import oripa.domain.fold.subface.FacesToCreasePatternConverter;
import oripa.domain.fold.subface.ParentFacesCollector;
import oripa.domain.fold.subface.SplitFacesToSubFacesConverter;
import oripa.domain.fold.subface.SubFace;
import oripa.domain.fold.subface.SubFacesFactory;

/**
 * @author OUCHI Koji
 *
 */
class SubfacesOneTimeFactory extends SubFacesFactory {

	private List<SubFace> subfaces = null;

	public SubfacesOneTimeFactory(
			final FacesToCreasePatternConverter facesToCPConverter,
			final OrigamiModelFactory modelFactory,
			final SplitFacesToSubFacesConverter facesToSubFacesConverter,
			final ParentFacesCollector parentCollector) {
		super(facesToCPConverter, modelFactory, facesToSubFacesConverter, parentCollector);
	}

	@Override
	public List<SubFace> createSubFaces(final List<OriFace> faces, final double paperSize, final double eps) {
		if (subfaces != null) {
			return subfaces;
		}

		subfaces = super.createSubFaces(faces, paperSize, eps);

		return subfaces;
	}
}
