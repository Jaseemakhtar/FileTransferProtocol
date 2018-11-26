import java.io.*;
import java.net.*;
import java.util.*;

public class Sender{
	public static void main(String[] args){
		File inputFile = null;
		FileInputStream fileInputStream = null;

		System.out.println("Enter ip address to connect");
		Scanner input = new Scanner(System.in);
		String ip = input.nextLine();
		String inpt = "";
		OutputStream os = null;
		InputStream is = null;
		DataOutputStream dos = null;
		DataInputStream dis = null;
		Socket server = null;

		try{
			server = new Socket(ip, 7313);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}

		try{
			os = server.getOutputStream();
			is = server.getInputStream();
			System.out.println("Connected: " + server.getInetAddress());
			dos = new DataOutputStream(server.getOutputStream());
			dis = new DataInputStream(is);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}

		while(true){

			try{
				System.out.println("Enter filename");
				inpt = input.nextLine();
				inputFile = new File(inpt);
				fileInputStream = new FileInputStream(inputFile);


				long fileSize = inputFile.length();

				dos.writeUTF("receive");

				String ack = dis.readUTF();

				System.out.println("Acknowledgement: " + ack);

				if(ack.equals("ok")){
					dos.writeUTF(inputFile.getName());
					dos.writeLong(inputFile.length());

					int read;
					int total = 0;
					byte[] bytes = new byte[4 * 1024];
					int fileLength = (int) (fileSize / 1024);
					long receivedKB;
					int percent;
					String per;

					while((read = fileInputStream.read(bytes)) > 0){
						os.write(bytes, 0, read);
						total += read;
						receivedKB = total / 1024;
						percent = (int) ((Math.floor(receivedKB) / fileLength) * 100);
						per = "\r" + percent + "%";
						System.out.write(per.getBytes());
					}
					dos.flush();
					os.flush();
					System.out.println();

					String successResult = dis.readUTF();
					if(successResult.equals("success")){
						System.out.println("Client received it successfully");
					}
					
					System.out.println("Completed");

					fileInputStream.close();
				}

			}catch(Exception e){
				System.out.println("Error: " + e.getMessage());
				break;
			}
		}

	}
}
