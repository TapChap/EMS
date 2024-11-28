import json

class Packet:
    def __init__(self, data):
        """
        Initialize a packet with a dictionary of key-value pairs.
        """
        if not isinstance(data, dict):
            raise TypeError("Packet data must be a dictionary.")
        self.data = data

    def __str__(self):
        """
        Convert the packet to a JSON string.
        """
        return json.dumps(self.data)

    @classmethod
    def parse(cls, string):
        """
        Create a Packet object from a JSON string.
        """
        data = json.loads(str(string))
        return cls(data)

    def __getitem__(self, key):
        """
        Allow direct access to packet data via key lookup.
        """
        return self.data[key]

    # Example usage
if __name__ == "__main__":
    # Create a packet
    packet = Packet({"sensor": "temperature", "value": 22.5, "unit": "Celsius"})

    # Convert to JSON string
    packet_string = packet
    print(f"Serialized Packet: {packet_string}")

    # Reconstruct packet from JSON string
    reconstructed_packet = Packet.parse(packet_string)
    print(f"Sensor: {reconstructed_packet['sensor']}")
    print(f"Value: {reconstructed_packet['value']}")
