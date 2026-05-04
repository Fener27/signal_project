package alerts;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.alerts.factories.*;
import com.alerts.strategies.*;
import com.alerts.Alert;
import com.alerts.AlertInterface;
import com.alerts.decorators.*;

public class DesignPatternTest {
    
    @Test
    void testSingletonPattern() {
        // DataStorage must be a singleton
        DataStorage instance1 = DataStorage.getInstance();
        DataStorage instance2 = DataStorage.getInstance();
        assertNotNull(instance1);
        assertSame(instance1, instance2, "Singleton should return the same instance");
    }

    @Test
    void testFactoryMethodPattern() {
        // Factory should create alerts without specifying exact classes
        AlertFactory factory = new BloodPressureAlertFactory();
        Alert alert = factory.createAlert("P001", "High Systolic", 12345L);
        
        assertNotNull(alert);
        assertTrue(alert.getCondition().contains("BP Alert"), "Factory should add specific prefix");
    }

    @Test
    void testStrategyPattern() {
        // Strategies should encapsulate algorithms for monitoring
        BloodPressureStrategy bpStrategy = new BloodPressureStrategy();
        Patient patient = new Patient(1);
        
        // Verifying the strategy class
        assertDoesNotThrow(() -> {
            bpStrategy.checkAlert(patient, patient.getRecords(0, Long.MAX_VALUE));
        });
    }

    @Test
    void testDecoratorPattern() {
        // Decorators should add additional conditions dynamically
        Alert baseAlert = new Alert("1", "Critical Condition", 12345L);
        
        AlertInterface priorityAlert = new PriorityAlertDecorator(baseAlert, "URGENT");
        AlertInterface repeatedPriorityAlert = new RepeatedAlertDecorator(priorityAlert);
        
        String condition = repeatedPriorityAlert.getCondition();
        assertTrue(condition.contains("URGENT PRIORITY"), "Should have priority decoration");
        assertTrue(condition.contains("REPEATED"), "Should have repeated decoration");
    }
}
