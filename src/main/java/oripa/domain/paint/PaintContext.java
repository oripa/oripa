package oripa.domain.paint;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import oripa.domain.cptool.Painter;
import oripa.geom.Line;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * This interface holds current state of GUI interaction for paint inputting.
 *
 * @author Koji
 *
 */
public interface PaintContext extends CreasePatternHolder {
    boolean isTriangularGridMode();

    void setTriangularGridMode(boolean enabled);

    // =================================================================================
    // Properties used by action state
    // =================================================================================

    // ---------------------------------------------------------------
    // State of input instruction

    /**
     *
     * @return true if user is trying to paste selected lines
     */
    boolean isPasting();

    /**
     * notify the painting algorithm that the user started pasting.
     */
    void startPasting();

    /**
     * notify the painting algorithm that the user finished pasting.
     */
    void finishPasting();

    // ---------------------------------------------------------------
    // Values Picked by User

    /**
     * remove all lines and all vertices in this context.
     *
     * @param unselect
     *            true if the removed lines should be marked as unselected.
     */
    void clear(boolean unselect);

    /**
     *
     * @return unmodifiable list of lines which user picked.
     */
    List<OriLine> getPickedLines();

    /**
     *
     * @return unmodifiable list of vertices which user picked.
     */
    List<Vector2d> getPickedVertices();

    /**
     *
     * @param index
     * @return a line at specified position in the order of user selection
     */
    OriLine getLine(int index);

    /**
     *
     * @param index
     * @return a vertex at specified position in the order of user selection
     */
    Vector2d getVertex(int index);

    /**
     *
     * @param picked
     *            line to be stored as the latest
     */
    void pushLine(OriLine picked);

    /**
     * pop the last pushed line and mark it unselected.
     *
     * @return popped line. empty if no line is pushed.
     */
    Optional<OriLine> popLine();

    /**
     *
     * @param picked
     *            vertex to be stored as the latest
     */
    void pushVertex(Vector2d picked);

    /**
     * pop the last pushed vertex.
     *
     * @return popped vertex. empty if no vertex is pushed.
     */
    Optional<Vector2d> popVertex();

    /**
     * performs the same as {@link List#remove(Object o)}.
     *
     * @param line
     * @return
     */
    boolean removeLine(OriLine line);

    /**
     *
     * @return the latest vertex
     */
    Optional<Vector2d> peekVertex();

    /**
     *
     * @return the latest line
     */
    Optional<OriLine> peekLine();

    /**
     *
     * @return count of lines in this context
     */
    int getLineCount();

    /**
     *
     * @return count of vertices in this context
     */
    int getVertexCount();

    // ---------------------------------------------------------------
    // Misc

    Painter getPainter();

    void setLineTypeOfNewLines(OriLine.Type lineType);

    OriLine.Type getLineTypeOfNewLines();

    void setCandidateLineToPick(OriLine pickCandidateL);

    Optional<OriLine> getCandidateLineToPick();

    void setCandidateVertexToPick(Vector2d pickCandidateV);

    Optional<Vector2d> getCandidateVertexToPick();

    void setSolutionLineToPick(Line solutionLine);

    Optional<Line> getSolutionLineToPick();

    CreasePatternUndoer creasePatternUndo();

    void refreshCreasePattern();

    void setAngleStep(AngleStep step);

    AngleStep getAngleStep();

    void setSolutionLines(Collection<Line> lines);

    /**
     * Returns unmodifiable collection.
     *
     * @return
     */
    Collection<Line> getSolutionLines();

    /**
     * Makes the solution lines empty.
     */
    void clearSolutionLines();

    void setSnapPoints(Collection<Vector2d> points);

    /**
     * Returns unmodifiable collection.
     *
     * @return
     */
    Collection<Vector2d> getSnapPoints();

    /**
     * Makes the snap points empty.
     */
    void clearSnapPoints();

    /**
     * Keeps the lines in a temporary place.
     *
     * @param lines
     */
    void SetImportedLines(Collection<OriLine> lines);

    /**
     * Adds all imported lines to picked lines.
     *
     * @param lines
     */
    void loadFromImportedLines();

    /**
     * Makes the imported lines empty.
     */
    void clearImportedLines();

    /**
     * sets division number of grid. should update grid points for
     * {@link #getGrids()}.
     *
     * @param divNum
     */
    void setGridDivNum(int divNum);

    int getGridDivNum();

    void updateGrids();

    /**
     * Returns unmodifiable list.
     *
     * @return
     */
    Collection<Vector2d> getGrids();

    void clearGrids();

    void setCircleCopyParameter(CircleCopyParameter p);

    CircleCopyParameter getCircleCopyParameter();

    void setArrayCopyParameter(ArrayCopyParameter p);

    ArrayCopyParameter getArrayCopyParameter();

    default int countSelectedLines() {
        return getPainter().countSelectedLines();
    }

    default boolean creasePatternChangeExists() {
        return creasePatternUndo().changeExists();
    }

    default void clearCreasePatternChanged() {
        creasePatternUndo().clearChanged();
    }

    public double getPointEps();
}