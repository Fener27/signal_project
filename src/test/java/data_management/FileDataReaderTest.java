package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.PatientRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Tests for FileDataReader to ensure it accurately parses files and 
 * populates DataStorage.
 */
public class FileDataReaderTest {

    /**
     * Create a temporary directory for file tests.
     */
    @TempDir
    Path tempDir;

    @Test
    void testReadDataPopulatesStorageCorrectly() throws IOException {
        Path mockFile = tempDir.resolve("test_data.txt");
        String content = "Patient ID: 1, Timestamp: 1714730000000, Label: Cholesterol, Data: 180.5\n" +
                         "Patient ID: 2, Timestamp: 1714730000000, Label: Saturation, Data: 95%";
        Files.writeString(mockFile, content);

        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader(tempDir.toString());

        reader.readData(storage);

        // Verifying patient 1 data
        List<PatientRecord> records1 = storage.getRecords(1, 1714730000000L, 1714730000000L);
        assertEquals(1, records1.size());
        assertEquals(180.5, records1.get(0).getMeasurementValue());
        assertEquals("Cholesterol", records1.get(0).getRecordType());

        //Verifying patient 2 data with percentage parsing
        List<PatientRecord> records2 = storage.getRecords(2, 1714730000000L, 1714730000000L);
        assertEquals(1, records2.size());
        assertEquals(95.0, records2.get(0).getMeasurementValue());
    }

    @Test
    void testReadDataHandlesMalformedLines() throws IOException {
        Path mockFile = tempDir.resolve("malformed_data.txt");
        String content = "Patient ID: 3, Timestamp: 1714730000000, Label: ECG, Data: 0.5\n" +
                         "This is a bad line that should be skipped";
        Files.writeString(mockFile, content);

        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader(tempDir.toString());

        // Skipping bad lines
        assertDoesNotThrow(() -> reader.readData(storage));
        
        List<PatientRecord> records = storage.getRecords(3, 1714730000000L, 1714730000000L);
        assertEquals(1, records.size());
    }

    @Test
    void testReadDataWithInvalidDirectoryThrowsException() {
        // Using a path that definitely doesn't exist
        FileDataReader reader = new FileDataReader("/non/existent/path/at/all");
        DataStorage storage = new DataStorage();

        assertThrows(IOException.class, () -> reader.readData(storage));
    }
}