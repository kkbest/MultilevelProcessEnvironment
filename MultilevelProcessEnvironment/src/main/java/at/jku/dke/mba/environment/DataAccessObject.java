/**
 * --------------------------------
 * Multilevel Process Environment
 * --------------------------------
  
 * Copyright (C) 2015 Christoph Schütz
   
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
  
 * Custom SCXML interpreter extension that defines multilevel predicates
 * for the use in dynamically evaluated SCXML expressions.
 */

package at.jku.dke.mba.environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQItemType;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;

public class DataAccessObject {
  private final Logger logger = LoggerFactory.getLogger(DataAccessObject.class); 
  
  private XQConnection connection = null;
    
  private XQConnection getConnection() {    
    Properties properties = new Properties();
    
    if (connection == null || connection.isClosed()) {
      try (InputStream stream = getClass().getResourceAsStream("/xqj.properties");) {
        properties.load(stream);
        
        final String xqdsClassName = properties.getProperty("className");
        properties.remove("className");
        
        Class<?> xqdsClass = Class.forName(xqdsClassName);
        
        XQDataSource xqds = (XQDataSource)xqdsClass.newInstance();
        
        xqds.setProperties(properties);

        connection = xqds.getConnection();
        
        try {
          connection.setAutoCommit(false);
        } catch (Exception e) {
          logger.debug("No transaction management available. Set auto-commit to true.");
        }
      } catch (FileNotFoundException e) {
        logger.error("Could not find XQJ properties.", e);
      } catch (IOException e) {
        logger.error("Could not read XQJ properties.", e);
      } catch (ClassNotFoundException e) {
        logger.error("Wrong data source class in XQJ properties.", e);
      } catch (InstantiationException e) {
        logger.error("Problem with instantiating XQJ data source.", e);
      } catch (IllegalAccessException e) {
        logger.error("Problem with instantiating XQJ data source.", e);
      } catch (XQException e) {
        logger.error("Could not establish connection.", e);
      }
    }
    
    return connection;
  }
  
  /**
   * Returns an array of MBAs that have been updated.
   * @param dbName the name of the database
   * @param collectionName the name of the collection
   * @return an array of updated MBAs
   */
  public MultilevelBusinessArtifact[] getUpdatedMultilevelBusinessArtifacts(String dbName,
                                                                            String collectionName) {
    XQConnection con = this.getConnection();
    
    List<MultilevelBusinessArtifact> returnValue = 
        new LinkedList<MultilevelBusinessArtifact>();
    
    try (InputStream xquery = 
           getClass().getResourceAsStream("/xquery/getUpdatedMultilevelBusinessArtifacts.xq")) {
      String[] result = runXQuery(
          new Binding[] {
              new Binding("dbName", 
                          dbName,
                          getConnection().createAtomicType(XQItemType.XQBASETYPE_STRING)),
              new Binding("collectionName", 
                          collectionName,
                          getConnection().createAtomicType(XQItemType.XQBASETYPE_STRING))
          },
          xquery
      );
      
      for (String xml : result) {
        MultilevelBusinessArtifact mba = new MultilevelBusinessArtifact(xml);
        mba.setDatabaseName(dbName);
        mba.setCollectionName(collectionName);
        
        returnValue.add(mba);
      }
    } catch (IOException e) {
      logger.error("Could not read XQuery file.", e);
    } catch (XQException e) {
      logger.error("Could not create element() type for binding.", e);
    }
    
    try {
      if (!con.getAutoCommit()) {
        con.commit();
      }
    } catch (XQException e) {
      logger.error("Error committing macrostep.", e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (XQException e) {
          // ignore
        }
      }
    }
    
    return returnValue.toArray(new MultilevelBusinessArtifact[returnValue.size()]);
  }
  
