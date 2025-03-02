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
package oripa.domain.creasepattern;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.geom.RectangleDomain;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class OriLineClip implements Clippable<OriLine> {
	private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final int divNum = 16;
	private final double interval;
	private final double domainSize;

	private final double minX, minY;

	private final Set<OriLine>[][] areas;

	/**
	 * the index of divided paper area. A given point is converted to the index
	 * it should belongs to.
	 *
	 * @author Koji
	 *
	 */
	private class AreaPosition {
		public final int x, y;

		/**
		 * doubles point to index
		 */
		public AreaPosition(final double x, final double y) {
			this.x = toDiv(x, minX);
			this.y = toDiv(y, minY);
		}

		public AreaPosition(final int xDiv, final int yDiv) {
			x = xDiv;
			y = yDiv;
		}
	}

	/**
	 * Computes a index on one axis.
	 *
	 * @param p
	 * @return
	 */
	private int toDiv(final double p, final double p0) {
		int div = (int) ((p - p0) / interval);

		if (div < 0) {
			return 0;
		}

		if (div >= divNum) {
			return divNum - 1;
		}

		return div;
	}

	public static OriLineClip create(final Collection<OriLine> lines) {
		var clip = new OriLineClip(RectangleDomain.createFromSegments(lines));

		lines.forEach(line -> clip.add(line));

		return clip;
	}

	@SuppressWarnings("unchecked")
	public OriLineClip(final RectangleDomain domain) {
		this.domainSize = domain.maxWidthHeight();
		this.minX = domain.getLeft();
		this.minY = domain.getTop();

		this.interval = domainSize / divNum;

		areas = new Set[divNum][divNum];
		for (int i = 0; i < divNum; i++) {
			for (int j = 0; j < divNum; j++) {
				areas[i][j] = new HashSet<OriLine>();
			}
		}
	}

	public OriLineClip(final Collection<OriLine> lines) {
		this(RectangleDomain.createFromSegments(lines));

	}

	public void add(final OriLine line) {
		apply(line, (position, line_) -> areas[position.x][position.y].add(line));
	}

	public void remove(final OriLine line) {
		apply(line, (position, line_) -> areas[position.x][position.y].remove(line));
	}

	private void apply(final OriLine line, final BiConsumer<AreaPosition, OriLine> action) {
		var canonical = line.createCanonical();
		var x0 = canonical.getP0().getX();
		var y0 = canonical.getP0().getY();
		var x1 = canonical.getP1().getX();
		var y1 = canonical.getP1().getY();

		logger.trace("p0:{},{}", x0, y0);
		logger.trace("p1:{},{}", x1, y1);
		logger.trace("interval:{}", interval);
		var p0Div = new AreaPosition(x0, y0);
		var p1Div = new AreaPosition(x1, y1);

		var xDiv = p0Div.x;
		var prevYDiv = p0Div.y;

		logger.trace("p0Div:{},{}", p0Div.x, p0Div.y);
		logger.trace("p1Div:{},{}", p1Div.x, p1Div.y);

		if (p0Div.x == p1Div.x) {
			applyForYDivs(xDiv, p0Div.y, p1Div.y, line, action);
		} else {

			for (var nextXDiv = p0Div.x; nextXDiv <= p1Div.x; nextXDiv++) {
				var x = nextXDiv == p0Div.x ? x0 : (minX + nextXDiv * interval);
				var yDiv = toDiv(line.getAffineYValueAt(x), minY);

				applyForYDivs(xDiv, prevYDiv, yDiv, line, action);

				xDiv = nextXDiv;
				prevYDiv = yDiv;
			}

			applyForYDivs(xDiv, prevYDiv, p1Div.y, line, action);
		}
	}

	private void applyForYDivs(final int xDiv, final int prevYDiv, final int yDiv,
			final OriLine line,
			final BiConsumer<AreaPosition, OriLine> action) {
		if (prevYDiv < yDiv) {
			for (int yDiv_ = prevYDiv; yDiv_ <= yDiv; yDiv_++) {
				logger.trace("accept div:{},{}", xDiv, yDiv_);
				action.accept(new AreaPosition(xDiv, yDiv_), line);
			}
		} else {
			for (int yDiv_ = prevYDiv; yDiv_ >= yDiv; yDiv_--) {
				logger.trace("accept div:{},{}", xDiv, yDiv_);
				action.accept(new AreaPosition(xDiv, yDiv_), line);
			}
		}
	}

	/**
	 * O(k^2 n) for n lines in the domain.
	 *
	 * @param domain
	 * @return
	 */
	@Override
	public Collection<OriLine> clip(final RectangleDomain domain, final double pointEps) {
		var p0Div = new AreaPosition(domain.getLeft() - pointEps, domain.getTop() - pointEps);
		var p1Div = new AreaPosition(domain.getRight() + pointEps, domain.getBottom() + pointEps);

		var lines = new HashSet<OriLine>();

		for (var xDiv = p0Div.x; xDiv < p1Div.x; xDiv++) {
			for (var yDiv = p0Div.y; yDiv < p1Div.y; yDiv++) {
				logger.trace("get div:{},{}", xDiv, yDiv);
				lines.addAll(areas[xDiv][yDiv]);
			}
		}

		return lines;
	}

	public void clear() {
		for (int i = 0; i < divNum; i++) {
			for (int j = 0; j < divNum; j++) {
				areas[i][j].clear();
			}
		}
	}
}
