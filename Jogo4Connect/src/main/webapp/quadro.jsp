<%@ page import="java.io.*, java.util.*, org.w3c.dom.*" %>
<%@ page language="java" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory, javax.xml.parsers.DocumentBuilder" %>

<!DOCTYPE html>
<html>
<head>
    <title>Player Data</title>
	<link rel="stylesheet" type="text/css" href="quadro.css">

    <script>
        const fetchCountryFlag = async (countryCode, playerId) => {
            try {
                const response = await fetch('https://flagcdn.com/en/codes.json');
                const data = await response.json();
                const countryKey = Object.keys(data).find(key => data[key] === countryCode);

                const flagDisplay = document.getElementById('flagDisplay-' + playerId);
                flagDisplay.innerHTML = ''; // Clear the flag display before appending new image

                const flagImg = document.createElement('img');
                flagImg.src = 'https://flagcdn.com/w20/' + countryKey + '.png';
                flagImg.alt = countryCode;
                flagDisplay.appendChild(flagImg);
            } catch (error) {
                console.error('Error:', error);
            }
        };
    </script>
</head>
<body>
    <table>
        
        <thead>
            <tr>
                <th>Player Name</th>
                <th>Victories</th>
                <th>Losses</th>
                <th>Draws</th>
                <th>Playing Time</th>
                <th>Country Flag</th>
            </tr>
        </thead>
        
        <tbody>
            <% 
                // Load and parse the XML file
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new File("/home/antonio/eclipse-workspace/Jogo4Connect/src/main/webapp/xml/users.xml"));

                // Get the player nodes from the XML
                NodeList playerNodes = document.getElementsByTagName("jogador");

                // Create a list to store player data
                List<Element> players = new ArrayList<>();

                // Iterate over player nodes and extract player information
                for (int i = 0; i < playerNodes.getLength(); i++) {
                    Element playerElement = (Element) playerNodes.item(i);
                    players.add(playerElement);
                }

                // Sort the players based on victories and playing time
                Collections.sort(players, new Comparator<Element>() {
                    public int compare(Element player1, Element player2) {
                        // Compare victories
                        float victories1 = Float.parseFloat(player1.getAttribute("vitorias"));
                        float victories2 = Float.parseFloat(player2.getAttribute("vitorias"));
                        if (victories1 != victories2) {
                            return Float.compare(victories2, victories1); // Descending order
                        }

                        // If victories are the same, compare playing time
                        float time1 = Float.parseFloat(player1.getAttribute("tempoJogado"));
                        float time2 = Float.parseFloat(player2.getAttribute("tempoJogado"));
                        return Double.compare(time1, time2); // Ascending order
                    }
                });

                // Display the sorted player data
                for (Element player : players) {
                    String name = player.getAttribute("nome");
                    float victories = Float.parseFloat(player.getAttribute("vitorias"));
                    float losses = Float.parseFloat(player.getAttribute("derrotas"));
                    float draws = Float.parseFloat(player.getAttribute("empate"));
                    float playingTime = Float.parseFloat(player.getAttribute("tempoJogado")) / (victories+losses+draws);
                    if(Float.isNaN(playingTime))
                    	playingTime = 0f;
                    String countryCode = player.getElementsByTagName("nacionalidade").item(0).getTextContent();

                    // Output the player information in a table row
                    out.println("<tr>");
                    out.println("<td>" + name + "</td>");
                    out.println("<td>" + victories + "</td>");
                    out.println("<td>" + losses + "</td>");
                    out.println("<td>" + draws + "</td>");
                    out.println("<td>" + playingTime + "</td>");
                    out.println("<td><div id='flagDisplay-" + name + "'></div></td>");
                    out.println("</tr>");

                    // Fetch and display the country flag
                    out.println("<script>fetchCountryFlag('" + countryCode + "', '" + name + "').catch(console.error);</script>");
                }
            %>
            
        </tbody>    
    </table>
    
    <div id="menu-Container">
		<form method="POST" action="menu.jsp">
	    	<input type="submit" value="Go Back">
	    </form>
	</div>
</body>
</html>
