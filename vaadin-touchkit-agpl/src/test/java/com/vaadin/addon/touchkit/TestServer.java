package com.vaadin.addon.touchkit;

import java.io.File;

import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import com.vaadin.addon.touchkit.itest.FallbackApplication;
import com.vaadin.addon.touchkit.itest.TouchKitTestApp;
import com.vaadin.addon.touchkit.server.TouchKitApplicationServlet;
import com.vaadin.terminal.gwt.server.Constants;

public class TestServer {

    /**
     * 
     * Test server for the addon.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Server server = new Server();

        final Connector connector = new SelectChannelConnector();

        connector.setPort(7777);
        server.setConnectors(new Connector[] { connector });

        WebAppContext context = new WebAppContext();

        ServletHolder servletHolder = new ServletHolder(
                TouchKitApplicationServlet.class);
        servletHolder.setInitParameter("widgetset",
                "com.vaadin.addon.touchkit.gwt.TouchKitWidgetSet");
        servletHolder.setInitParameter("application",
                TouchKitTestApp.class.getName());

        MimeTypes mimeTypes = context.getMimeTypes();
        mimeTypes.addMimeMapping("appcache", "text/cache-manifest");
        mimeTypes.addMimeMapping("manifest", "text/cache-manifest");
        context.setMimeTypes(mimeTypes);

        // test fallback app and widgetset
        servletHolder.setInitParameter("fallbackApplication",
                FallbackApplication.class.getName());
        servletHolder.setInitParameter("fallbackWidgetset",
                Constants.DEFAULT_WIDGETSET);

        File file = new File("./target");
        File[] listFiles = file.listFiles();
        for (File file2 : listFiles) {
            if (file2.isDirectory()
                    && file2.getName().startsWith("vaadin-touchkit-")) {
                context.setWar(file2.getPath());
                break;
            }
        }
        context.setContextPath("/");
        context.addServlet(servletHolder, "/*");

        server.setHandler(context);

        server.start();
        server.join();
    }

}
