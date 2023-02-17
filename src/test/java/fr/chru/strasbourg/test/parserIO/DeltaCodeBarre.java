package fr.chru.strasbourg.test.parserIO;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


// Copyright (C) 2009-2014 Association Réseau Phast
// This file is part of ParserIO.
// ParserIO is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// ParserIO is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with ParserIO.  If not, see <http://www.gnu.org/licenses/>.
//
//For more information, please consult the ParserIO web site at
//<http://parserio.codeplex.com>
//
// 14/09/11 Version 1.0.0.0
// 14/09/11 DU [fr] Livraison de la première version qui utilise le schéma Barcodestore.0.0.1.xsd
//             [en] First release Barcodestore.0.0.1.xsd compliant

/**
 * Programme qui va comparer le fichier de test de code barre au fichier de résultat généré
 * 
 * @author Guillaume Lefebvre
 */
public class DeltaCodeBarre {

  private final static Logger logger = Logger.getLogger(DeltaCodeBarre.class);

  /**
   * @param args
   */
  public static void main(String[] args) {

    try {
      // On définit un pattern plus simple que celui par défaut pour le logger
      PatternLayout patternLayout = new PatternLayout("[%t] %-5p %M - %m%n");
      ConsoleAppender consoleAppender = new ConsoleAppender(patternLayout);
      BasicConfigurator.configure(consoleAppender);

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();

      /*
       * Lecture du fichier XML
       */
      String filename1 = "Barcodestore_master_20180829154420.xml";
      Document inputDocument1 = builder.parse(new File("src/main/resources/"+filename1));
      XPathFactory xPathfactory1 = XPathFactory.newInstance();
      XPath xpath1 = xPathfactory1.newXPath();
      XPathExpression expr1 = xpath1.compile("/Barcodestore/Analyses/Analyse");
      NodeList nl1 = (NodeList) expr1.evaluate(inputDocument1, XPathConstants.NODESET);
      int length1 = nl1.getLength();
      logger.info("Nb de node trouvé pour "+filename1+" : "+length1);

      /*
       * Lecture du fichier XML
       */
      String filename2 = "20181008111539.xml";
      Document inputDocument2 = builder.parse(new File("src/main/resources/"+filename2));
      XPathFactory xPathfactory2 = XPathFactory.newInstance();
      XPath xpath2 = xPathfactory2.newXPath();
      XPathExpression expr2 = xpath2.compile("/Barcodestore/Analyses/Analyse");
      NodeList nl2 = (NodeList) expr2.evaluate(inputDocument2, XPathConstants.NODESET);
      int length2 = nl2.getLength();
      logger.info("Nb de node trouvé pour "+filename2+" : "+length2);

      
      XMLUnit.setIgnoreWhitespace(true);
      XMLUnit.setIgnoreAttributeOrder(true);

      DetailedDiff diff = new DetailedDiff(XMLUnit.compareXML(new FileReader(new File("src/main/resources/"+filename1)),
          new FileReader(new File("src/main/resources/"+filename2))));

      List<?> allDifferences = diff.getAllDifferences();
      Assert.assertEquals("Differences found: "+ diff.toString(), 0, allDifferences.size());
      
      /*
       * Création du document XML en sortie
       */
//      SimpleDateFormat spdf = new SimpleDateFormat("yyyyMMddHHmmss");
//      String result = spdf.format(XTimestamp.now());
//      String outputFileName = "src/main/resources/" + result + ".xml";
//      logger.info("Nom du fichier en sortie : "+outputFileName);
//      Document outputDocument = builder.newDocument();
//
//      Element bareCodeElt = outputDocument.createElement("Barecodestore");
//      bareCodeElt.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
//      bareCodeElt.setAttribute("xsi:noNamespaceSchemaLocation", "Barcodestore.0.1.4.xsd");
//      bareCodeElt.setAttribute("Version", "20180829134424");
//      outputDocument.appendChild(bareCodeElt);
//
//      float countDelta = 0;
//      for (int count = 0; count < length1; count++) {
//        Node node1 = nl1.item(count);
//        Node node2 = nl2.item(count);
//
//        if (node1 == null) {
//          logger.log(Level.ERROR, "node1 est null pour count="+count+"/"+length1);
//        }
//        else if (node2 == null) {
//          logger.log(Level.ERROR, "node2 est null pour count="+count+"/"+length1);
//        }
//        else if (!node1.getTextContent().equals(node2.getTextContent())) {
//          logger.info("Différence détectée :");
//          logger.info("Dans "+filename1+" : "+ node1.getTextContent());
//          logger.info("Dans "+filename2+" : "+ node2.getTextContent());
//
//          Element elementAnalyse = outputDocument.createElement("Analyse");
//          elementAnalyse.appendChild(node2);
//          bareCodeElt.appendChild(elementAnalyse);
//          countDelta++;
//        }
//      }
//      float taux = (countDelta / length1) * 100;
//      logger.info("");
//      logger.info("count1: " + length1);
//      logger.info("count2: " + length2);
//      logger.info("countDelta: " + countDelta);
//      logger.info("taux: " + taux);
//      
//      ecrireDocument(outputDocument, outputFileName);
      
    } catch (ParserConfigurationException e) {
      logger.log(Level.ERROR, "", e);
    } catch (SAXException e) {
      logger.log(Level.ERROR, "", e);
    } catch (IOException e) {
      logger.log(Level.ERROR, "", e);
    } catch (XPathExpressionException e) {
      logger.log(Level.ERROR, "", e);
    } catch (DOMException e) {
      logger.log(Level.ERROR, "", e);
    } catch (Exception e) {
      logger.log(Level.ERROR, "", e);
    }
  }
  

  /**
   * Ecrit dans un fichier un document DOM, étant donné un nom de fichier.
   * 
   * @param doc le document à écrire
   * @param nomFichier le nom du fichier de sortie
   * @throws TransformerException 
   */
  private static void ecrireDocument(Document doc, String nomFichier)
  throws TransformerException {
    // on considère le document "doc" comme étant la source d'une
    // transformation XML
    Source source = new DOMSource(doc);

    // le résultat de cette transformation sera un flux d'écriture dans
    // un fichier
    Result resultat = new StreamResult(new File(nomFichier));

    // création du transformateur XML
    Transformer transfo = null;
    try {
      transfo = TransformerFactory.newInstance().newTransformer();
    }
    catch (TransformerConfigurationException e) {
      logger.log(Level.ERROR, "Impossible de créer un transformateur XML.", e);
      throw e;
    }

    // configuration du transformateur

    // sortie en XML
    transfo.setOutputProperty(OutputKeys.METHOD, "xml");

    // inclut une déclaration XML (recommandé)
    transfo.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

    // codage des caractères : UTF-8. Ce pourrait être également ISO-8859-1
    transfo.setOutputProperty(OutputKeys.ENCODING, "utf-8");

    // idente le fichier XML
    transfo.setOutputProperty(OutputKeys.INDENT, "yes");

    try {
      transfo.transform(source, resultat);
    }
    catch (TransformerException e) {
      logger.log(Level.ERROR, "La transformation a échoué : " + e);
      throw e;
    }
  }}