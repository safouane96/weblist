/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db.daos;

import db.exceptions.DAOException;
import db.exceptions.DAOFactoryException;
import java.util.List;

/**
 *
 * @author octopussy
 * @param <ENTITY_CLASS>
 * @param <PRIMARY_KEY_CLASS>
 */
public interface DAO<ENTITY_CLASS, PRIMARY_KEY_CLASS> {
    public Long getCount() throws DAOException;

    /**
     * Returns the {@code ENTITY_CLASS} instance of the storage system record
     * with the primary key equals to the one passed as parameter.
     *
     * @param primaryKey the primary key used to obtain the entity instance.
     * @return the {@code ENTITY_CLASS} instance of the storage system record
     * with the primary key equals to the one passed as parameter or
     * {@code null} if no entities with that primary key is present into the
     * storage system.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     *
    
     */
    public ENTITY_CLASS getByPrimaryKey(PRIMARY_KEY_CLASS primaryKey) throws DAOException;

    /**
     * Returns the list of all the valid entities of type {@code ENTITY_CLASS}
     * stored by the storage system.
     *
     * @return the list of all the valid entities of type {@code ENTITY_CLASS}.
     * @throws DAOException if an error occurred during the information
     * retrieving.
     *
  
     */
    public List<ENTITY_CLASS> getAll() throws DAOException;
    
    /**
     * If this DAO can interact with it, then returns the DAO of class passed as
     * parameter.
     *
     * @param <DAO_CLASS> the class name of the DAO that can interact with this
     * DAO.
     * @param daoClass the class of the DAO that can interact with this DAO.
     * @return the instance of the DAO or null if no DAO of the type passed as
     * parameter can interact with this DAO.
     * @throws DAOFactoryException if an error occurred.
     *
 
     */
    public <DAO_CLASS extends DAO> DAO_CLASS getDAO(Class<DAO_CLASS> daoClass) throws DAOFactoryException;
    
}
