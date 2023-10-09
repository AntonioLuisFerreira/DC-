/**
 * 
 */
package bean;

import java.io.File;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Prof. P. Filipe
 *
 */
final public class Dir {
	static final String dirName="images";
	
	static public String getDir(HttpServletRequest request) {
		return request.getServletContext().getRealPath("/")+dirName+File.separator;
	}

	// retorna true se criou a diretoria
	static public boolean mkDir(String dirPath) {
		// creates a directory if it does not exists
		File fileSaveDir = new File(dirPath);
		if (!fileSaveDir.exists()) {
			return fileSaveDir.mkdirs();
		}
		return false;
	}
}
