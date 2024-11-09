package oripa.persistence.doc.exporter;

import java.io.IOException;

import oripa.domain.creasepattern.CreasePattern;
import oripa.persistence.doc.Doc;
import oripa.persistence.entity.exporter.CreasePatternExporterSVG;

public class ExporterSVGFactory {

	private static class CreasePatternExporter implements DocExporter {

		@Override
		public boolean export(final Doc doc, final String filepath, final Object configObj)
				throws IOException, IllegalArgumentException {
			CreasePattern creasePattern = doc.getCreasePattern();

			var exporter = new CreasePatternExporterSVG();

			return exporter.export(creasePattern, filepath, configObj);
		}

	}

	public static DocExporter createCreasePatternExporter() {
		return new CreasePatternExporter();
	}
}
