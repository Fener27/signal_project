package data_management;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.MyWebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Test suite for verifying the functionality of the MyWebSocketClient.
 * The tests focus on message parsing accuracy.
 */
public class WebSocketClientTest {
    private DataStorage mockStorage;
    private MyWebSocketClient client;

    @BeforeEach
    void setUp() throws URISyntaxException {
        mockStorage = mock(DataStorage.class);
        client = new MyWebSocketClient(new URI("ws://localhost:8080"));
        client.readData(mockStorage);
    }

    /**
     * Verifies that the client correctly parses a valid CSV message string
     * and successfully adds it to the DataStorage.
     */
    @Test
    void testOnMessageParsesValidData() {
        // Simulating the format
        String validMessage = "1,1714854000,SystolicBP,120.0";
        
        client.onMessage(validMessage);

        // Verifying that the data was sent to storage correctly
        verify(mockStorage, times(1)).addPatientData(1, 120.0, "SystolicBP", 1714854000L);
    }

    /**
     * Tests the client's behavior when receiving malformed or incomplete data.
     */
    @Test
    void testOnMessageHandlesMalformedData() {
        String malformedMessage = "Invalid,Data,Format";
        
        assertDoesNotThrow(() -> client.onMessage(malformedMessage));
        
        verify(mockStorage, never()).addPatientData(anyInt(), anyDouble(), anyString(), anyLong());
    }

    /**
     * Tests the client's behavior when receiving partial data.
     */
    @Test
    void testOnMessageHandlesPartialData() {
        // Test a message with missing fields
        String partialMessage = "1,1714854000,SystolicBP";
        
        client.onMessage(partialMessage);
        
        verify(mockStorage, never()).addPatientData(anyInt(), anyDouble(), anyString(), anyLong());
    }

    /**
     * Tests possible network errors.
     */
    @Test
    void testOnErrorHandling() {
        // Handling network errors
        Exception testException = new Exception("Connection Reset");
        
        assertDoesNotThrow(() -> client.onError(testException));
    }
}
