package utils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Comando {
	
	private Document cmd;
	
	public Comando() {
		cmd = documentFromString("<?xml version='1.0' encoding='UTF-8'?>"+ "<protocol>"+ "</protocol>");
	}
	
	//getter do comando
	public Document getComando() {
		return cmd;
	}
	public void cleanProtocol() {
		cmd = documentFromString("<?xml version='1.0' encoding='UTF-8'?>"+ "<protocol>"+ "</protocol>");
	}
	public static final Document documentFromString(String strXML) {
		Document xmlDoc = null;
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			xmlDoc = builder.parse(new InputSource(new StringReader(strXML)));
		} catch (Exception e) {
//			System.err.println("Error: Unable to read XML from string!\n\t" + e);
//			e.printStackTrace();
		}
		return xmlDoc;
	}
	
	//print do xml
	public static final String documentToString(Document xmlDoc) {
		Writer out = new StringWriter();
		try {
			Transformer tf = TransformerFactory.newInstance().newTransformer();
			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); // "UTF-8"
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.transform(new DOMSource(xmlDoc), new StreamResult(out));
		} catch (Exception e) {
			System.out.println("Error: Unable to write XML to string!\n\t" + e);
			e.printStackTrace();
		}
		return out.toString();
	}
	
	//Document to socket
	
	
	//Document from socket
	
	//validar de acordo com xsd
	public static final boolean documentValidation(Document document) {
		String type = XMLConstants.W3C_XML_SCHEMA_NS_URI;
		// create a SchemaFactory capable of understanding WXS schemas
		SchemaFactory factory = SchemaFactory.newInstance(type);

		// load a WXS schema, represented by a Schema instance
		Source schemaFile = new StreamSource(new File("/home/antonio/eclipse-workspace/Jogo4Connect/src/main/webapp/xml/protocol.xsd"));
		try {
			Schema schema = factory.newSchema(schemaFile);

			// create a Validator instance, which can be used to validate an instance
			// document
			Validator validator = schema.newValidator();

			// validate the DOM tree

			validator.validate(new DOMSource(document));
			return true;
		} catch (IOException | SAXException e) {
			// instance document is invalid!
			System.err.println("\nReason: " + e.getLocalizedMessage());
		}
		return false;
	}
}
