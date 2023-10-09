package utils;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jogador.Jogador;

@WebServlet("/LogOutServlet")
public class LogOutServlet  extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	HttpSession session = request.getSession();
    	Jogador jogador = (Jogador) request.getSession().getAttribute("jogador");
		
		if (jogador != null) {
            String username = jogador.getUserInUse();
            jogador.logout(username);
            
            String message = "";
			
            try {
				message = jogador.recebeServidor();
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(message.equals("Logout successful")) {
				session.invalidate();
				response.sendRedirect("index.jsp");
			}
            
        } else {
            response.getWriter().write("Logout failed");
        }
    }

}
