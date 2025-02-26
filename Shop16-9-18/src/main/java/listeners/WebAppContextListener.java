/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import db.exceptions.DAOFactoryException;
import db.factories.DAOFactory;
import db.factories.jdbc.JDBCDAOFactory;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Web application lifecycle listener.
 *
 * @author octopussy
 */
@WebListener
public class WebAppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String dburl = sce.getServletContext().getInitParameter("dburl");
        String dbpass= sce.getServletContext().getInitParameter("dbpass");
        String dbut = sce.getServletContext().getInitParameter("dbut");
        try {
            JDBCDAOFactory.configure(dburl,dbut,dbpass);
            DAOFactory daoFactory = JDBCDAOFactory.getInstance();   
            sce.getServletContext().setAttribute("daoFactory", daoFactory);
        } catch (DAOFactoryException ex) {
            Logger.getLogger(getClass().getName()).severe(ex.toString());
            throw new RuntimeException(ex);
        }
    }

    /**
     * The servlet container call this method when destroyes the application.
     * @param sce the event generated by the servlet container when destroyes
     * the application.
     * 
     * @author Stefano Chirico
     * @since 1.0.180404
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
               
            } catch (SQLException e) {
                
            }

        }
        DAOFactory daoFactory = (DAOFactory) sce.getServletContext().getAttribute("daoFactory");
        if (daoFactory != null) {
            daoFactory.shutdown();
        }
        daoFactory = null;
    }
}