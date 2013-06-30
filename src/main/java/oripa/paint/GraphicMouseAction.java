package oripa.paint;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.vecmath.Vector2d;

import oripa.Config;
import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.paint.geometry.GeometricOperation;
import oripa.viewsetting.main.MainScreenSettingDB;
import oripa.viewsetting.main.ScreenUpdater;

public abstract class GraphicMouseAction {

	private EditMode editMode = EditMode.INPUT;
	private boolean needSelect = false;
	private ActionState state;

	protected void log(double x, double y){
		System.out.println(x + "," + y);

	}

	protected void log(Vector2d p){
		if(p == null){
			return;
		}
		log(p.x, p.y);
	}	

	protected final void setActionState(ActionState state){
		this.state = state;
	}

	protected final ActionState getActionState(){
		return state;
	}
	
	protected final boolean currentStateIs(Class<? extends ActionState> s){
		return state.equals(s);
	}


	public final boolean needSelect() {
		return needSelect;
	}

	protected final void setNeedSelect(boolean selectable) {
		this.needSelect = selectable;
	}

	protected final void setEditMode(EditMode mode){
		editMode = mode;
	}
	public final EditMode getEditMode(){
		return editMode;
	}





	/**
	 * define action on destroy.
	 * @param context
	 */
	public void destroy(PaintContext context){
		context.clear(false);
	}


	/**
	 * define action for recovering the status of this object 
	 * with given context.
	 * @param context
	 */
	public void recover(PaintContext context){
	}
	
	/**
	 * performs action.
	 * 
	 * @param context
	 * @param affine
	 * @param differentAction
	 * 
	 * @return Next mouse action. This class returns {@code this} object.
	 */
	public GraphicMouseAction onLeftClick(PaintContext context, 
			AffineTransform affine, boolean differentAction){
		Point2D.Double clickPoint = context.getLogicalMousePoint();

		doAction(context, clickPoint, differentAction);
		return this;
	}

	public void doAction(PaintContext context, Point2D.Double point, boolean differntAction){

		state = state.doAction(context, 
				point, differntAction);

		ScreenUpdater screenUpdater = ScreenUpdater.getInstance();
		screenUpdater.updateScreen();
	}

	/**
	 * undo action.
	 * @param context
	 * @param affine
	 * @param differentAction
	 */
	public void onRightClick(PaintContext context, AffineTransform affine,
			boolean differentAction) {

		undo(context);
	}

	public void undo(PaintContext context){
		state = BasicUndo.undo(state, context);
	}

	/**
	 * searches a vertex and a line close enough to the mouse cursor.
	 * The result is stored into context.pickCandidateL(and V).
	 * 
	 * @param context
	 * @param affine
	 * @param differentAction
	 * @return close vertex. null if not found.
	 */
	public Vector2d onMove(
			PaintContext context, AffineTransform affine, boolean differentAction) {


		setCandidateVertexOnMove(context, differentAction);
		setCandidateLineOnMove(context);

		return context.pickCandidateV;
	}	

	protected final void setCandidateVertexOnMove(
			PaintContext context, boolean differentAction) {
		context.pickCandidateV = GeometricOperation.pickVertex(
				context, differentAction);		
		
	}

	
	protected final void setCandidateLineOnMove(PaintContext context) {
		context.pickCandidateL = GeometricOperation.pickLine(
				context);		
	}
	
	public abstract void onPress(PaintContext context, AffineTransform affine, boolean differentAction);
	public abstract void onDrag(PaintContext context, AffineTransform affine, boolean differentAction);

	public abstract void onRelease(PaintContext context, AffineTransform affine, boolean differentAction);


	/**
	 * draws selected lines and selected vertices as selected state.
	 * Override for more drawing.
	 * 
	 * @param g2d
	 * @param context
	 */
	public void onDraw(Graphics2D g2d, PaintContext context){
		drawPickedLines(g2d, context);
		drawPickedVertices(g2d, context);

	}

	private void drawPickedLines(Graphics2D g2d, PaintContext context){
		for(int i = 0; i < context.getLineCount(); i++){
			g2d.setColor(Config.LINE_COLOR_PICKED);
			g2d.setStroke(Config.STROKE_PICKED);

			OriLine line = context.getLine(i);

			drawLine(g2d, line);
		}

	}


	private void drawPickedVertices(Graphics2D g2d, PaintContext context){
		ElementSelector selector = new ElementSelector();

		for(int i = 0; i < context.getVertexCount(); i++){
			g2d.setColor(selector.selectColorByLineType(Globals.inputLineType));

			Vector2d vertex = context.getVertex(i);
			drawVertex(g2d, context, vertex.x, vertex.y);
		}		
	}


	/**
	 * draw a picked vertex as an small rectangle at (x, y)
	 * @param g2d
	 * @param context
	 * @param x
	 * @param y
	 */
	protected void drawVertex(Graphics2D g2d, PaintContext context, 
			double x, double y){
		double scale = context.scale;
		g2d.fill(new Rectangle2D.Double(x - 5.0 / scale,
				y - 5.0 / scale, 10.0 / scale, 10.0 / scale));

	}

	protected void drawPickCandidateVertex(Graphics2D g2d, PaintContext context){
		if (context.pickCandidateV != null) {
			g2d.setColor(Config.LINE_COLOR_CANDIDATE);
			Vector2d candidate = context.pickCandidateV;
			drawVertex(g2d, context, candidate.x, candidate.y);
		}
	}

	protected void drawLine(Graphics2D g2d, OriLine line){
		g2d.draw(new Line2D.Double(line.p0.x, line.p0.y, 
				line.p1.x, line.p1.y));

	}

	protected void drawLine(Graphics2D g2d, Vector2d p0, Vector2d p1){
		g2d.draw(new Line2D.Double(p0.x, p0.y, 
				p1.x, p1.y));

	}

	protected void drawPickCandidateLine(Graphics2D g2d, PaintContext context){
		if (context.pickCandidateL!= null) {
			g2d.setColor(Config.LINE_COLOR_CANDIDATE);
			OriLine candidate = context.pickCandidateL;
			drawLine(g2d, candidate);
		}
	}


	/**
	 * draws the line between the most recently selected vertex and 
	 * the closest vertex sufficiently to the mouse cursor.
	 * if every vertex is far from cursor, this method uses the cursor point
	 * instead of close vertex.
	 * @param g2d
	 * @param context
	 */
	protected void drawTemporaryLine(Graphics2D g2d, PaintContext context){
		ElementSelector selector = new ElementSelector();

		if(context.getVertexCount() > 0){
			Vector2d picked = context.peekVertex();

			Color color = selector.selectColorByLineType(Globals.inputLineType);
			g2d.setColor(color);
			drawLine(g2d, picked, GeometricOperation.getCandidateVertex(context, true));
		}

	}




}
