package org.vaadin.touchkit.settings;

import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.vaadin.touchkit.extensions.LocalStorage;
import org.vaadin.touchkit.gwt.client.offlinemode.CacheManifestStatusIndicator;

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.VBrowserDetails;

/**
 * This class is used to control HTML5 application cache settings.
 */
@SuppressWarnings("serial")
public class ApplicationCacheSettings implements BootstrapListener {

    private boolean offlineModeEnabled = true;

    @Override
    public void modifyBootstrapFragment(BootstrapFragmentResponse response) {
        // NOP
    }

    @Override
    public void modifyBootstrapPage(BootstrapPageResponse response) {
        Document document = response.getDocument();

        // Add the widgetsetUrl parameter to the bootstrap parameters.
        // This is overridden to avoid adding the naive random query
        // parameter (used by core to avoid caching of js file).

        final VaadinService service = response.getSession().getService();
        final VaadinRequest request = response.getRequest();
        final String staticFilePath = service
                .getStaticFileLocation(request);
        // VAADIN folder location
        final String vaadinDir = staticFilePath + "/VAADIN/";
        // Figure out widgetset
        final UICreateEvent event = new UICreateEvent(request,
                response.getUiClass());
        String widgetset = response.getUIProvider().getWidgetset(event);
        if (widgetset == null) {
            widgetset = request.getService()
                    .getConfiguredWidgetset(request);
        }
        // Url for the widgetset
        final String widgetsetUrl = String.format(
                "%swidgetsets/%s/%s.nocache.js", vaadinDir, widgetset,
                widgetset);

        // Update the bootstrap page
        Element scriptTag = document.getElementsByTag("script").last();
        String script = scriptTag.html();
        scriptTag.html("");

        script = script.replace("});", ",\"widgetsetUrl\":\"" + widgetsetUrl
                + "\",\"offlineEnabled\":" + isOfflineModeEnabled() + "});");

        scriptTag.appendChild(new DataNode(script));

    }

    /**
     * @return true is offline is enabled in client side.
     */
    public boolean isOfflineModeEnabled() {
        return offlineModeEnabled;
    }

    /**
     * Enable or disable offline mode in client side.
     */
    public void setOfflineModeEnabled(boolean offlineModeEnabled) {
        this.offlineModeEnabled = offlineModeEnabled;
    }

    /**
     * Specifies the message to show when an update to the application cache is
     * available. When a new version of the application cache has been loaded by
     * the client, this message is shown in a confirmation box. Answering 'OK'
     * in this box causes the application to refresh and use the new application
     * cache (== new version of the widget set).
     *
     * @param message
     *            The new message. The default is
     *            "There are updates ready to be installed. Would you like to restart now?"
     */
    public void setUpdateNowMessage(String message) {
        LocalStorage.get().put(CacheManifestStatusIndicator.UPDATE_NOW_MSG_KEY,
                message);
    }

    /**
     * Specifies how often to check for and download updates to the application
     * cache (== widget set).
     *
     * @param interval
     *            The interval in seconds. The default is 30 minutes (1800
     *            seconds).
     */
    public void setUpdateCheckInterval(int interval) {
        LocalStorage.get().put(
                CacheManifestStatusIndicator.UPDATE_CHECK_INTERVAL_KEY,
                String.valueOf(interval));
    }
}
