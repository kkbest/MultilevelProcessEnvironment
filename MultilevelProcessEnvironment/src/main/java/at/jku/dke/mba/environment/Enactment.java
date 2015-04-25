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
      MultilevelBusinessArtifact[] updatedMbas = 
          dao.getUpdatedMultilevelBusinessArtifacts(dbName, collectionName);
      
      for (MultilevelBusinessArtifact mba : updatedMbas) {
        logger.info("Conducting microstep for " + mba.getName() + ".");
        dao.macrostep(mba);
      }
    }
  }

}
