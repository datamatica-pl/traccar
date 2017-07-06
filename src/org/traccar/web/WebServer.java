/*
 * Copyright 2012 - 2016 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.web;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.JDBCSessionIdManager;
import org.eclipse.jetty.server.session.JDBCSessionManager;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.traccar.Config;
import org.traccar.helper.Log;

public class WebServer {

    private Server server;
    private final Config config;
    private final DataSource dataSource;
    private final HandlerList handlers = new HandlerList();
    private final SessionManager sessionManager;
    private JDBCSessionIdManager sessionIdManager;

    private void initServer() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(config.getString("web.config"));
            XmlConfiguration conf = new XmlConfiguration(fis);
            server = (Server)conf.configure();
            
            sessionIdManager = new JDBCSessionIdManager(server);
            sessionIdManager.setWorkerName("node1");
            sessionIdManager.setDatasource(dataSource);
            sessionManager.setSessionIdManager(sessionIdManager);
        } catch (Exception ex) {
            Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(fis != null)
                    fis.close();
            } catch (IOException ex) {
                Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public WebServer(Config config, DataSource dataSource) {
        this.config = config;
        this.dataSource = dataSource;

        sessionManager = new JDBCSessionManager();
        int sessionTimeout = config.getInteger("web.sessionTimeout");
        if (sessionTimeout != 0) {
            sessionManager.setMaxInactiveInterval(sessionTimeout);
        }

        initServer();
        initApi();
        if (config.getBoolean("web.console")) {
            initConsole();
        }
        switch (config.getString("web.type", "new")) {
            case "old":
                initOldWebApp();
                break;
            default:
                initWebApp();
                break;
        }                
        server.setHandler(handlers);

        server.addBean(new ErrorHandler() {
            @Override
            protected void handleErrorPage(
                    HttpServletRequest request, Writer writer, int code, String message) throws IOException {
                writer.write("<!DOCTYPE<html><head><title>Error</title></head><html><body>"
                        + code + " - " + HttpStatus.getMessage(code) + "</body></html>");
            }
        }, false);
    }

    private void initWebApp() {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(config.getString("web.path"));
        if (config.getBoolean("web.debug")) {
            resourceHandler.setWelcomeFiles(new String[] {"debug.html"});
        } else {
            resourceHandler.setWelcomeFiles(new String[] {"release.html", "index.html"});
        }
        handlers.addHandler(resourceHandler);
    }

    private void initOldWebApp() {
        try {
            javax.naming.Context context = new InitialContext();
            context.bind("java:/DefaultDS", dataSource);
            context.bind("java:/StringsDir", config.getString("api.stringsDir"));
            context.bind("java:/ImagesDir", config.getString("api.imagesDir"));
            
            context.bind("java:/versions.ios", config.getString("api.versions.ios"));
            context.bind("java:/versions.android", config.getString("api.versions.android"));
            context.bind("java:/versions.iosRequired", config.getString("api.versions.iosrequired"));
            context.bind("java:/versions.androidRequired", config.getString("api.versions.androidrequired"));
            context.bind("java:/versions.messageKey", config.getString("api.versions.messagekey"));
            context.bind("java:/versions.messageLocalized", config.getString("api.versions.messagelocalized"));
            context.bind("java:/versions.messageUrl", config.getString("api.versions.messageurl"));

        } catch (Exception error) {
            Log.warning(error);
        }

        WebAppContext app = new WebAppContext();
        app.setContextPath("/");
        app.getSessionHandler().setSessionManager(sessionManager);
        app.setWar(config.getString("web.application"));
        app.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        handlers.addHandler(app);
    }

    private void initApi() {
        if(config.getBoolean("api.enable")) {
            JDBCSessionManager dbSessionManager = new JDBCSessionManager();
            dbSessionManager.setSessionIdManager(sessionIdManager);
            WebAppContext app = new WebAppContext();
            app.setContextPath("/api");
            app.getSessionHandler().setSessionManager(dbSessionManager);
            app.setWar(config.getString("api.path"));
            handlers.addHandler(app);
        }
    }

    private void initConsole() {
        ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletHandler.setContextPath("/console");
        servletHandler.addServlet(new ServletHolder(new ConsoleServlet()), "/*");
        handlers.addHandler(servletHandler);
    }

    public void start() {
        try {
            server.start();
        } catch (Exception error) {
            Log.warning(error);
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception error) {
            Log.warning(error);
        }
    }

}
