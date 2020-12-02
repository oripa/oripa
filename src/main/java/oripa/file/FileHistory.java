package oripa.file;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class FileHistory {
	private LinkedList<String> mostRecentlyUsedHistory = new LinkedList<>();
	private final int maxSize;

	public FileHistory(final int maxSize) {
		this.maxSize = maxSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	/**
	 *
	 * @param filePath
	 * @return true if the given path is appended to history
	 */
	public boolean useFile(final String filePath) {
		int index = mostRecentlyUsedHistory.indexOf(filePath);

		if (index < 0) {
			if (mostRecentlyUsedHistory.size() >= maxSize) {
				mostRecentlyUsedHistory.removeLast();
			}

			mostRecentlyUsedHistory.addFirst(filePath);
			return true;
		}

		String item = mostRecentlyUsedHistory.remove(index);
		mostRecentlyUsedHistory.addFirst(item);

		return false;
	}

	public Collection<String> getHistory() {
		return mostRecentlyUsedHistory;
	}

	public void setHistory(final Collection<String> history) {
		mostRecentlyUsedHistory = new LinkedList<>(history);
	}

	public String getLastPath() {
		if (mostRecentlyUsedHistory.isEmpty()) {
			return System.getProperty("user.home");
		}

		return mostRecentlyUsedHistory.getFirst();
	}

	public String getLastDirectory() {
		if (mostRecentlyUsedHistory.isEmpty()) {
			return System.getProperty("user.home");
		}

		File file = new File(mostRecentlyUsedHistory.getFirst());
		return file.getParent();
	}

	public void loadFromInitData(final InitData ini) {
		var mruList = Arrays.asList(ini.getMRUFiles())
				.subList(0, Math.min(maxSize, ini.getMRUFiles().length)).stream()
				.filter(fileName -> fileName != null && !fileName.isEmpty())
				.collect(Collectors.toList());

		setHistory(mruList);
	}

}
