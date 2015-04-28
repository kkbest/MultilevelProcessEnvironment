package at.jku.dke.mba.tests;

import at.jku.dke.mba.environment.DataAccessObject;
import at.jku.dke.mba.environment.MultilevelBusinessArtifact;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
  @Ignore
  public void testGuard() {
    String dbName = "myMBAse";
    String collectionName = "JohannesKeplerUniversity";
    MultilevelBusinessArtifact mba = 
        new MultilevelBusinessArtifact(dbName, 
                                       collectionName,
                                       "InformationSystems");
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"setDegree\" xmlns=\"\">"
        + " <degree xmlns=\"\">MA</degree>"
        + "</event>"
    );
    
    dao.macrostep(mba);
    
    // TODO assert
  }
  
  @Test
  @Ignore
  public void testAssign() {
    String dbName = "myMBAse";
    String collectionName = "JohannesKeplerUniversity";
    MultilevelBusinessArtifact mba = 
        new MultilevelBusinessArtifact(dbName, 
                                       collectionName,
                                       "InformationSystems");
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"setDegree\" xmlns=\"\">"
        + " <degree xmlns=\"\">MSc</degree>"
        + "</event>"
    );
    
    dao.macrostep(mba);
    
    // TODO assert
  }
  
  @Test
  @Ignore
  public void testTransition() {
    String dbName = "myMBAse";
    String collectionName = "JohannesKeplerUniversity";
    MultilevelBusinessArtifact mba = 
        new MultilevelBusinessArtifact(dbName, 
                                       collectionName,
                                       "InformationSystems");
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"done\" xmlns=\"\"/>"
    );
    
    dao.macrostep(mba);
    
    // TODO assert
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"discontinue\" xmlns=\"\"/>"
    );
    
    dao.macrostep(mba);
    
    // TODO assert
  }
  
  @Test
  @Ignore
  public void testNewDescendant() {
    String dbName = "myMBAse";
    String collectionName = "JohannesKeplerUniversity";
    MultilevelBusinessArtifact mba = 
        new MultilevelBusinessArtifact(dbName, 
                                       collectionName,
                                       "JohannesKeplerUniversity");
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"addSchool\" xmlns=\"\">"
        + " <name xmlns=\"\">Medical</name>"
        + "</event>"
    );
    
    dao.macrostep(mba);
    
    // TODO assert
  }
  
  @Test
  public void testNewDescendantUnder() {
    String dbName = "myMBAse";
    String collectionName = "JohannesKeplerUniversity";
    MultilevelBusinessArtifact mba = 
        new MultilevelBusinessArtifact(dbName, 
                                       collectionName,
                                       "InformationSystems");
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"addModule\" xmlns=\"\">"
        + " <name xmlns=\"\">BusinessIntelligence</name>"
        + "</event>"
    );
    
    dao.macrostep(mba);
    
    // TODO assert
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"addCourse\" xmlns=\"\">"
        + " <name xmlns=\"\">DataWarehousing</name>"
        + " <mod xmlns=\"\">BusinessIntelligence</mod>"
        + "</event>"
    );
    
    dao.macrostep(mba);
  }
}
