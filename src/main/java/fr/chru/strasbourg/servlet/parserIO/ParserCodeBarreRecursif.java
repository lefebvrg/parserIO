package fr.chru.strasbourg.servlet.parserIO;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import fr.chru.strasbourg.objects.parserIO.CodeBarreStructure;
import fr.chru.strasbourg.objects.parserIO.IParserIO;
import fr.chru.strasbourg.objects.parserIO.ParserIORecursif;

/**
 * Méthode qui renvoie un objet JSON correspondant aux infos structurées extraites d'un code barre
 * 
 * Parametre :
 * - codeBarre: le code barre à parser
 *
 * @author Guillaume Lefebvre
 */
@SuppressWarnings("serial")
@WebServlet("/parserCodeBarreRecursif.view")
public class ParserCodeBarreRecursif extends HttpServlet {
	
	private final static Logger logger = Logger.getLogger(ParserCodeBarreRecursif.class);

  /**
   * Méthode qui renvoie un objet JSON correspondant aux infos structurées extraites d'un code barre
   * 
   * @param request
   * @param response 
   */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
  	
    String codeBarre = request.getParameter("codeBarre") != null && !request.getParameter("codeBarre").trim().equals("") ? request.getParameter("codeBarre").trim() : null;

    if (logger.isDebugEnabled()) {
    	logger.log(Level.DEBUG, "request.codeBarre = " + codeBarre);
    }

    if (codeBarre == null) {
			logger.log(Level.ERROR, "Le parametre 'codeBarre' est obligatoire");
	    response.setContentType("application/json; charset=UTF-8");
	    response.setHeader("Cache-Control", "no-cache");
	    PrintWriter pw = response.getWriter();
	    pw.write("null");
	    pw.close();
    }
    
    JSONObject infoCodeBarreJSON = null;
		try {
		  IParserIO parserIO = new ParserIORecursif();
		  CodeBarreStructure codeBarreStructure = parserIO.parse(codeBarre);
      if (logger.isDebugEnabled())
        logger.log(Level.DEBUG, "codeBarreStructure = " + codeBarreStructure);

		  if (codeBarreStructure != null) {
		    infoCodeBarreJSON = codeBarreStructure.toJSON();
		  }
		} catch (JSONException e) {
			logger.log(Level.ERROR, "Impossible de créer l'objet JSON", e);
			throw new ServletException("Impossible de créer l'objet JSON", e);
    }

    /**
     * Envoie de la réponse
     */
    response.setContentType("application/json; charset=UTF-8");
    response.setHeader("Cache-Control", "no-cache");
    PrintWriter pw = response.getWriter();
    pw.write(infoCodeBarreJSON != null ? infoCodeBarreJSON.toString() : "null");
		pw.close();
  }
}
