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
package oripa.domain.paint.byvalue;

import oripa.domain.paint.PaintContext;
import oripa.geom.GeomUtil;
import oripa.util.Command;

/**
 * @author OUCHI Koji
 *
 */
public class LengthMeasureCommand implements Command {
	private final PaintContext context;
	private final ValueSetting valueSetting;

	public LengthMeasureCommand(final PaintContext context, final ValueSetting valueSetting) {
		this.context = context;
		this.valueSetting = valueSetting;
	}

	@Override
	public void execute() {
		double length = GeomUtil.distance(
				context.getVertex(0), context.getVertex(1));

		valueSetting.setLength(length);

		context.clear(false);
	}

}