  /**
   * Creates a new database with a given name.
   * @param dbName the name of the new database
   */
  public void createDatabase(String dbName) {
    XQConnection con = this.getConnection();
    
    try (InputStream xquery = getClass().getResourceAsStream("/xquery/createDatabase.xq")) {
      
      runXQueryUpdate(
          new Binding[] {
              new Binding("dbName", 
                  dbName,
                  getConnection().createAtomicType(XQItemType.XQBASETYPE_STRING))
          },
          xquery
      );
    } catch (IOException e) {
      logger.error("Could not read XQuery file.", e);
    } catch (XQException e) {
      logger.error("Could not create element() type for binding.", e);
    }
    
    try {
      if (!con.getAutoCommit()) {
        con.commit();
      }
    } catch (XQException e) {
      logger.error("Error committing macrostep.", e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (XQException e) {
          // ignore
        }
      }
    }
  }
  
  /**
   * Drops the database with the given name.
   * @param dbName the name of the database
   */
  public void dropDatabase(String dbName) {
    XQConnection con = this.getConnection();
    
    try (InputStream xquery = getClass().getResourceAsStream("/xquery/dropDatabase.xq")) {
      
      runXQueryUpdate(
          new Binding[] {
              new Binding("dbName", 
                  dbName,
                  getConnection().createAtomicType(XQItemType.XQBASETYPE_STRING))
          },
          xquery
      );
    } catch (IOException e) {
      logger.error("Could not read XQuery file.", e);
    } catch (XQException e) {
      logger.error("Could not create element() type for binding.", e);
    }
    
    try {
      if (!con.getAutoCommit()) {
        con.commit();
      }
    } catch (XQException e) {
      logger.error("Error committing macrostep.", e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (XQException e) {
          // ignore
        }
      }
    }
  }

  /**
   * Creates a new database with a given name.
   * @param dbName the name of the new database
   */
  public void insertAsCollection(String dbName, String xml) {
    XQConnection con = this.getConnection();
    
    try (InputStream xqueryInsert = getClass().getResourceAsStream("/xquery/insertAsCollection.xq");
         InputStream xqueryInitMbas = getClass().getResourceAsStream("/xquery/initMbas.xq");) {
      
      runXQueryUpdate(
          new Binding[] {
              new Binding("dbName", 
                  dbName,
                  getConnection().createAtomicType(XQItemType.XQBASETYPE_STRING)),
              new Binding("mba", 
                  xml,
                  getConnection().createElementType(new QName("mba"),
                                                    XQItemType.XQITEMKIND_ELEMENT))
          },
          xqueryInsert
      );
      
      String collectionName = null;
      
      {
        MultilevelBusinessArtifact mba = new MultilevelBusinessArtifact(xml);
        collectionName = mba.getName();
        
        runXQueryUpdate(
            new Binding[] {
                new Binding("dbName", 
                    dbName,
                    getConnection().createAtomicType(XQItemType.XQBASETYPE_STRING)),
                new Binding("collectionName", 
                    collectionName,
                    getConnection().createAtomicType(XQItemType.XQBASETYPE_STRING))
            },
            xqueryInitMbas
        );
      }
      
      MultilevelBusinessArtifact[] mbaSeq = 
          this.getMultilevelBusinessArtifacts(dbName, collectionName);
      
      for (MultilevelBusinessArtifact mba : mbaSeq) {
        try (InputStream xqueryInitScxml = 
               getClass().getResourceAsStream("/xquery/initScxml.xq");) {
          runXQueryUpdate(
              mba,
              xqueryInitScxml
          );        
        }
      }
    } catch (IOException e) {
      logger.error("Could not read XQuery file.", e);
    } catch (XQException e) {
      logger.error("Could not create element() type for binding.", e);
    }
    
    try {
      if (!con.getAutoCommit()) {
        con.commit();
      }
    } catch (XQException e) {
      logger.error("Error committing macrostep.", e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (XQException e) {
          // ignore
        }
      }
    }
  }
  
