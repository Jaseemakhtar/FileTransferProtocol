import java.io.*;
import java.net.*;
import java.util.*;

public class Receiver{
	public static void main(String[] args){
		ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(7313);
        } catch (IOException ex) {
            System.out.println("Can't setup server on this port number. ");
        }

        Socket socket = null;
        InputStream in = null;
        OutputStream os = null;
	    FileOutputStream out = null;
	    DataOutputStream dos = null;
	    DataInputStream dis = null;
        long fileSize = 0;
        String fileName = "";

        try {
	        System.out.println("Waiting for client @: " + serverSocket.getInetAddress());
            socket = serverSocket.accept();
    	    System.out.println("Connected client: " + socket.getInetAddress());
        } catch (IOException ex) {
            System.out.println("Can't accept client connection. ");
        }

        try {
            in = socket.getInputStream();
            os = socket.getOutputStream();
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream. ");
        }

        try {
            dis  = new DataInputStream(in);
            dos = new DataOutputStream(os);
        }catch(Exception e){
            System.out.println(e);
        }

        while(true){
            String ack;

            try{
                ack = dis.readUTF();
                if(ack.equals("receive")){
                    dos.writeUTF("ok");
                }else{
                    return;
                }
                fileName = dis.readUTF();
                fileSize = dis.readLong();

                System.out.println("FileName: " + fileName);
                System.out.println("FileLength: " + fileSize);
                out = new FileOutputStream(fileName);
            } catch (FileNotFoundException ex) {
                System.out.println("File not found. ");
                break;
            }catch (IOException ex){
                System.out.println("Error: " + ex.getMessage());
                break;
            }

            byte[] bytes = new byte[4 * 1024];

            int count = 0;
            int total = 0;

            int fileLength = (int) (fileSize / 1024);
            long receivedKB;
            int percent;
            String per;

            try{

                while (fileSize > 0 && (count = in.read(bytes)) != -1) {
                    out.write(bytes, 0, count);
                    total += count;
                    fileSize -= count;

                    receivedKB = total / 1024;
                    percent = (int) ((Math.floor(receivedKB) / fileLength) * 100);
                    per = "\r" + percent + "%";
                    System.out.write(per.getBytes());
                }
                out.flush();
                dos.writeUTF("success");
                System.out.println();
            }catch(IOException e){
                System.out.println("Error: " + e.getMessage());
                break;
            }
    }
}
}
