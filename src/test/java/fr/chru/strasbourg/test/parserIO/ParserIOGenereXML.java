package fr.chru.strasbourg.test.parserIO;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.junit.Test;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.chru.strasbourg.enums.parserIO.Type;
import fr.chru.strasbourg.objects.parserIO.CodeBarreStructure;
import fr.chru.strasbourg.objects.parserIO.IParserIO;
import fr.chru.strasbourg.objects.parserIO.Identifier;
import fr.chru.strasbourg.objects.parserIO.ParserIO;
import fr.chru.strasbourg.objects.parserIO.ParserIORecursif;

// Copyright (C) 2009-2011 Association Réseau Phast
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

/**
 * Classe servant à charger un fichier de test contenant les codes barres,
 * à la parser et à comparer avec les résultats attendus
 * 
 * @author Guillaume Lefebvre
 */
public class ParserIOGenereXML {
  
  private final static Logger logger = Logger.getLogger(ParserIOGenereXML.class);
  
  private final static String xsdFilename = "Barcodestore.0.1.4.xsd";

  /**
   * 
   * @param parserIO
   * @param filename
   */
  private static void genererXMLFromParserIO(IParserIO parserIO, String filename) {
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
      Document inputDocument = builder.parse(new File("src/main/resources/"+filename));
      XPathFactory xPathfactory = XPathFactory.newInstance();
      XPath xpath = xPathfactory.newXPath();
      XPathExpression expr = xpath.compile("/Barcodestore/Analyses/Analyse/Barcode");
      NodeList nl = (NodeList) expr.evaluate(inputDocument, XPathConstants.NODESET);
      int length = nl.getLength();

      /*
       * Création du document XML en sortie
       */
      SimpleDateFormat spdf = new SimpleDateFormat("yyyyMMddHHmmss");
      String result = spdf.format(new java.util.Date());
      String outputFileName = "src/main/resources/"+result+".xml";
      logger.log(Level.INFO, "Nom du fichier en sortie : "+outputFileName);
      Document outputDocument = builder.newDocument();
      
      Element bareCodeElt = outputDocument.createElement("Barcodestore");
      bareCodeElt.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
      bareCodeElt.setAttribute("xsi:noNamespaceSchemaLocation", xsdFilename);
      outputDocument.appendChild(bareCodeElt);
      
      Element version = outputDocument.createElement("Version");
//      version.appendChild(outputDocument.createTextNode(XTimestamp.now().toFileNameString()));
      bareCodeElt.appendChild(version);

      Element providerName = outputDocument.createElement("ProviderName");
      providerName.appendChild(outputDocument.createTextNode("LEFEBVRG"));
      bareCodeElt.appendChild(providerName);

      Element analyses = outputDocument.createElement("Analyses");
      bareCodeElt.appendChild(analyses);

      Map<Type, Integer> mapNbCBparType = new HashMap<Type, Integer>();

      for (int count = 0;count < length; count++) {
        Node node = nl.item(count);
        String codeBarre = node.getTextContent();
        logger.log(Level.INFO, (count+1)+"/"+length+" code barre : "+codeBarre);
        
        CodeBarreStructure codeBarreStructure = parserIO.parse(codeBarre);

        Integer nbCB = mapNbCBparType.get(codeBarreStructure.getType());
        if (nbCB == null) {
          nbCB = new Integer(0);
        }
        nbCB++;
        mapNbCBparType.put(codeBarreStructure.getType(), nbCB);
        
        outputDocument = parseBarcode(outputDocument, analyses, codeBarreStructure);
      }
      
      ecrireDocument(outputDocument, outputFileName);
      
      logger.log(Level.INFO, "Nombre de codes barre analysés : " + length);
      for (Type type : mapNbCBparType.keySet()) {
        logger.log(Level.INFO, "  "+type+" : "+mapNbCBparType.get(type));
      }
    }
    catch (ParserConfigurationException e) {
      logger.log(Level.ERROR, "", e);
    }
    catch (SAXException e) {
      logger.log(Level.ERROR, "", e);
    }
    catch (IOException e) {
      logger.log(Level.ERROR, "", e);
    }
    catch (XPathExpressionException e) {
      logger.log(Level.ERROR, "", e);
    }
    catch (DOMException e) {
      logger.log(Level.ERROR, "", e);
    }
    catch (Exception e) {
      logger.log(Level.ERROR, "", e);
    }
  }
  
  /**
   * Analyse des codes barres avec l'ancien parserIO
   */
  @Test
  public void testParserIOGenereXML() {
    genererXMLFromParserIO(new ParserIO(), "Barcodestore_master_20180829154420.xml");
  }

  /**
   * Analyse des codes barres avec le nouveau parserIO récursif
   */
  @Test
  public void testParserIORecursifGenereXML() {
    genererXMLFromParserIO(new ParserIORecursif(), "Barcodestore_master_20180829154420.xml");
  }
  
  private static Document parseBarcode(Document document, Element eltRoot, CodeBarreStructure codeBarreStructure)
      throws Exception {
    
    if (codeBarreStructure == null) {
      throw new Exception("Barcode est null"); 
    }
    
    String barcode = codeBarreStructure.getCodeBarreOrigine();
    Type type = codeBarreStructure.getType();
    String subType = codeBarreStructure.getSubType();
    String ACL = codeBarreStructure.getAcl();
    String ADDITIONALID = codeBarreStructure.getAdditionnalId();
    String AdditionalInformation = codeBarreStructure.getAdditionalInformation();
    String BESTBEFORE = codeBarreStructure.getBestBefore();
    String CIP = codeBarreStructure.getCip();
    boolean containsOrMayContainId = codeBarreStructure.isContainsOrMayContainId();
    String CONTENT = codeBarreStructure.getContent();
    String COUNT = codeBarreStructure.getCount();
    String custPartNo = codeBarreStructure.getCustPartNo();
    String EAN = codeBarreStructure.getEan();
    String Expiry = codeBarreStructure.getExpiry();
    String Family = codeBarreStructure.getFamily();
    String GTIN = codeBarreStructure.getGtin();
    String INTERNAL_91 = codeBarreStructure.getInternal_91();
    String INTERNAL_92 = codeBarreStructure.getInternal_92();
    String INTERNAL_93 = codeBarreStructure.getInternal_93();
    String INTERNAL_94 = codeBarreStructure.getInternal_94();
    String INTERNAL_95 = codeBarreStructure.getInternal_95();
    String INTERNAL_96 = codeBarreStructure.getInternal_96();
    String INTERNAL_97 = codeBarreStructure.getInternal_97();
    String INTERNAL_98 = codeBarreStructure.getInternal_98();
    String INTERNAL_99 = codeBarreStructure.getInternal_99();
    String LIC = codeBarreStructure.getLic();
    String Lot = codeBarreStructure.getLot();
    String LPP = codeBarreStructure.getLpp();
    String NaS7 = codeBarreStructure.getNas7();
    String NormalizedBESTBEFORE = codeBarreStructure.getNormalizedBestBefore();
    String NormalizedExpiry = codeBarreStructure.getNormalizedExpiry();
    String NormalizedPRODDATE = codeBarreStructure.getNormalizedProdDate();
    String parserIOversion = codeBarreStructure.getParserIOVersion();
    String PCN = codeBarreStructure.getPcn();
    String PRODDATE = codeBarreStructure.getProdDate();
    String Quantity = codeBarreStructure.getQuantity();
    String Reference = codeBarreStructure.getReference();
    String NaSIdParamName = codeBarreStructure.getNasIdParamName();
    String Serial = codeBarreStructure.getSerial();
    String SymbologyID = codeBarreStructure.getSymbologyId();
    String SSCC = codeBarreStructure.getSscc();
    String storageLocation = codeBarreStructure.getStorageLocation();
    String UoM = codeBarreStructure.getUom();
    String UPN = codeBarreStructure.getUpn();
    String VARCOUNT = codeBarreStructure.getVarCount();
    String VARIANT = codeBarreStructure.getVariant();
    String UDI = codeBarreStructure.getUdi();
//    List<Identifier> identifiers = codeBarreStructure.getIdentifiers();
    
    String executeResult = "0";
    
    Element elementAnalyse = document.createElement("Analyse");
    eltRoot.appendChild(elementAnalyse);
    
    Element element = null;
    
    Element elementIS = document.createElement("InformationSet");
    elementAnalyse.appendChild(elementIS);
    
    element = document.createElement("AnalyseId");
    element.setTextContent(Integer.toString(eltRoot.getChildNodes().getLength()-1));
    elementAnalyse.appendChild(element);

    element = document.createElement("Barcode");
    element.setTextContent(barcode);
    elementAnalyse.appendChild(element);

    element = document.createElement("Commentary");
    element.setTextContent("");
    elementAnalyse.appendChild(element);

    element = document.createElement("TimeStamp");
//    element.setTextContent(XTimestamp.now().toString());
    elementAnalyse.appendChild(element);

    element = document.createElement("executeResult");
    element.setTextContent(executeResult);
    elementIS.appendChild(element);

    element = document.createElement("ACL");
    element.setTextContent(ACL);
    elementIS.appendChild(element);

    element = document.createElement("ADDITIONALID");
    element.setTextContent(ADDITIONALID);
    elementIS.appendChild(element);

    element = document.createElement("BESTBEFORE");
    element.setTextContent(BESTBEFORE);
    elementIS.appendChild(element);

    element = document.createElement("CIP");
    element.setTextContent(CIP);
    elementIS.appendChild(element);

    element = document.createElement("Company");
    element.setTextContent("");
    elementIS.appendChild(element);

    element = document.createElement("ContainsOrMayContainId");
    element.setTextContent(Boolean.toString(containsOrMayContainId));
    elementIS.appendChild(element);

    element = document.createElement("CONTENT");
    element.setTextContent(CONTENT);
    elementIS.appendChild(element);

    element = document.createElement("COUNT");
    element.setTextContent(COUNT);
    elementIS.appendChild(element);

    element = document.createElement("CUSTPARTNO");
    element.setTextContent(custPartNo);
    elementIS.appendChild(element);

    element = document.createElement("EAN");
    element.setTextContent(EAN);
    elementIS.appendChild(element);

    element = document.createElement("Expiry");
    element.setTextContent(Expiry);
    elementIS.appendChild(element);

    element = document.createElement("Family");
    element.setTextContent(Family);
    elementIS.appendChild(element);

    element = document.createElement("GTIN");
    element.setTextContent(GTIN);
    elementIS.appendChild(element);

    element = document.createElement("INTERNAL_91");
    element.setTextContent(INTERNAL_91);
    elementIS.appendChild(element);

    element = document.createElement("INTERNAL_92");
    element.setTextContent(INTERNAL_92);
    elementIS.appendChild(element);

    element = document.createElement("INTERNAL_93");
    element.setTextContent(INTERNAL_93);
    elementIS.appendChild(element);

    element = document.createElement("INTERNAL_94");
    element.setTextContent(INTERNAL_94);
    elementIS.appendChild(element);

    element = document.createElement("INTERNAL_95");
    element.setTextContent(INTERNAL_95);
    elementIS.appendChild(element);

    element = document.createElement("INTERNAL_96");
    element.setTextContent(INTERNAL_96);
    elementIS.appendChild(element);

    element = document.createElement("INTERNAL_97");
    element.setTextContent(INTERNAL_97);
    elementIS.appendChild(element);

    element = document.createElement("INTERNAL_98");
    element.setTextContent(INTERNAL_98);
    elementIS.appendChild(element);

    element = document.createElement("INTERNAL_99");
    element.setTextContent(INTERNAL_99);
    elementIS.appendChild(element);

    element = document.createElement("LIC");
    element.setTextContent(LIC);
    elementIS.appendChild(element);

    element = document.createElement("Lot");
    element.setTextContent(Lot);
    elementIS.appendChild(element);

    element = document.createElement("LPP");
    element.setTextContent(LPP);
    elementIS.appendChild(element);

    element = document.createElement("NaS7");
    element.setTextContent(NaS7);
    elementIS.appendChild(element);

    element = document.createElement("NormalizedBESTBEFORE");
    element.setTextContent(NormalizedBESTBEFORE);
    elementIS.appendChild(element);

    element = document.createElement("NormalizedExpiry");
    element.setTextContent(NormalizedExpiry);
    elementIS.appendChild(element);

    element = document.createElement("NormalizedPRODDATE");
    element.setTextContent(NormalizedPRODDATE);
    elementIS.appendChild(element);

    element = document.createElement("PCN");
    element.setTextContent(PCN);
    elementIS.appendChild(element);

    element = document.createElement("PRODDATE");
    element.setTextContent(PRODDATE);
    elementIS.appendChild(element);

    element = document.createElement("Product");
    element.setTextContent("");
    elementIS.appendChild(element);

    element = document.createElement("Quantity");
    element.setTextContent(Quantity);
    elementIS.appendChild(element);

    element = document.createElement("Reference");
    element.setTextContent(Reference);
    elementIS.appendChild(element);

    element = document.createElement("NaSIdParamName");
    element.setTextContent(NaSIdParamName);
    elementIS.appendChild(element);

    element = document.createElement("Serial");
    element.setTextContent(Serial);
    elementIS.appendChild(element);

    element = document.createElement("SSCC");
    element.setTextContent(SSCC);
    elementIS.appendChild(element);

    element = document.createElement("StorageLocation");
    element.setTextContent(storageLocation);
    elementIS.appendChild(element);

    element = document.createElement("SubType");
    element.setTextContent(subType);
    elementIS.appendChild(element);

    element = document.createElement("SymbologyID");
    element.setTextContent(SymbologyID);
    elementIS.appendChild(element);

    element = document.createElement("Type");
    element.setTextContent(type.toString());
    elementIS.appendChild(element);

    element = document.createElement("UDI");
    element.setTextContent(UDI);
    elementIS.appendChild(element);

    element = document.createElement("UoM");
    element.setTextContent(UoM);
    elementIS.appendChild(element);

    element = document.createElement("UPN");
    element.setTextContent(UPN);
    elementIS.appendChild(element);

    element = document.createElement("VARCOUNT");
    element.setTextContent(VARCOUNT);
    elementIS.appendChild(element);

    element = document.createElement("VARIANT");
    element.setTextContent(VARIANT);
    elementIS.appendChild(element);

    element = document.createElement("AdditionalInformation");
    element.setTextContent(AdditionalInformation);
    elementIS.appendChild(element);

    Element elementIdentifiers = document.createElement("Identifiers");
    elementIS.appendChild(elementIdentifiers);

//    for (Identifier identifier : identifiers) {
//      Element elementIdentifier = document.createElement("Identifier");
//      elementIdentifiers.appendChild(elementIdentifier);
//  
//      element = document.createElement("Value");
//      element.setTextContent(identifier.getValue());
//      elementIdentifier.appendChild(element);
//    }

    element = document.createElement("ParserIOVersion");
    element.setTextContent(parserIOversion);
    elementIS.appendChild(element);

    return document;
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
  }
}