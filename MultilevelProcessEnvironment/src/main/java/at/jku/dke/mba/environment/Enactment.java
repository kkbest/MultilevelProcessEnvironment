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

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Enactment implements Job {
  private final Logger logger = LoggerFactory.getLogger(Enactment.class); 

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    JobDataMap data = context.getJobDetail().getJobDataMap();
    DataAccessObject dao = new DataAccessObject();
    
    String dbName = data.getString("database");
    String[] collectionNames = (String[]) data.get("collections");
    
    for (String collectionName : collectionNames) {
      MultilevelBusinessArtifact[] newMbas = 
          dao.getNewMultilevelBusinessArtifacts(dbName, collectionName);
      
      for (MultilevelBusinessArtifact mba : newMbas) {
        logger.info("Initializing newly created MBA " + mba.getName() + ".");
        dao.initMba(mba);
      }
      
      MultilevelBusinessArtifact[] updatedMbas = 
          dao.getUpdatedMultilevelBusinessArtifacts(dbName, collectionName);
      
      for (MultilevelBusinessArtifact mba : updatedMbas) {
        logger.info("Conducting microstep for " + mba.getName() + ".");
        dao.macrostep(mba);
      }
    }
  }

}