  private MultilevelBusinessArtifact[] getMultilevelBusinessArtifacts(
      String dbName, String collectionName
  ) {
    List<MultilevelBusinessArtifact> returnValue = 
        new LinkedList<MultilevelBusinessArtifact>();

    try (InputStream xquery = 
           getClass().getResourceAsStream("/xquery/getMultilevelBusinessArtifacts.xq")) {
      String[] result = runXQuery(
          new Binding[] {
              new Binding("dbName", 
                          dbName,
                          getConnection().createAtomicType(XQItemType.XQBASETYPE_STRING)),
              new Binding("collectionName", 
                          collectionName,
                          getConnection().createAtomicType(XQItemType.XQBASETYPE_STRING))
          },
          xquery
      );
      
      for (String xml : result) {
        MultilevelBusinessArtifact mba = new MultilevelBusinessArtifact(xml);
        mba.setDatabaseName(dbName);
        mba.setCollectionName(collectionName);
        
        returnValue.add(mba);
      }
    } catch (IOException e) {
      logger.error("Could not read XQuery file.", e);
    } catch (XQException e) {
      logger.error("Could not create element() type for binding.", e);
    }
    
    return returnValue.toArray(new MultilevelBusinessArtifact[returnValue.size()]);
  }

  /**
   * Enqueues an event in a given MBA's external event queue.
   * @param mba the mba that is concerned by the event
   * @param externalEvent a string representation of the XML element to be enqueued
   */
  public void enqueueExternalEvent(MultilevelBusinessArtifact mba, String externalEvent) {
    XQConnection con = this.getConnection();
    
    try (InputStream xquery = getClass().getResourceAsStream("/xquery/enqueueExternalEvent.xq")) {
      
      runXQueryUpdate(
          mba,
          new Binding[] {
              new Binding("externalEvent", 
                          externalEvent,
                          getConnection().createElementType(new QName("event"),
                                                            XQItemType.XQITEMKIND_ELEMENT))
          },
          xquery
      );
    } catch (IOException e) {
      logger.error("Could not read XQuery file.", e);
    } catch (XQException e) {
      logger.error("Could not create element() type for binding.", e);
    }
    
    try {
      if (!con.getAutoCommit()) {
        con.commit();
      }
    } catch (XQException e) {
      logger.error("Error committing macrostep.", e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (XQException e) {
          // ignore
        }
      }
    }
  }
  
  /**
   * Processes an event from the given MBA's external event queue.
   * @param mba the MBA the event of which is executed
   */
  public void macrostep(MultilevelBusinessArtifact mba) {
    XQConnection con = this.getConnection();
    
    removeFromUpdateLog(mba);
    loadNextExternalEvent(mba);
    
    String[] executableContents = getExecutableContents(mba);
    
    for (Object content : executableContents) {
      runExecutableContent(mba, content);
    }
    
    changeCurrentStatus(mba);
    removeCurrentExternalEvent(mba);
    processEventlessTransitions(mba);
    
    try {
      if (!con.getAutoCommit()) {
        con.commit();
      }
    } catch (XQException e) {
      logger.error("Error committing macrostep.", e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (XQException e) {
          // ignore
        }
      }
    }
  }
  
  private void removeFromUpdateLog(MultilevelBusinessArtifact mba) {
    try (InputStream xquery = getClass().getResourceAsStream("/xquery/removeFromUpdateLog.xq")) {
      runXQueryUpdate(mba, xquery);
    } catch (IOException e) {
      logger.error("Could not read XQuery file.", e);
    }
  }

  private void processEventlessTransitions(MultilevelBusinessArtifact mba) {
    Object[] executableContents = getExecutableContentsEventless(mba);
    
    for (Object content : executableContents) {
      runExecutableContent(mba, content);
    }
    
    changeCurrentStatusEventless(mba);
  }
  
