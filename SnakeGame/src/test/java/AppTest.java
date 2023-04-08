import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class AppTest {

    @Test
    public void testApplicationIsRunningOnPort8080() throws Exception {
        // Set the URL to the root of your application
        URL url = new URL("http://localhost:8080");

        // Open a connection to the URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the request method to GET
        connection.setRequestMethod("GET");

        // Send the request and get the response code
        int responseCode = connection.getResponseCode();

        // Verify that the response code is 200 OK
        assertEquals(HttpURLConnection.HTTP_OK, responseCode);
    }
}
