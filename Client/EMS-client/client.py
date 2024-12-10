import socket

from ContinuesListener import ContinuesListener
from clientUtil import ClientUtil
from packet import Packet


def main():
    # host = '192.168.181.111'  # server's IP address
    host = '127.0.0.1'  # server's IP address
    port = 12345  # server's port

    # Create a socket and connect to the server
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.connect((host, port))

    util = ClientUtil(server)

    print("\nConnected to the server.")
    # print(f"myId: {myId}\n")

    username = input("username: ")
    util.send(Packet({"username": username}))

    signin = util.receive()
    myId = signin["id"]

    if myId == -1:
        print("username already taken")
        return

    users = util.receive()
    connectedUsers = users["users"]

    print("sign in successful")
    print("connected users:")
    print(connectedUsers)

    print("'exit' to quit")

    while True:
        targetUser = input("who would you like to message with?")

        if targetUser == 'exit':
            break

        util.send(Packet({"targetUser", targetUser}))
        print(f"waiting for {targetUser}'s approval")

        incoming = util.receive()
        if incoming["reponse"] == 200:
            print(f"entering chat with {targetUser}, type end to exit chat")
            listener = ContinuesListener(targetUser)
            listener.run()

            while listener.is_alive():
                message = input("message: ")
                util.send(Packet({"message", message}))

            print(f"{targetUser} has left the chat")

        else:
            print(f"{targetUser} has declined the chat invitation\n")

    # server.close()
    print("Disconnected.")

    # util.send(Packet({"key1": val1, "key2": val2}));
    # incoming = util.receive()


if __name__ == "__main__":
    main()
