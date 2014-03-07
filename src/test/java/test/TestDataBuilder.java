package test;

import oripa.domain.cptool.Painter;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.persistent.doc.Doc;
import oripa.persistent.doc.exporter.DocExporter;
import oripa.persistent.doc.exporter.ExporterXML;
import oripa.value.OriLine;

public class TestDataBuilder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		final int SIZE = 400;
		final int TOP = - SIZE / 2;
		final int BOTTOM = -TOP;
		final int LEFT = -SIZE / 2;
		final int RIGHT = -LEFT;
		
		CreasePatternFactory factory = new CreasePatternFactory();
		CreasePatternInterface creasePattern = factory.createCreasePattern(SIZE);
		Painter painter = new Painter();
		
		final int DIV_NUM = 100;
		for(int i = 0; i < DIV_NUM; i++){
			double x = (SIZE / DIV_NUM)*i + LEFT;
			OriLine line = new OriLine(x, TOP, x, BOTTOM, OriLine.TYPE_RIDGE);
			painter.addLine(line, creasePattern);
		}
		
		for(int i = 0; i < DIV_NUM; i++){
			double y = (SIZE / DIV_NUM)*i + TOP;
			OriLine line = new OriLine(LEFT, y, RIGHT, y, OriLine.TYPE_RIDGE);
			painter.addLine(line, creasePattern);
		}
		
		for(int i = 0; i < DIV_NUM; i++){
			double p = (SIZE / DIV_NUM) * i;
			OriLine line = new OriLine(LEFT + p, TOP, 
					RIGHT, BOTTOM - p, OriLine.TYPE_RIDGE);
			painter.addLine(line, creasePattern);

			line = new OriLine(LEFT, TOP + p, 
					RIGHT - p, BOTTOM, OriLine.TYPE_RIDGE);
			painter.addLine(line, creasePattern);

		}
		
		DocExporter exporter = new ExporterXML();

		try{
			Doc doc = new Doc(SIZE);
			doc.setCreasePattern(creasePattern);
			exporter.export(doc, "heavy_test.opx");
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		System.out.println("done!");
	}

}
