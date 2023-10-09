package jogador;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import utils.Comando;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Image;

public class Jogador {
//
	private final static String DEFAULT_HOST = "localhost";
	private final static int DEFAULT_PORT = 5025;

	private Socket socket = null;
	private BufferedReader is = null;
	private PrintWriter os = null;

	public Jogador() {
		try {
			this.socket = new Socket(DEFAULT_HOST, DEFAULT_PORT);
			this.os = new PrintWriter(socket.getOutputStream(), true);
			this.is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
		} catch (IOException e) {
			System.err.println("Erro na ligação: " + e.getMessage());
		}
		comando = new Comando();
	}
	private Comando comando;
	private String userInUse;

	public String getUserInUse() {
		return userInUse;
	}

	public void setUserInUse(String userInUse) {
		this.userInUse = userInUse;
	}
	

	public void login(String username, String password) {

		Document existingDoc = comando.getComando();
		// Get the existing <protocol> element
		Element protocolElement = existingDoc.getDocumentElement();

		// Create the <login> element
		Element loginElement = existingDoc.createElement("login");
		protocolElement.appendChild(loginElement);

		// Create the <pedido> element
		Element pedidoElement = existingDoc.createElement("pedido");
		loginElement.appendChild(pedidoElement);

		// Create the <username> element and set its value
		Element usernameElement = existingDoc.createElement("nome");
		usernameElement.appendChild(existingDoc.createTextNode(username));
		pedidoElement.appendChild(usernameElement);

		// Create the <password> element and set its value
		Element passwordElement = existingDoc.createElement("password");
		passwordElement.appendChild(existingDoc.createTextNode(password));
		pedidoElement.appendChild(passwordElement);

		String xml = Comando.documentToString(existingDoc);
		// Escreve no socket
		os.println(xml.replaceAll("\n|\r", ""));
		comando.cleanProtocol();

	}

	public void logout(String username) {
		Document existingDoc = comando.getComando();

		// Get the existing <protocol> element
		Element protocolElement = existingDoc.getDocumentElement();

		// Create the <pedirInfo> element
		Element pedirInfoElement = existingDoc.createElement("logout");
		protocolElement.appendChild(pedirInfoElement);

		// Create the <pedido> element
		Element pedidoElement = existingDoc.createElement("pedido");
		pedirInfoElement.appendChild(pedidoElement);

		// Create the <nome> element and set its value
		Element nomeElement = existingDoc.createElement("nome");
		nomeElement.appendChild(existingDoc.createTextNode(String.valueOf(username)));
		pedidoElement.appendChild(nomeElement);

		String xml = Comando.documentToString(existingDoc);
		// Escreve no socket
		os.println(xml.replaceAll("\n|\r", ""));
		comando.cleanProtocol();
	}

	public void register(String username, String password, String age, String nationality, String cor) {

		Document existingDoc = comando.getComando();
		// Get the existing <protocol> element
		Element protocolElement = existingDoc.getDocumentElement();

		// Create the <register> element
		Element registerElement = existingDoc.createElement("register");
		protocolElement.appendChild(registerElement);

		// Create the <pedido> element
		Element pedidoElement = existingDoc.createElement("pedido");
		registerElement.appendChild(pedidoElement);

		// Create the <nome> element and set its value
		Element nomeElement = existingDoc.createElement("nome");
		nomeElement.appendChild(existingDoc.createTextNode(username));
		pedidoElement.appendChild(nomeElement);

		// Create the <password> element and set its value
		Element passwordElement = existingDoc.createElement("password");
		passwordElement.appendChild(existingDoc.createTextNode(password));
		pedidoElement.appendChild(passwordElement);

		// Create the <idade> element and set its value
		Element idadeElement = existingDoc.createElement("idade");
		idadeElement.appendChild(existingDoc.createTextNode(age));
		pedidoElement.appendChild(idadeElement);

		// Create the <nacionalidade> element and set its value
		Element nacionalidadeElement = existingDoc.createElement("nacionalidade");
		nacionalidadeElement.appendChild(existingDoc.createTextNode(nationality));
		pedidoElement.appendChild(nacionalidadeElement);

		// Create the <cor> element and set its value
		Element fotoElement = existingDoc.createElement("foto");
		pedidoElement.appendChild(fotoElement);
		
		// Create the <cor> element and set its value
		Element corElement = existingDoc.createElement("cor");
		corElement.appendChild(existingDoc.createTextNode(cor));
		pedidoElement.appendChild(corElement);

		String xml = Comando.documentToString(existingDoc);
		// Escreve no socket
		os.println(xml.replaceAll("\n|\r", ""));
		comando.cleanProtocol();
	}

	public void enterWaitList(String username) {
		Document existingDoc = comando.getComando();

		// Get the existing <protocol> element
		Element protocolElement = existingDoc.getDocumentElement();

		// Create the <pedirInfo> element
		Element pedirInfoElement = existingDoc.createElement("jogar");
		protocolElement.appendChild(pedirInfoElement);

		// Create the <pedido> element
		Element pedidoElement = existingDoc.createElement("pedido");
		pedirInfoElement.appendChild(pedidoElement);

		// Create the <nome> element and set its value
		Element nomeElement = existingDoc.createElement("nome");
		nomeElement.appendChild(existingDoc.createTextNode(String.valueOf(username)));
		pedidoElement.appendChild(nomeElement);

		String xml = Comando.documentToString(existingDoc);
		// Escreve no socket
		os.println(xml.replaceAll("\n|\r", ""));
		comando.cleanProtocol();
	}
	
	

