<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="jogador.Jogador" %>
<!DOCTYPE html>
<html>
<head>
  <title>Game Page</title>
  <link rel="stylesheet" type="text/css" href="jogo.css">
  
  <%
  	String serverResponse = "";
	String infoPlayer = "";
	String infoAgainst = "";
	
	
	String gameIdParam = request.getParameter("gameId");
	int gameId = 0; 

	if (gameIdParam != null && gameIdParam.matches("\\d+")) {
		
	    gameId = Integer.parseInt(gameIdParam);
	}

	Jogador jogador = (Jogador) session.getAttribute("temporary" + gameId);

    System.out.println(jogador.getUserInUse() + "temporary" + gameId);
  %>
  
</head>
<body>
	
	<div class="container">
		<h1>Game Page</h1>

		<table>
			<thead>
				<tr>
					<th></th>
					<th>0</th>
					<th>1</th>
					<th>2</th>
					<th>3</th>
					<th>4</th>
					<th>5</th>
					<th>6</th>
					<th>7</th>
				</tr>
			</thead>
			<tbody>
				<%
				if (jogador != null) {
					if (request.getMethod().equals("POST")) {
						String nextMove = request.getParameter("nextMove");
						if (nextMove != null && !nextMove.isEmpty()) {
							int move = Integer.parseInt(nextMove);
							if (((move >= 0 && move <= 7) || move == 9)) {
								jogador.play(nextMove);
							}	
						}
						
					}

					serverResponse = jogador.recebeServidor();
					//System.out.println(serverResponse);
					String[] lines = serverResponse.split("\\r?\\n");
					for (int i = 0; i < lines.length; i++) {
						if (i % 2 == 0) {
							if (i == 16) {
								infoPlayer = lines[i];
								continue;
							}
						String line = lines[i];
						String[] cells = line.split("\\|");
					%>
					<tr>
						<td><%=i / 2 + 1%></td>
						<%
						for (String cell : cells) {
						%>
						<%
						if (cell.equals("-")) {
							cell = " ";
						}
						%>
						<td><%=cell%></td>
						<%
						}
						%>
					</tr>
					<%
					}else if(i == 17){
						infoAgainst = lines[i];
					}
				}
				} else {
				// Handle the case when 'jogador' is not found in the session
				out.println("<tr><td colspan='8'>Jogador not found!</td></tr>");
				}
				%>
			</tbody>
		</table>

		<form id="playForm" action="jogo.jsp" method="post">
			<input type="text" name="nextMove" id="nextMoveInput" /> <input
				type="submit" value="Play" />
		</form>
	</div>
	<div class="profile-container">
		<%
		if (infoPlayer.contains("Vitória")) {
			String playerName = infoPlayer.substring(infoPlayer.indexOf("do ") + 3, infoPlayer.indexOf(" O"));
			if(playerName.equals(jogador.getUserInUse())){
				infoAgainst = infoPlayer.substring(infoPlayer.indexOf("O") + 2);
				infoPlayer = "Parabéns Ganhou!";
			}else{
				infoAgainst = infoPlayer.substring(infoPlayer.indexOf("O") + 2);
				infoPlayer = "Game Over";
			}
			session.removeAttribute("gameSessionName");
			// Close the tab after 3 seconds
            out.println("<script type='text/javascript'>");
            out.println("setTimeout(closeTab, 3000);");
            out.println("</script>");
		}else if(infoPlayer.contains("Empate")){
			infoAgainst = infoPlayer.substring(infoPlayer.indexOf("O") + 2);
			infoPlayer = "Empate!";
			session.removeAttribute("gameSessionName");
			// Close the tab after 3 seconds
            out.println("<script type='text/javascript'>");
            out.println("setTimeout(closeTab, 3000);");
            out.println("</script>");
		}
		
		%>
		<h1><%= infoPlayer %></h1>
	</div>
	
	<div class="against-container">
		<h1><%= infoAgainst %></h1>
	</div>
	<script>
	setTimeout(function() {
		if (document.getElementById('nextMoveInput').value === '' ) {
			document.getElementById('nextMoveInput').value = '9';
			document.getElementById('playForm').submit();
		}
	}, 30000);

	</script>
	
	
</body>
</html>