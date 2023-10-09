package utils;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jogador.Jogador;

@WebServlet("/ChangeColorServlet")
public class ChangeColorServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve the jogador object from the session
        Jogador jogador = (Jogador) request.getSession().getAttribute("jogador");

        if (jogador != null) {
        	 String color = request.getParameter("color");
        	 
        	 jogador.changeColor(jogador.getUserInUse(), color);
        	 String resposta = "";
        	 
        	 try {
				resposta = jogador.recebeServidor();
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	 if(resposta.equals("Color Changed successful")) {
        		response.sendRedirect(request.getContextPath() + "/menu.jsp");
        	 }
             
        
        	
        }
    }

}
