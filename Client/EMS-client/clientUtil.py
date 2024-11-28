from socket import socket
from packet import Packet


# send message:
# server.sendall(f"{message}\n".encode('utf-8'))  # Send message to server

# receive message:
# response = server.recv(1024).decode('utf-8')  # Receive response
class ClientUtil:
    def __init__(self, server):
        self.server: socket = server

    def receive(self):
        return Packet.parse(self.server.recv(1024).decode('utf-8'))

    def send(self, packet):
        self.server.sendall(f"{packet}\n".encode('utf-8'))
