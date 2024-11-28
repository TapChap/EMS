import com.google.gson.Gson;

import java.util.Map;

public class Packet {
    private final Map<String, Object> data;

    // Gson instance for JSON serialization/deserialization
    private static final Gson gson = new Gson();

    // Constructor
    public Packet(Map<String, Object> data) {
        this.data = data;
    }

    // Convert the packet to a JSON string
    public String data() {
        return gson.toJson(this.data);
    }

    @Override
    public String toString() {
        return this.data();
    }

    // Static method to create a Packet from a JSON string
    public static Packet fromString(String jsonString) {
        Map<String, Object> data = gson.fromJson(jsonString, Map.class);
        return new Packet(data);
    }

    // Get a value from the packet by key
    public Object get(String key) {
        Object val = this.data.get(key);

        if (val instanceof Double) return (int)(val.toString().charAt(0)) - 48;
        return this.data.get(key);
    }

    // Example usage
    public static void main(String[] args) {
        // Create a packet
        Packet packet = new Packet(Map.of("sensor", "temperature", "value", 22.5, "unit", "Celsius"));

        // Convert to JSON string
        String packetString = packet.data();
        System.out.println("Serialized Packet: " + packetString);

        // Reconstruct packet from JSON string
        Packet reconstructedPacket = Packet.fromString(packetString);
        System.out.println("Sensor: " + reconstructedPacket.get("sensor"));
        System.out.println("Value: " + reconstructedPacket.get("value"));
    }
}
