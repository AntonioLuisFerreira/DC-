package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/AutoCompleteServlet")
public class AutoCompleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query = request.getParameter("query");

        // Retrieve the suggestions based on the query from the XML database
        List<String> suggestions = retrieveSuggestionsFromXml(query);

        // Convert the suggestions list to XML format
        Document xmlDoc = convertSuggestionsToXml(suggestions);

        // Set the response content type to XML
        response.setContentType("text/xml");

        // Write the XML response to the client
        response.getWriter().write(getStringFromXmlDocument(xmlDoc));
    }

    private List<String> retrieveSuggestionsFromXml(String query) {
        List<String> suggestions = new ArrayList<>();

        try {
            // Load the XML database file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse("/home/antonio/eclipse-workspace/Jogo4Connect/src/main/webapp/xml/users.xml");

            // Get all jogador elements from the XML document
            NodeList jogadorNodes = doc.getElementsByTagName("jogador");

            // Iterate over the jogador elements
            for (int i = 0; i < jogadorNodes.getLength(); i++) {
                Element jogadorElement = (Element) jogadorNodes.item(i);

                // Get the nome attribute of the jogador element
                String nome = jogadorElement.getAttribute("nome");

                // Check if the nome starts with the query
                if (nome.toLowerCase().startsWith(query.toLowerCase())) {
                    suggestions.add(nome);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return suggestions;
    }

    private Document convertSuggestionsToXml(List<String> suggestions) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document xmlDoc = builder.newDocument();

            // Create the root element
            Element rootElement = xmlDoc.createElement("suggestions");
            xmlDoc.appendChild(rootElement);

            // Create and append the suggestion elements
            for (String suggestion : suggestions) {
                Element suggestionElement = xmlDoc.createElement("nome");
                suggestionElement.setTextContent(suggestion);
                rootElement.appendChild(suggestionElement);
            }

            return xmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getStringFromXmlDocument(Document xmlDoc) {
        try {
            // Transform the XML document to string representation
            javax.xml.transform.TransformerFactory tf =
                    javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            java.io.StringWriter writer = new java.io.StringWriter();
            transformer.transform(new javax.xml.transform.dom.DOMSource(xmlDoc),
                    new javax.xml.transform.stream.StreamResult(writer));
            return writer.getBuffer().toString().replaceAll("\n|\r", "");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}

