import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;


public class Klient extends JFrame implements ActionListener {
    private JTextArea area = new JTextArea(20, 30);
    private JPanel panel = new JPanel();
    private JScrollPane sp = new JScrollPane(area,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    private JTextField message = new JTextField(null,30);
    private JButton end_button = new JButton("Avsluta");
    private JButton skicka = new JButton("skicka");

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String namn;

    public Klient(Socket socket, String namn){
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.namn = namn;
        } catch(IOException e){
            stängAllt(socket, bufferedReader,bufferedWriter);
        }

        //Grafik
        setLayout(new BorderLayout());
        this.setTitle("Chatty Systems 2.0");
        panel.setLayout(new FlowLayout());
        this.add(end_button,BorderLayout.NORTH);
        this.add(sp,BorderLayout.CENTER);
        this.add(panel, BorderLayout.SOUTH);
        panel.add(message);
        panel.add(skicka);
        this.pack();
        this.setLocation(500, 300);
        this.setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        end_button.addActionListener(this);
        message.addActionListener(this);
        skicka.addActionListener(this);

    }
    public void actionPerformed(ActionEvent e) {
        String attSkicka;
            if (e.getSource() == end_button) {
                System.exit(0);
            }
            if(e.getSource() == message){
                attSkicka = message.getText();
                try {
                    bufferedWriter.write(namn+": "+attSkicka);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    message.setText("");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    stängAllt(socket, bufferedReader,bufferedWriter);
                }
            }
            //Ifall "enter" inte funkar för JtextField message.
            if(e.getSource()==skicka){

                attSkicka=message.getText();
                try {
                    bufferedWriter.write(namn+": "+attSkicka);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    message.setText("");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    stängAllt(socket, bufferedReader,bufferedWriter);
                }
            }
    }
    // En metod som lyssnar efter inkommande meddelanden
    // Ligger på egen tråd så lyssnande och skickande inte stoppar upp varandra
    public void inkommande(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String meddelandeFrånChat;
                while(socket.isConnected()){
                    try {
                        meddelandeFrånChat = bufferedReader.readLine();
                        area.append(meddelandeFrånChat);
                        area.append("\n");
                        area.setCaretPosition(area.getDocument().getLength());
                    }catch(IOException e){
                        stängAllt(socket, bufferedReader,bufferedWriter);
                    }
                }

            }
        }).start();
    }

    public void stängAllt(Socket socket, BufferedReader bufferedReader,BufferedWriter bufferedWriter){
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



    public static void main(String[] args) throws IOException{
        //Här får du ge ditt namn/username innan vi startar
        String namn = JOptionPane.showInputDialog(null, "Ange Namn:");
        if (namn == null || namn.length() == 0) {
            System.exit(0);
        }
        //Här kan du ange host
        String host= JOptionPane.showInputDialog(null, "Ange Host:","localhost");
        if (host == null || host.length() == 0) {
            System.exit(0);
        }
        Socket socket = new Socket(host, 12345);

        //Lyckades inte komma på nåt snyggare sätt att skicka namn till KlientHanterare
        BufferedWriter buff= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        buff.write(namn);
        buff.newLine();
        buff.flush();


        //Skapa upp en klient och starta den lyssnande inkommande-metoden
        Klient klient = new Klient(socket,namn);
        klient.inkommande();
    }
}
