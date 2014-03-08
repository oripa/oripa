package oripa.persistent.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import oripa.ORIPA;
import oripa.persistent.doc.exporter.ExporterCP;
import oripa.persistent.doc.exporter.ExporterDXFFactory;
import oripa.persistent.doc.exporter.ExporterSVGFactory;
import oripa.persistent.doc.exporter.ExporterXML;
import oripa.persistent.doc.exporter.ModelExporterOBJ;
import oripa.persistent.doc.exporter.PictureExporter;
import oripa.persistent.doc.loader.LoaderCP;
import oripa.persistent.doc.loader.LoaderDXF;
import oripa.persistent.doc.loader.LoaderPDF;
import oripa.persistent.doc.loader.LoaderXML;
import oripa.persistent.filetool.FileAccessSupportFilter;

/**
 * Manages available filters for file access.
 * 
 * @author OUCHI Koji
 * 
 */
public class DocFilterSelector {

	private final HashMap<FileTypeKey, FileAccessSupportFilter<Doc>> filters = new HashMap<>();

	// private static DocFilterSelector instance = null;

	// public static FilterDB getInstance() {
	// if (instance == null) {
	// instance = new FilterDB();
	// }
	// return instance;
	// }

	// /**
	// * for back compatibility.
	// */
	// public static final String KEY_OPX = "opx";
	// /**
	// * for back compatibility.
	// */
	// public static final String KEY_PICT = "pict";
	// /**
	// * for back compatibility.
	// */
	// public static final String KEY_DXF = "dxf";
	// /**
	// * for back compatibility.
	// */
	// public static final String KEY_OBJ = "obj";
	// /**
	// * for back compatibility.
	// */
	// public static final String KEY_CP = "cp";
	// /**
	// * for back compatibility.
	// */
	// public static final String KEY_SVG = "svg";
	// /**
	// * for back compatibility.
	// */
	// public static final String KEY_PDF = "pdf";

	/**
	 * 
	 * A constructor that puts default filters into this instance.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DocFilterSelector() {

		FileTypeKey key = FileTypeKey.OPX;
		putFilter(key, createDescription(key, "ORIPA_File"),
				new ExporterXML(), new LoaderXML());

		key = FileTypeKey.PICT;
		putFilter(key, createDescription(key, "Picture_File"),
				new PictureExporter(), null);

		key = FileTypeKey.DXF;
		putFilter(key, createDescription(key, "File"),
				ExporterDXFFactory.createCreasePatternExporter(),
				new LoaderDXF());
		// filter = new FileAccessSupportFilter(key,
		// FileAccessSupportFilter.createDefaultDescription(key,
		// ORIPA.res.getString("File")),
		// new SavingAction(
		// ExporterDXFFactory.createCreasePatternExporter()));
		// filter.setLoadingAction(new LoadingAction(new LoaderDXF()));
		// this.putFilter(FileTypeKey.DXF, filter);

		key = FileTypeKey.OBJ_MODEL;
		putFilter(key, createDescription(key, "File"),
				new ModelExporterOBJ(),
				null);
		// filter = new FileAccessSupportFilter(key,
		// FileAccessSupportFilter.createDefaultDescription(key,
		// ORIPA.res.getString("File")),
		// new SavingAction(new ModelExporterOBJ()));
		// this.putFilter(FileTypeKey.OBJ_MODEL, filter);

		key = FileTypeKey.CP;
		putFilter(key, createDescription(key, "File"),
				new ExporterCP(),
				new LoaderCP());
		// filter = new FileAccessSupportFilter(key,
		// FileAccessSupportFilter.createDefaultDescription(key,
		// ORIPA.res.getString("File")),
		// new SavingAction(new ExporterCP()));
		// filter.setLoadingAction(new LoadingAction(new LoaderCP()));
		// this.putFilter(FileTypeKey.CP, filter);

		key = FileTypeKey.SVG;
		putFilter(key, createDescription(key, "File"),
				ExporterSVGFactory.createCreasePatternExporter(),
				null);

		// filter = new FileAccessSupportFilter(key,
		// FileAccessSupportFilter.createDefaultDescription(key,
		// ORIPA.res.getString("File")),
		// new SavingAction(
		// ExporterSVGFactory.createCreasePatternExporter()));
		// this.putFilter(FileTypeKey.SVG, filter);

		key = FileTypeKey.PDF;
		putFilter(key, createDescription(key, "File"),
				null,
				new LoaderPDF());

		// filter = new FileAccessSupportFilter(key,
		// FileAccessSupportFilter.createDefaultDescription(key,
		// ORIPA.res.getString("File")));
		// filter.setLoadingAction(new LoadingAction(new LoaderPDF()));
		// this.putFilter(FileTypeKey.PDF, filter);

	}

	/**
	 * 
	 * @param key
	 * @param desctiption
	 * @param exporter
	 * @param loader
	 */
	private void putFilter(FileTypeKey key, String desctiption,
			Exporter<Doc> exporter, Loader<Doc> loader) {
		FileAccessSupportFilter<Doc> filter;

		filter = new FileAccessSupportFilter<>(key,
				desctiption);

		if (exporter != null) {
			filter.setSavingAction(new SavingAction(exporter));
		}

		if (loader != null) {
			filter.setLoadingAction(new LoadingAction(loader));
		}
		this.putFilter(key, filter);

	}

