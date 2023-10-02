import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket){
        this.serverSocket= serverSocket;
    }
    public void startaServer(){
        try{
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("Ny klient uppkopplad!");
                KlientHanterare klientHanterare = new KlientHanterare(socket);

                Thread tråd = new Thread(klientHanterare);
                tråd.start();
            }
        }catch(IOException e){
            e.printStackTrace();

        }
    }
    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(12345);
        Server server = new Server(serverSocket);
        server.startaServer();
    }
}
