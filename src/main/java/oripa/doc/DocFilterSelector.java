package oripa.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import oripa.ORIPA;
import oripa.doc.exporter.ExporterCP;
import oripa.doc.exporter.ExporterDXFFactory;
import oripa.doc.exporter.ExporterSVGFactory;
import oripa.doc.exporter.ExporterXML;
import oripa.doc.exporter.ModelExporterOBJ;
import oripa.doc.exporter.PictureExporter;
import oripa.doc.loader.LoaderCP;
import oripa.doc.loader.LoaderDXF;
import oripa.doc.loader.LoaderPDF;
import oripa.doc.loader.LoaderXML;
import oripa.file.FileAccessSupportFilter;

/**
 * Manages available filters for file access.
 * 
 * @author OUCHI Koji
 * 
 */
public class DocFilterSelector {

	private final HashMap<String, FileAccessSupportFilter<Doc>> filters = new HashMap<>();

	// private static DocFilterSelector instance = null;

	// public static FilterDB getInstance() {
	// if (instance == null) {
	// instance = new FilterDB();
	// }
	// return instance;
	// }

	/**
	 * for back compatibility.
	 */
	public static final String KEY_OPX = "opx";
	/**
	 * for back compatibility.
	 */
	public static final String KEY_PICT = "pict";
	/**
	 * for back compatibility.
	 */
	public static final String KEY_DXF = "dxf";
	/**
	 * for back compatibility.
	 */
	public static final String KEY_OBJ = "obj";
	/**
	 * for back compatibility.
	 */
	public static final String KEY_CP = "cp";
	/**
	 * for back compatibility.
	 */
	public static final String KEY_SVG = "svg";
	/**
	 * for back compatibility.
	 */
	public static final String KEY_PDF = "pdf";

	/**
	 * 
	 * A constructor that puts default filters into this instance.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DocFilterSelector() {

		FileAccessSupportFilter filter;

		FileTypeKey key = FileTypeKey.OPX;

		filter = new FileAccessSupportFilter(key,
				FileAccessSupportFilter.createDefaultDescription(key,
						ORIPA.res.getString("ORIPA_File")),
				new SavingAction(new ExporterXML()));
		filter.setLoadingAction(new LoadingAction(new LoaderXML()));
		this.putFilter(key, filter);

		key = FileTypeKey.PICT;
		filter = new FileAccessSupportFilter(key,
				FileAccessSupportFilter.createDefaultDescription(key,
						ORIPA.res.getString("Picture_File")),
				new SavingAction(new PictureExporter()));
		this.putFilter(FileTypeKey.PICT, filter);

		key = FileTypeKey.DXF;
		filter = new FileAccessSupportFilter(key,
				FileAccessSupportFilter.createDefaultDescription(key,
						ORIPA.res.getString("File")),
				new SavingAction(
						ExporterDXFFactory.createCreasePatternExporter()));
		filter.setLoadingAction(new LoadingAction(new LoaderDXF()));
		this.putFilter(FileTypeKey.DXF, filter);

		key = FileTypeKey.OBJ_MODEL;
		filter = new FileAccessSupportFilter(key,
				FileAccessSupportFilter.createDefaultDescription(key,
						ORIPA.res.getString("File")),
				new SavingAction(new ModelExporterOBJ()));
		this.putFilter(FileTypeKey.OBJ_MODEL, filter);

		key = FileTypeKey.CP;
		filter = new FileAccessSupportFilter(key,
				FileAccessSupportFilter.createDefaultDescription(key,
						ORIPA.res.getString("File")),
				new SavingAction(new ExporterCP()));
		filter.setLoadingAction(new LoadingAction(new LoaderCP()));
		this.putFilter(FileTypeKey.CP, filter);

		key = FileTypeKey.SVG;
		filter = new FileAccessSupportFilter(key,
				FileAccessSupportFilter.createDefaultDescription(key,
						ORIPA.res.getString("File")),
				new SavingAction(
						ExporterSVGFactory.createCreasePatternExporter()));
		this.putFilter(FileTypeKey.SVG, filter);

		key = FileTypeKey.PDF;
		filter = new FileAccessSupportFilter(key,
				FileAccessSupportFilter.createDefaultDescription(key,
						ORIPA.res.getString("File")));
		filter.setLoadingAction(new LoadingAction(new LoaderPDF()));
		this.putFilter(FileTypeKey.PDF, filter);

	}

	/**
	 * 
	 * @param key
	 *            A value that describes the file type you want.
	 * @return A filter for given key.
	 */
	public FileAccessSupportFilter<Doc> getFilter(FileTypeKey key) {
		return filters.get(key.getKeyText());
	}

	/**
	 * 
	 * @param key
	 *            A value that describes the file type you want.
	 * @return A filter for given key.
	 */
	@Deprecated
	public FileAccessSupportFilter<Doc> getFilter(String key) {
		return filters.get(key);
	}

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
		return filters.put(key.getKeyText(), filter);
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
		for (String key : filters.keySet()) {
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

		for (String key : filters.keySet()) {
			FileAccessSupportFilter<Doc> filter = filters.get(key);
			if (filter.getLoadingAction() != null) {
				loadables.add(filter);
			}
		}

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

		for (String key : filters.keySet()) {
			FileAccessSupportFilter<Doc> filter = filters.get(key);
			if (filter.getSavingAction() != null) {
				savables.add(filter);
			}
		}

		return (FileAccessSupportFilter<Doc>[]) savables.toArray();
	}

}
