package servidor;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import utils.Comando;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandleLoginThread extends Thread {

	private Comando comando;

	private Socket socket;
	private String name;

	private BufferedReader is;
	private PrintWriter os;
	private HashMap<Socket,String> listaEmEspera;
	private boolean running;
	
	
	private HandleConnectionThread connection;

	public HandleLoginThread(Socket socket, HashMap<Socket,String> listaEmEspera) throws IOException {
		this.socket = socket;
		this.listaEmEspera = listaEmEspera;
		comando = new Comando();
		running = true;
		name = null;
	}

	@Override
	public void run() {
	    try {
	        is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        os = new PrintWriter(socket.getOutputStream(), true);

	        while (running) {
	            String command = is.readLine();
	           
	            Document pedido = Comando.documentFromString(command);
	            
	            if(pedido == null) 
	            	break;
	            
	            if (!Comando.documentValidation(pedido)) {
	                System.out.println("Validação Falhou!");
	            } else {
	                NodeList loginStr = pedido.getElementsByTagName("login");
	                NodeList logoutStr = pedido.getElementsByTagName("logout");
	                NodeList registerStr = pedido.getElementsByTagName("register");
	                NodeList pedirInfoStr = pedido.getElementsByTagName("pedirInfo");
	                NodeList pedirJogarStr = pedido.getElementsByTagName("jogar");
	                NodeList pedirFotoStr = pedido.getElementsByTagName("alterarFoto");
	                NodeList pedirCorStr = pedido.getElementsByTagName("alterarColor");
	                
	                if (loginStr.getLength() > 0) {
	                    handleLogin(pedido, os);
	                } else if (logoutStr.getLength() > 0) {
	                	handleLogout(pedido, os);
	                }else if (registerStr.getLength() > 0) {
	                    handleRegister(pedido, os);
	                } else if (pedirInfoStr.getLength() > 0) {
	                    handleInfo(pedido, os);
	                } else if (pedirFotoStr.getLength() > 0) {
	                    handleFoto(pedido, os);
	                }else if (pedirCorStr.getLength() > 0) {
	                    handleColor(pedido, os);
	                }else if (pedirJogarStr.getLength() > 0) {
	                	name = handlePlayRequest(pedido,os);
	        	    	escreveResposta("Match Starting", os);
	                    listaEmEspera.put(socket, name);
	                    running = false;
	                }
	            }
	        }

	    } catch (IOException | ParserConfigurationException | TransformerException e) {
	        e.printStackTrace();
	    } catch (SAXException e) {
	        e.printStackTrace();
	    }

	    if (listaEmEspera.size() > 1) {
	    	
	    	
	        try {
	            fazerLigacao(listaEmEspera);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }
	}

	public void fazerLigacao(HashMap<Socket, String> listaEmEspera) throws InterruptedException {
	    List<Socket> socketList = new ArrayList<>();
	    List<String> playerList = new ArrayList<>();

	    for (Map.Entry<Socket, String> entry : listaEmEspera.entrySet()) {
	        Socket s = entry.getKey();
	        socketList.add(s);
	        String player = entry.getValue();
	        playerList.add(player);
	    }

	    listaEmEspera.clear();

	    Socket s1 = socketList.get(0);
	    Socket s2 = socketList.get(1);

	    String p1 = playerList.get(0);
	    String p2 = playerList.get(1);
	    
	    System.out.println("player1" + p1);
	    System.out.println("player2" + p2);
	    
	    if(!p1.equals(p2)) {
	    	
	    	connection = new HandleConnectionThread(s1, p1, s2, p2);
		    Thread connectionThread = new Thread(connection);
		    
		    connectionThread.start();
		    // Wait for connectionThread to complete
		    connectionThread.join();
		    
	    }
	}


	private void handleLogin(Document pedido, PrintWriter os)
			throws IOException, ParserConfigurationException, TransformerException, SAXException {
		String username = pedido.getElementsByTagName("nome").item(0).getTextContent();
		String password = pedido.getElementsByTagName("password").item(0).getTextContent();

		File file = new File("/home/antonio/eclipse-workspace/Jogo4Connect/src/main/webapp/xml/users.xml");

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);

		NodeList jogadorList = doc.getElementsByTagName("jogador");

		for (int i = 0; i < jogadorList.getLength(); i++) {
			Node jogadorNode = jogadorList.item(i);
			if (jogadorNode.getNodeType() == Node.ELEMENT_NODE) {
				Element jogadorElement = (Element) jogadorNode;
				String jogadorUsername = jogadorElement.getAttribute("nome");
				String jogadorPassword = jogadorElement.getAttribute("password");
				String jogadorOnline = jogadorElement.getAttribute("online");
				
				if (jogadorUsername.equals(username) && jogadorPassword.equals(password) && jogadorOnline.equals("0") ) {
					jogadorElement.setAttribute("online", "1"); 
					saveDocumentToFile(doc, file); 
					name = jogadorUsername;
					escreveResposta("Login successful", os);
					return;
				}
			}
		}

		escreveResposta("Invalid username or password", os);
	}
	
	private void handleLogout(Document pedido, PrintWriter os) throws ParserConfigurationException, TransformerException, SAXException, IOException {
	    String username = pedido.getElementsByTagName("nome").item(0).getTextContent();
	    
	    File file = new File("/home/antonio/eclipse-workspace/Jogo4Connect/src/main/webapp/xml/users.xml");
	    
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(file);
	    
	    NodeList jogadorList = doc.getElementsByTagName("jogador");
	    
	    for (int i = 0; i < jogadorList.getLength(); i++) {
	        Node jogadorNode = jogadorList.item(i);
	        if (jogadorNode.getNodeType() == Node.ELEMENT_NODE) {
	            Element jogadorElement = (Element) jogadorNode;
	            String jogadorUsername = jogadorElement.getAttribute("nome");
	            String jogadorOnline = jogadorElement.getAttribute("online");
	            
	            if (jogadorUsername.equals(username) && jogadorOnline.equals("1")) {
	                jogadorElement.setAttribute("online", "0"); // Update online status to 0
	                saveDocumentToFile(doc, file); // Save the updated XML file
	                escreveResposta("Logout successful", os);
	                return;
	            }
	        }
	    }
	    
	    escreveResposta("User not found or already logged out", os);
	}


	private void handleRegister(Document pedido, PrintWriter os)
			throws IOException, ParserConfigurationException, TransformerException, SAXException {
		String username = pedido.getElementsByTagName("nome").item(0).getTextContent();
		String password = pedido.getElementsByTagName("password").item(0).getTextContent();
		String idade = pedido.getElementsByTagName("idade").item(0).getTextContent();
		String nacionalidade = pedido.getElementsByTagName("nacionalidade").item(0).getTextContent();
		String cor = pedido.getElementsByTagName("cor").item(0).getTextContent();
		
		File file = new File("/home/antonio/eclipse-workspace/Jogo4Connect/src/main/webapp/xml/users.xml");

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);

		NodeList jogadorList = doc.getElementsByTagName("jogador");

		for (int i = 0; i < jogadorList.getLength(); i++) {
			Node jogadorNode = jogadorList.item(i);
			if (jogadorNode.getNodeType() == Node.ELEMENT_NODE) {
				Element jogadorElement = (Element) jogadorNode;
				String jogadorUsername = jogadorElement.getAttribute("nome");

				if (jogadorUsername.equals(username)) {
					escreveResposta("Username already exists", os);
					return;
				}
			}
		}

		Element jogadoresElement = (Element) doc.getElementsByTagName("jogadores").item(0);

		Element jogadorElement = doc.createElement("jogador");
		jogadorElement.setAttribute("nome", username);
		jogadorElement.setAttribute("password", password);
		jogadorElement.setAttribute("vitorias", "0");
		jogadorElement.setAttribute("derrotas", "0");
		jogadorElement.setAttribute("empate", "0");
		jogadorElement.setAttribute("tempoJogado", "0.0");
		jogadorElement.setAttribute("online", "1"); // Set online attribute to 1

		Element idadeElement = doc.createElement("idade");
		idadeElement.appendChild(doc.createTextNode(idade));
		jogadorElement.appendChild(idadeElement);

		Element nacionalidadeElement = doc.createElement("nacionalidade");
		nacionalidadeElement.appendChild(doc.createTextNode(nacionalidade));
		jogadorElement.appendChild(nacionalidadeElement);
		
		Element fotoElement = doc.createElement("foto");
		jogadorElement.appendChild(fotoElement);
		
		Element corElement = doc.createElement("cor");
		corElement.appendChild(doc.createTextNode(cor));
		jogadorElement.appendChild(corElement);

		jogadoresElement.appendChild(jogadorElement);

		saveDocumentToFile(doc, file);
		
		name = username;

		escreveResposta("Registration successful", os);
	}

	private void handleInfo(Document pedido, PrintWriter os)
			throws IOException, ParserConfigurationException, TransformerException, SAXException {

		String username = pedido.getElementsByTagName("nome").item(0).getTextContent();
		String show = pedido.getElementsByTagName("show").item(0).getTextContent();
		System.out.println(show);
		
		String derrotas = null;
		String vitorias = null;
		String empates = null;
		String tempoJogado = null;
		String nacionalidade = null;
		String idade = null;
		String cor = null;
		String foto = null;

		File file = new File("/home/antonio/eclipse-workspace/Jogo4Connect/src/main/webapp/xml/users.xml");

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);

		NodeList jogadorList = doc.getElementsByTagName("jogador");

		for (int i = 0; i < jogadorList.getLength(); i++) {
			Node jogadorNode = jogadorList.item(i);
			if (jogadorNode.getNodeType() == Node.ELEMENT_NODE) {
				Element jogadorElement = (Element) jogadorNode;
				String jogadorUsername = jogadorElement.getAttribute("nome");

				if (jogadorUsername.equals(username)) {
					derrotas    = jogadorElement.getAttribute("derrotas");
					vitorias    = jogadorElement.getAttribute("vitorias");
					empates     = jogadorElement.getAttribute("empate");
					tempoJogado = jogadorElement.getAttribute("tempoJogado");

					Element fotoElement = (Element) jogadorElement.getElementsByTagName("foto").item(0);
					foto = fotoElement.getTextContent();
					
					Element nationalityElement = (Element) jogadorElement.getElementsByTagName("nacionalidade").item(0);
					nacionalidade = nationalityElement.getTextContent();
					
					Element ageElement = (Element) jogadorElement.getElementsByTagName("idade").item(0);
					idade = ageElement.getTextContent();
					
					Element corElement = (Element) jogadorElement.getElementsByTagName("cor").item(0);
					cor = corElement.getTextContent();
				}
			}
		}

		if (show.equals("showLosses")) {
			escreveResposta(derrotas, os);
		} else if (show.equals("showWins")) {
			escreveResposta(vitorias, os);
		} else if (show.equals("showDraws")) {
			escreveResposta(empates, os);
		} else if (show.equals("showTimeSpent")) {
			escreveResposta(tempoJogado, os);
		} else if (show.equals("showFoto")) {
			escreveResposta(foto, os);
		}else if (show.equals("showNationality")) {
			escreveResposta(nacionalidade, os);
		}else if (show.equals("showAge")) {
			escreveResposta(idade, os);
		}else if (show.equals("showColor")) {
			escreveResposta(cor, os);
		}
	}
	
	private void handleFoto(Document pedido, PrintWriter os) {
	    String username = pedido.getElementsByTagName("nome").item(0).getTextContent();
	    String path = pedido.getElementsByTagName("path").item(0).getTextContent();

	    File file = new File("/home/antonio/eclipse-workspace/Jogo4Connect/src/main/webapp/xml/users.xml");

	    try {
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(file);

	        NodeList jogadorList = doc.getElementsByTagName("jogador");

	        for (int i = 0; i < jogadorList.getLength(); i++) {
	            Node jogadorNode = jogadorList.item(i);
	            if (jogadorNode.getNodeType() == Node.ELEMENT_NODE) {
	                Element jogadorElement = (Element) jogadorNode;
	                String jogadorUsername = jogadorElement.getAttribute("nome");

	                if (jogadorUsername.equals(username)) {
	                    Element fotoElement = (Element) jogadorElement.getElementsByTagName("foto").item(0);
	                    fotoElement.setTextContent(path); // Update the text content of the foto element
	                    saveDocumentToFile(doc, file); // Save the updated XML file
	                    escreveResposta("Foto updated successfully", os);
	                    return;
	                }
	            }
	        }

	        escreveResposta("Username not found", os);
	    } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
	        e.printStackTrace();
	        escreveResposta("Error updating foto", os);
	    }
	}
	
	private void handleColor(Document pedido, PrintWriter os) {
	    String username = pedido.getElementsByTagName("nome").item(0).getTextContent();
	    String cor = pedido.getElementsByTagName("cor").item(0).getTextContent();
	    
	    File file = new File("/home/antonio/eclipse-workspace/Jogo4Connect/src/main/webapp/xml/users.xml");

	    try {
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(file);

	        NodeList jogadorList = doc.getElementsByTagName("jogador");

	        for (int i = 0; i < jogadorList.getLength(); i++) {
	            Node jogadorNode = jogadorList.item(i);
	            if (jogadorNode.getNodeType() == Node.ELEMENT_NODE) {
	                Element jogadorElement = (Element) jogadorNode;
	                String jogadorUsername = jogadorElement.getAttribute("nome");

	                if (jogadorUsername.equals(username)) {
	                	System.out.println("entrei");
	                    Element corElement = (Element) jogadorElement.getElementsByTagName("cor").item(0);
	                    corElement.setTextContent(cor); // Update the text content of the foto element
	                    saveDocumentToFile(doc, file); // Save the updated XML file
	                    escreveResposta("Color Changed successful", os);
	                    return;
	                }
	            }
	        }

	        escreveResposta("Username not found", os);
	    } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
	        e.printStackTrace();
	        escreveResposta("Error updating foto", os);
	    }
	}
	
	private String handlePlayRequest(Document pedido, PrintWriter os) {
		
		String username = pedido.getElementsByTagName("nome").item(0).getTextContent();
		String playerResponse = "Player " + username + ", you are in wait list";
		escreveResposta(playerResponse, os);
		return username;
	}


	private void escreveResposta(String value, PrintWriter os) {
		Document existingDoc = comando.getComando();

		// Get the existing <protocol> element
		Element protocolElement = existingDoc.getDocumentElement();

		// Create the <resposta> element
		Element respostaElement = existingDoc.createElement("resposta");
		protocolElement.appendChild(respostaElement);

		// Create the <sucesso> element and set its value
		Element sucessoElement = existingDoc.createElement("sucesso");
		sucessoElement.appendChild(existingDoc.createTextNode(value));
		respostaElement.appendChild(sucessoElement);

		String xml = Comando.documentToString(existingDoc);

		// Escreve no socket
		os.println(xml.replaceAll("\n|\r", ""));
		comando.cleanProtocol();
	}

	private void closeStreams() {
		try {
			if (os != null)
				os.close();
			if (is != null)
				is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveDocumentToFile(Document doc, File file) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(file);
		transformer.transform(source, result);
	}

    public boolean isRunning() {
        return running;
    }
}
