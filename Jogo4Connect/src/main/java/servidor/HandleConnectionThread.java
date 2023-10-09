package servidor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import jogo.Jogo;
import utils.Comando;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

class HandleConnectionThread extends Thread {

	private Comando comando;
	
	private Socket connection1;
	private Socket connection2;
	
	private String resultados;
	
	private String player1;
	private String player2;
	
	protected boolean running;
		
	public HandleConnectionThread(Socket connection1, String player1, Socket connection2, String player2) {
		this.connection1 = connection1;
		this.connection2 = connection2;
		this.player1 = player1;
		this.player2 = player2;
		comando = new Comando();
		running = true;
	}

	public void run() {

		BufferedReader is1 = null;
		PrintWriter os1 = null;
		
		BufferedReader is2 = null;
		PrintWriter os2 = null;
				
		try { 
			System.out.println("Thread do Jogo: " + this.getId() + ", "	+ connection1.getRemoteSocketAddress()+ ", "+ connection2.getRemoteSocketAddress());

			is1 = new BufferedReader(new InputStreamReader(connection1.getInputStream()));
			os1 = new PrintWriter(connection1.getOutputStream(), true);
			
			is2 = new BufferedReader(new InputStreamReader(connection2.getInputStream()));
			os2 = new PrintWriter(connection2.getOutputStream(), true);
			
			
			//Cria a instancia do jogo
			Jogo JG = new Jogo();
						
			while(running) {
							
				
				String tabela1 = (JG.JogoToTXT()+"\nJogador X: " + player1 + "\nJogador 0: " + player2).replaceAll("\n", "<");
				escreveResposta(os1, tabela1);

				//lê a informação do buffer
				String inputLine1 = leJogada(is1);
				
				if(inputLine1==null) {
					break;
				}
								
//				System.out.println("Recebi do X: " + inputLine1);
				 
				JG.jogar(Short.parseShort(inputLine1), 'X');
				
				if(JG.vitoria('X')) {
					String resp=(JG.JogoToTXT()+"\nVitória do " +  player1).replaceAll("\n", "<");
					String dur = " O Jogo durou " + JG.getDuracao();
					escreveResposta(os1, resp + dur);
					escreveResposta(os2, resp + dur);
					settResultados("s1", JG.getDuracao());
					updateDataBase(player1,"1","0",Float.toString(JG.getDuracao()));
					updateDataBase(player2,"0","1",Float.toString(JG.getDuracao()));
					break;
				}
				else
					if(JG.empate()) {
						String resp=(JG.JogoToTXT()+"\nEmpate!").replaceAll("\n", "<");
						String dur = " O Jogo durou " + JG.getDuracao();
						escreveResposta(os1, resp + dur);
						escreveResposta(os2, resp + dur);
						settResultados(" ", JG.getDuracao());
						updateDataBase(player1,"0","0",Float.toString(JG.getDuracao()));
						updateDataBase(player2,"0","0",Float.toString(JG.getDuracao()));
						break;
					}
				
				String tabela2 = (JG.JogoToTXT()+"\nJogador O: "+ player2+"\nJogador X: " + player1).replaceAll("\n", "<");
				escreveResposta(os2, tabela2);
				
				
				//lê a informação do buffer
				String inputLine2 = leJogada(is2);
				
				if(inputLine2==null) {
					break;
				}

				JG.jogar(Short.parseShort(inputLine2), 'O');	
				//mostra o jogo na consola do servidor
				//JG.mostrar();
				
				if(JG.vitoria('O')) {
					String resp=(JG.JogoToTXT()+"\nVitória do "+ player2).replaceAll("\n", "<");
					String dur = " O Jogo durou " + JG.getDuracao();
					escreveResposta(os1, resp + dur);
					escreveResposta(os2, resp + dur);
					settResultados("s2", JG.getDuracao());
					updateDataBase(player2,"1","0",Float.toString(JG.getDuracao()));
					updateDataBase(player1,"0","1",Float.toString(JG.getDuracao()));
					break;
				}
				else
					if(JG.empate()) {
						String resp=(JG.JogoToTXT()+"\nEmpate!").replaceAll("\n", "<");
						String dur = " O jogo durou " + JG.getDuracao();
						escreveResposta(os1, resp + dur);
						escreveResposta(os2, resp + dur);
						settResultados(" ", JG.getDuracao());
						updateDataBase(player1,"0","0",Float.toString(JG.getDuracao()));
						updateDataBase(player2,"0","0",Float.toString(JG.getDuracao()));
						break;
					}
			}
			
			
		} catch (IOException | ParserConfigurationException | SAXException e) {
			System.err.println("Erro na ligaçao : "	+ e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		finally {			
			try {
				if(running)
					running = false;
				if (is1 != null)
					is1.close();
				if (os1 != null)
					os1.close();
				if (is2 != null)
					is2.close();
				if (os2 != null)
					os2.close();
				if (connection1 != null)
					connection1.close();
				if (connection2 != null)
					connection2.close();
			} catch (IOException e) {
			}
		}
			System.out.println("Terminou a Thread " + this.getId() + ", " + connection1.getRemoteSocketAddress()+ ", " + connection2.getRemoteSocketAddress());
	}
	
	private String leJogada(BufferedReader is) throws IOException, ParserConfigurationException, SAXException, InterruptedException {
		String coluna = null;
		String pedidoDoc = null;
		while (pedidoDoc == null) {
	        if (is.ready()) {
	            pedidoDoc = is.readLine();
	        } else {
	            Thread.sleep(100);
	        }
	    }
        Document doc = Comando.documentFromString(pedidoDoc);
        
        if(!Comando.documentValidation(doc)) {
        	System.out.println("Validação Falhou!");	
        }else {
        	coluna = doc.getElementsByTagName("coluna").item(0).getTextContent();
//        	System.out.println("Validação Feita!");
        }
        System.out.println(coluna);
		return coluna;
	}
	
	private void escreveResposta(PrintWriter os, String value) {
		Document existingDoc = comando.getComando();
		
    	// Get the existing <protocol> element
        Element protocolElement = existingDoc.getDocumentElement();
        
        // Create the <resposta> element
        Element respostaElement = existingDoc.createElement("resposta");
        protocolElement.appendChild(respostaElement);
        
        //Create the <sucesso> element and set its value
        Element sucessoElement = existingDoc.createElement("sucesso");
        sucessoElement.appendChild(existingDoc.createTextNode(value));
        respostaElement.appendChild(sucessoElement);
        
        String xml = Comando.documentToString(existingDoc);
        
        //Escreve no socket
		os.println(xml.replaceAll("\n|\r", ""));
		comando.cleanProtocol();
	}
	
	public void settResultados(String resultado, double duracao) {
		resultados = resultado + "/" + Double.toString(duracao);
	}
	
	public String getResultados() {
		return resultados;
	}
	
	private void updateAttribute(Element element, String attributeName, float valueToAdd) {
	    String attributeValue = element.getAttribute(attributeName);
	    if (!attributeValue.isEmpty()) {
	    	float existingValue = Float.parseFloat(attributeValue);
	    	float updatedValue = existingValue + valueToAdd;
	        element.setAttribute(attributeName, String.valueOf(updatedValue));
	    }
	}

	private Element getJogadorElementByName(Document doc, String playerName) {
	    NodeList jogadorNodes = doc.getElementsByTagName("jogador");
	    for (int i = 0; i < jogadorNodes.getLength(); i++) {
	        Element jogadorElement = (Element) jogadorNodes.item(i);
	        String nome = jogadorElement.getAttribute("nome");
	        if (nome.equals(playerName)) {
	            return jogadorElement;
	        }
	    }
	    return null; // Jogador not found
	}

	private void saveDocumentToFile(Document doc, File file) throws TransformerException {
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer();
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    DOMSource source = new DOMSource(doc);
	    StreamResult result = new StreamResult(file);
	    transformer.transform(source, result);
	}

	public void updateDataBase(String playerName, String vitorias, String derrotas, String tempoJogado) {
		
	    File file = new File("/home/antonio/eclipse-workspace/Jogo4Connect/src/main/webapp/xml/users.xml");

	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    try {
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(file);

	        Element jogadorElement = getJogadorElementByName(doc, playerName);
	        if (jogadorElement != null) {
	            updateAttribute(jogadorElement, "vitorias", Float.parseFloat(vitorias));
	            updateAttribute(jogadorElement, "derrotas", Float.parseFloat(derrotas));
	            updateAttribute(jogadorElement, "tempoJogado", Float.parseFloat(tempoJogado));

	            saveDocumentToFile(doc, file);
	        } else {
	            System.out.println("Jogador not found");
	        }
	    } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
	        e.printStackTrace();
	    }
	}


}