  private String[] getExecutableContents(MultilevelBusinessArtifact mba) {
    String[] returnValue = null;
    
    try (InputStream xquery = getClass().getResourceAsStream("/xquery/getExecutableContents.xq")) {
      returnValue = runXQuery(mba, xquery);
    } catch (IOException e) {
      logger.error("Could not read XQuery file.", e);
    }
    
    return returnValue;
  }
  
  private Object[] getExecutableContentsEventless(MultilevelBusinessArtifact mba) {
    Object[] returnValue = null;
    
    try (InputStream xquery =
           getClass().getResourceAsStream("/xquery/getExecutableContentsEventless.xq")) {
      returnValue = runXQuery(mba, xquery);
    } catch (IOException e) {
      logger.error("Could not read XQuery file.", e);
    }
    
    return returnValue;
  }
  
  private void runExecutableContent(MultilevelBusinessArtifact mba, Object content) {
    try (InputStream xquery = getClass().getResourceAsStream("/xquery/runExecutableContent.xq")) {
      runXQueryUpdate(
          mba,
          new Binding[] {
              new Binding("content", 
                          content,
                          getConnection().createElementType(null, XQItemType.XQITEMKIND_ELEMENT))
          },
          xquery
      );
    } catch (IOException e) {
      logger.error("Could not read XQuery file.", e);
    } catch (XQException e) {
      logger.error("Could not create element() type for binding.", e);
    }
  }
  
  private void loadNextExternalEvent(MultilevelBusinessArtifact mba) {
    try (InputStream xquery = getClass().getResourceAsStream("/xquery/loadNextExternalEvent.xq")) {
      runXQueryUpdate(mba, xquery);
    } catch (IOException e) {
      logger.error("Could not read XQuery file.", e);
    }
  }

  private void removeCurrentExternalEvent(MultilevelBusinessArtifact mba) {
    try (InputStream xquery = 
           getClass().getResourceAsStream("/xquery/removeCurrentExternalEvent.xq")) {
      runXQueryUpdate(mba, xquery);
    } catch (IOException e) {
      logger.error("Could not read XQuery file.", e);
    }
  }
  
  private void changeCurrentStatus(MultilevelBusinessArtifact mba) {
    try (InputStream xquery = getClass().getResourceAsStream("/xquery/changeCurrentStatus.xq")) {
      runXQueryUpdate(mba, xquery);
    } catch (IOException e) {
      logger.error("Could not read XQuery file.", e);
    }
  }
  
  private void changeCurrentStatusEventless(MultilevelBusinessArtifact mba) {
    try (InputStream xquery = 
           getClass().getResourceAsStream("/xquery/changeCurrentStatusEventless.xq")) {
      runXQueryUpdate(mba, xquery);
    } catch (IOException e) {
      logger.error("Could not read XQuery file.", e);
    }
  }
  
  private void runXQueryUpdate(MultilevelBusinessArtifact mba, 
                               InputStream xquery) {
    XQConnection con = this.getConnection();

    XQPreparedExpression expression = null;
    
    try {
      expression = con.prepareExpression(xquery);
      
      expression.bindString(new QName("dbName"), mba.getDatabaseName(), null);
      expression.bindString(new QName("collectionName"), mba.getCollectionName(), null);
      expression.bindString(new QName("mbaName"), mba.getName(), null);
      
      XQResultSequence result = null;
      
      try {
        result = expression.executeQuery();
      } finally {     
        if (result != null) {
          result.close();
        }
      }
    } catch (XQException e) {
      logger.error("Problem with XQuery.", e);
    }  finally {
      if (expression != null) {
        try {
          expression.close();
        } catch (XQException e) {
          // ignore
        }
      }
    }
  }
  
