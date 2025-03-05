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

import oripa.domain.cptool.ElementRemover;
import oripa.domain.cptool.LineAdder;
import oripa.domain.cptool.OverlappingLineMerger;
import oripa.domain.cptool.PointsMerger;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.fold.halfedge.ModelType;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.halfedge.OrigamiModelFactory;
import oripa.domain.fold.subface.FacesToCreasePatternConverter;
import oripa.domain.fold.subface.ParentFacesCollector;
import oripa.domain.fold.subface.SplitFacesToSubFacesConverter;
import oripa.domain.fold.subface.SubFacesFactory;

/**
 * Creates the Folder instance.
 *
 * @author OUCHI Koji
 *
 */
public class FolderFactory {

	/**
	 *
	 * @param type
	 * @return Instance of {@link Folder}. Note that the instance for
	 *         {@link ModelType#UNASSIGNED} can be used repeatedly only for the
	 *         same {@link OrigamiModel} instance since the folder creates
	 *         subfaces only once to reduce computation time.
	 */
	public Folder create(final ModelType type) {
		return switch (type) {
		case ASSIGNED -> createAssigned();

		case UNASSIGNED -> createUnassigned();

		case ERROR_CONTAINING -> createErrorContaining();

		default -> throw new IllegalArgumentException();
		};
	}

	private Folder createAssigned() {
		var lineAdder = new LineAdder();
		var subfacesFactory = new SubFacesFactory(
				new FacesToCreasePatternConverter(
						new CreasePatternFactory(),
						lineAdder,
						new ElementRemover(),
						new PointsMerger(lineAdder),
						new OverlappingLineMerger()),
				new OrigamiModelFactory(),
				new SplitFacesToSubFacesConverter(),
				new ParentFacesCollector());

		return new AssignedModelFolder(
				new SimpleFolder(),
				new LayerOrderEnumerator(subfacesFactory, true));
	}

	private Folder createUnassigned() {
		var lineAdder = new LineAdder();
		var subfacesFactory = new SubfacesOneTimeFactory(
				new FacesToCreasePatternConverter(
						new CreasePatternFactory(),
						lineAdder,
						new ElementRemover(),
						new PointsMerger(lineAdder),
						new OverlappingLineMerger()),
				new OrigamiModelFactory(),
				new SplitFacesToSubFacesConverter(),
				new ParentFacesCollector());

		return new UnassignedModelFolder(
				new SimpleFolder(),
				new LayerOrderEnumerator(subfacesFactory, false));
	}

	private Folder createErrorContaining() {
		return new ErrorAllowedFolder(new SimpleFolder());
	}

}
