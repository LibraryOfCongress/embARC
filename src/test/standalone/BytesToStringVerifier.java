package standalone;

public class BytesToStringVerifier {
    
    public static String toString(byte[] value) {
        StringBuilder result = new StringBuilder();
        boolean isAscii = true;
        
        for (byte b : value) {
            // Skip null bytes
            if (b == 0x00) continue;
            
            // ASCII printable range (32-126)
            if (b >= 32 && b <= 126) {
                result.append((char)b);
            } else {
                result.append(String.format("[0x%02x]", b));
                isAscii = false;
            }
        }
        
        return result.toString();
    }
    
    public static void main(String[] args) {
        // Test cases
        byte[] pureAscii = {'H', 'e', 'l', 'l', 'o'};
        byte[] pureNonAscii = {0x00, (byte)0xA9, (byte)0xAE};
        byte[] mixed = {'A', 'B', 'C', 0x00, (byte)0xA9, 'D', 'E', 'F'};
        
        System.out.println("Pure ASCII: " + toString(pureAscii));
        System.out.println("Pure Non-ASCII: " + toString(pureNonAscii));
        System.out.println("Mixed Content: " + toString(mixed));
    }
}