  private void runXQueryUpdate(MultilevelBusinessArtifact mba, 
                               Binding[] bindings,
                               InputStream xquery) {
    XQConnection con = this.getConnection();
    XQPreparedExpression expression = null;
    
    try {
      expression = con.prepareExpression(xquery);
      
      expression.bindString(new QName("dbName"), mba.getDatabaseName(), null);
      expression.bindString(new QName("collectionName"), mba.getCollectionName(), null);
      expression.bindString(new QName("mbaName"), mba.getName(), null);
      
      for (Binding binding : bindings) {
        expression.bindObject(new QName(binding.getVarName()), 
                              binding.getValue(), binding.getType());
      }
      
      XQResultSequence result = null;
      
      try {
        result = expression.executeQuery();
      } finally {        
        if (result != null) {
          result.close();
        }
      }
    } catch (XQException e) {
      logger.error("Problem with XQuery.", e);
    }  finally {
      if (expression != null) {
        try {
          expression.close();
        } catch (XQException e) {
          // ignore
        }
      }
    }
  }
  
  private void runXQueryUpdate(Binding[] bindings,
                               InputStream xquery) {
    XQConnection con = this.getConnection();
    XQPreparedExpression expression = null;
    
    try {
      expression = con.prepareExpression(xquery);
      
      for (Binding binding : bindings) {
        expression.bindObject(new QName(binding.getVarName()), 
                              binding.getValue(), binding.getType());
      }
      
      XQResultSequence result = null;
      
      try {
        result = expression.executeQuery();
      } finally {        
        if (result != null) {
          result.close();
        }
      }
    } catch (XQException e) {
      logger.error("Problem with XQuery.", e);
    }  finally {
      if (expression != null) {
        try {
          expression.close();
        } catch (XQException e) {
          // ignore
        }
      }
    }
  }
  
  private String[] runXQuery(MultilevelBusinessArtifact mba,
                             InputStream xquery) {
    XQConnection con = this.getConnection();
    XQPreparedExpression expression = null;
    
    List<String> returnValue = new LinkedList<String>();
    
    try {
      expression = con.prepareExpression(xquery);
      
      expression.bindString(new QName("dbName"), mba.getDatabaseName(), null);
      expression.bindString(new QName("collectionName"), mba.getCollectionName(), null);
      expression.bindString(new QName("mbaName"), mba.getName(), null);
            
      XQResultSequence result = null;
      
      try {
        result = expression.executeQuery();
        
        while (result.next()) {
          returnValue.add(result.getItemAsString(null));
        }
      } finally {        
        if (result != null) {
          result.close();
        }
      }
    } catch (XQException e) {
      logger.error("Problem with XQuery.", e);
    }  finally {
      if (expression != null) {
        try {
          expression.close();
        } catch (XQException e) {
          // ignore
        }
      }
    }
    
    return returnValue.toArray(new String[returnValue.size()]);
  }
  
  private String[] runXQuery(Binding[] bindings,
                             InputStream xquery) {
    XQConnection con = this.getConnection();
    XQPreparedExpression expression = null;
    
    List<String> returnValue = new LinkedList<String>();
    
    try {
      expression = con.prepareExpression(xquery);
      
      for (Binding binding : bindings) {
        expression.bindObject(new QName(binding.getVarName()), 
                              binding.getValue(), binding.getType());
      }
            
      XQResultSequence result = null;
      
      try {
        result = expression.executeQuery();
        
        while (result.next()) {
          returnValue.add(result.getItemAsString(null));
        }
      } finally {        
        if (result != null) {
          result.close();
        }
      }
    } catch (XQException e) {
      logger.error("Problem with XQuery.", e);
    }  finally {
      if (expression != null) {
        try {
          expression.close();
        } catch (XQException e) {
          // ignore
        }
      }
    }
    
    return returnValue.toArray(new String[returnValue.size()]);
  }
  
  private class Binding {
    private String varName = null;
    private Object value = null;
    private XQItemType type = null;
    
    public Binding(String varName, Object value, XQItemType type) {
      this.varName = varName;
      this.value = value;
      this.type = type;
    }
    
    public String getVarName() {
      return varName;
    }
    
    public Object getValue() {
      return value;
    }
    
    public XQItemType getType() {
      return type;
    }
  }
}
