<%@ page language="java" import="jogador.Jogador"
	contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.io.InputStreamReader"%>
<%@ page import="java.net.Socket"%>

<%
// Retrieve the jogador instance from the session
Jogador jogador = (Jogador) session.getAttribute("jogador");

String message = ""; 
String resposta = "";
String username = "";

// Retrieve form data upon form submission
if (request.getMethod().equalsIgnoreCase("POST")) {
	username = request.getParameter("username");
	String password = request.getParameter("password");
	String age = request.getParameter("age");
	String nationality = request.getParameter("nationality");
	String cor = request.getParameter("color");

	// Validate the form data (you can add additional validation as needed)
	if (username != null && !username.isEmpty() && password != null && !password.isEmpty() && age != null
	&& !age.isEmpty() && nationality != null && !nationality.isEmpty() && cor != null
	&& !cor.isEmpty()) {

		// Call the register method on the jogador object
		jogador.register(username, password, age, nationality, cor);

		// Call the recebeServidor method on the jogador object
		resposta = jogador.recebeServidor();
	} else {
		message = "Please fill in all the fields.";
	}
	System.out.println(resposta);
	if (resposta.equals("Registration successful")) {
		jogador.setUserInUse(username);
		response.sendRedirect("menu.jsp");
	} else {
		resposta = "Please try again";
	}
}
%>

<!DOCTYPE html>
<html>
<head>
<title>Register</title>
<link rel="stylesheet" type="text/css" href="loginRegister.css">
</head>
<body>
	<div class="container">
		<h1>Register</h1>
		<form method="post">
			<label for="username">Username:</label> <input type="text"
				name="username" id="username" required> <label
				for="password">Password:</label> <input type="password"
				name="password" id="password" required> 
				<label for="age">Age:</label>
			<input type="text" name="age" id="age" required> <label
				for="nationality">Nationality:</label> <input type="text"
				name="nationality" id="nationality" required>
				<label for="cor">Cor:</label>
        		<input type="color" id="color" name="color" value="#000000">
   			 
   
		
				
			

			<div class="feedback">
				<div class="feedback-box">
					<p id="feedback-message"><%=resposta%></p>
				</div>
			</div>

			<div class="button-container">
				
					<input type="submit" value="Regiter">
				
			</div>
		</form>
		<div class="login-link">
			<a href="login.jsp">Go to Login</a>
		</div>
	</div>

</body>
</html>
