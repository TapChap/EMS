import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {
    private static ServerSocket server;
    private static int port = 12345;
    private static int backlog = 3;
    private static int maxUsers = 10;
    private static String ip = "127.0.0.1";

    private static ArrayList<ThreadedSocket> clients = new ArrayList<>();

    public synchronized static ServerSocket getServerInstance() throws IOException {
        if (server == null) server = new ServerSocket(port, backlog, InetAddress.getByName("127.0.0.1"));

        return server;
    }

    public static void openNewSocket(){
        for (int i = 0; i < clients.size(); i++) {
            if (!clients.get(i).isConnected()) {
                System.out.println("replacing client " + i + " with a new socket");
                clients.set(i, new ThreadedSocket(i));
                break;
            }
        }

        if (clients.size() < maxUsers){
            System.out.println("opening new socket with id: " + clients.size() + "\n");
            clients.add(new ThreadedSocket(clients.size()));
        }
    }

    public static void waitForClient() throws InterruptedException {
        ThreadedSocket newSocket = new ThreadedSocket(0);
        clients.add(newSocket);
        newSocket.join();
    }

    public static void sendMessageToID(String message, int targetId, int senderId){
        clients.get(targetId).passToUser(senderId, message);
    }

//    public synchronized static void removeClient(ThreadedSocket client) {
//        clients.set(client.getClientId(), new ThreadedSocket());
//        clients.get(client.getClientId()).start();
//    }

    public static void main(String[] args) throws IOException, InterruptedException {
        getServerInstance(); // create the server instance
        System.out.println("server running on ip: " + ip + ", port: " + port);
        System.out.println();

        waitForClient();
    }
}
