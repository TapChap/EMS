import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Server {
    private static ServerSocket server;
    private static int port = 12345;
    private static int backlog = 3;
    private static int maxUsers = 10;
    private static String ip = "127.0.0.1";

    private static ArrayList<ThreadedSocket> clients = new ArrayList<>();
    private static HashMap<String, Integer> usernames = new HashMap<>();

    public synchronized static ServerSocket getServerInstance() throws IOException {
        if (server == null) server = new ServerSocket(port, backlog, InetAddress.getByName("127.0.0.1"));

        return server;
    }

    public static void openNewSocket(){
        for (int i = 0; i < clients.size(); i++) {
            if (!clients.get(i).isConnected()) {
                System.out.println("replacing client " + i + " with a new socket");
                clients.set(i, new ThreadedSocket(i));
                return;
            }
        }

        if (clients.size() < maxUsers){
            System.out.println("opening new socket with id: " + clients.size() + "\n");
            clients.add(new ThreadedSocket(clients.size()));
        }
    }

    public static void initServer() throws InterruptedException {
        ThreadedSocket newSocket = new ThreadedSocket(0);
        clients.add(newSocket);
        newSocket.join();
    }

    public static int sendMessageToID(String message, String targetName, String senderName){
        ThreadedSocket recipient = null;
        for(ThreadedSocket socket: clients){
            if (socket.getUsername().equals(targetName)) {
                recipient = socket;
                break;
            }
        }

        if (recipient == null) return 404;

        recipient.passToUser(senderName, message);
        return 200;
    }

    private static int getUserByName(String name){
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getUsername().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private static void inviteToChat(String user, String sender){
        clients.get(getUserByName(user)).askToChat(sender);
    }

    private static void updateUsernames(){
        ArrayList<String> clientNames = new ArrayList<>();
        for (ThreadedSocket client: clients){
            if(client.isConnected()) clientNames.add(client.getUsername());
        }

        usernames.forEach((key, val)-> {
            if (!clientNames.contains(key)) usernames.remove(key);
        });
    }

    public static boolean signUser(String username, int id){
        if (Server.usernames.containsKey(username)) return false;
        updateUsernames();
        usernames.put(username, id);
        return true;
    }

    public static void removeUser(ThreadedSocket socket){
        clients.remove(socket);
        usernames.remove(socket.getUsername());
    }

    public static Set<String> getConnectedUsers(){
        return usernames.keySet();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        getServerInstance(); // create the server instance
        System.out.println("server running on ip: " + ip + ", port: " + port);
        System.out.println();

        initServer();
    }
}
