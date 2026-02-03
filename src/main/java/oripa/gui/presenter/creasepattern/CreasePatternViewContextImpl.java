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
package oripa.gui.presenter.creasepattern;

import oripa.gui.view.main.InitialVisibilities;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
class CreasePatternViewContextImpl implements CreasePatternViewContext {

    private Vector2d logicalPoint = new Vector2d(0, 0);

    private double scale;

    private boolean gridVisible = InitialVisibilities.GRID;

    private boolean vertexVisible = InitialVisibilities.VERTEX;
    private boolean mvLineVisible = InitialVisibilities.MVU;
    private boolean auxLineVisible = InitialVisibilities.AUX;
    private boolean crossLineVisible = InitialVisibilities.CROSS;
    private boolean zeroLineWidth = InitialVisibilities.ZERO_LINE_WIDTH;

    public CreasePatternViewContextImpl() {
    }

    @Override
    public void setLogicalMousePoint(final Vector2d logicalPoint) {
        this.logicalPoint = logicalPoint;
    }

    @Override
    public Vector2d getLogicalMousePoint() {
        return logicalPoint;
    }

    /**
     * @return scale
     */
    @Override
    public double getScale() {
        return scale;
    }

    /**
     * @param scale
     *            Sets scale
     */
    @Override
    public void setScale(final double scale) {
        this.scale = scale;
    }

    @Override
    public void setMVLineVisible(final boolean visible) {
        mvLineVisible = visible;
    }

    @Override
    public boolean isMVLineVisible() {
        return mvLineVisible;
    }

    @Override
    public boolean isVertexVisible() {
        return vertexVisible;
    }

    @Override
    public void setVertexVisible(final boolean visible) {
        vertexVisible = visible;
    }

    @Override
    public void setAuxLineVisible(final boolean visible) {
        auxLineVisible = visible;
    }

    @Override
    public boolean isAuxLineVisible() {
        return auxLineVisible;
    }

    /**
     * @return whether grid is visible or not.
     */
    @Override
    public boolean isGridVisible() {
        return gridVisible;
    }

    /**
     * @param gridVisible
     *            Sets gridVisible
     */
    @Override
    public void setGridVisible(final boolean gridVisible) {
        this.gridVisible = gridVisible;
    }

    @Override
    public void setCrossLineVisible(final boolean visible) {
        crossLineVisible = visible;
    }

    @Override
    public boolean isCrossLineVisible() {
        return crossLineVisible;
    }

    @Override
    public boolean isZeroLineWidth() {
        return zeroLineWidth;
    }

    @Override
    public void setZeroLineWidth(final boolean zeroLineWidth) {
        this.zeroLineWidth = zeroLineWidth;
    }
}