	/**
	 * 
	 * @param fileTypeKey
	 * @param resourceKey
	 * @return
	 */
	private String createDescription(FileTypeKey fileTypeKey, String resourceKey) {
		return FileAccessSupportFilter.createDefaultDescription(fileTypeKey,
				ORIPA.res.getString(resourceKey));

	}

	/**
	 * 
	 * @param key
	 *            A value that describes the file type you want.
	 * @return A filter for given key.
	 */
	public FileAccessSupportFilter<Doc> getFilter(FileTypeKey key) {
		return filters.get(key);
	}

	// /**
	// *
	// * @param key
	// * A value that describes the file type you want.
	// * @return A filter for given key.
	// */
	// @Deprecated
	// public FileAccessSupportFilter<Doc> getFilter(String key) {
	// return filters.get(key);
	// }

	/**
	 * 
	 * @param key
	 *            A value that describes the file type you want.
	 * @param filter
	 *            A filter to be set.
	 * @return A filter for given key.
	 */

	public FileAccessSupportFilter<Doc> putFilter(FileTypeKey key,
			FileAccessSupportFilter<Doc> filter) {
		return filters.put(key, filter);
	}

	/**
	 * 
	 * @return all filters in this instance.
	 */
	public FileAccessSupportFilter<Doc>[] toArray() {
		@SuppressWarnings("unchecked")
		FileAccessSupportFilter<Doc>[] array = new FileAccessSupportFilter[filters
				.size()];

		int i = 0;
		for (FileTypeKey key : filters.keySet()) {
			array[i] = filters.get(key);
			i++;
		}

		return array;
	}

	/**
	 * 
	 * @return filters that can load Doc from a file.
	 */
	public FileAccessSupportFilter<Doc>[] getLoadables() {
		ArrayList<FileAccessSupportFilter<Doc>> loadables = new ArrayList<>();

		for (FileTypeKey key : filters.keySet()) {
			FileAccessSupportFilter<Doc> filter = filters.get(key);
			if (filter.getLoadingAction() != null) {
				loadables.add(filter);
			}
		}

		Collections.sort(loadables);

		@SuppressWarnings("unchecked")
		FileAccessSupportFilter<Doc>[] array = new FileAccessSupportFilter[loadables
				.size()];

		return loadables.toArray(array);
	}

	/**
	 * returns null if any filter cannot load the file.
	 * 
	 * @param path
	 * @return a filter that can load the file at the path.
	 */
	public FileAccessSupportFilter<Doc> getLoadableFilterOf(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			return null;
		}

		for (FileAccessSupportFilter<Doc> filter : this.toArray()) {
			if (filter.accept(file)) {
				filter.getLoadingAction().setPath(path);
				return filter;
			}
		}

		return null;
	}

	/**
	 * 
	 * @return filters that can save a Doc object.
	 */
	@SuppressWarnings("unchecked")
	public FileAccessSupportFilter<Doc>[] getSavables() {
		ArrayList<FileAccessSupportFilter<Doc>> savables = new ArrayList<>();

		for (FileTypeKey key : filters.keySet()) {
			FileAccessSupportFilter<Doc> filter = filters.get(key);
			if (filter.getSavingAction() != null) {
				savables.add(filter);
			}
		}

		Collections.sort(savables);

		return (FileAccessSupportFilter<Doc>[]) savables
				.toArray(new FileAccessSupportFilter<?>[savables.size()]);
	}

}
