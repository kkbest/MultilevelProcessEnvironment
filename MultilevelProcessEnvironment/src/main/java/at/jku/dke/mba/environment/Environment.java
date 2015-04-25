package at.jku.dke.mba.environment;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.repeatSecondlyForever;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.impl.StdSchedulerFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class Environment implements Runnable {
  final Logger logger = LoggerFactory.getLogger(Environment.class);
  
  @Override
  public void run() {    
    try {      
      Properties properties = new Properties();
      
      try (InputStream stream = Environment.class.getResourceAsStream("/environment.properties");) {
        properties.load(stream);
      }
      
      final String database = properties.getProperty("database");
      final String[] collections = properties.getProperty("collections").split(",");
      final int repeatFrequency = Integer.parseInt(properties.getProperty("repeatFrequency"));
      
      Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
      
      JobDetail job = newJob(Enactment.class)
          .withIdentity("enactment", "enactmentGroup")
          .build();

      job.getJobDataMap().put("database", database);
      job.getJobDataMap().put("collections", collections);
      
      Trigger trigger = newTrigger()
          .withIdentity("enactmentTrigger", "enactmentGroup")
          .startNow()
          .withSchedule(repeatSecondlyForever(repeatFrequency))
          .build();
      
      scheduler.scheduleJob(job, trigger);
      
      scheduler.start();
    } catch (SchedulerException se) {
      LoggerFactory.getLogger(Environment.class).error("Problem with job scheduler.", se);
    } catch (IOException e) {
      logger.error("Could not find environment properties.", e);
    }
  }
  
  /**
   * The main loop of the business process environment.
   * @param args empty
   */
  public static void main(String[] args) {
    new Thread(new Environment()).start();
  }

}
