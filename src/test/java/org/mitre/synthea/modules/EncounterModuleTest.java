package org.mitre.synthea.modules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mitre.synthea.world.agents.Payer;
import org.mitre.synthea.world.agents.Person;
import org.mitre.synthea.world.agents.Provider;
import org.mitre.synthea.world.concepts.HealthRecord.Encounter;
import org.mitre.synthea.world.geography.Location;

public class EncounterModuleTest {

  private Location location;
  private Person person; 
  private EncounterModule module;
  
  /**
   * Setup the Encounter Module Tests.
   * @throws IOException on loading error
   */
  @Before
  public void setup() throws IOException {
    person = new Person(0L);
    // Give person an income to prevent null pointer.
    person.attributes.put(Person.INCOME, 100000);
    person.attributes.put(Person.BIRTHDATE, 0L);
    location = new Location("Massachusetts", null);
    location.assignPoint(person, location.randomCityName(person.random));
    Provider.loadProviders(location, 1L);
    module = new EncounterModule();
    // Ensure Person's Payer is not null.
    Payer.loadNoInsurance();
    person.setPayerAtTime(System.currentTimeMillis(), Payer.noInsurance);
  }

  @Test
  public void testEncounterHasClinician() {
    module.process(person, System.currentTimeMillis());
    assertNotNull(person.record);
    assertFalse(person.record.encounters.isEmpty());
    int last = person.record.encounters.size() - 1;
    Encounter encounter = person.record.encounters.get(last);
    assertNotNull("Encounter must have clinician", encounter.clinician);
    assertNotNull("Encounter must have provider organization", encounter.provider);
  }
  
  @Test
  public void testEmergencySymptomEncounterHasClinician() {
    person.setSymptom("Test", "Test", EncounterModule.EMERGENCY_SYMPTOM_THRESHOLD + 1, false);
    module.process(person, System.currentTimeMillis());
    assertNotNull(person.record);
    assertFalse(person.record.encounters.isEmpty());
    int last = person.record.encounters.size() - 1;
    Encounter encounter = person.record.encounters.get(last);
    assertNotNull("Encounter must have clinician", encounter.clinician);
    assertNotNull("Encounter must have provider organization", encounter.provider);
  }

  @Test
  public void testUrgentcareSymptomEncounterHasClinician() {
    person.setSymptom("Test", "Test", EncounterModule.URGENT_CARE_SYMPTOM_THRESHOLD + 1, false);
    module.process(person, System.currentTimeMillis());
    assertNotNull(person.record);
    assertFalse(person.record.encounters.isEmpty());
    int last = person.record.encounters.size() - 1;
    Encounter encounter = person.record.encounters.get(last);
    assertNotNull("Encounter must have clinician", encounter.clinician);
    assertNotNull("Encounter must have provider organization", encounter.provider);
  }

  @Test
  public void testPrimarySymptomEncounterHasClinician() {
    person.setSymptom("Test", "Test", EncounterModule.PCP_SYMPTOM_THRESHOLD + 1, false);
    module.process(person, System.currentTimeMillis());
    assertNotNull(person.record);
    assertFalse(person.record.encounters.isEmpty());
    int last = person.record.encounters.size() - 1;
    Encounter encounter = person.record.encounters.get(last);
    assertNotNull("Encounter must have clinician", encounter.clinician);
    assertNotNull("Encounter must have provider organization", encounter.provider);
  }
}
