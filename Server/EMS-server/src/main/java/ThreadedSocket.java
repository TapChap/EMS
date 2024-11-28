import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class ThreadedSocket extends Thread {
    private Socket socket;
    private int id;

    private BufferedReader in;
    private PrintWriter out;

    public ThreadedSocket(int id){
        this.id = id;
        this.start();
    }

    public int getClientId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public boolean isConnected(){
        if (this.socket == null) return false;
        return this.socket.isConnected();
    }

    @Override
    public String toString() {
        return (isConnected()? "client{" : "socket{") + getClientId() + "}";
    }

    public String todebugString(){
        return "id: " + getClientId() + ", isConnected: " + isConnected();
    }

    public void passToUser(int senderId, String message){
        out.println(new Packet(Map.of("senderId", senderId, "message", message)));
    }

    public void run(){
        // server - client code here
        // Input and output streams for communication

        try {
            this.socket = Server.getServerInstance().accept();
            System.out.println(this + " connected");
            Server.openNewSocket(); // create another socket for the next client

            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);

            // send the client it's id
            out.println(new Packet(Map.of("id", id)));

            // Chat loop
            while (true) {
                Packet incoming = Packet.fromString(in.readLine());
                String message = (String) incoming.get("message");
                int targetID = (Integer) incoming.get("targetId");

                if (message.equals("exit")) break;

                System.out.println("(" + this + " to client{" + targetID + "}): " + message);
                Server.sendMessageToID(message, targetID, getClientId());
            }

            socket.close();
            System.out.println("\nsocket with " + this + " closed.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
