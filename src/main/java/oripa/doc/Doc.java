/**
 * ORIPA - Origami Pattern Editor 
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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

package oripa.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.fold.FoldedModelInfo;
import oripa.fold.OrigamiModel;
import oripa.paint.creasepattern.CreasePattern;
import oripa.paint.creasepattern.tool.LineAdder;
import oripa.resource.Constants;
import oripa.sheetcut.SheetCutOutlineFactory;
import oripa.value.OriLine;


public class Doc {

	
	private double paperSize;

	// Crease Pattern

	private CreasePattern creasePattern = null;
	private List<OriLine> sheetCutLines = new ArrayList<OriLine>();


	// Origami Model for Estimation
	private OrigamiModel origamiModel = null;

		
	// Folded Model Information (Result of Estimation)

	private FoldedModelInfo foldedModelInfo = null;
	

	final public static int NO_OVERLAP = 0;
	final public static int UPPER = 1;
	final public static int LOWER = 2;
	final public static int UNDEFINED = 9;

	// Project data

	private String dataFilePath = "";
	private String title;
	private String editorName;
	private String originalAuthorName;
	private String reference;
	public String memo;
	private UndoManager<UndoInfo> undoManager = new UndoManager<>(30);



	int debugCount = 0;


	public Doc(){
		initialize(Constants.DEFAULT_PAPER_SIZE);
	}   

	public Doc(double size) {
		initialize(size);
	}

	private void initialize(double size){

		this.paperSize = size;
		creasePattern = new CreasePattern(size);

		// FIXME move to factory
		OriLine l0 = new OriLine(-size / 2.0, -size / 2.0, size / 2.0, -size / 2.0, OriLine.TYPE_CUT);
		OriLine l1 = new OriLine(size / 2.0, -size / 2.0, size / 2.0, size / 2.0, OriLine.TYPE_CUT);
		OriLine l2 = new OriLine(size / 2.0, size / 2.0, -size / 2.0, size / 2.0, OriLine.TYPE_CUT);
		OriLine l3 = new OriLine(-size / 2.0, size / 2.0, -size / 2.0, -size / 2.0, OriLine.TYPE_CUT);
		creasePattern.add(l0);
		creasePattern.add(l1);
		creasePattern.add(l2);
		creasePattern.add(l3);

		
		origamiModel  = new OrigamiModel(size);
		foldedModelInfo = new FoldedModelInfo();
	}

	public void setDataFilePath(String path){
		this.dataFilePath = path;
	}

	public String getDataFilePath(){
		return dataFilePath;
	}

	public String getDataFileName(){
		File file = new File(ORIPA.doc.dataFilePath);
		String fileName = file.getName();

		return fileName;

	}



	public UndoInfo createUndoInfo(){
		UndoInfo undoInfo = new UndoInfo(creasePattern);
		return undoInfo;
	}

	public void cacheUndoInfo(){
		undoManager.setCache(createUndoInfo());
	}

	public void pushCachedUndoInfo(){
		undoManager.pushCachedInfo();
	}

	public void pushUndoInfo() {
		UndoInfo ui = new UndoInfo(creasePattern);
		undoManager.push(ui);
	}

	public void pushUndoInfo(UndoInfo uinfo){
		undoManager.push(uinfo);
	}

	public void loadUndoInfo() {
		UndoInfo info = undoManager.pop();

		if(info == null){
			return;
		}

		creasePattern.clear();
		creasePattern.addAll(info.getLines());
	}

	public boolean canUndo(){
		return undoManager.canUndo();
	}

	public boolean isChanged(){
		return undoManager.isChanged();
	}

	public void clearChanged(){
		undoManager.clearChanged();
	}




	public void addLine(OriLine inputLine) {
		LineAdder lineAdder = new LineAdder();
		
		lineAdder.addLine(inputLine, creasePattern);		
	}


	/**
	 * make lines that composes the outline of a shape
	 * obtained by cutting the folded model.
	 * @param scissorLine
	 */
	public void updateSheetCutOutlines(OriLine scissorLine) {
		SheetCutOutlineFactory factory = new SheetCutOutlineFactory();

		sheetCutLines.clear();
		sheetCutLines.addAll(
				factory.createLines(scissorLine, origamiModel));
	}



		
	public Collection<Vector2d> getVerticesAround(Vector2d v){
		return creasePattern.getVerticesAround(v);
	}
	
	public Collection<Collection<Vector2d>> getVerticesArea(
			double x, double y, double distance){
		
		return creasePattern.getVerticesArea(x, y, distance);
	}
	
	public CreasePattern getCreasePattern(){
		return creasePattern;
	}

	/**
	 * @return origamiModel
	 */
	public OrigamiModel getOrigamiModel() {
		return origamiModel;
	}
	
	/**
	 * @param origamiModel origamiModel is set to this instance.
	 */
	public void setOrigamiModel(OrigamiModel origamiModel) {
		this.origamiModel = origamiModel;
	}
	
	

	/**
	 * @return foldedModelInfo
	 */
	public FoldedModelInfo getFoldedModelInfo() {
		return foldedModelInfo;
	}

	/**
	 * @param foldedModelInfo foldedModelInfo is set to this instance.
	 */
	public void setFoldedModelInfo(FoldedModelInfo foldedModelInfo) {
		this.foldedModelInfo = foldedModelInfo;
	}

	//======================================================================
	// Getter/Setter eventually unnecessary
	

	/**
	 * @return crossLines
	 */
	public List<OriLine> getSheetCutOutlines() {
		return sheetCutLines;
	}

//	/**
//	 * @param crossLines crossLines is set to this instance.
//	 */
//	public void setCrossLines(List<OriLine> sheetCutOutlines) {
//		this.sheetCutLines = sheetCutOutlines;
//	}



	/**
	 * @param size size is set to this instance.
	 */
	public void setPaperSize(double size) {
		this.paperSize = size;
		origamiModel.setPaperSize(size);
		creasePattern.changePaperSize(size);
	}
	/**
	 * @return size
	 */
	public double getPaperSize() {
		return paperSize;
	}

	/**
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title title is set to this instance.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return editorName
	 */
	public String getEditorName() {
		return editorName;
	}

	/**
	 * @param editorName editorName is set to this instance.
	 */
	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}

	/**
	 * @return originalAuthorName
	 */
	public String getOriginalAuthorName() {
		return originalAuthorName;
	}

	/**
	 * @param originalAuthorName originalAuthorName is set to this instance.
	 */
	public void setOriginalAuthorName(String originalAuthorName) {
		this.originalAuthorName = originalAuthorName;
	}

	/**
	 * @return memo
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * @param memo memo is set to this instance.
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * @return reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference reference is set to this instance.
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	
	
	
}
