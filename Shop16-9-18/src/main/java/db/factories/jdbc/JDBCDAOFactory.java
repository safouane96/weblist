/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db.factories.jdbc;

import db.daos.DAO;
import db.daos.jdbc.JDBCDAO;
import db.exceptions.DAOFactoryException;
import db.factories.DAOFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 *
 * @author octopussy
 */
public class JDBCDAOFactory implements DAOFactory {
    private final transient Connection CON;
    private final transient HashMap<Class, DAO> DAO_CACHE;

    private static JDBCDAOFactory instance;
    
    /**
     * Call this method before use the instance of this class.
     * @param dbUrl the url to access to the database.
     * @param ut the user to access to the database
     * @param pass password to access to the database
     * @throws DAOFactoryException if an error occurred during dao factory
     * configuration.
     * 
     
     */
    public static void configure(String dbUrl,String ut,String pass) throws DAOFactoryException {
        if (instance == null) {
            instance = new JDBCDAOFactory(dbUrl,ut,pass);
        } else {
            throw new DAOFactoryException("DAOFactory already configured. You can call configure only one time");
        }
    }
    
    /**
     * Returns the singleton instace of this {@link DAOFactory}.
     * @return the singleton instance of this {@code DAOFactory}.
     * @throws DAOFactoryException if an error occurred if this dao factory is
     * not yet configured.
     * 
 
     */
    public static JDBCDAOFactory getInstance() throws DAOFactoryException {
        if (instance == null) {
            throw new DAOFactoryException("DAOFactory not yet configured. Call DAOFactory.configure(String dbUrl) before use the class");
        }
        return instance;
    }
    
    /**
     * The private constructor used to create the singleton instance of this
     * {@code DAOFactory}.
     * @param dbUrl the url to access the database.
     * @throws DAOFactoryException if an error occurred during {@code DAOFactory}
     * creation.
   
     */
    private JDBCDAOFactory(String dbUrl, String ut, String pass) throws DAOFactoryException {
        super();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            CON = DriverManager.getConnection(dbUrl,ut,pass);
        } catch (ClassNotFoundException cnfe) {
            throw new RuntimeException(cnfe.getMessage(), cnfe.getCause());
        }catch (SQLException sqle) {
            throw new DAOFactoryException("Cannot create connection", sqle);
        }

        DAO_CACHE = new HashMap<>();
    }

    /**
     * Shutdowns the access to the storage system.
     * 
 
     */
    @Override
    public void shutdown() {
        try {
            DriverManager.getConnection("jdbc:mysql:;shutdown=true");
        } catch (SQLException sqle) {
            Logger.getLogger(JDBCDAOFactory.class.getName()).info(sqle.getMessage());
        }
    }

    /**
     * Returns the concrete {@link DAO dao} which type is the class passed as
     * parameter.
     *
     * @param <DAO_CLASS> the class name of the {@code dao} to get.
     * @param daoInterface the class instance of the {@code dao} to get.
     * @return the concrete {@code dao} which type is the class passed as
     * parameter.
     * @throws DAOFactoryException if an error occurred during the operation.
     *
     
     */
    @Override
    public <DAO_CLASS extends DAO> DAO_CLASS getDAO(Class<DAO_CLASS> daoInterface) throws DAOFactoryException {
        DAO dao = DAO_CACHE.get(daoInterface);
        if (dao != null) {
            return (DAO_CLASS) dao;
        }
        
        Package pkg = daoInterface.getPackage();
        String prefix = pkg.getName() + ".jdbc.JDBC";
        
        try {
            Class daoClass = Class.forName(prefix + daoInterface.getSimpleName());
            
            Constructor<DAO_CLASS> constructor = daoClass.getConstructor(Connection.class);
            DAO_CLASS daoInstance = constructor.newInstance(CON);
            if (!(daoInstance instanceof JDBCDAO)) {
                throw new DAOFactoryException("The daoInterface passed as parameter doesn't extend JDBCDAO class");
            }
            DAO_CACHE.put(daoInterface, daoInstance);
            return daoInstance;
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | SecurityException ex) {
            throw new DAOFactoryException("Impossible to return the DAO", ex);
        }
    }
    
}
