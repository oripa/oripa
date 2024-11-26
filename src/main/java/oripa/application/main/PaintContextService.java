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
package oripa.application.main;

import java.util.Collection;
import java.util.List;

import jakarta.inject.Inject;
import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.PaintContext;
import oripa.resource.Constants;
import oripa.value.OriLine;

/**
 * A service object to update {@link PaintContext} appropriately.
 *
 * @author OUCHI Koji
 *
 */
public class PaintContextService {
	private final PaintContext paintContext;
	private final CutModelOutlinesHolder cutModelOutlinesHolder;

	@Inject
	public PaintContextService(
			final PaintContext paintContext,
			final CutModelOutlinesHolder cutModelOutlinesHolder) {
		this.paintContext = paintContext;
		this.cutModelOutlinesHolder = cutModelOutlinesHolder;
	}

	/**
	 * Clears the context, sets the given crease pattern, and clears the undo
	 * history.
	 *
	 * @param creasePattern
	 * @param paintContext
	 */
	public void setCreasePatternToPaintContext(final CreasePattern creasePattern) {
		paintContext.clear(true);
		paintContext.setCreasePattern(creasePattern);
		paintContext.creasePatternUndo().clear();
		cutModelOutlinesHolder.setOutlines(List.of());
	}

	public void setToImportedLines(final Collection<OriLine> lines) {
		paintContext.getPainter().resetSelectedOriLines();

		lines.forEach(l -> l.setSelected(true));
		paintContext.SetImportedLines(lines);

	}

	public void clear() {
		setCreasePatternToPaintContext(
				new CreasePatternFactory().createCreasePattern(Constants.DEFAULT_PAPER_SIZE));
	}

	public boolean linesSelected() {
		return paintContext.countSelectedLines() > 0;
	}

	public void clearCreasePatternChanged() {
		paintContext.clearCreasePatternChanged();
	}

	public boolean creasePatternChangeExists() {
		return paintContext.creasePatternChangeExists();
	}

	public CreasePattern getCreasePattern() {
		return paintContext.getCreasePattern();
	}

}
