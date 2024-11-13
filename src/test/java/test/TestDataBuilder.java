package test;

import oripa.domain.cptool.Painter;
import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.projectprop.Property;
import oripa.geom.GeomUtil;
import oripa.persistence.doc.Doc;
import oripa.persistence.doc.exporter.DocExporter;
import oripa.persistence.doc.exporter.ExporterXML;
import oripa.value.OriLine;

public class TestDataBuilder {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		final int SIZE = 400;
		final int TOP = -SIZE / 2;
		final int BOTTOM = -TOP;
		final int LEFT = -SIZE / 2;
		final int RIGHT = -LEFT;

		CreasePatternFactory factory = new CreasePatternFactory();
		CreasePattern creasePattern = factory.createCreasePattern(SIZE);
		Painter painter = new Painter(creasePattern, GeomUtil.pointEps());

		final int DIV_NUM = 100;
		for (int i = 0; i < DIV_NUM; i++) {
			double x = (SIZE / DIV_NUM) * i + LEFT;
			OriLine line = new OriLine(x, TOP, x, BOTTOM, OriLine.Type.MOUNTAIN);
			painter.addLine(line);
		}

		for (int i = 0; i < DIV_NUM; i++) {
			double y = (SIZE / DIV_NUM) * i + TOP;
			OriLine line = new OriLine(LEFT, y, RIGHT, y, OriLine.Type.MOUNTAIN);
			painter.addLine(line);
		}

		for (int i = 0; i < DIV_NUM; i++) {
			double p = (SIZE / DIV_NUM) * i;
			OriLine line = new OriLine(LEFT + p, TOP,
					RIGHT, BOTTOM - p, OriLine.Type.MOUNTAIN);
			painter.addLine(line);

			line = new OriLine(LEFT, TOP + p,
					RIGHT - p, BOTTOM, OriLine.Type.MOUNTAIN);
			painter.addLine(line);

		}

		DocExporter exporter = new ExporterXML();

		try {
			var doc = Doc.forSaving(creasePattern, new Property());
			exporter.export(doc, "heavy_test.opx", null);
		} catch (Exception e) {

		}

		System.out.println("done!");
	}

}
