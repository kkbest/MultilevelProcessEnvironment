package at.jku.dke.mba.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
        dao.getMultilevelBusinessArtifact(dbName, 
                                          collectionName,
                                          "InformationSystems");
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"setDegree\" xmlns=\"\">"
        + " <degree xmlns=\"\">MA</degree>"
        + "</event>"
    );
    
    dao.macrostep(mba);
    
    assertNull(mba.getDataContents("degree"));
  }
  
  @Test
  @Ignore
  public void testAssign() {
    String dbName = "myMBAse";
    String collectionName = "JohannesKeplerUniversity";
    MultilevelBusinessArtifact mba = 
        dao.getMultilevelBusinessArtifact(dbName, 
                                          collectionName,
                                          "InformationSystems");
    
    assertNull(mba.getDataContents("degree"));
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"setDegree\" xmlns=\"\">"
        + " <degree xmlns=\"\">MSc</degree>"
        + "</event>"
    );
    
    dao.macrostep(mba);
    
    mba = dao.getMultilevelBusinessArtifact(dbName, 
                                            collectionName,
                                            "InformationSystems");
        
    assertEquals("MSc", mba.getDataContents("degree"));
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"setDegree\" xmlns=\"\">"
        + " <degree xmlns=\"\">BSc</degree>"
        + "</event>"
    );
    
    dao.macrostep(mba);
    
    mba = dao.getMultilevelBusinessArtifact(dbName, 
                                            collectionName,
                                            "InformationSystems");
        
    assertEquals("BSc", mba.getDataContents("degree"));
  }
  
  @Test
  @Ignore
  public void testTransition() {
    String dbName = "myMBAse";
    String collectionName = "JohannesKeplerUniversity";
    MultilevelBusinessArtifact mba = 
        dao.getMultilevelBusinessArtifact(dbName, 
                                          collectionName,
                                          "InformationSystems");

    assertTrue(mba.isInState("Developing"));
    assertFalse(mba.isInState("Active"));
    assertFalse(mba.isInState("Discontinued"));
    
    
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"done\" xmlns=\"\"/>"
    );
    
    dao.macrostep(mba);
    
    
    
    mba = dao.getMultilevelBusinessArtifact(dbName, 
                                            collectionName,
                                            "InformationSystems");
    
    assertTrue(mba.isInState("Active"));
    assertFalse(mba.isInState("Developing"));
    assertFalse(mba.isInState("Discontinued"));
    
    
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"discontinue\" xmlns=\"\"/>"
    );
    
    
    
    dao.macrostep(mba);
    
    mba = dao.getMultilevelBusinessArtifact(dbName, 
                                            collectionName,
                                            "InformationSystems");
    
    assertTrue(mba.isInState("Discontinued"));
    assertFalse(mba.isInState("Developing"));
    assertFalse(mba.isInState("Active"));
  }
  
  @Test
  @Ignore
  public void testNewDescendant() {
    String dbName = "myMBAse";
    String collectionName = "JohannesKeplerUniversity";
    MultilevelBusinessArtifact mba = 
        dao.getMultilevelBusinessArtifact(dbName, 
                                          collectionName,
                                          "JohannesKeplerUniversity");
    
    assertNull(dao.getMultilevelBusinessArtifact(dbName, collectionName, "Medical"));
    assertFalse(mba.hasConcretization("Medical"));
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"addSchool\" xmlns=\"\">"
        + " <name xmlns=\"\">Medical</name>"
        + "</event>"
    );
    
    dao.macrostep(mba);
    
    mba = dao.getMultilevelBusinessArtifact(dbName, 
                                            collectionName,
                                            "JohannesKeplerUniversity");

    assertNotNull(dao.getMultilevelBusinessArtifact(dbName, collectionName, "Medical"));
    assertTrue(mba.hasConcretization("Medical"));
  }
  
  @Test
  @Ignore
  public void testNewDescendantUnder() {
    String dbName = "myMBAse";
    String collectionName = "JohannesKeplerUniversity";
    MultilevelBusinessArtifact mba = 
        dao.getMultilevelBusinessArtifact(dbName, 
                                          collectionName,
                                          "InformationSystems");
    
    assertNull(dao.getMultilevelBusinessArtifact(dbName, collectionName, "BusinessIntelligence"));
    assertFalse(mba.hasConcretization("BusinessIntelligence"));
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"addModule\" xmlns=\"\">"
        + " <name xmlns=\"\">BusinessIntelligence</name>"
        + "</event>"
    );
    
    dao.macrostep(mba);
    
    mba = dao.getMultilevelBusinessArtifact(dbName, 
                                            collectionName,
                                            "InformationSystems");
    
    assertNotNull(
        dao.getMultilevelBusinessArtifact(dbName, collectionName, "BusinessIntelligence")
    );
    assertTrue(mba.hasConcretization("BusinessIntelligence"));
    
    
    assertNull(dao.getMultilevelBusinessArtifact(dbName, collectionName, "DataWarehousing"));
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"addCourse\" xmlns=\"\">"
        + " <name xmlns=\"\">DataWarehousing</name>"
        + " <mod xmlns=\"\">BusinessIntelligence</mod>"
        + "</event>"
    );
    
    dao.macrostep(mba);
    
    mba = dao.getMultilevelBusinessArtifact(dbName, 
                                            collectionName,
                                            "BusinessIntelligence");
    
    assertNotNull(
        dao.getMultilevelBusinessArtifact(dbName, collectionName, "DataWarehousing")
    );
    assertTrue(mba.hasConcretization("DataWarehousing"));
  }

  @Test
  @Ignore
  public void testEveryDescendantIsInState() {
    String dbName = "myMBAse";
    String collectionName = "JohannesKeplerUniversity";
    MultilevelBusinessArtifact mba =
        dao.getMultilevelBusinessArtifact(dbName, collectionName, "JohannesKeplerUniversity");
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"addSchool\" xmlns=\"\">"
        + " <name xmlns=\"\">Medical</name>"
        + "</event>"
    );
    
    dao.macrostep(mba);
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "Medical");

    dao.initMba(mba);
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"addProgram\" xmlns=\"\">"
        + " <name xmlns=\"\">HumanMedicine</name>"
        + "</event>"
    );
    
    dao.macrostep(mba);
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "HumanMedicine");
    
    dao.initMba(mba);
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"addCourse\" xmlns=\"\">"
        + " <name xmlns=\"\">Anatomy</name>"
        + "</event>"
    );
    
    dao.macrostep(mba);
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "Anatomy");

    dao.initMba(mba);
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"offer\" xmlns=\"\"/>"
    );
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"addCourseInstance\" xmlns=\"\">"
        + " <name xmlns=\"\">AnatomySummer2015</name>"
        + "</event>"
    );
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"addCourseInstance\" xmlns=\"\">"
        + " <name xmlns=\"\">AnatomyWinter2015</name>"
        + "</event>"
    );
    
    dao.macrostep(mba);
    dao.macrostep(mba);
    dao.macrostep(mba);
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "AnatomySummer2015");

    dao.initMba(mba);
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "AnatomyWinter2015");

    dao.initMba(mba);
    
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "HumanMedicine");
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"done\" xmlns=\"\"/>"
    );
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"discontinue\" xmlns=\"\"/>"
    );
    
    dao.macrostep(mba);
    dao.macrostep(mba);
    
    mba = dao.getMultilevelBusinessArtifact(dbName,
                                            collectionName,
                                            "HumanMedicine");
    
    assertTrue(mba.isInState("Active"));
    assertFalse(mba.isInState("Discontinued"));
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "AnatomySummer2015");
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"start\" xmlns=\"\"/>"
    );

    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"finish\" xmlns=\"\"/>"
    );
    
    dao.macrostep(mba);
    dao.macrostep(mba);
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "HumanMedicine");
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"discontinue\" xmlns=\"\"/>"
    );
    
    dao.macrostep(mba);
    
    mba = dao.getMultilevelBusinessArtifact(dbName,
                                            collectionName,
                                            "HumanMedicine");
    
    assertTrue(mba.isInState("Active"));
    assertFalse(mba.isInState("Discontinued"));
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "AnatomyWinter2015");
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"start\" xmlns=\"\"/>"
    );

    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"finish\" xmlns=\"\"/>"
    );
    
    dao.macrostep(mba);
    dao.macrostep(mba);
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "HumanMedicine");
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"discontinue\" xmlns=\"\"/>"
    );
    
    dao.macrostep(mba);
    
    mba = dao.getMultilevelBusinessArtifact(dbName,
                                            collectionName,
                                            "HumanMedicine");
    
    assertFalse(mba.isInState("Active"));
    assertTrue(mba.isInState("Discontinued"));
  }
  

  @Test
  public void testSendDescendants() {
    String dbName = "myMBAse";
    String collectionName = "JohannesKeplerUniversity";
    MultilevelBusinessArtifact mba =
        dao.getMultilevelBusinessArtifact(dbName, collectionName, "JohannesKeplerUniversity");
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"addSchool\" xmlns=\"\">"
        + " <name xmlns=\"\">Medical</name>"
        + "</event>"
    );
    
    dao.macrostep(mba);
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "Medical");

    dao.initMba(mba);
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"addProgram\" xmlns=\"\">"
        + " <name xmlns=\"\">HumanMedicine</name>"
        + "</event>"
    );
    
    dao.macrostep(mba);
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "HumanMedicine");
    
    dao.initMba(mba);
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"addCourse\" xmlns=\"\">"
        + " <name xmlns=\"\">Anatomy</name>"
        + "</event>"
    );
    
    dao.macrostep(mba);
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "Anatomy");

    dao.initMba(mba);
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"offer\" xmlns=\"\"/>"
    );
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"addCourseInstance\" xmlns=\"\">"
        + " <name xmlns=\"\">AnatomySummer2015</name>"
        + "</event>"
    );
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"addCourseInstance\" xmlns=\"\">"
        + " <name xmlns=\"\">AnatomyWinter2015</name>"
        + "</event>"
    );
    
    dao.macrostep(mba);
    dao.macrostep(mba);
    dao.macrostep(mba);
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "AnatomySummer2015");

    dao.initMba(mba);
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "AnatomyWinter2015");

    dao.initMba(mba);
    
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "HumanMedicine");
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"done\" xmlns=\"\"/>"
    );
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"discontinue\" xmlns=\"\"/>"
    );
    
    dao.macrostep(mba);
    dao.macrostep(mba);
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "AnatomySummer2015");
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"start\" xmlns=\"\"/>"
    );

    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"finish\" xmlns=\"\"/>"
    );
    
    dao.macrostep(mba);
    dao.macrostep(mba);
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "HumanMedicine");
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"discontinue\" xmlns=\"\"/>"
    );
    
    dao.macrostep(mba);
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "AnatomyWinter2015");
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"start\" xmlns=\"\"/>"
    );

    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"finish\" xmlns=\"\"/>"
    );
    
    dao.macrostep(mba);
    dao.macrostep(mba);
    
    mba =
        new MultilevelBusinessArtifact(dbName,
                                       collectionName,
                                       "HumanMedicine");
    
    dao.enqueueExternalEvent(mba, 
          "<event name=\"discontinue\" xmlns=\"\"/>"
    );
    
    dao.macrostep(mba);
    
    mba = dao.getMultilevelBusinessArtifact(dbName,
                                            collectionName,
                                            "Anatomy");

    dao.macrostep(mba);
    dao.macrostep(mba);
    
    mba = dao.getMultilevelBusinessArtifact(dbName,
                                            collectionName,
                                            "Anatomy");

    assertFalse(mba.isInState("Planning"));
    assertFalse(mba.isInState("Available"));
    assertTrue(mba.isInState("Unavailable"));
  }
}
