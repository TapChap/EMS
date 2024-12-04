import socket

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
        mode = input("send or receive? (s/r): ")

        if mode == 'exit':
            util.send(Packet({"targetName": -1, "message": "exit"}))
            break

        elif mode == 's':
            targetName = input("target name: ")
            message = input("message: ")

            util.send(Packet({"targetName": targetName, "message": message}));
            print("message sent\n")

        else:
            print("\nwaiting for incoming messages...")
            incoming = util.receive()

            print(f"message from {incoming["senderName"]}:\n{incoming["message"]}\n")

    # server.close()
    print("Disconnected.")


if __name__ == "__main__":
    main()
