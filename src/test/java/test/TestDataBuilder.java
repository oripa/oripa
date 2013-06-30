package test;

import oripa.doc.Doc;
import oripa.doc.exporter.Exporter;
import oripa.doc.exporter.ExporterXML;
import oripa.geom.OriLine;

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
		
		Doc doc = new Doc(SIZE);
		
		final int DIV_NUM = 100;
		for(int i = 0; i < DIV_NUM; i++){
			double x = (SIZE / DIV_NUM)*i + LEFT;
			OriLine line = new OriLine(x, TOP, x, BOTTOM, OriLine.TYPE_RIDGE);
			doc.addLine(line);
		}
		
		for(int i = 0; i < DIV_NUM; i++){
			double y = (SIZE / DIV_NUM)*i + TOP;
			OriLine line = new OriLine(LEFT, y, RIGHT, y, OriLine.TYPE_RIDGE);
			doc.addLine(line);
		}
		
		for(int i = 0; i < DIV_NUM; i++){
			double p = (SIZE / DIV_NUM) * i;
			OriLine line = new OriLine(LEFT + p, TOP, 
					RIGHT, BOTTOM - p, OriLine.TYPE_RIDGE);
			doc.addLine(line);

			line = new OriLine(LEFT, TOP + p, 
					RIGHT - p, BOTTOM, OriLine.TYPE_RIDGE);
			doc.addLine(line);

		}
		
		Exporter exporter = new ExporterXML();

		try{
			exporter.export(doc, "heavy_test.opx");
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		System.out.println("done!");
	}

}
