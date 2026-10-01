import java.util.Base64;

/**
 * Quick test to verify encryption/decryption
 * Run this to debug password encryption issues
 */
public class EncryptionTest {
    public static void main(String[] args) {
        // Your encrypted password from the request
        String encrypted = "YWRtaW4xMjN8MTc3NjQ0MDk1MTMyNw==";

        try {
            // Decode from Base64
            String decoded = new String(Base64.getDecoder().decode(encrypted));
            System.out.println("Decoded: " + decoded);

            // Split by '|'
            String[] parts = decoded.split("\\|");
            System.out.println("Password: " + parts[0]);
            System.out.println("Timestamp: " + parts[1]);

            // Validate timestamp
            long requestTime = Long.parseLong(parts[1]);
            long currentTime = System.currentTimeMillis();
            long diff = currentTime - requestTime;
            long maxAgeMs = 5 * 60 * 1000; // 5 minutes

            System.out.println("Current Time: " + currentTime);
            System.out.println("Request Time: " + requestTime);
            System.out.println("Diff (ms): " + diff);
            System.out.println("Max Age (ms): " + maxAgeMs);
            System.out.println("Valid: " + (diff < maxAgeMs));

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

