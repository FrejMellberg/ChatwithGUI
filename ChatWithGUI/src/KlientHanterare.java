import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class KlientHanterare implements Runnable{

    public static ArrayList<KlientHanterare> klienter = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String namn;


    public KlientHanterare(Socket socket) {
        try{
            this.socket = socket;
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.namn = bufferedReader.readLine();
            klienter.add(this);
            tillAlla("Välkommen till chatten "+namn+"!");

        }catch(IOException e){
            stängAllt(socket, bufferedReader,bufferedWriter);
        }
    }
    //Tråd metod som läser från sin klients outputstream(denna sockets inputstream)
    //och sen, via tillAlla(), skickar den vidare.
    @Override
    public void run(){
        String frånKlient;

        while(socket.isConnected()){
            try {
                frånKlient = bufferedReader.readLine();
                tillAlla(frånKlient);
            }catch (IOException e){
                stängAllt(socket, bufferedReader,bufferedWriter);
                break;
            }
        }
    }
    // Metod som loopar igenom klientlistan och skriver ut till alla
    public void tillAlla(String frånKlient){
        for (KlientHanterare klientHanterare : klienter){
            try{
                klientHanterare.bufferedWriter.write(frånKlient);
                klientHanterare.bufferedWriter.newLine();
                klientHanterare.bufferedWriter.flush();

            }catch (IOException e){
                stängAllt(socket, bufferedReader,bufferedWriter);
            }
        }
    }
    // Metod som tar bort klienten ur listan och
    // upplyser resten av chatten om att användaren är borta
    public void taBortKlient(){
        klienter.remove(this);
        tillAlla(namn+" har övergett oss!");

    }
    //Metod som stänger ner allt vid IOExceptions, och sign off.
    public void stängAllt(Socket socket, BufferedReader bufferedReader,BufferedWriter bufferedWriter){
        taBortKlient();
        try {
            if (bufferedReader!=null){
                bufferedReader.close();
            }
            if (bufferedWriter!=null) {
                bufferedWriter.close();
            }
            if (socket!=null) {
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
