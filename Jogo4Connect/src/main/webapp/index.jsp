<%@ page language="java" import="jogador.Jogador"
    contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.io.InputStreamReader"%>
<%@ page import="java.net.Socket"%>

<%
    // Create a new instance of Jogador
    Jogador jogador = new Jogador();
    // Store the jogador instance in the session
    session.setAttribute("jogador", jogador);

    // Redirect the user to the register.jsp page
    response.sendRedirect("login.jsp");
%>
