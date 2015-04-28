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

import org.basex.core.Context;
import org.basex.data.Result;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultilevelBusinessArtifact {  
  private final Logger logger = LoggerFactory.getLogger(MultilevelBusinessArtifact.class);
  
  private String name = null;
  private String collectionName = null;
  private String databaseName = null;
  
  private Map<String,String> datamodel = new HashMap<String,String>();
  
  
  /**
   * Takes an XML representation of an MBA as argument, the resulting
   * instance contains the contents of the XML representation.
   */
  public MultilevelBusinessArtifact(String database, String collection, String name) {
    this.databaseName = database;
    this.collectionName = collection;
    this.name = name;
  }
  
  /**
   * Takes an XML representation of an MBA as argument.
   * Does not set collection and database.
   * @param xml the xml representation of the MBA
   */
  public MultilevelBusinessArtifact(String xml) {
    Context context = new Context();
    
    String query = 
        "declare variable $mba external;\n"
        + "fn:string($mba/@name)\n";
      
    try (QueryProcessor proc = new QueryProcessor(query, context)) {
      proc.bind("mba", xml, "element()");
      
      Result queryResult = proc.execute();
      
      this.name = queryResult.serialize();
    } catch (QueryException e) {
      logger.error("Could not retrieve name of MBA.", e);
    } catch (IOException e) {
      logger.error("Could not query.", e);
      e.printStackTrace();
    } finally {
      if (context != null) {
        context.close();
      }
    }
  }
  
  public String getName() {
    return name;
  }
  
  public String getCollectionName() {
    return collectionName;
  }

  public String getDatabaseName() {
    return databaseName;
  }
  
  public void setCollectionName(String collectionName) {
    this.collectionName = collectionName;
  }

  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }
  
  public String getData(String id) {
    return this.datamodel.get(id);
  }
  
  public void setData(String id, String value) {
    this.datamodel.put(id, value);
  }
}
