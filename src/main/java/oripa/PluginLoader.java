package oripa;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.gui.presenter.plugin.GraphicMouseActionPlugin;
import oripa.gui.view.main.MainFrameSetting;
import oripa.gui.view.main.UIPanelSetting;

public class PluginLoader {
    private static Logger logger = LoggerFactory.getLogger(PluginLoader.class);

    public List<GraphicMouseActionPlugin> loadMouseActionPlugins(final MainFrameSetting frameSetting,
            final UIPanelSetting uiPanelSetting) {

        var loader = ServiceLoader.load(GraphicMouseActionPlugin.class);

        var plugins = new ArrayList<GraphicMouseActionPlugin>();

        loader.forEach(plugin -> {
            plugin.setMainFrameSetting(frameSetting);
            plugin.setUIPanelSetting(uiPanelSetting);

            plugins.add(plugin);
        });

        logger.debug("{} plugins", plugins.size());

        return plugins;
    }
}
