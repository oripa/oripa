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
package oripa.persistent.entity.exporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.persistent.filetool.Exporter;
import oripa.persistent.svg.CreasePatternToSvgConverter;

import static oripa.persistent.svg.SVGUtils.*;

/**
 * @author OUCHI Koji
 *
 */
public class CreasePatternExporterSVG implements Exporter<CreasePatternInterface> {
	@Override
	public boolean export(final CreasePatternInterface creasePattern, final String filepath)
			throws IOException, IllegalArgumentException {

		double scale = SVG_SIZE / creasePattern.getPaperSize();
		CreasePatternToSvgConverter creasePatternToSvgConverter = new CreasePatternToSvgConverter(creasePattern, scale);

		try (var fw = new FileWriter(filepath);
				var bw = new BufferedWriter(fw)) {
			bw.write(SVG_START);
			bw.write(creasePatternToSvgConverter.getSvgCreasePattern());
			bw.write(SVG_END_TAG);
		}

		return true;
	}
}
