<%@ page language="java" import="jogador.Jogador" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
    Jogador jogador = (Jogador) session.getAttribute("jogador");

    String username = jogador.getUserInUse();
    String imagem = "";
    String nacionalidade = "";
    String idade = "";
    String vitorias = "";
    String derrotas = "";
    String empates = "";
    String tempoMedioJogado = "";
    String cor = "";
    String color = "";
    String resposta = "";
    String message = "";

    jogador.showInfo(username, "showFoto");
    imagem = jogador.recebeServidor();

    jogador.showInfo(username, "showNationality");
    nacionalidade = jogador.recebeServidor();

    jogador.showInfo(username, "showAge");
    idade = jogador.recebeServidor();

    jogador.showInfo(username, "showWins");
    vitorias = jogador.recebeServidor();

    jogador.showInfo(username, "showLosses");
    derrotas = jogador.recebeServidor();

    jogador.showInfo(username, "showDraws");
    empates = jogador.recebeServidor();

    jogador.showInfo(username, "showTimeSpent");
    tempoMedioJogado = jogador.recebeServidor();

    jogador.showInfo(username, "showColor");
    cor = jogador.recebeServidor();
%>

<!DOCTYPE html>
<html>
<head>
    <title>Menu</title>
    <link rel="stylesheet" type="text/css" href="menu.css">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script>
        $(document).ready(function() {
            $("#nomeInput").on("keyup", function() {
                var query = $(this).val().trim();
                if (query.length >= 1) {
                    $.ajax({
                        url: "AutoCompleteServlet",
                        method: "GET",
                        dataType: "xml",
                        data: { query: query },
                        success: function(response) {
                            var suggestions = [];
                            // Parse the XML response and extract the suggested nomes
                            $(response).find("nome").each(function() {
                                suggestions.push($(this).text());
                            });

                            var suggestionsList = "";

                            for (var i = 0; i < suggestions.length; i++) {
                                suggestionsList += "<li>" + suggestions[i] + "</li>";
                            }

                            $("#suggestionsList").html(suggestionsList);
                        }
                    });
                } else {
                    $("#suggestionsList").empty();
                }
            });

            const fetchCountryCode = async () => {
                try {
                    const response = await fetch('https://flagcdn.com/en/codes.json');
                    const data = await response.json();
                    const countryCode = "<%=nacionalidade%>";

                    const getKeyByValue = (object, value) => {
                        return Object.keys(object).find(key => object[key] === value);
                    };

                    const countryKey = getKeyByValue(data, countryCode);
                    console.log(countryKey);
                    const countryData = data[countryKey];
                    const countryName = countryData.name;
                    const cc = countryKey;

                    const flagDisplay = document.getElementById('flagDisplay');
                    flagDisplay.innerHTML = ''; // Clear the flag display before appending new image

                    const flagImg = document.createElement('img');
                    flagImg.src = 'https://flagcdn.com/w20/' + cc + '.png';
                    flagImg.srcset = 'https://flagcdn.com/w40/' + cc + '.png 2x';
                    flagImg.width = '20';
                    flagImg.alt = '<%=nacionalidade%>';
                    flagDisplay.appendChild(flagImg);
                } catch (error) {
                    console.error('Error:', error);
                }
            };

            fetchCountryCode(); // Call the function to fetch and display the country flag

            document.body.style.backgroundColor = '<%=cor%>';
        });

        
    </script>
</head>
<body>
    <div id="MyProfileImage">
        <img src="<%=imagem%>" alt="Profile Image">
    </div>

    <div class="container">
        <h1>Menu</h1>

        <div id="usernameDisplay">
            <p>Username: <%= username %></p>
        </div>

        <div class="contimage">
            <p>Nacionalidade: <span id="nacionalidade"><%= nacionalidade %></span></p>
            <div id="flagDisplay"></div>
        </div>

        <div id="ageDisplay">
            <p>Idade: <span id="idade"><%= idade %></span></p>
        </div>

        <div id="playerStats">
            <p>Número de Vitórias: <span id="vitorias"><%= vitorias %></span></p>
            <p>Número de Derrotas: <span id="derrotas"><%= derrotas %></span></p>
            <p>Número de Empates: <span id="empates"><%= empates %></span></p>
            <p>Tempo Jogado: <span id="tempoMedioJogado"><%= tempoMedioJogado %></span>s</p>
        </div>

        <div id="changeColorContainer">
            <form method="POST" action="ChangeColorServlet">
                <input type="color" name="color" id="color" value="#000000">
                <button id="changeColor">Change Color</button>
            </form>
        </div>

        <div id="changeImageContainer">
            <form method="POST" action="FileUpload" enctype="multipart/form-data">
                <input type="file" name="imagem">
                <input type="submit" value="Atualizar Foto">
            </form>
        </div>

        <div id="searchContainer">
            <p>Pesquise jogadores:</p>
            <input type="text" id="nomeInput">
            <ul id="suggestionsList"></ul>
        </div>

        <div id="jogar-Container">
	        <form method="POST" action="JogarServlet" target="_blank">
	            <input type="submit" value="Jogar">
	        </form>
    	</div>

        <div id="logOut-Container">
            <form method="POST" action="LogOutServlet">
                <input type="submit" value="LogOut">
            </form>
        </div>

        <div id="quadro-Container">
            <form method="POST" action="QuadroServlet">
                <input type="submit" value="Quadro">
            </form>
        </div>
    </div>
</body>
</html>
