package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jogador.Jogador;

@WebServlet("/FileUpload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
maxFileSize = 1024 * 1024 * 50, // 50 MB
maxRequestSize = 1024 * 1024 * 100)
// 100 MB
public class FileUpload extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String uploadFilePath = bean.Dir.getDir(request);
		bean.Dir.mkDir(uploadFilePath); // cria a diretoria se não existir
		
		Jogador jogador = (Jogador) request.getSession().getAttribute("jogador");
		String username = jogador.getUserInUse();
		// Get all the parts from request and write it to the file on server
		for (Part part : request.getParts()) {
			String fileName = getFileName(part);
			fileName = fileName.substring(fileName.lastIndexOf('\\') + 1); // by P Filipe
			System.out.println("printo do uploadfile " + uploadFilePath);
			String loadPath = uploadFilePath + File.separator + fileName;
			System.out.println("File to be uploaded: " + loadPath);
			byte[] buffer = new byte[8 * 1024];
			InputStream input = part.getInputStream();
			OutputStream output = new FileOutputStream(loadPath);
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1) 
				output.write(buffer, 0, bytesRead);
			String newPath ="images/" + fileName;
						
			jogador.changeFoto(username, newPath);
			String message = "";
			try {
				message = jogador.recebeServidor();
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(message);
			output.close();
			input.close();
		}

		request.setAttribute("message", "File(s) uploaded successfully!");
		getServletContext().getRequestDispatcher("/changeFoto.jsp")
				.forward(request, response);
	}

	/**
	 * Utility method to get file name from HTTP header content-disposition
	 */
	private String getFileName(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		System.out.println("content-disposition header= " + contentDisp);
		String[] tokens = contentDisp.split(";");
		for (String token : tokens) {
			if (token.trim().startsWith("filename")) {
				return token.substring(token.indexOf("=") + 2,
						token.length() - 1);
			}
		}
		return "";
	}
}