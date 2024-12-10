import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class ThreadedSocket extends Thread {
    private Socket socket;
    private int id;
    private String username;

    private BufferedReader in;
    private PrintWriter out;

    public ThreadedSocket(int id){
        this.id = id;
        this.start();
    }

    public int getClientId(){
        return this.id;
    }

    public String getUsername() {
        return username;
    }

    public void setId(int id){
        this.id = id;
    }

    public boolean isConnected(){
        if (this.socket == null) return false;
//        System.out.println("client{" + this.id+"} " + (this.socket.isConnected()? "connected" : "disconnected"));
        return this.socket.isConnected();
    }

    @Override
    public String toString() {
        return (isConnected()? "client{" : "socket{") + getClientId() + "}";
    }

    public void passToUser(String senderName, String message){
        out.println(new Packet(Map.of("senderName", senderName, "message", message)));
    }

    public void run() {
        // server - client code here
        // Input and output streams for communication

        try {
            this.socket = Server.getServerInstance().accept();
            System.out.println(this + " connected");
            Server.openNewSocket(); // create another socket for the next client

            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);

            this.username = (String) Packet.parse(in.readLine()).get("username");
            System.out.println("user signin attempt with username: " + this.username);

            // auth system
            if (!Server.signUser(this.username, this.id)) {
                out.println(new Packet(Map.of("id", -1)));
                System.out.println("signin fail, closing connection");
                socket.close();
                return;
            } else {
                out.println(new Packet(Map.of("id", this.id)));

                System.out.println("sign in successful");
            }

            Object[] connectedUsers = Server.getConnectedUsers().toArray();
            ArrayList<String> foreignUsername = new ArrayList<>(); // all usernames excluding this one

            for (Object name: connectedUsers) if (!name.equals(this.username)) foreignUsername.add((String) name);

            out.println(new Packet(Map.of("users", foreignUsername.toArray())));

            // Chat loop
            while (true) {
                Packet incoming = Packet.parse(in.readLine());
                String targetName = (String) incoming.get("targetUser");

                if (targetName.equals("exit")) break;

                System.out.println("(" + this + " to client{" + targetName + "}): " + message);
                out.println(new Packet(Map.of("status", Server.sendMessageToID(message, targetName, this.username))));

            }

            socket.close();
            System.out.println("\nsocket with " + this + " closed.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void askToChat(String sender) throws IOException {
        out.println(new Packet(Map.of("askToChat", sender)));
        Packet response = Packet.parse(in.readLine());
    }
}
