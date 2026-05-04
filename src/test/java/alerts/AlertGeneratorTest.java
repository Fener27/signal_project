package alerts;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.alerts.AlertGenerator;
import com.alerts.strategies.BloodPressureStrategy;
import com.alerts.strategies.HeartRateStrategy;
import com.alerts.strategies.HypotensiveHypoxemiaStrategy;
import com.alerts.strategies.OxygenSaturationStrategy;

import java.io.ByteArrayOutputStream;

/**
 * Tests for AlertGenerator to verify critical thresholds.
 */
public class AlertGeneratorTest {
    private DataStorage storage;
    private AlertGenerator generator;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new java.io.PrintStream(outputStreamCaptor));

        storage = DataStorage.getInstance();
        generator = new AlertGenerator(storage);
        
        generator.addStrategy(new BloodPressureStrategy());
        generator.addStrategy(new OxygenSaturationStrategy());
        generator.addStrategy(new HeartRateStrategy());
        generator.addStrategy(new HypotensiveHypoxemiaStrategy());
    }

    @Test
    void testSystolicHighThresholdTriggersAlert() {
        // Testing if threshold is > 180
        int patientId = 1;
        storage.addPatientData(patientId, 190.0, "SystolicBP", System.currentTimeMillis());
        Patient patient = storage.getPatient(patientId);

        generator.evaluateData(patient);

        assertTrue(outputStreamCaptor.toString().contains("Critical Systolic BP"), 
            "Alert should trigger for Systolic BP above 180");
    }

    @Test
    void testSystolicLowThresholdTriggersAlert() {
        // Test if threshold is < 90
        int patientId = 2;
        storage.addPatientData(patientId, 85.0, "SystolicBP", System.currentTimeMillis());
        Patient patient = storage.getPatient(patientId);

        generator.evaluateData(patient);

        assertTrue(outputStreamCaptor.toString().contains("Critical Systolic BP"), 
            "Alert should trigger for Systolic BP below 90");
    }

    @Test
    void testLowSaturationTriggersAlert() {
        // Test if threshold is < 92%
        int patientId = 3;
        storage.addPatientData(patientId, 91.0, "Saturation", System.currentTimeMillis());
        Patient patient = storage.getPatient(patientId);

        generator.evaluateData(patient);

        assertTrue(outputStreamCaptor.toString().contains("Low Saturation"), 
            "Alert should trigger for Saturation below 92%");
    }

    @Test
    void testNormalVitalsDoNotTriggerAlert() {
        int patientId = 4;
        storage.addPatientData(patientId, 120.0, "SystolicBP", System.currentTimeMillis());
        storage.addPatientData(patientId, 95.0, "Saturation", System.currentTimeMillis());
        Patient patient = storage.getPatient(patientId);

        generator.evaluateData(patient);

        assertTrue(outputStreamCaptor.toString().isEmpty(), 
            "No alerts should trigger for normal vital signs");
    }

    @Test
    void testRapidSaturationDropAlert() {
        int patientId = 6;
        long startTime = System.currentTimeMillis();
        
        // Initial reading: 98%
        storage.addPatientData(patientId, 98.0, "Saturation", startTime);
        // 6% drop within < 10 mins
        storage.addPatientData(patientId, 92.0, "Saturation", startTime + (9 * 60 * 1000));

        generator.evaluateData(storage.getPatient(patientId));

        assertTrue(outputStreamCaptor.toString().contains("Rapid Saturation Drop"),
            "Alert should trigger for a 5%+ drop within 10 minutes [cite: 108]");
    }

    @Test
    void testSaturationDropAlert() {
        int patientId = 7;
        long startTime = System.currentTimeMillis();
        
        // Initial reading: 98%
        storage.addPatientData(patientId, 98.0, "Saturation", startTime);
        // The drop is 6%, but the time interval is too long
        storage.addPatientData(patientId, 92.0, "Saturation", startTime + (11 * 60 * 1000));

        generator.evaluateData(storage.getPatient(patientId));

        assertFalse(outputStreamCaptor.toString().contains("Rapid Saturation Drop"),
            "Alert should NOT trigger if the 5% drop takes longer than 10 minutes ");
    }

    @Test
    void testHypotensiveHypoxemiaAlert() {
        int patientId = 8;
        long now = System.currentTimeMillis();
        
        // Meets the condition
        storage.addPatientData(patientId, 85.0, "SystolicBP", now);
        storage.addPatientData(patientId, 90.0, "Saturation", now);

        generator.evaluateData(storage.getPatient(patientId));

        assertTrue(outputStreamCaptor.toString().contains("Hypotensive Hypoxemia"),
            "Combined alert should trigger when both BP and Saturation are critically low [cite: 110]");
    }

    @Test
    void testECGPeakTriggersAlert() {
        int patientId = 9;
        long now = System.currentTimeMillis();

        // Adding 10 baseline records of 0.5
        for (int i = 0; i < 10; i++) {
            storage.addPatientData(patientId, 0.5, "ECG", now + (i * 1000));
        }

        storage.addPatientData(patientId, 1.2, "ECG", now + 11000);
        generator.evaluateData(storage.getPatient(patientId));

        assertTrue(outputStreamCaptor.toString().contains("Abnormal ECG Peak"),
            "Alert should trigger when an ECG peak exceeds the sliding window average");
    }

    @AfterEach
    void tearDown() {
        // Reset the system output
        System.setOut(System.out);
    }
}