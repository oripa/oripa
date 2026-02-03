package oripa.persistence.filetool;

import java.util.function.Supplier;

public class FileAccessSupport<Data>
        implements Comparable<FileAccessSupport<Data>> {

    private final FileTypePropertyWithAccessor<Data> fileType;
    private final String description;

    private LoadingAction<Data> loadingAction;
    private SavingAction<Data> savingAction;

    /**
     *
     *
     * @param fileType
     *            specifies what to filter
     * @param description
     *            message in filter box
     */
    public FileAccessSupport(final FileTypePropertyWithAccessor<Data> fileType, final String description) {
        this.fileType = fileType;
        this.description = description;

        var exporter = fileType.getExporter();
        if (exporter != null) {
            savingAction = new SavingAction<>(exporter);
        }

        var loader = fileType.getLoader();
        if (loader != null) {
            loadingAction = new LoadingAction<>(loader);
        }
    }

    /**
     *
     * @return acceptable extensions
     */
    public String[] getExtensions() {
        return fileType.getExtensions();
    }

    public boolean extensionsMatch(final String filePath) {
        return fileType.extensionsMatch(filePath);
    }

    public FileTypeProperty<Data> getTargetType() {
        return fileType;
    }

    public String getDescription() {
        return description;
    }

    private Integer getOrder() {
        return fileType.getOrder();
    }

    /**
     * The order property is the most prior, the second is the description
     * property.
     */
    @Override
    public int compareTo(final FileAccessSupport<Data> o) {
        int cmp = getOrder().compareTo(o.getOrder());
        if (cmp == 0) {
            return description.compareTo(o.description);
        }

        return cmp;
    }

    /**
     * @return loadingAction
     */
    public LoadingAction<Data> getLoadingAction() {
        return loadingAction;
    }

    /**
     * @return savingAction
     */
    public SavingAction<Data> getSavingAction() {
        return savingAction;
    }

    public void setConfigToSavingAction(final Supplier<Object> configSupplier) {
        savingAction.setConfig(configSupplier);
    }

}
