package servidor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Servidor extends Thread{

	public final static int DEFAULT_PORT = 5025; // porto onde vai ficar á espera

	
	private ServerSocket serverSocket;
	private Socket socket;
	private HashMap<Socket,String> listaEmEspera = new HashMap<>();
	
	HandleLoginThread  login;
	
	
	public Servidor() {
		serverSocket = null;
		socket       = null;
	}
	
	public void run() {
		try {
			serverSocket = new ServerSocket(DEFAULT_PORT);
			
			for (;;) {
				System.out.println("Servidor TCP concorrente aguarda ligacao no porto " + DEFAULT_PORT + "...");

				//canal de comunicação
				socket = serverSocket.accept();
				
				//handle do login
				iniciarSessao(socket);
					 
				
			}
		} catch (IOException e) {
			System.err.println("Excepção no servidor: " + e);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public void iniciarSessao(Socket socket) throws IOException, InterruptedException {
	    login = new HandleLoginThread(socket, listaEmEspera);
	    login.start();
	}

	
	
		
	public static void main(String[] args) throws InterruptedException {
		Servidor servidor = new Servidor();
		servidor.start();	
	}

}

