package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;

import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * Tests for the Patient class to verify record management and filtering.
 */
public class PatientTest {

    @Test
    void testGetRecordsFiltersByTimeCorrectly() {
        Patient patient = new Patient(101);
        patient.addRecord(98.0, "Saturation", 1000L);
        patient.addRecord(95.0, "Saturation", 2000L);
        patient.addRecord(92.0, "Saturation", 3000L);

        // Requesting records between 1500 and 2500
        List<PatientRecord> records = patient.getRecords(1500L, 2500L);

        assertEquals(1, records.size(), "Should only return one record within the range");
        assertEquals(95.0, records.get(0).getMeasurementValue());
    }

    @Test
    void testGetRecordsWithInclusiveBoundaries() {
        Patient patient = new Patient(102);
        patient.addRecord(120.0, "SystolicPressure", 1000L);
        patient.addRecord(130.0, "SystolicPressure", 2000L);

        // Filtering exactly on the timestamps
        List<PatientRecord> records = patient.getRecords(1000L, 2000L);

        assertEquals(2, records.size(), "Should include records exactly at the boundary times");
    }
    
    @Test
    void testGetRecordsReturnsEmptyWhenNoneInRange() {
        Patient patient = new Patient(103);
        patient.addRecord(70.0, "HeartRate", 5000L);

        // Filtering for a range before the record exists
        List<PatientRecord> records = patient.getRecords(0L, 1000L);

        assertTrue(records.isEmpty(), "Should return an empty list when no records match the criteria");
    }
}