	public void play(String jogada) {

		Document existingDoc = comando.getComando();

		// Get the existing <protocol> element
		Element protocolElement = existingDoc.getDocumentElement();

		// Create the <enviarJogada> element
		Element enviarJogadaElement = existingDoc.createElement("enviarJogada");
		protocolElement.appendChild(enviarJogadaElement);

		// Create the <pedido> element
		Element pedidoElement = existingDoc.createElement("pedido");
		enviarJogadaElement.appendChild(pedidoElement);

		// Create the <jogador1> element and set its value
		Element jogador1Element = existingDoc.createElement("jogador1");
		jogador1Element.appendChild(existingDoc.createTextNode("nickname"));
		pedidoElement.appendChild(jogador1Element);

		// Create the <coluna> element and set its value
		Element colunaElement = existingDoc.createElement("coluna");
		colunaElement.appendChild(existingDoc.createTextNode(jogada));
		pedidoElement.appendChild(colunaElement);

		String xml = Comando.documentToString(existingDoc);
		// Escreve no socket
		os.println(xml.replaceAll("\n|\r", ""));
		comando.cleanProtocol();
	}

	public void showFoto(String imagePath) {
		ImageIcon imageIcon = new ImageIcon(imagePath);
		Image image = imageIcon.getImage();

		JFrame frame = new JFrame();
		JLabel label = new JLabel(new ImageIcon(image));
		frame.getContentPane().add(label);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}

	public void showInfo(String username, String pedido) {

		Document existingDoc = comando.getComando();

		// Get the existing <protocol> element
		Element protocolElement = existingDoc.getDocumentElement();

		// Create the <pedirInfo> element
		Element pedirInfoElement = existingDoc.createElement("pedirInfo");
		protocolElement.appendChild(pedirInfoElement);

		// Create the <pedido> element
		Element pedidoElement = existingDoc.createElement("pedido");
		pedirInfoElement.appendChild(pedidoElement);

		// Create the <nome> element and set its value
		Element nomeElement = existingDoc.createElement("nome");
		nomeElement.appendChild(existingDoc.createTextNode(String.valueOf(username)));
		pedidoElement.appendChild(nomeElement);

		// Create the <show> element and set its value
		Element showElement = existingDoc.createElement("show");
		showElement.appendChild(existingDoc.createTextNode(String.valueOf(pedido)));
		pedidoElement.appendChild(showElement);

		String xml = Comando.documentToString(existingDoc);
		// Escreve no socket
		os.println(xml.replaceAll("\n|\r", ""));
		comando.cleanProtocol();

	}

	public void changeFoto(String username,String newPath) {

		Document existingDoc = comando.getComando();

		// Get the existing <protocol> element
		Element protocolElement = existingDoc.getDocumentElement();

		// Create the <pedirInfo> element
		Element pedirInfoElement = existingDoc.createElement("alterarFoto");
		protocolElement.appendChild(pedirInfoElement);

		// Create the <pedido> element
		Element pedidoElement = existingDoc.createElement("pedido");
		pedirInfoElement.appendChild(pedidoElement);

		// Create the <nome> element and set its value
		Element nomeElement = existingDoc.createElement("nome");
		nomeElement.appendChild(existingDoc.createTextNode(String.valueOf(username)));
		pedidoElement.appendChild(nomeElement);

		// Create the <show> element and set its value
		Element showElement = existingDoc.createElement("path");
		showElement.appendChild(existingDoc.createTextNode(String.valueOf(newPath)));
		pedidoElement.appendChild(showElement);

		String xml = Comando.documentToString(existingDoc);
		// Escreve no socket
		os.println(xml.replaceAll("\n|\r", ""));
		comando.cleanProtocol();
	}
	
	public void changeColor(String username,String newColor) {

		Document existingDoc = comando.getComando();

		// Get the existing <protocol> element
		Element protocolElement = existingDoc.getDocumentElement();

		// Create the <pedirInfo> element
		Element pedirInfoElement = existingDoc.createElement("alterarColor");
		protocolElement.appendChild(pedirInfoElement);

		// Create the <pedido> element
		Element pedidoElement = existingDoc.createElement("pedido");
		pedirInfoElement.appendChild(pedidoElement);

		// Create the <nome> element and set its value
		Element nomeElement = existingDoc.createElement("nome");
		nomeElement.appendChild(existingDoc.createTextNode(String.valueOf(username)));
		pedidoElement.appendChild(nomeElement);

		// Create the <show> element and set its value
		Element showElement = existingDoc.createElement("cor");
		showElement.appendChild(existingDoc.createTextNode(String.valueOf(newColor)));
		pedidoElement.appendChild(showElement);

		String xml = Comando.documentToString(existingDoc);
		// Escreve no socket
		os.println(xml.replaceAll("\n|\r", ""));
		comando.cleanProtocol();
	}

	public String recebeServidor() throws IOException, InterruptedException {
		String respostaJogo = null;
		String pedidoDoc = null;
		
		while (pedidoDoc == null) {
			if (is.ready()) {
				pedidoDoc = is.readLine();
			} else {
				// Input stream is not ready, wait for a short period before checking again
				Thread.sleep(100);
			}
		}
		
		Document doc = comando.documentFromString(pedidoDoc);

		if (!comando.documentValidation(doc)) {
			System.out.println("Validação Falhou!");
		} else {
			respostaJogo = doc.getElementsByTagName("sucesso").item(0).getTextContent();
		}
		
		respostaJogo = doc.getElementsByTagName("sucesso").item(0).getTextContent();
		
		return respostaJogo.replaceAll("<", "\n");
	}

	// vai validar o xsd
	public boolean validateXSD() {
		return false;
	}

}