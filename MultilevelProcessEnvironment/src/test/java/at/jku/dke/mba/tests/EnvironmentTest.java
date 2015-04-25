package at.jku.dke.mba.tests;

import at.jku.dke.mba.environment.DataAccessObject;
import at.jku.dke.mba.environment.MultilevelBusinessArtifact;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.IOException;

public class EnvironmentTest {
  private final Logger logger = LoggerFactory.getLogger(EnvironmentTest.class); 
  
  private DataAccessObject dao = new DataAccessObject();
  
  /**
   * Setting up the test case.
   */
  @Before
  public void setUp() {
    dao.createDatabase("myMBAse");
    
    try (InputStream xml = getClass().getResourceAsStream("/xml/academic_simple.xml")) {
      dao.insertAsCollection("myMBAse", IOUtils.toString(xml));
    } catch (IOException e) {
      logger.error("Could not read XML file.", e);
    }
  }
  
  @After
  public void tearDown() {
    //dao.dropDatabase("myMBAse");
  }
  
  @Test
  public void testSendEventsAndProcess() {
    String dbName = "myMBAse";
    String collectionName = "JohannesKeplerUniversity";
    
    {
      MultilevelBusinessArtifact mba = 
          new MultilevelBusinessArtifact(dbName, 
                                         collectionName,
                                         "InformationSystems");
      
      dao.enqueueExternalEvent(mba, 
            "<event name=\"addModule\" xmlns=\"\">"
          + " <name xmlns=\"\">BusinessIntelligence</name>"
          + "</event>"
      );
    }
    
    MultilevelBusinessArtifact[] updatedMbas = 
        dao.getUpdatedMultilevelBusinessArtifacts(dbName, collectionName);
      
    for (MultilevelBusinessArtifact mba : updatedMbas) {
      logger.info("Conducting microstep for " + mba.getName() + ".");
      dao.macrostep(mba);
    }
  }
}
