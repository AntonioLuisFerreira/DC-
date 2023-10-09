<%@ page language="java" import="jogador.Jogador" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%
    String message = ""; // Variable to store success or error message
    String username = "";
    String password = "";
    Jogador jogador = (Jogador) session.getAttribute("jogador");
    
    // Retrieve form data upon form submission
    if (request.getMethod().equalsIgnoreCase("POST")) {
        username = request.getParameter("username");
        password = request.getParameter("password");

        // Validate the form data (you can add additional validation as needed)
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            

            // Check if the jogador object exists in the session
            if (jogador != null) {
            	
            	 // Call the register method on the jogador object
                jogador.login(username, password);

                // Call the recebeServidor method on the jogador object
                message = jogador.recebeServidor();
            } else {
                message = "Jogador object not found in session";
            }
        } else {
            message = "Please enter both username and password";
        }
    }
    
    if(message.equals("Login successful")){
    	jogador.setUserInUse(username);
    	response.sendRedirect("menu.jsp");
    } else{
    	message = "Please try again";
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" type="text/css" href="loginRegister.css">
</head>
<body>
    <div class="container">
        <h1>Login</h1>
        <form method="post">
            <label for="username">Username:</label>
            <input type="text" name="username" id="username" required>
            
            <label for="password">Password:</label>
            <input type="password" name="password" id="password" required>
            
            <input type="submit" value="Login">
        </form>
        <div class="register-link">
            <a href="register.jsp">Register an account</a>
        </div>
    </div>
</body>
</html>
