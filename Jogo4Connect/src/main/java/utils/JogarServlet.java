package utils;

import java.io.IOException;
import java.util.Enumeration;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.RequestDispatcher;

import jogador.Jogador;

@WebServlet("/JogarServlet")
public class JogarServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {

	    HttpSession session = request.getSession();
	    Jogador jogador = (Jogador) session.getAttribute("jogador");
	    String inUse = jogador.getUserInUse();

	    Jogador player = new Jogador();
	    player.setUserInUse(inUse);

	    Enumeration<String> attributeNames = session.getAttributeNames();
	    int maxNumber = -1;

	    while (attributeNames.hasMoreElements()) {
	        String attributeName = attributeNames.nextElement();
	        if (attributeName.startsWith("temporary")) {
	            String numberString = attributeName.substring("temporary".length());
	            int number = Integer.parseInt(numberString);
	            if (number > maxNumber) {
	                maxNumber = number;
	            }
	        }
	    }

	    int newNumber = maxNumber + 1;
	    String newAttributeName = "temporary" + newNumber;
	    session.setAttribute(newAttributeName, player);

	    player.enterWaitList(player.getUserInUse());

	    String serverResponse = "";
	    long startTime = System.currentTimeMillis();
	    long timeout = 30000; // Timeout after 30 seconds

	    try {
	        while (!serverResponse.equals("Match Starting")) {
	            serverResponse = player.recebeServidor();
	            if (System.currentTimeMillis() - startTime > timeout) {
	                RequestDispatcher dispatcher = request.getRequestDispatcher("menu.jsp");
	                dispatcher.forward(request, response);
	                return;
	            }
	            Thread.sleep(1000);
	        }

	        int gameId = newNumber; // Use the incremented number as the game ID
	        String gameUrl = "/jogo.jsp";
	        request.setAttribute("gameId", gameId); // Set the gameId as a request attribute

	        // Forward the request to the game page
	        RequestDispatcher dispatcher = request.getRequestDispatcher(gameUrl);
	        dispatcher.forward(request, response);
	    } catch (IOException | InterruptedException e) {
	        e.printStackTrace();
	    }
	}

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

}
