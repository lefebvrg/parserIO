package fr.chru.strasbourg.objects.parserIO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.chru.strasbourg.enums.parserIO.Type;

// Copyright (C) 2009-2016 Association Réseau Phast
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
 * Classe principale du parser
 * 
 * @author Guillaume Lefebvre
 */
public class ParserIO implements IParserIO {

  private final static Logger logger = Logger.getLogger(ParserIO.class);

  private static String hibcASDlist = "(/14D|/16D|/S)";
  
  private static int[] _month_days = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

  private static List<Character> nValueAssignements = new ArrayList<Character>(); 

  /**
   * Constructeur
   */
  public ParserIO() {
    char[] nValuesAssignmets = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '-', '.', ' ', '$', '/', '+', '%' };
    for (char c : nValuesAssignmets) {
      nValueAssignements.add(c);
    }
  }
  
//  private boolean Alphabetic(char c) {
//    return (c == 'A' || c == 'B' || c == 'C' || c == 'D' || c == 'E' || c == 'F' || c == 'G' || c == 'H' || c == 'I'
//         || c == 'J' || c == 'K' || c == 'L' || c == 'M' || c == 'N' || c == 'O' || c == 'P' || c == 'Q'
//         || c == 'R' || c == 'S' || c == 'T' || c == 'U' || c == 'V' || c == 'W' || c == 'X' || c == 'Y' || c == 'Z');
//  }

//  private boolean numericChar(char c) {
//    return (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9');
//  }

//  private boolean numericString(String code) {
//    boolean ok = true;
//    
//    char[] array = code.toCharArray();
//    for (int i = 0; i < code.length(); i++) {
//      if (Character.isDigit(array[i])) {
//        ok = false;
//        break;
//      }
//    }
//    return ok;
//  }

  @Override
  public String getVersion() {
    return "1.0.0.10";
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return
   */
  public String getSymbologyId(String code) {
    String result = "";
    code = cleanse(code);
    String id = "";
    if (code.length() >= 3)
      id = code.substring(0, 3);
    if (id.equals("]A0"))
      result = "A0";
    else if (id.equals("]C0"))
      result = "C0";
    else if (id.equals("]C1"))
      result = "C1";
    else if (id.equals("]d1"))
      result = "d1";
    else if (id.equals("]d2"))
      result = "d2";
    return result;
  }

  /**
   * @param code le code barre brut à analyser
   * @return GS1(240): Identification complémentaire du produit
   */
  public String getAdditionnalId(String code) {
    String result = "";
    Type type = getType(code);
    String subType = getSubType(code, type);
    result = getAdditionnalId(code, type, subType);
    return result;
  }

  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return GS1(240): Identification complémentaire du produit
   */
  public String getAdditionnalId(String code, Type type, String subType) {
    code = cleanse(code);
    String result = "";
    if (type == Type.GS1_128 || type == Type.GS1_Datamatrix) {
      code = cleanSymbologyId(code);
      if (subType.equals("01.17.10.240")) {
        if (containsGS(code)) {
          // #TODO MB Controler Longueur code
          int nextGS = indexOfGS(code, 27);
          result = code.substring(nextGS + 4, code.length());
        }
      }
      else if (subType.equals("01.240")) {
        // #TODO MB Controler Longueur code
        result = code.substring(19, code.length());
      }
      else if (subType.equals("240")) {
        // #TODO MB Controler Longueur code
        result = code.substring(3, code.length());
      }
      else if (subType.startsWith("240.")) {
        // #TODO MB Controler Longueur code
        int nextGS = indexOfGS(code, 1);
        result = code.substring(3, nextGS);
      }
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return HIBC: Universal Product Identifier (LIC+PCN+UOM)
   */
  public String getUpn(String code) {
    Type type = getType(code);
    String subType = getSubType(code);
    String result = getUpn(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return HIBC: Universal Product Identifier (LIC+PCN+UOM)
   */
  public String getUpn(String code, Type type, String subType) {
    String result = "";
    if (type == Type.HIBC && subType.startsWith("Primary")) {
      result = getLic(code, type, subType).toString() + getPcn(code, type, subType).toString() + getUom(code, type, subType);
    }
    return result;
  }
  
  /**
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return NaS7 ou EAN ou ACL
   */
  public String getNaSIdParamName(Type type, String subType) {
    String result = "";
    if (type == Type.NaS) {
      if (subType.equals("NaS7")) {
        result = "NaS7";
      }
      else if (subType.equals("001") || subType.equals("004")) {
        result = "EAN";
      }
      else if (subType.equals("002") || subType.equals("003")) {
        result = "ACL";
      }
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return GS1(15): Date limite de conservation
   */
  public String getBestBefore(String code) {
    Type type = getType(code);
    String subType = getSubType(code);
    String result = getBestBefore(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return GS1(15): Date limite de conservation
   */
  public String getBestBefore(String code, Type type, String subType) {
    code = cleanse(code);
    String result = "";
    if (type == Type.GS1_128 || type == Type.GS1_Datamatrix) {
      code = cleanSymbologyId(code);
      if (subType.startsWith("01.15")) {
//        result = code.substring(18, 6);
        result = code.substring(18, 24);
      }
      else if (subType.startsWith("02.10.15")) {
        int nextGS = indexOfGS(code, 16);
//        result = code.substring(nextGS + 3, 6);
        result = code.substring(nextGS + 3, nextGS + 9);
      }
    }
    return result;
  }

  private boolean checkEan13Key(String code) {
    boolean result = false;
    int length = code.length();
    if (length == 13) {
      int sum = 0;
      boolean ok = true;
      for (int i = 0; i < 12; i++) {
        char c = code.charAt(i);
        try {
          int n = Integer.parseInt(Character.toString(c));
          if (i % 2 == 0) { // [fr] pair
            sum = sum + n;
          } else { // [fr] impair
            sum = sum + (3 * n);
          }
        } catch (NumberFormatException e) {
          ok = false;
          break;
        }
      }
      if (ok) {
        int n1 = (10 - (sum % 10)) % 10;
        char c = code.charAt(12);
        try {
          int n2 = Integer.parseInt(Character.toString(c));
          result = (n1 == n2);
        } catch (NumberFormatException e) {
        }
      }
    }
    return result;
  }

//  private boolean checkEan14Key(String code) {
//    boolean result = false;
//    int length = code.length();
//    if (length == 14) {
//      int sum = 0;
//      boolean ok = true;
//      for (int i = 0; i < 13; i++) {
//        char c = code.charAt(i);
//        try {
//          int n = Integer.parseInt(Character.toString(c));
//          if (i % 2 == 0) { // [fr] pair
//                            // [en] even
//            sum = sum + (3 * n);
//          } else { // [fr] impair
//                   // [en] odd
//            sum = sum + n;
//          }
//        } catch (NumberFormatException e) {
//          ok = false;
//          break;
//        }
//      }
//      if (ok) {
//        int n1 = (10 - (sum % 10)) % 10;
//        char c = code.charAt(13);
//        try {
//          int n2 = Integer.parseInt(Character.toString(c));
//          result = (n1 == n2);
//        } catch (NumberFormatException e) {
//        }
//      }
//    }
//    return result;
//  }

  private boolean checkHIBCKey(String code) {
    boolean result = false;
    int sum = 0;
    char[] array = code.toCharArray();
    int mod = 0;
    for (int i = 0; i < code.length() - 1; i++) {
      char c = array[i];
      sum = sum + nValueAssignements.indexOf(c);
      // sum = sum + mod;
    }
    mod = sum % 43;
    char lastCharCode = array[code.length() - 1];
    if (nValueAssignements.get(mod) == lastCharCode)
      result = true;
    return result;
  }

  private boolean checkGTINKey(String code) {
    boolean result = false;
    int sum = 0;
    int n = -1;
    for (int i = 0; i < 13; i++) {
      char c = code.charAt(i);
      try {
        n = Integer.parseInt(Character.toString(c));
        if (i % 2 == 0) { // [fr] pair
                          // [en] even
          sum = sum + (3 * n);
        } else { // [fr] impair
                 // [en] odd
          sum = sum + n;
        }
      }
      catch (NumberFormatException e) {
      }
    }
    int n1 = (10 - (sum % 10)) % 10;
    char key = code.charAt(13);
    try {
      int n2 = Integer.parseInt(Character.toString(key));
      result = (n1 == n2);
    }
    catch (NumberFormatException e) {
    }

    return result;
  }

  private boolean checkSSCCKey(String code) {
    boolean result = false;
    int sum = 0;
    int n = -1;
    for (int i = 2; i < 19; i++) {
      char c = code.charAt(i);
      try {
        n = Integer.parseInt(Character.toString(c));
        if (i % 2 == 0) { // [fr] pair
          sum = sum + (3 * n);
        } else { // [fr] impair
          sum = sum + n;
        }
      } catch (NumberFormatException e) {
      }
    }
    int n1 = (10 - (sum % 10)) % 10;
    char key = code.charAt(19);
    try {
      int n2 = Integer.parseInt(Character.toString(key));
      result = (n1 == n2);
    } catch (NumberFormatException e) {
    }
    return result;
  }

  private boolean check7Car(String code) {
    boolean result = false;
    boolean ok = true;
    int sum = 0;
    int n = -1;
    for (int i = 0; i < code.length(); i++) {
      if (!Character.isDigit(code.charAt(i))) {
        ok = false;
        break;
      }
    }
    if (ok) {
      for (int i = 0; i < 6; i++) {
        char c = code.charAt(i);
        try {
          n = Integer.parseInt(Character.toString(c));
          sum = sum + n * (i + 2);
        } catch (NumberFormatException e) {
        }
      }
      int n1 = (sum % 11) % 10;
      char key = code.charAt(6);
      try {
        int n2 = Integer.parseInt(Character.toString(key));
        result = (n1 == n2);
      } catch (NumberFormatException e) {
      }
    }
    return result;
  }

  private String getKey7Car(String code) {
    String result = "-1";
    boolean ok = true;
    int sum = 0;
    char[] array = code.toCharArray();
    int n = -1;
    for (int i = 0; i < code.length(); i++) {
      if (!Character.isDigit(array[i])) {
        ok = false;
        break;
      }
    }
    if (ok) {
      for (int i = 0; i < 6; i++) {
        char c = code.charAt(i);
        try {
          n = Integer.parseInt(Character.toString(c));
          sum = sum + n * (i + 2);
        } catch (NumberFormatException e) {
        }
      }
      int n1 = (sum % 11) % 10;
      result = new Integer(n1).toString();
    }
    return result;
  }
  
  /**
   * 
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return indique si le code barre contient ou pourrait contenir un identifiant
   */
  public boolean containsOrMayContainId(String code, Type type, String subType) {
    boolean result = false;
    code = cleanse(code);
    code = cleanSymbologyId(code);
    if (type == Type.GS1_128 || type == Type.GS1_Datamatrix) {
      if (subType.contains("01") || subType.contains("240")
       || subType.contains("90") || subType.contains("91")
       || subType.contains("92") || subType.contains("93"))
        result = true;
    } else if (type == Type.HIBC && subType.contains("Primary")) {
      result = true;
    } else if (type == Type.NaS && code.length() > 0) // To support case where just the SymbologyID is provided
    {
      if (subType.equals("") || subType.equals("NaS") || subType.equals("NaS7") || subType.equals("001")
       || subType.equals("002") || subType.equals("003") || subType.equals("004") || subType.equals("005")
       || subType.equals("006") || subType.equals("007") || subType.equals("008") || subType.equals("009")
       || subType.equals("012") || subType.equals("013") || subType.equals("014")
       || subType.equals("016")) {
        result = true;
      }
    } else if (type == Type.EAN13) { // || type == Type.EAN14) {
      result = true;
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return EAN13/GS1/NaS: Code ACL
   */
  public String getAcl(String code) {
    String result = "";
    Type type = getType(code);
    String subType = getSubType(code);
    result = getAcl(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return EAN13/GS1/NaS: Code ACL
   */
  public String getAcl(String code, Type type, String subType) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    String result = "";
    if (code.length() >= 7) {
      if (type == Type.EAN13 && subType.equals("ACL 13")) {
        result = code;
//      } else if ((type == Type.GS1_128 && (subType.startsWith("01")) && (code.substring(3, 4).equals("3401")) {
      } else if ((type == Type.GS1_128 || type == Type.GS1_Datamatrix) && subType.startsWith("01") && code.substring(3, 7).equals("3401")) {
//        result = code.substring(3, 13);
        result = code.substring(3, 16);
      } else if (type == Type.NaS && (subType.equals("002") || subType.equals("003"))) {
        result = code.substring(0, 13);
      }
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return GS1/EAN13: Code CIP
   */
  public String getCip(String code) {
    String result = "";
    Type type = getType(code);
    String subType = getSubType(code);
    result = getCip(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return GS1/EAN13: Code CIP
   */
  public String getCip(String code, Type type, String subType) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    String result = "";
    if (type == Type.GS1_128 || type == Type.GS1_Datamatrix) {
      code = cleanSymbologyId(code);
      if ((code.length() > 3) && subType.startsWith("01") && (code.substring(0, 7).equals("0103400"))) {
//        result = code.substring(3, 13);
        result = code.substring(3, 16);
      }
    } else if (type == Type.EAN13 && subType.equals("CIP 13")) {
      result = code;
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return EAN13/NaS: Code EAN
   */
  public String getEan(String code) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    Type type = getType(code);
    String subType = getSubType(code);
    String result = getEan(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return EAN13/NaS: Code EAN
   */
  public String getEan(String code, Type type, String subType) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    String result = "";
    if (type == Type.EAN13 && (subType.equals("") || subType.equals("ACL 13"))) {
      result = code;
    }
    else if (type == Type.NaS && (subType.equals("001") || subType.equals("004"))) {
      result = code.substring(0, 13);
    }
    return result;
  }

  private String cleanse(String code) {
    String result = code;
    result = result.replace("(01)", "01");
    result = result.replace("(10)", "10");
    result = result.replace("(11)", "11");
    result = result.replace("(17)", "17");
    result = result.replace("(21)", "21");
    result = result.replace("(22)", "22");
    result = result.replace("(30)", "30");
    result = result.replace("(240)", "240");
    if (result.startsWith("*") && (result.endsWith("*"))) {
//      result = result.substring(1, result.length() - 2);
      result = result.substring(1, result.length() - 1);
    }
    if (result.startsWith("]C0*") && (result.endsWith("*"))) {
      result = result.replace("*", "");
    }
    return result;
  }

  private boolean containsSymbologyId(String code) {
    boolean result = false;
    if (code.startsWith("]A0")
     || code.startsWith("]C0")
     || code.startsWith("]C1")
     || code.startsWith("]d1")
     || code.startsWith("]d2")
     || code.startsWith("]E0")) {
      result = true;
    }
    return result;
  }

  private String cleanSymbologyId(String code) {
    String result = code;
    if (containsSymbologyId(code)) {
//      result = result.substring(3, result.length() - 3);
      result = result.substring(3, result.length());
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return GS1(02): Identification of trade items contained in a logistic unit / GTIN des unités contenues dans une unité logistique
   */
  public String getContent(String code) {
    Type type = getType(code);
    String subType = getSubType(code, type);
    String result = getContent(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return GS1(02): Identification of trade items contained in a logistic unit / GTIN des unités contenues dans une unité logistique
   */
  public String getContent(String code, Type type, String subType) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    String result = "";
    if (type == Type.GS1_128 || type == Type.GS1_Datamatrix) {
      code = cleanSymbologyId(code);
      if (subType.substring(0, 2).equals("02")) {
//        result = code.substring(2, 14);
        result = code.substring(2, 16);
      }
    }
    return result;
  }

  /**
   * 
   * @param str la chaîne de caractère contenant la date
   * @param typeDate le type de date (1 à 9)
   * @return la date convertie depuis la chaîne de caractère
   */
  public Date convertDateTimeFromStr(String str, int typeDate) {
    int y = 0, m = 0, d = 0, h = 0, j = 0;
    // y ------years, m ------months, d ------days, h -------hours

    try {
      switch (typeDate) {
      case 1:
        m = Integer.parseInt(str.substring(0, 2));
        y = Integer.parseInt(str.substring(2, 4));
        break;
      case 2:
        m = Integer.parseInt(str.substring(0, 2));
        d = Integer.parseInt(str.substring(2, 4));
        y = Integer.parseInt(str.substring(4, 6));
        break;
      case 3:
        y = Integer.parseInt(str.substring(0, 2));
        m = Integer.parseInt(str.substring(2, 4));
        d = Integer.parseInt(str.substring(4, 6));
        break;
      case 4:
        y = Integer.parseInt(str.substring(0, 2));
        m = Integer.parseInt(str.substring(2, 4));
        d = Integer.parseInt(str.substring(4, 6));
        h = Integer.parseInt(str.substring(6, 8));
        break;
      case 5:
        y = Integer.parseInt(str.substring(0, 2));
        j = Integer.parseInt(str.substring(2, 5));
        break;
      case 6:
        y = Integer.parseInt(str.substring(0, 2));
        j = Integer.parseInt(str.substring(2, 5));
        h = Integer.parseInt(str.substring(5, 7));
        break;
      case 7:
        y = Integer.parseInt(str.substring(2, 4));
        m = Integer.parseInt(str.substring(5, 7));
        break;
      case 8:
        y = Integer.parseInt(str.substring(4, 6));
        m = Integer.parseInt(str.substring(0, 2));
        d = Integer.parseInt(str.substring(2, 4));
        break;
      case 9:
        y = Integer.parseInt(str.substring(8, 10));
        m = Integer.parseInt(str.substring(3, 5));
        d = Integer.parseInt(str.substring(0, 2));
        break;
      case 10:
        y = Integer.parseInt(str.substring(0, 2));
        m = Integer.parseInt(str.substring(2, 3));
        break;
      }
    }
    catch (NumberFormatException e) {
      // date invalide, on renvoie null
      return null;
    }

    // convert 2 digits year to 4 digits year
    // dt = DateTime.ParseExact(String.Format("{0:00}", y), "yy",
    // CultureInfo.CurrentUICulture);
    // y = dt.get(Calendar.YEAR);

    if (y < 100) {
      y += 2000;
    }
    y = Integer.parseInt(String.format("%04d", y));
    
    Date dt = new Date(y, 1, 1, 0, 0, 0);
    
//    DateFormat sdfp = new SimpleDateFormat("yyyy");
//    try {
//      dt = new XTimestamp(sdfp.parse(new Integer(y).toString()).getTime());
//      y = dt.get(Calendar.YEAR);
//    }
//    catch (ParseException e) {
//      return null;
//    }

    if (0 == y) {
      // invalid date time String... on renvoie null
      return null;
    }

    // convert Julian Date to DateTime
    if (0 != j) {
      dt.setDate(j - 1);
      if (h > 0) {
        dt.setHours(h);
      }
      return dt;
    }

    // if month is zero
    if (0 == m) {
      m = 12;
    }

    if (m > 12) {
      // mois invalide, on renvoie null
      return null;
    }
    // if days invalid
    if (_month_days[m - 1] < d || 0 == d) {
      if (2 == m) {
        // leap year
        if (new GregorianCalendar().isLeapYear(y)) {
          d = 29;
        } else {
          d = 28;
        }
      } else {
        d = _month_days[m - 1];
      }
    }

    // convert y,m,d,h to DateTime
    if (y > 0 && m > 0) {
      dt.setMonth(m - 1);
      dt.setDate(d - 1);
      if (h > 0) {
        dt.setHours(h);
      }
    }
    return dt;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return GS1(37): Quantité d'unités commerciales contenues (unité logistique)
   */
  public String getCount(String code) {
    Type type = getType(code);
    String subType = getSubType(code, type);
    String result = getCount(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return GS1(37): Quantité d'unités commerciales contenues (unité logistique)
   */
  public String getCount(String code, Type type, String subType) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    String result = "";
    if (type == Type.GS1_128 || type == Type.GS1_Datamatrix) {
      code = cleanSymbologyId(code);
      if (subType.equals("02.37")) {
//        result = code.substring(18, code.length() - 18);
        result = code.substring(18, code.length());
      }
      else if (subType.equals("02.10.37")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 16);
          result = code.substring(nextGS + 3, code.length());
//          result = code.substring(nextGS + 3, code.length() - (nextGS + 3));
        }
        // result = code.substring(25, 3);
      }
      else if (subType.startsWith("02.17.37.10")) {
        int nextGS = indexOfGS(code, 24);
//        result = code.substring(26, nextGS - 26);
        result = code.substring(26, nextGS);
      }
      else if (subType.startsWith("02.10.15.37")) {
        int nextGS = indexOfGS(code, 16);
//        result = code.substring(nextGS + 11, code.length() - nextGS - 11);
        result = code.substring(nextGS + 11, code.length());
      }
      else if (subType.equals("02.17.37")) {
//      result = code.substring(26, nextGS - 26);
        result = code.substring(26, code.length());
      }
      else if (subType.equals("02.37.10")) {
        int nextGS = indexOfGS(code, 16);
//        result = code.substring(18, nextGS - 17);
        result = code.substring(18, nextGS);
      }
      else if (subType.startsWith("37")) {
        int nextGS = indexOfGS(code, 1);
//        result = code.substring(2, nextGS - 2);
        result = code.substring(2, nextGS);
      }
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return GS1(17)/HIBC/NaS: Date de péremption
   */
  public String getExpiry(String code) {
    Type type = getType(code);
    String subType = getSubType(code, type);
    String result = getExpiry(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return GS1(17)/HIBC/NaS: Date de péremption
   */
  public String getExpiry(String code, Type type, String subType) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    String result = "";
    if (type == Type.GS1_128 || type == Type.GS1_Datamatrix) {

      // code = CleanSymbologyId(code);
      if (subType.startsWith("01.10.17")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 16);
//          result = code.substring(nextGS + 3, 6);
          result = code.substring(nextGS + 3, nextGS + 9);
        }
        // result = code.substring(code.length()-6, 6);
      }
      else if (subType.startsWith("01.11.10.17")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 28);
          result = code.substring(nextGS + 3, nextGS + 9);
        }
      }
      else if (subType.startsWith("01.11.17")) {
//        result = code.substring(26, 6);
        result = code.substring(26, 32);
      }
      else if (subType.startsWith("01.17")) {
//        result = code.substring(18, 6);
        result = code.substring(18, 24);
      }
      else if (subType.startsWith("02.17")) {
//        result = code.substring(18, 6);
        result = code.substring(18, 24);
      }
      else if (subType.startsWith("10.11.17")) {
        int firstGS = indexOfGS(code, 1);
//        result = code.substring(firstGS + 9 + 2, 6); //TODO MB Controler Longueur code 
        result = code.substring(firstGS + 9 + 2, firstGS + 9 + 2 + 6);
      }
      else if (subType.startsWith("10.17")) {
        int length = code.length();
        int nextGS = indexOfGS(code, 1);
//        result = code.substring(nextGS + 3, 6);
        result = code.substring(nextGS + 3, nextGS + 9);
      }
      else if (subType.startsWith("11.17")) {
//        result = code.substring(10, 6);
        result = code.substring(10, 16);
      }
      else if (subType.startsWith("17")) {
//        result = code.substring(2, 6);
        result = code.substring(2, 8);
      }
      else if (subType.equals("01.21.17")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 18);
//          result = code.substring(nextGS + 3, 6);
          result = code.substring(nextGS + 3, nextGS + 9);
        }
      }
      else if (subType.startsWith("20.17")) {
//        result = code.substring(6, 6);
        result = code.substring(6, 12);
      }
      else if (subType.startsWith("91.17.10")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 1);
//          result = code.substring(nextGS + 3, 6);
          result = code.substring(nextGS + 3, nextGS + 9);
        }
      }
    }
    else if (type == Type.HIBC && subType.contains("Secondary")) {
      // code = CleanSymbologyId(code);
      String secondaryCode = null;
      // if (subType.startsWith(@"Primary/Secondary"))
      if (subType.startsWith("Primary/Secondary")) {
        int position = code.indexOf('/');
        secondaryCode = "+" + code.substring(position + 1);
      } else {
        secondaryCode = code;
      }
      int length = secondaryCode.length();
      if (subType.endsWith("Secondary.N") && (length >= 8)) {
//        result = secondaryCode.substring(1, 5);
        result = secondaryCode.substring(1, 6);
      } else if (subType.endsWith("Secondary.$$") && (length > 7)) {
//        result = secondaryCode.substring(3, 4);
        result = secondaryCode.substring(3, 7);
      } else if (subType.endsWith("Secondary.$$.2") && (length > 10)) {
//        result = secondaryCode.substring(4, 6);
        result = secondaryCode.substring(4, 10);
      } else if (subType.endsWith("Secondary.$$.3") && (length > 10)) {
//        result = secondaryCode.substring(4, 6);
        result = secondaryCode.substring(4, 10);
      } else if (subType.endsWith("Secondary.$$.4") && (length > 12)) {
//        result = secondaryCode.substring(4, 8);
        result = secondaryCode.substring(4, 12);
      } else if (subType.contains("Secondary.$$.5") && (length > 9)) {
//        result = secondaryCode.substring(4, 9);
        int position = secondaryCode.indexOf("$$5");
        result = secondaryCode.substring(position + 3, position + 8);
      } else if (subType.endsWith("Secondary.$$.6") && (length > 11)) {
//        result = secondaryCode.substring(4, 7);
        result = secondaryCode.substring(4, 11);
      } else if (subType.endsWith("Secondary.$$.8") && (length > 10)) {
//        result = secondaryCode.substring(6, 4);
        result = secondaryCode.substring(6, 10);
      } else if (subType.endsWith("Secondary.$$.8.2") && (length > 13)) {
//        result = secondaryCode.substring(7, 6);
        result = secondaryCode.substring(7, 13);
      } else if (subType.endsWith("Secondary.$$.8.3") && (length > 13)) {
//        result = secondaryCode.substring(7, 6);
        result = secondaryCode.substring(7, 13);
      } else if (subType.endsWith("Secondary.$$.8.4") && (length > 15)) {
//        result = secondaryCode.substring(7, 15);
        result = secondaryCode.substring(7, 8);
      } else if (subType.endsWith("Secondary.$$.8.5") && (length > 12)) {
//        result = secondaryCode.substring(7, 5);
        result = secondaryCode.substring(7, 12);
      } else if (subType.endsWith("Secondary.$$.8.6") && (length > 14)) {
//        result = secondaryCode.substring(7, 7);
        result = secondaryCode.substring(7, 14);
      } else if (subType.endsWith("Secondary.$$.9") && (length > 13)) {
//        result = secondaryCode.substring(9, 4);
        result = secondaryCode.substring(9, 13);
      } else if (subType.endsWith("Secondary.$$.9.2") && (length > 16)) {
//        result = secondaryCode.substring(10, 6);
        result = secondaryCode.substring(10, 16);
      } else if (subType.endsWith("Secondary.$$.9.3") && (length > 16)) {
//        result = secondaryCode.substring(10, 6);
        result = secondaryCode.substring(10, 16);
      } else if (subType.endsWith("Secondary.$$.9.4") && (length > 18)) {
//        result = secondaryCode.substring(10, 8);
        result = secondaryCode.substring(10, 18);
      } else if (subType.endsWith("Secondary.$$.9.5") && (length > 15)) {
//        result = secondaryCode.substring(10, 5);
        result = secondaryCode.substring(10, 15);
      } else if (subType.endsWith("Secondary.$$.9.6") && (length > 17)) {
//        result = secondaryCode.substring(10, 7);
        result = secondaryCode.substring(10, 17);
      }
      if (containsASD(code)) {
        if (subType.contains("14D")) {
          int position = secondaryCode.indexOf("/14D");
          result = secondaryCode.substring(position + 4, position + 12);
        }
      }
    } else if (type == Type.NaS) {
      if (subType.equals("005")) {
//        result = code.substring(21, 7);
        result = code.substring(21, 28);
      } else if (subType.equals("007")) {
//        result = code.substring(16, 6);
        result = code.substring(16, 22);
      } else if (subType.equals("015")) {
//        result = code.substring(5, 10);
        result = code.substring(5, 15);
      } else if (subType.equals("016")) {
//        result = code.substring(18, 6);
        result = code.substring(18, 24);
      } else if (subType.equals("017")) {
  //      result = code.substring(7, 3);
        result = code.substring(7, 10);
      }
    }
    // if (!NumericString(result)) // Il faut améliorer NormalizedExpriry pour
    // supprimer définitivement ce code
    // {
    // result = "";
    // }

    return result;
  }
  
  /**
   * @param code le code barre brut à analyser 
   * @return Code famille pour le code ACL. La famille sur 1 caractère pour le ACL 13
   * <ul>
   * <li>0 pour un accessoire d'usage médical</li>
   * <li>1 pour une spécialité vétérinaire</li>
   * <li>2 pour une spécialité diététique</li>
   * <li>3 pour une spécialité cosmétique</li>
   * <li>5 pour une spécialité parapharmaceutique</li>
   * </ul>
   */
  public String getFamily(String code) {
    Type type = getType(code);
    String subType = getSubType(code, type);
    String result = getFamily(code, type, subType);
    return result;
  }

  /**
   * @param code le code barre brut à analyser 
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return Code famille pour le code ACL. La famille sur 1 caractère pour le ACL 13
   * <ul>
   * <li>0 pour un accessoire d'usage médical</li>
   * <li>1 pour une spécialité vétérinaire</li>
   * <li>2 pour une spécialité diététique</li>
   * <li>3 pour une spécialité cosmétique</li>
   * <li>5 pour une spécialité parapharmaceutique</li>
   * </ul>
   */
  public String getFamily(String code, Type type, String subType) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    String result = "";
    if (code.length() >= 7) {
      if (subType.equals("ACL 13")) {
//        result = code.substring(4, 1);
        result = code.substring(4, 5);
      }
      else if ((type == Type.GS1_128 || type == Type.GS1_Datamatrix) && subType.startsWith("01") && code.substring(3, 7).equals("3401")) {
//        result = code.substring(7, 1);
        result = code.substring(7, 8);
      }
      else if (type == Type.NaS && (subType.equals("002") || subType.equals("003"))) {
//        result = code.substring(4, 1);
        result = code.substring(4, 5);
      }
    }
    return result;
  }
  
  private List<Identifier> getIdentifiers(CodeBarreStructure codeBarreStructure) {
    List<Identifier> result = new ArrayList<Identifier>();

    if (codeBarreStructure.getType() == Type.EAN13) {
      result.add(new Identifier(codeBarreStructure.getGtin()));
    } else if (codeBarreStructure.getType() == Type.GS1_128 || codeBarreStructure.getType() == Type.GS1_Datamatrix) {
      if (codeBarreStructure.getSubType().contains("01")) {
        result.add(new Identifier(codeBarreStructure.getGtin()));
      }
      if (codeBarreStructure.getSubType().contains("240")) {
        result.add(new Identifier(codeBarreStructure.getAdditionnalId()));
      }
      if (codeBarreStructure.getSubType().contains("241")) {
        result.add(new Identifier(codeBarreStructure.getCustPartNo()));
      }
      if (codeBarreStructure.getSubType().contains("91")) {
        result.add(new Identifier(codeBarreStructure.getInternal_91()));
      }
      if (codeBarreStructure.getSubType().contains("92")) {
        result.add(new Identifier(codeBarreStructure.getInternal_92()));
      }
      if (codeBarreStructure.getSubType().contains("93")) {
        result.add(new Identifier(codeBarreStructure.getInternal_93()));
      }
      if (codeBarreStructure.getSubType().contains("94")) {
        result.add(new Identifier(codeBarreStructure.getInternal_94()));
      }
      if (codeBarreStructure.getSubType().contains("95")) {
        result.add(new Identifier(codeBarreStructure.getInternal_95()));
      }
      if (codeBarreStructure.getSubType().contains("96")) {
        result.add(new Identifier(codeBarreStructure.getInternal_96()));
      }
      if (codeBarreStructure.getSubType().contains("97")) {
        result.add(new Identifier(codeBarreStructure.getInternal_97()));
      }
      if (codeBarreStructure.getSubType().contains("98")) {
        result.add(new Identifier(codeBarreStructure.getInternal_98()));
      }
      if (codeBarreStructure.getSubType().contains("99")) {
        result.add(new Identifier(codeBarreStructure.getInternal_99()));
      }
    } else if (codeBarreStructure.getType() == Type.HIBC) {
      if (codeBarreStructure.getSubType().startsWith("Primary")) {
        result.add(new Identifier(codeBarreStructure.getUdi()));
      }
    } else if (codeBarreStructure.getType() == Type.NaS) {
      if (codeBarreStructure.getSubType().equals("001") || codeBarreStructure.getSubType().equals("004")) {
        result.add(new Identifier(codeBarreStructure.getEan()));
      } else if (codeBarreStructure.getSubType().equals("002") || codeBarreStructure.getSubType().equals("003")) {
        result.add(new Identifier(codeBarreStructure.getAcl()));
      } else if (codeBarreStructure.getSubType().equals("NaS7")) {
        result.add(new Identifier(codeBarreStructure.getNas7()));
      } else {
        result.add(new Identifier(codeBarreStructure.getReference()));
      }
    }
    return result;
  }

  /**
   * @param code le code barre brut à analyser
   * @return NaS: Code sur 7 caractères
   */
  public String getNaS7(String code) {
    String result = "";
    Type type = getType(code);
    String subType = getSubType(code, type);
    result = getNaS7(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return NaS: Code sur 7 caractères
   */
  public String getNaS7(String code, Type type, String subType) {
    String result = "";
    code = cleanSymbologyId(code);
    if (type == Type.NaS && subType.equals("NaS7")) {
      result = code;
    }
    return result;
  }

  private String getNormalizedDate(String dateBrute, Type type, String subType) {
    String result = "";
    if (dateBrute != null && !dateBrute.isEmpty()) {
      int dateType = getDateType(type, subType, dateBrute);
      if (dateType == 0) {
        result = dateBrute;
      }
      else if (dateType != -1) {
        Date dateTime = convertDateTimeFromStr(dateBrute, dateType);
        if (dateTime != null) {
          if (dateTime.getHours() > 0) {
            // result = dateTime.toString("yyyyMMddHH");
            DateFormat spdf = new SimpleDateFormat("yyyyMMddHH");
            result = spdf.format(dateTime);
          } else {
            // result = dateTime.toString("yyyyMMdd");
            DateFormat spdf = new SimpleDateFormat("yyyyMMdd");
            result = spdf.format(dateTime);
          }
        }
      }
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return GS1(15): Date limite de conservation normalisée
   */
  public String getNormalizedBestBefore(String code) {
    String dateBrute = getBestBefore(code);
    Type type = getType(code);
    String subType = getSubType(code);
    String result = getNormalizedDate(dateBrute, type, subType);
    return result;
  }
 
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barrele type de code barre
   * @param subType le sous type de code barre
   * @return GS1(15): Date limite de conservation normalisée
   */
  public String getNormalizedBestBefore(String code, Type type, String subType) {
    String dateBrute = getBestBefore(code);
    String result = getNormalizedDate(dateBrute, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return GS1(17)/HIBC/NaS: Date de péremption normalisée
   */
  public String getNormalizedExpiry(String code) {
    String dateBrute = getExpiry(code);
    Type type = getType(code);
    String subType = getSubType(code);
    String result = getNormalizedDate(dateBrute, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return GS1(17)/HIBC/NaS: Date de péremption normalisée
   */
  public String getNormalizedExpiry(String code, Type type, String subType) {
    code = cleanse(code);
    String dateBrute = getExpiry(code);
    String result = getNormalizedDate(dateBrute, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return GS1(11)/HIBC(16D): Date de production normalisée
   */
  public String getNormalizedProdDate(String code) {
    String dateBrute = getProdDate(code);
    Type type = getType(code);
    String subType = getSubType(code);
    String result = getNormalizedDate(dateBrute, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return GS1(11)/HIBC(16D): Date de production normalisée
   */
  public String getNormalizedProdDate(String code, Type type, String subType) {
    code = cleanse(code);
    String dateBrute = getProdDate(code);
    String result = getNormalizedDate(dateBrute, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return EAN13/NaS: Identification du fabricant
   */
  public String getCompany(String code) {
    Type type = getType(code);
    String subType = getSubType(code, type);
    String result = getCompany(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return EAN13/NaS: Identification du fabricant
   */
  public String getCompany(String code, Type type, String subType) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    String result = "";
    if (type == Type.EAN13 && subType.equals("")) {
      result = code.substring(0, 7);
    }
//    else if (type == Type.EAN14 && subType.equals("")) {
//      result = code.substring(1, 8);
//    }
    if (type == Type.NaS && (subType.equals("001") || subType.equals("004"))) {
      result = code.substring(0, 7);
    }
    return result;
  }

  /**
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @param dateRaw
   * @return type of date
   * <ul>
   *   <li>1-------MMyy</li>
   *   <li>2-------MMddyy</li>
   *   <li>3-------yyMMdd</li>
   *   <li>4-------yyMMddHH</li>
   *   <li>5-------yyJJJ</li>
   *   <li>6-------yyJJJHH</li>
   *   <li>7-------yyyy-MM</li>
   *   <li>8-------MMddyy ??</li>
   *   <li>-1------error type</li>
   * </ul>
   */
  private static int getDateType(Type type, String subType, String dateRaw) {
    int typeDate = -1;
    if (type == Type.GS1_128 || type == Type.GS1_Datamatrix) {
      // YYMMDD
      typeDate = 3;
    }
    else if (type == Type.HIBC && subType.contains("Secondary")) {
      if (dateRaw.length() == 4) {
        // MMYY
        typeDate = 1;
      }
      else if (dateRaw.length() == 5) {
        // YYJJJ
        typeDate = 5;
      }
      else if (dateRaw.length() == 6) {
        if ((subType.contains("$$.2") || (subType.contains("$$.8.2")))) {
          // MMDDYY
          typeDate = 2;
        } else if ((subType.contains("$$.3") || (subType.contains("$$.8.3")))) {
          // YYMMDD
          typeDate = 3;
        }
      }
      else if (dateRaw.length() == 7) {
        // YYJJJHH
        typeDate = 6;
      }
      else if (dateRaw.length() == 8) {
        if ((subType.contains(".14") || subType.contains(".16"))
            && !(subType.contains("$$.4") || (subType.contains("$$.8.4")))) {
          // YYYYMMDD
          typeDate = 0;
        }
        else if (!(subType.contains(".14") || subType.contains(".16"))
               && (subType.contains("$$.4") || (subType.contains("$$.8.4")))) {
          // YYMMDDHH
          typeDate = 4;
        }
        else {
          // If the secondary structure contains both?
          // Todo
          // ? > YYYY > ?
          // 12 => MM >= 01
          // 31 => DD >= 01
          // 24 => HH >= 00
        }
      }
    }
    else if (type == Type.NaS) {
      if (subType.equals("005")) {
        typeDate = 7;
      } else if (subType.equals("007")) {
        typeDate = 8;
      } else if (subType.equals("015")) {
        typeDate = 9;
      } else if (subType.equals("016")) {
        typeDate = 3;
      } else if (subType.equals("017")) {
        typeDate = 10;
      }
    }
    return typeDate;
  }

  @SuppressWarnings("unused")
  @Deprecated
  private int getDateType_old(Type type, String subType, String code) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    int typeDate = -1;
    if (type == Type.GS1_128) {
      typeDate = 3;
    } else if (type == Type.HIBC) {
      String secondaryCode = null;
      // if (subType.startsWith(@"Primary/Secondary"))
      if (subType.startsWith("Primary/Secondary")) {
        int position = code.indexOf('/');
        secondaryCode = "+" + code.substring(position + 1);
      } else {
        secondaryCode = code;
      }
      int length = secondaryCode.length();
      if (subType.endsWith("Secondary.N") && (length >= 8)) {
        typeDate = 5;
      } else if (subType.endsWith("Secondary.$$") && (length > 7)) {
        typeDate = 1;
      } else if (subType.endsWith("Secondary.$$.2") && (length > 10)) {
        typeDate = 2;
      } else if (subType.endsWith("Secondary.$$.3") && (length > 10)) {
        typeDate = 3;
      } else if (subType.endsWith("Secondary.$$.4") && (length > 12)) {
        typeDate = 4;
      } else if (subType.contains("Secondary.$$.5") && (length > 9)) {
        typeDate = 5;
      } else if (subType.endsWith("Secondary.$$.6") && (length > 11)) {
        typeDate = 6;
      } else if (subType.endsWith("Secondary.$$.8") && (length > 10)) {
        typeDate = 1;
      } else if (subType.endsWith("Secondary.$$.8.2") && (length > 13)) {
        typeDate = 2;
      } else if (subType.endsWith("Secondary.$$.8.3") && (length > 13)) {
        typeDate = 3;
      } else if (subType.endsWith("Secondary.$$.8.4") && (length > 15)) {
        typeDate = 4;
      } else if (subType.endsWith("Secondary.$$.8.5") && (length > 12)) {
        typeDate = 5;
      } else if (subType.endsWith("Secondary.$$.8.6") && (length > 14)) {
        typeDate = 6;
      } else if (subType.endsWith("Secondary.$$.9") && (length > 13)) {
        typeDate = 1;
      } else if (subType.endsWith("Secondary.$$.9.2") && (length > 16)) {
        typeDate = 2;
      } else if (subType.endsWith("Secondary.$$.9.3") && (length > 16)) {
        typeDate = 3;
      } else if (subType.endsWith("Secondary.$$.9.4") && (length > 18)) {
        typeDate = 4;
      } else if (subType.endsWith("Secondary.$$.9.5") && (length > 15)) {
        typeDate = 5;
      } else if (subType.endsWith("Secondary.$$.9.6") && (length > 17)) {
        typeDate = 6;
      } else if ((subType.contains("14D") || (subType.contains("16D")))) {
        typeDate = 99;
      }
    } else if (type == Type.NaS) {
      if (subType.equals("005")) {
        typeDate = 7;
      } else if (subType.equals("007")) {
        typeDate = 8;
      } else if (subType.equals("015")) {
        typeDate = 9;
      } else if (subType.equals("016")) {
        typeDate = 3;
      } else if (subType.equals("017")) {
        typeDate = 10;
      }
    }
    return typeDate;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return GS1(01): Global Trade Item Number
   */
  public String getGtin(String code) {
    code = cleanse(code);
    Type type = getType(code);
    String subType = getSubType(code, type);
    String result = getGtin(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return GS1(01): Global Trade Item Number
   */
  public String getGtin(String code, Type type, String subType) {
    code = cleanse(code);
    String result = "";
    if (type == Type.GS1_128 || type == Type.GS1_Datamatrix) {
      code = cleanSymbologyId(code);
      if (subType.substring(0, 2).equals("01")) {
//        if (CheckGTINKey(code.substring(2, 14))) {
        if (checkGTINKey(code.substring(2, 16))) {
//          result = code.substring(2, 14);
          result = code.substring(2, 16);
        }
      }
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return HIBC: Identification du fabricant
   */
  public String getLic(String code) {
    Type type = getType(code);
    String subType = getSubType(code, type);
    String result = getLic(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return HIBC: Identification du fabricant
   */
  public String getLic(String code, Type type, String subType) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    String result = "";
    if (type == Type.HIBC) {
      if (subType.startsWith("Primary")) {
        code = cleanSymbologyId(code);
//        result = code.substring(1, 4);
        result = code.substring(1, 5);
      }
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return GS1(10)/HIBC/NaS: Numéro de lot de production
   */
  public String getLot(String code) {
    Type type = getType(code);
    String subType = getSubType(code, type);
    String result = getLot(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return GS1(10)/HIBC/NaS: Numéro de lot de production
   */
  public String getLot(String code, Type type, String subType) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    int codeLength = code.length();
    String result = "";
    if (type == Type.GS1_128 || type == Type.GS1_Datamatrix) {
      code = cleanSymbologyId(code);
      if (subType.equals("01.10")) {
        result = code.substring(18);
      } else if (subType.startsWith("01.10.17")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 16);
//          result = code.substring(18, nextGS - 18);
          result = code.substring(18, nextGS);
        }
      } else if (subType.startsWith("01.11.10.17")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 26);
//          result = code.substring(18, nextGS - 18);
          result = code.substring(26, nextGS);
        }
      } else if (subType.startsWith("01.11.17.10")) {
//        result = code.substring(34, lenght - 34);
        result = code.substring(34, codeLength); // TODO MB Controler Longueur code
      } else if (subType.startsWith("01.15.10")) {
        int lenght = code.length();
//        result = code.substring(26, lenght - 26);
        result = code.substring(26, lenght);
      } else if (subType.equals("01.17.10")) {
        result = code.substring(26);
      } else if (subType.startsWith("01.17.10")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 24);
//          result = code.substring(26, nextGS - 26);
          result = code.substring(26, nextGS);
        }
      } else if (subType.startsWith("01.17.30.10")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 27);
//          result = code.substring(nextGS + 3, code.length() - (nextGS + 3));
          result = code.substring(nextGS + 3, code.length());
        }
      } else if (subType.startsWith("02.10")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 16);
//          result = code.substring(18, nextGS - 18);
          result = code.substring(18, nextGS);
        } else {
          result = code.substring(18);
        }
      } else if (subType.startsWith("02.17.37.10")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 27);
//          result = code.substring(nextGS + 3, code.length() - (nextGS + 3));//TODO MB Controler Longueur code 
          result = code.substring(nextGS + 3, code.length());
        }
      } else if (subType.startsWith("02.37.10")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 16);
//          result = code.substring(nextGS + 3, code.length() - (nextGS + 3));
          result = code.substring(nextGS + 3, code.length());
        }
      } else if (subType.startsWith("10")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 1);
//          result = code.substring(2, nextGS - 2);
          result = code.substring(2, nextGS);
        } else {
//          result = code.substring(2, code.length() - 2);
          result = code.substring(2, code.length());
        }
      }
      else if (subType.equals("11.17.10")) {
        int lenght = code.length();
//        result = code.substring(18, lenght - 18);
        result = code.substring(18, lenght);
      }
      else if (subType.startsWith("17.10")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 10);
//          result = code.substring(10, nextGS - 10);
          result = code.substring(10, nextGS);
        }
        else {
          result = code.substring(10);
        }
      }
      else if (subType.startsWith("17.30.10")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 8);
//          result = code.substring(10, nextGS - 10);
          result = code.substring(nextGS + 3, code.length());
        }
      }
      else if (subType.startsWith("20.17.10")) {
        if (containsGS(code)) {
          int firstBL = indexOfGS(code, 1);
//          result = code.substring(firstBL + 11, code.length() - (firstBL + 11));
          result = code.substring(firstBL + 11, code.length());
        }
        else {
          result = code.substring(14,  code.length());
        }
      }
      else if (subType.startsWith("37.10")) {
        if (containsGS(code)) {
          int firstBL = indexOfGS(code, 1);
          int secondBL = indexOfGS(code, firstBL + 1);
//          result = code.substring(firstBL + 3, secondBL - (firstBL + 3));
          result = code.substring(firstBL + 3, secondBL);
        }
      }

      else if (subType.startsWith("91.17.10")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 1);
//          result = code.substring(nextGS + 11, code.length() - (nextGS + 11));
          result = code.substring(nextGS + 11, code.length());
        }
      }

      else if (subType.startsWith("240.10")) {
        int firstBL = indexOfGS(code, 1);
        int secondBL = indexOfGS(code, firstBL + 1);

        if (secondBL != -1) {
//          result = code.substring(firstBL + 3, secondBL - firstBL - 3);
          result = code.substring(firstBL + 3, secondBL);
        } else {
//          result = code.substring(firstBL + 3, code.length() - firstBL - 3);
          result = code.substring(firstBL + 3, code.length());
        }
      }

      else if (subType.startsWith("240.21.30.10")) {
        if (containsGS(code)) {
          int firstBL = indexOfGS(code, 1);
          int secondBL = indexOfGS(code, firstBL + 1);
          int thirdBL = indexOfGS(code, secondBL + 1);
//          result = code.substring(thirdBL + 3, code.length() - (thirdBL + 3));
          result = code.substring(thirdBL + 3, code.length());
        }
      }
    } else if (type == Type.HIBC) {
      if (containsASD(code)) { // Version 2.5
        int position;
        String secondaryCode = null;
        if (subType.startsWith("Primary/Secondary")) {
          position = code.indexOf('/');
        }
        else {
          position = 0;
        }
        secondaryCode = code.substring(position + 1);
        String [] parties = secondaryCode.split("/");
        String temp = parties[0].substring(1);

        // Shift
        int shift = 0;
        if (subType.contains("$$.3")) {
          shift = 8;// 10;
        }
        if (subType.contains("$$.8.3")) {
          shift = 13;
        }
        if (subType.contains("$$.9.3")) {
          shift = 16;
        }
        if (subType.contains("$$.5")) {
          shift = 7;
        }
        if (subType.contains("$$.8.5")) {
          shift = 10;
        }
        if (subType.contains("$$.9.5")) {
          shift = 13;
        }
        result = temp.substring(shift);
      }
      else { // Version 2.4
        String secondaryCode = null;
        // if (subType.startsWith(@"Primary/Secondary"))
        if (subType.startsWith("Primary/Secondary")) {
          int position = code.indexOf('/');
          secondaryCode = "+" + code.substring(position + 1);
        } else {
          secondaryCode = code;
        }
        int length = secondaryCode.length();
        if (subType.startsWith("Secondary")) {
          if (subType.endsWith("Secondary.N") && (length > 8)) {
  //          result = secondaryCode.substring(6, length - 8);
            result = secondaryCode.substring(6, length - 2);
          } else if (subType.endsWith("Secondary.$") && (length > 4)) {
  //          result = secondaryCode.substring(2, length - 4);
            result = secondaryCode.substring(2, length - 2);
          } else if (subType.endsWith("Secondary.$$") && (length > 9)) {
  //          result = secondaryCode.substring(7, length - 2);
            result = secondaryCode.substring(7, length - 2);
          } else if (subType.endsWith("Secondary.$$.2") && (length > 12)) {
  //          result = secondaryCode.substring(10, length - 12);
            result = secondaryCode.substring(10, length - 2);
          } else if (subType.endsWith("Secondary.$$.3") && (length > 12)) {
  //          result = secondaryCode.substring(10, length - 12);
            result = secondaryCode.substring(10, length - 2);
          } else if (subType.endsWith("Secondary.$$.4") && (length > 14)) {
  //          result = secondaryCode.substring(12, length - 2);
            result = secondaryCode.substring(12, length - 2);
          } else if (subType.endsWith("Secondary.$$.5") && (length > 8)) {
  //          result = secondaryCode.substring(9, length - 11);
            result = secondaryCode.substring(9, length - 2);
          } else if (subType.endsWith("Secondary.$$.6") && (length > 13)) {
  //          result = secondaryCode.substring(11, length - 13);
            result = secondaryCode.substring(11, length - 2);
          } else if (subType.endsWith("Secondary.$$.7") && (length > 6)) {
  //          result = secondaryCode.substring(4, length - 6);
            result = secondaryCode.substring(4, length - 2);
          } else if (subType.endsWith("Secondary.$$.8") && (length > 12)) {
  //          result = secondaryCode.substring(10, length - 12);
            result = secondaryCode.substring(10, length - 2);
          } else if (subType.endsWith("Secondary.$$.8.2") && (length > 15)) {
  //          result = secondaryCode.substring(13, length - 15);
            result = secondaryCode.substring(13, length - 2);
          } else if (subType.endsWith("Secondary.$$.8.3") && (length > 15)) {
  //          result = secondaryCode.substring(13, length - 15);
            result = secondaryCode.substring(13, length - 2);
          } else if (subType.endsWith("Secondary.$$.8.4") && (length > 17)) {
  //          result = secondaryCode.substring(15, length - 17);
            result = secondaryCode.substring(15, length - 2);
          } else if (subType.endsWith("Secondary.$$.8.5") && (length > 14)) {
  //          result = secondaryCode.substring(12, length - 14);
            result = secondaryCode.substring(12, length - 2);
          } else if (subType.endsWith("Secondary.$$.8.6") && (length > 16)) {
  //          result = secondaryCode.substring(14, length - 16);
            result = secondaryCode.substring(14, length - 2);
          } else if (subType.endsWith("Secondary.$$.8.7") && (length > 9)) {
  //          result = secondaryCode.substring(7, length - 9);
            result = secondaryCode.substring(7, length - 2);
          } else if (subType.endsWith("Secondary.$$.9") && (length > 15)) {
  //          result = secondaryCode.substring(13, length - 15);
            result = secondaryCode.substring(13, length - 2);
          } else if (subType.endsWith("Secondary.$$.9.2") && (length > 18)) {
  //          result = secondaryCode.substring(16, length - 18);
            result = secondaryCode.substring(16, length - 2);
          } else if (subType.endsWith("Secondary.$$.9.3") && (length > 18)) {
  //          result = secondaryCode.substring(16, length - 18);
            result = secondaryCode.substring(16, length - 2);
          } else if (subType.endsWith("Secondary.$$.9.4") && (length > 20)) {
  //          result = secondaryCode.substring(18, length - 20);
            result = secondaryCode.substring(18, length - 2);
          } else if (subType.endsWith("Secondary.$$.9.5") && (length > 17)) {
  //          result = secondaryCode.substring(15, length - 17);
            result = secondaryCode.substring(15, length - 2);
          } else if (subType.endsWith("Secondary.$$.9.6") && (length > 19)) {
  //          result = secondaryCode.substring(17, length - 19);
            result = secondaryCode.substring(17, length - 2);
          } else if (subType.endsWith("Secondary.$$.9.7") && (length > 12)) {
  //          result = secondaryCode.substring(10, length - 12);
            result = secondaryCode.substring(10, length - 2);
          }
        }

        if (subType.startsWith("Primary/Secondary")) {
          if (subType.endsWith(".N")) {
            result = secondaryCode.substring(6, length - 1);
          } else if (subType.endsWith(".$")) {
            result = secondaryCode.substring(2, length - 1);
          } else if (subType.endsWith(".$$") && (length > 9)) {
            result = secondaryCode.substring(7, length - 1);
          } else if (subType.endsWith(".$$.2") && (length > 12)) {
            result = secondaryCode.substring(10, length - 1);
          } else if (subType.endsWith(".$$.3") && (length > 12)) {
            result = secondaryCode.substring(10, length - 1);
          } else if (subType.endsWith(".$$.4") && (length > 14)) {
            result = secondaryCode.substring(12, length - 1);
          } else if (subType.endsWith(".$$.5") && (length > 11)) {
            result = secondaryCode.substring(9, length - 1);
          } else if (subType.endsWith(".$$.6") && (length > 13)) {
            result = secondaryCode.substring(11, length - 1);
          } else if (subType.endsWith(".$$.7") && (length > 6)) {
            result = secondaryCode.substring(4, length - 1);
          } else if (subType.endsWith(".$$.8") && (length > 12)) {
            result = secondaryCode.substring(10, length - 1);
          } else if (subType.endsWith(".$$.8.2") && (length > 15)) {
            result = secondaryCode.substring(13, length - 1);
          } else if (subType.endsWith(".$$.8.3") && (length > 15)) {
            result = secondaryCode.substring(13, length - 1);
          } else if (subType.endsWith(".$$.8.4") && (length > 17)) {
            result = secondaryCode.substring(15, length - 1);
          } else if (subType.endsWith(".$$.8.5") && (length > 14)) {
            result = secondaryCode.substring(12, length - 1);
          } else if (subType.endsWith(".$$.8.6") && (length > 16)) {
            result = secondaryCode.substring(14, length - 1);
          } else if (subType.endsWith(".$$.8.7") && (length > 9)) {
            result = secondaryCode.substring(7, length - 1);
          } else if (subType.endsWith(".$$.9") && (length > 15)) {
            result = secondaryCode.substring(13, length - 1);
          } else if (subType.endsWith(".$$.9.2") && (length > 18)) {
            result = secondaryCode.substring(16, length - 1);
          } else if (subType.endsWith(".$$.9.3") && (length > 18)) {
            result = secondaryCode.substring(16, length - 1);
          } else if (subType.endsWith(".$$.9.4") && (length > 20)) {
            result = secondaryCode.substring(18, length - 1);
          } else if (subType.endsWith(".$$.9.5") && (length > 17)) {
            result = secondaryCode.substring(15, length - 1);
          } else if (subType.endsWith(".$$.9.6") && (length > 19)) {
            result = secondaryCode.substring(17, length - 1);
          } else if (subType.endsWith(".$$.9.7") && (length > 12)) {
            result = secondaryCode.substring(10, length - 1);
          }
        }
      }
    } else if (type == Type.NaS) {
      if (subType.equals("006")) {
//        result = code.substring(11, 6);
        result = code.substring(11, 17);
      } else if (subType.equals("007")) {
//        result = code.substring(8, 8);
        result = code.substring(8, 16);
      } else if (subType.equals("010")) {
//        result = code.substring(1, 6);
        result = code.substring(1, 7);
      } else if (subType.equals("012")) {
//        result = code.substring(code.indexOf('^') + 1, code.length() - code.indexOf('^') - 2);
        result = code.substring(code.indexOf("^") + 1, code.length() - 1);
      } else if (subType.equals("015")) {
        result = code.substring(0, 4);
      } else if (subType.equals("016")) {
//        result = code.substring(9, 9);
        result = code.substring(9, 18);
      } else if (subType.equals("017")) {
//        result = code.substring(0, 7);
        result = code.substring(0, 7);
      }
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return NaS: Code LPP
   */
  public String getLpp(String code) {
    String result = "";
    Type type = getType(code);
    String subType = getSubType(code);
    result = getLpp(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return NaS: Code LPP
   */
  public String getLpp(String code, Type type, String subType) {
    String result = "";
    code = cleanSymbologyId(code);
    if (type == Type.NaS && subType.equals("001")) {
//      result = code.substring(13, 6) + Key7Car(code.substring(13, 6));
      result = code.substring(13, 19) + getKey7Car(code.substring(13, 19));
    }
    if (type == Type.NaS && (subType.equals("002") || subType.equals("004"))) {
//      result = code.substring(13, 7);
      result = code.substring(13, 20);
    }
    if (type == Type.NaS && subType.equals("003")) {
//      result = code.substring(14, 7);
      result = code.substring(14, 21);
    }
    return result;
  }

  /**
   * @param code le code barre brut à analyser
   * @return HIBC: Labelers Product or Catalog Number
   */
  public String getPcn(String code) {
    Type type = getType(code);
    String subType = getSubType(code, type);
    String result = getPcn(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return HIBC: Labelers Product or Catalog Number
   */
  public String getPcn(String code, Type type, String subType) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    String result = "";
    if (type == Type.HIBC) {
      code = cleanSymbologyId(code);
      if (subType.equals("Primary")) {
//        result = code.substring(5, code.length() - 7);
        result = code.substring(5, code.length() - 2);
      }
      // else if (subType.startsWith(@"Primary/Secondary"))
      else if (subType.startsWith("Primary/Secondary")) {
        int position = code.indexOf('/');
//        result = code.substring(5, position - 6);
        result = code.substring(5, position - 1);
      }
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return GS1(11)/HIBC(16D): Date de production
   */
  public String getProdDate(String code) {
    Type type = getType(code);
    String subType = getSubType(code);
    String result = getProdDate(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return GS1(11)/HIBC(16D): Date de production
   */
  public String getProdDate(String code, Type type, String subType) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    String result = "";
    if (type == Type.GS1_128 || type == Type.GS1_Datamatrix) {
      if (subType.startsWith("01.11")) {
//        result = code.substring(18, 6);
        result = code.substring(18, 24);
      }
      else if (subType.startsWith("10.11")) {
        int firstGS = indexOfGS(code, 1);
//        result = code.substring(firstGS + 3, 6);
        result = code.substring(firstGS + 3, firstGS + 9);
      }
      else if (subType.startsWith("11")) {
//        result = code.substring(2, 6);
        result = code.substring(2, 8);
      }
    }
    else if (type == Type.HIBC) {
      if (subType.contains("16D")) {
        int position = code.indexOf("/16D");
        result = code.substring(position + 4, position + 12);
      }
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return NaS: Reference
   */
  public String getReference(String code) {
    Type type = getType(code);
    String subType = getSubType(code, type);
    String result = getReference(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return NaS: Reference
   */
  public String getReference(String code, Type type, String subType) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    String result = "";
    if (type == Type.NaS) {
      if (subType.equals("005")) {
        result = code.substring(0, 9);
      } else if (subType.equals("006")) {
        result = code.substring(0, 10);
      } else if (subType.equals("007")) {
        result = code.substring(0, 8);
      } else if (subType.equals("008")) {
        result = code.substring(0, 8);
      } else if (subType.equals("009")) {
//        result = code.substring(1, code.length() - 1);
        result = code.substring(1, code.length());
      } else if (subType.equals("012")) {
//        result = code.substring(3, 6);
        result = code.substring(3, 9);
      } else if (subType.equals("013")) {
//        result = code.substring(1, 13);
        result = code.substring(1, 14);
      } else if (subType.equals("014")) {
//        result = code.substring(0, 4) + code.substring(5, 5);
        result = code.substring(0, 4) + code.substring(5, 10);
      } else if (subType.equals("015")) {
        // To Do : waiting for Symbios answer
      } else if (subType.equals("016")) {
        result = code.substring(0, 9);
      } else if (subType.equals("NaS")) {
        result = code;
      }
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return EAN13/NaS: Code du produit
   */
  public String getProduct(String code) {
    Type type = getType(code);
    String subType = getSubType(code, type);
    String result = getProduct(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return EAN13/NaS: Code du produit
   */
  public String getProduct(String code, Type type, String subType) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    String result = "";
    if (type == Type.EAN13 && subType.equals("")) {
      result = code.substring(7, 12);
    }
//    else if (type == Type.EAN14 && subType.equals("")) {
//      result = code.substring(8, 13);
//    }
    else if (type == Type.NaS && (subType.equals("001") || subType.equals("004"))) {
      result = code.substring(7, 12);
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return GS1(21)/HIBC/NaS: Numéro de série de l'objet
   */
  public String getSerial(String code) {
    Type type = getType(code);
    String subType = getSubType(code);
    String result = getSerial(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return GS1(21)/HIBC/NaS: Numéro de série de l'objet
   */
  public String getSerial(String code, Type type, String subType) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    int length = code.length();
    String result = "";
    if (type == Type.GS1_128 || type == Type.GS1_Datamatrix) {
      code = cleanSymbologyId(code);
      if (subType.equals("01.17.21")) {
//        result = code.substring(26, code.length() - 26);
        result = code.substring(26, code.length());
      }
      else if (subType.startsWith("01.10.17.21")) {
        int firstGS = indexOfGS(code, 16);
//        result = code.substring(firstGS + 11, length - (firstGS + 11));
        result = code.substring(firstGS + 11, length);
      }
      else if (subType.startsWith("01.11.17.21")) {
//        result = code.substring(34, length - 34);
        result = code.substring(34, length);
      }
      else if (subType.startsWith("01.21")) {
        if (containsGS(code)) {
          int firstGS = indexOfGS(code, 18);
//          result = code.substring(18, firstGS - 18);
          result = code.substring(18, firstGS);
        }
      }
      else if (subType.startsWith("10.21")) {
        int firstGS = indexOfGS(code, 1);
//        result = code.substring(18, firstGS - 18);
        result = code.substring(firstGS + 3, length);
      }
      else if (subType.equals("17.21")) {
//        result = code.substring(10, code.length() - 10);
        result = code.substring(10, code.length());
      }
      else if (subType.equals("37.10.21")) {
        if (containsGS(code)) {
          int firstBL = indexOfGS(code, 1);
          int secondBL = indexOfGS(code, firstBL + 1);
//          result = code.substring(secondBL + 3, code.length() - (secondBL + 3));
          result = code.substring(secondBL + 3, code.length());
        }
      }
      else if (subType.startsWith("240.21")) {
        if (containsGS(code)) {
          int firstBL = indexOfGS(code, 3);
          int secondBL = indexOfGS(code, firstBL + 1);
//          result = code.substring(firstBL + 3, secondBL - (firstBL + 3));
          result = code.substring(firstBL + 3, secondBL);
        }
      }
    } else if (type == Type.HIBC && subType.contains("Secondary")) {
      String data = semanticData(code, subType);
      if (containsASD(data)) {
        if (subType.endsWith(".S")) {
          int start = data.indexOf("/S") + 2;
          int stop = data.length();
          result = data.substring(start, stop);
        }
      }
    } else if (type == Type.NaS) {
      if (subType.equals("005")) {
//        result = code.substring(10, 10);
        result = code.substring(10, 20);
      }
    }
    return result;
  }
  
  /**
   * @param code
   * @param subType
   * @return
   */
  public static String semanticData(String code, String subType) {
    String result = "";
    if (subType.contains("Secondary")) {
      if (subType.startsWith("Primary/")) {
        result = code.substring(code.indexOf("/"), code.length() - 1);
      }
      else {
        result = code.substring(1, code.length() - 1);
      }
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return GS1(30): quantité pour une unité à contenu variable
   */
  public String getVarCount(String code) {
    Type type = getType(code);
    String subType = getSubType(code);
    String result = getVarCount(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return GS1(30): quantité pour une unité à contenu variable
   */
  public String getVarCount(String code, Type type, String subType) {
    code = cleanse(code);
    int length = code.length();
    String result = "";
    if (type == Type.GS1_128 || type == Type.GS1_Datamatrix) {
      code = cleanSymbologyId(code);
      if (subType.startsWith("01.10.17.30")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 19);
//          result = code.substring(nextGS + 11, code.length() - (nextGS + 11));
          result = code.substring(nextGS + 11, code.length());
        }
      }
      else if (subType.equals("01.30")) {
//        result = code.substring(18, code.length() - 18);
        result = code.substring(18, code.length());
      }
      else if (subType.startsWith("01.17.30")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 24);
//          result = code.substring(26, nextGS - 26);
          result = code.substring(26, nextGS);
        }
      }
      else if (subType.startsWith("01.17.10.30")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 27);
//          result = code.substring(nextGS + 3, code.length() - (nextGS + 3));
          result = code.substring(nextGS + 3, code.length());
        }
      }
      else if (subType.equals("10.11.17.30")) {
        int firstGS = indexOfGS(code, 1);
        int shift = firstGS + 1 + 8 + 8 + 2;
        int nb = length - shift - 3;
//        result = code.substring(shift, nb);
        result = code.substring(shift, shift + nb);
      }
      else if (subType.equals("10.17.30")) {
        int firstGS = indexOfGS(code, 1);
        int shift = firstGS + 1 + 8 + 2;
        int nb = length - shift - 3;
//        result = code.substring(shift, nb);
        result = code.substring(shift, shift + nb);
      }
      else if (subType.startsWith("17.10.30")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 8);
//          result = code.substring(nextGS + 3, code.length() - (nextGS + 3));
          result = code.substring(nextGS + 3, code.length());
        }
      } 
      else if (subType.startsWith("17.30")) {
        if (containsGS(code)) {
          int nextGS = indexOfGS(code, 8);
//          result = code.substring(10, nextGS -10);
          result = code.substring(10, nextGS);
        }
        else {
//          result = code.substring(10, code.length() - 10);
          result = code.substring(10, code.length());
        }
      }
      else if (subType.startsWith("240.21.30")) {
        if (containsGS(code)) {
          int firstBL = indexOfGS(code, 1);
          int secondBL = indexOfGS(code, firstBL + 1);
          int thirdBL = indexOfGS(code, secondBL + 1);
//          result = code.substring(secondBL + 3, thirdBL - (secondBL + 3));
          result = code.substring(secondBL + 3, thirdBL);
        }
      }
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return GS1(20): Variante de production
   */
  public String getVariant(String code) {
    Type type = getType(code);
    String subType = getSubType(code);
    String result = getVariant(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return GS1(20): Variante de production
   */
  public String getVariant(String code, Type type, String subType) {
    code = cleanse(code);
    String result = "";
    if (type == Type.GS1_128 || type == Type.GS1_Datamatrix) {
      code = cleanSymbologyId(code);
      if (subType.startsWith("20")) {
//        result = code.substring(2, 2);
        result = code.substring(2, 4);
      }
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return HIBC/NaS: Quantité du produit
   */
  public String getQuantity(String code) {
    Type type = getType(code);
    String subType = getSubType(code);
    String result = getQuantity(code, type, subType);
    return result;
  }

  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return HIBC/NaS: Quantité du produit
   */
  public String getQuantity(String code, Type type, String subType) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    String result = "";

    if ((type == Type.HIBC) && subType.contains("Secondary")) {
      int start = 1;
      int stop;
      String secondaryCode = null;
      String data = "";
      if (subType.startsWith("Primary/Secondary")) {
        start = start + code.indexOf("/");
      } else {
        // Nothing
      }
      secondaryCode = code.substring(start);

      if (containsASD(code)) // Version 2.5
      {
        String[] parties = secondaryCode.split("/");
        data = parties[0].substring(1);
      } else {
        if (subType.startsWith("Primary")) {
          data = secondaryCode.substring(0, secondaryCode.length() - 1);
        } else {
          data = secondaryCode.substring(0, secondaryCode.length() - 2);
        }
      }
      if (subType.contains("Secondary.$$.8")) {
        result = data.substring(3, 5);
      } else if (subType.contains("Secondary.$$.9")) {
        result = data.substring(3, 8);
      }
    } else if (type == Type.NaS) {
      if (subType == "011") {
        result = code.substring(1, 2);
      }
    }
    return result;
  }

  @SuppressWarnings("javadoc")
  @Deprecated
  public String getQuantity_old(String code, Type type, String subType) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    String result = "";

    if (type == Type.HIBC) {
      String secondaryCode = null;
      // if (subType.startsWith(@"Primary/Secondary"))
      if (subType.startsWith("Primary/Secondary")) {
        int position = code.indexOf('/');
        secondaryCode = "+" + code.substring(position + 1);
      } else {
        secondaryCode = code;
      }
      int length = secondaryCode.length();
      if (subType.contains("Secondary.$$.8") && (length > 8)) {
//        result = secondaryCode.substring(4, 2);
        result = secondaryCode.substring(4, 6);
      }
      else if (subType.endsWith("Secondary.$$.9") && (length > 4)) {
//        result = secondaryCode.substring(4, 5);
        result = secondaryCode.substring(4, 9);
      }
    }
    // if (type == Type.HIBC
    // {
    // if (subType.startsWith("Secondary.$$.8"))
    // result = code.substring(4, 2);
    // if (subType.startsWith("Secondary.$$.9"))
    // result = code.substring(4, 5);
    // }
    else if (type == Type.NaS) {
      if (subType.equals("011")) {
//        result = code.substring(1, 1);
        result = code.substring(1, 2);
      }
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return GS1(00): Identification of a logistic unit (Serial Shipping Container Code) / Identification de l'unité de transport
   */
  public String getSscc(String code) {
    Type type = getType(code);
    String subType = getSubType(code, type);
    String result = getSscc(code, type, subType);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return GS1(00): Identification of a logistic unit (Serial Shipping Container Code) / Identification de l'unité de transport
   */
  public String getSscc(String code, Type type, String subType) {
    code = cleanse(code);
    String result = "";
    if (type == Type.GS1_128 || type == Type.GS1_Datamatrix) {
      code = cleanSymbologyId(code);
      if (subType.substring(0, 2).equals("00")) {
//        result = code.substring(2, 18);
        result = code.substring(2, 20);
      }
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return le type de code barre {@link Type}
   */
  @Override
  public Type getType(String code) {
    Type result = Type.NaS;
    int length = code.length();
    code = cleanse(code);
    if ((length > 5) && code.startsWith("]C1")) {
      result = Type.GS1_128;
    }
    else if ((length > 5) && code.startsWith("]d2")) {
      result = Type.GS1_Datamatrix;
    }
    else if ((code.startsWith("]d1+") || (code.startsWith("]C0+") || code.startsWith("+")))) {
      code = cleanSymbologyId(code);
      if (checkHIBCKey(code))
        result = Type.HIBC;
    }
    else if (code.startsWith("]E0")) {
      code = cleanSymbologyId(code);
      length = code.length();
      if (length == 13 && checkEan13Key(code)) {
        result = Type.EAN13;
      }
    }
    else if (length == 13 && checkEan13Key(code)) {
      result = Type.EAN13;
    }
//    else if (length == 14 && checkGTINKey(code)) {
//      result = Type.EAN14;
//    }
    else if ((length >= 20) && (code.startsWith("00"))) {
      boolean ok = true;
      char[] array = code.toCharArray();
      for (int i = 2; i < 20; i++) {
        if (!Character.isDigit(array[i])) {
          ok = false;
          break;
        }
      }
      if (ok) {
        if (checkSSCCKey(code)) {
          result = Type.GS1_128;
        }
      }
    }
    else if ((length >= 16) && (code.startsWith("01"))) {
      boolean ok = true;
      char[] array = code.toCharArray();
      for (int i = 2; i < 16; i++) {
        if (!Character.isDigit(array[i])) {
          ok = false;
          break;
        }
      }
      if (ok) {
//        if (CheckGTINKey(code.substring(2, 14))) {
        if (checkGTINKey(code.substring(2, 16))) {
          result = Type.GS1_128;
        }
      }
    }
    else if ((length >= 16) && (code.startsWith("02"))) {
      boolean ok = true;
      char[] array = code.toCharArray();
      for (int i = 2; i < 16; i++) {
        if (!Character.isDigit(array[i])) {
          ok = false;
          break;
        }
      }
      if (ok) {
//        if (CheckGTINKey(code.substring(2, 14))) {
        if (checkGTINKey(code.substring(2, 16))) {
          result = Type.GS1_128;
        }
      }
    }
    //    else if ((length >= 10) && code.startsWith("11")) {
    ////      if ((code.substring(8, 2).equals("17")) && (length >= 16))
    //      if ((code.substring(8, 10).equals("17")) && (length >= 16))
    ////        if ((code.substring(16, 2).equals("10")) && (length >= 18))
    //        if ((code.substring(16, 18).equals("10")) && (length >= 18))
    //          result = Type.GS1_128;
    //    }
    // This conditions is not enough strong
    // else if (code.startsWith("17") && (length >= 11))
    // {
    // String ai2 = code.substring(8, 2);
    // if ((ai2.equals("10") || (ai2.equals("30") || (ai2.equals("21"))
    // result = "GS1-128";
    // }

    // This conditions is not enough strong
    // else if (code.startsWith("20") && (length >= 6))
    // {
    // String ai2 = code.substring(4, 2);
    // if (ai2.equals("17")
    // result = "GS1-128";
    // }

    // else if ((code.startsWith("240") && (length >= 4)))
    // {
    // result = "GS1-128";
    // }
    else if (containsGS(code)) {
      result = Type.GS1_128;
      // int testposition = indexOfGS(code, 0);
    }
    return result;
  }
  
  private static String getGStype(String code) {
    String result = "Not";
    String GS = "\u001D";
    if (code.contains(GS)) {
      result = "GS";
    }
    else if (code.contains("@")) {
      result = "@";
    }
    return result;
  }

  private static int indexOfGS(String code, int start) {
    int result = -1;
    String GSchar = getGStype(code);
    if (GSchar.equals("GS")) {
      String GS = "\u001D";
      result = code.indexOf(GS, start);
    }
    else if (GSchar.equals("@")) {
      result = code.indexOf("@", start);
    }
    return result;
  }

  private static boolean containsGS(String code) {
    boolean result = false;
    String temp = getGStype(code);
    if (!temp.equals("Not")) {
      result = true;
    }
    return result;
  }

  private static int indexOfASD(String code, int start) {
    int result = -1;
    Pattern pattern = Pattern.compile(".*"+hibcASDlist+".*");
    Matcher matcher = pattern.matcher(code);
    if (matcher.matches()) {
      result = matcher.start();
    }
    return result;
  }
  
  /**
   * @param secondaryData
   * @return true si on le code contient des ASD (Additional Supplemental Data)
   */
  public static boolean containsASD(String secondaryData) {
    boolean result = false;
    if (indexOfASD(secondaryData, 0) != -1) {
      result = true;
    }
    return result;
  }

  @SuppressWarnings("unused")
  @Deprecated
  private static boolean containsASD_old(String secondaryData) { //Additional Supplemental Data
    boolean result = false;
    if (secondaryData.contains("/") && (secondaryData.indexOf("/") < secondaryData.length() - 2)) {
      if (secondaryData.contains("/14D") ||
          secondaryData.contains("/16D") ||
          secondaryData.contains("/S")) {
        result = true;
      }
    }
    return result;
  }

  /**
   * @param code le code barre brut à analyser
   * @return GS1(01)/HIBC: Universal Device Identifier (gtin ou upn)
   */
  public String getUdi(String code) {
    String result = "";
    Type type = getType(code);
    String subType = getSubType(code, type);
    result = getUdi(code, type, subType);
    return result;
  }

  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return GS1(01)/HIBC: Universal Device Identifier (gtin ou upn)
   */
  public String getUdi(String code, Type type, String subType) {
    String result = "";
    code = cleanse(code);
    if (type == Type.HIBC) {
      if (subType.contains("Primary")) {
        result = getUpn(code, type, subType);
      }
    }
    else if (type == Type.GS1_128 || type == Type.GS1_Datamatrix) {
      result = getGtin(code, type, subType);
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return HIBC: Unité de mesure
   */
  public String getUom(String code) {
    String result = "";
    Type type = getType(code);
    String subType = getSubType(code, type);
    result = getUom(code, type, subType);
    return result;
  }

  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return HIBC: Unité de mesure
   */
  public String getUom(String code, Type type, String subType) {
    String result = "";
    code = cleanse(code);
    if (type == Type.HIBC) {
      code = cleanSymbologyId(code);
      if (subType.equals("Primary")) {
//        result = code.substring(code.length() - 2, 1);
        result = code.substring(code.length() - 2, code.length() - 1);
      }
      else if (subType.startsWith("Primary/Secondary")) {
//        result = code.substring(code.indexOf("/") - 1, 1);
        result = code.substring(code.indexOf("/") - 1, code.indexOf("/"));
      }
    }
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @return Sous type de code barre détecté en fonction du type lui-même
   */
  public String getSubType(String code) {
    Type type = getType(code);
    String result = getSubType(code, type);
    return result;
  }
  
  /**
   * @param code le code barre brut à analyser
   * @param type le type de code barre
   * @return Sous type de code barre détecté en fonction du type lui-même
   */
  public String getSubType(String code, Type type) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    String result = "NaS";
    String ai2 = "";
    String ai3 = "";
    int length = code.length();

    // Start EAN 13
    if (type == Type.EAN13) {
      if (code.substring(0, 4).equals("3401")) {
        result = "ACL 13";
      } else if (code.substring(0, 4).equals("3400")) {
        result = "CIP 13";
      } else {
        result = "";
      }
    }
    // End EAN 13

    // Start EAN 14
//    else if (type == Type.EAN14) {
//      if (code.substring(1, 5).equals("3401")) {
//        result = "ACL 13";
//      } else if (code.substring(1, 5).equals("3400")) {
//        result = "CIP 13";
//      } else {
//        result = "";
//      }
//    }
    // End EAN 14

    // Start HIBC
    else if (type == Type.HIBC) {
      char [] array = code.toCharArray();
      if (length >= 8) {
        if (Character.isLetter(array[1])) { //...with the first character always being alphabetic.
          result = "Primary";
          int position = code.indexOf('/');
          if ((position != -1) && (position != code.length() - 1)) {
            // result = result + @"/Secondary";
            result = result + "/Secondary";
            array = code.substring(position).toCharArray();
          }
        } else {
          result = "Secondary";
        }
        if (result.endsWith("Secondary") && (array.length > 0)) {
          if (Character.isDigit(array[1])) {
            result = result + ".N";
          } else if (array[1] == '$') {
            result = result + ".$";
            if (array[2] == '$') {
              result = result + "$";
              char c1 = array[3];
              if ((c1 == '2') || (c1 == '3') || (c1 == '4') || (c1 == '5') || (c1 == '6') || (c1 == '7')) {
                result = result + "." + c1;
              } else if ((c1 == '8') || (c1 == '9')) {
                result = result + "." + c1;
                if (c1 == '8') {
                  if (length > 8) {
                    char c2 = array[6];
                    if ((c2 == '2') || (c2 == '3') || (c2 == '4') || (c2 == '5') || (c2 == '6') || (c2 == '7')) {
                      result = result + "." + c2;
                    }
                  }
                }
                if (c1 == '9') {
                  if (length > 11) {
                    char c2 = array[9];
                    if ((c2 == '2') || (c2 == '3') || (c2 == '4') || (c2 == '5') || (c2 == '6') || (c2 == '7')) {
                      result = result + "." + c2;
                    }
                  }
                }
              }
            }
          }
          
          String secondaryData = new String(array);//.Substring(1);

          if (containsASD(secondaryData)) {
            // There is at least a supplementary data!
            int nextDI = 0;
            for (int i = 0; i < secondaryData.length(); i++) {
              nextDI = secondaryData.indexOf("/", nextDI + i);
              if ((nextDI != -1) & (nextDI + 1 != secondaryData.length())) {
                if (secondaryData.substring(nextDI, nextDI + 4).equals("/14D")) {
                  result = result + ".14D";
                }
                else if (secondaryData.substring(nextDI, nextDI + 4).equals("/16D")) {
                  result = result + ".16D";
                }
                else if (secondaryData.substring(nextDI, nextDI + 2).equals("/S")) {
                  result = result + ".S";
                }
              } else {
                break;
              }
            }
          }
        }
      }
    }
    // End HIBC

    // Start GS1-128
    else if (type == Type.GS1_128 || type == Type.GS1_Datamatrix) {
      // Starting here analyse using a GS1-128 code without special characters
      ai2 = code.substring(0, 2);
      ai3 = code.substring(0, 3);
      if (ai2.equals("00")) {
        result = "00"; // 00
      } else if (ai2.equals("01")) {
        result = "01"; // 01
        if (length > 19) {
          ai2 = code.substring(16, 18);
          ai3 = code.substring(16, 19);
          if (ai2.equals("10")) {
            result = result + ".10";
          }
          else if (ai2.equals("11")) {
            result = result + ".11"; // 01.11
            if (length > 25) {
              ai2 = code.substring(24, 26);
              if (ai2.equals("10")) {
                result = result + ".10"; // 01.11.10
              }
              else if (ai2.equals("17")) {
                result = result + ".17"; // 01.11.17
                if (length >= 34) {
                  ai2 = code.substring(32, 34);
                  if (ai2.equals("10")) {
                    result = result + ".10"; // 01.11.17.10
                  }
                  if (ai2.equals("21")) {
                    result = result + ".21"; // 01.11.17.21
                  }
                }
              }
            }
          }
          else if (ai2.equals("15")) {
            result = result + ".15"; // 01.15
            if (length >= 26) {
              ai2 = code.substring(24, 26);
              if (ai2.equals("10")) {
                result = result + ".10"; // 01.15.10
              }
            }
          }
          else if (ai2.equals("17")) {
            result = result + ".17"; // 01.17
            if (length > 24) {
              ai2 = code.substring(24, 26);
              if (ai2.equals("10")) {
                result = result + ".10"; // 01.17.10
              } else if (ai2.equals("21")) {
                result = result + ".21"; // 01.17.21
              } else if (ai2.equals("30")) {
                result = result + ".30"; // 01.17.30
              }
            }
          }
          else if (ai3.equals("240")) {
            result = result + ".240"; // 01.240
          }
          else if (ai2.equals("30")) {
            result = result + ".30"; // 01.30
          }
        }
      } else if (ai2.equals("02")) {
        result = "02"; // 02
        if (length >= 18) {
          ai2 = code.substring(16, 18);
          if (ai2.equals("10")) {
            result = result + ".10"; // 02.10
          } else if (ai2.equals("17")) {
            result = result + ".17"; // 02.17
            if (length >= 27) {
              ai2 = code.substring(24, 26);
              if (ai2.equals("37")) {
                result = result + ".37"; // 02.17.37
              }
            }
          } else if (ai2.equals("37")) {
            result = result + ".37"; // 02.37
          }
        }
      } else if (ai2.equals("10")) {
        result = "10"; // 10
      } else if (ai2.equals("11")) {
        result = "11"; // 11
        if (length > 9) {
          ai2 = code.substring(8, 10);
          if (ai2.equals("17")) {
            result = result + ".17"; // 11.17
            if (length >= 15) {
              ai2 = code.substring(13, 15);
              if (ai2.equals("10")) {
                result = result + ".10"; // 11.17.10
              }
            }
          }
        }
      } else if (ai2.equals("17")) {
        result = "17"; // 17
        if (length > 9) {
          ai2 = code.substring(8, 10);
          if (ai2.equals("10")) {
            result = result + ".10"; // 17.10
          } else if (ai2.equals("21")) {
            result = result + ".21"; // 17.21
          } else if (ai2.equals("30")) {
            result = result + ".30"; // 17.30
          }
        }
      } else if (ai2.equals("20")) {
        result = "20"; // 20
        if (length > 6) {
          ai2 = code.substring(4, 6);
          if (ai2.equals("17")) {
            result = result + ".17"; // 20.17
            if (length >= 13) {
              ai2 = code.substring(12, 14);
              if (ai2.equals("10")) {
                result = result + ".10"; // 20.17.10
              }
            }
          }
        }
      } else if (ai2.equals("22")) {
        result = "22"; // 22
      } else if (ai3.equals("240")) {
        result = "240"; // 240
      } else if (ai2.equals("91")) {
        result = "91"; // 91
      }

      if (containsGS(code)) {
        // Starting here analyse using a GS1-128 code with special characters
        int nextGS = -1;
        if (result.equals("01.10")) {
          nextGS = indexOfGS(code, 18);
          ai2 = code.substring(nextGS + 1, nextGS + 3);
          if (ai2.equals("17")) {
            result = result + ".17"; // 01.10.17
            if (length > nextGS + 11) {
              ai2 = code.substring(nextGS + 9, nextGS + 11);
              if (ai2.equals("21")) {
                result = result + ".21"; // 01.10.17.21
              } else if (ai2.equals("30")) {
                result = result + ".30"; // 01.10.17.30
              }
            }
          }
        }
        else if (result.equals("01.11.10")) {
          nextGS = indexOfGS(code, 26);
          ai2 = code.substring(nextGS + 1, nextGS + 3);
          if (ai2.equals("17")) {
            result = result + ".17"; // 01.10.17
            if (length > nextGS + 11) {
              ai2 = code.substring(nextGS + 9, nextGS + 11);
              if (ai2.equals("21")) {
                result = result + ".21"; // 01.10.17.21
              } else if (ai2.equals("30")) {
                result = result + ".30"; // 01.10.17.30
              }
            }
          }
        }
        else if (result.equals("01.17.10")) {
          nextGS = indexOfGS(code, 24);
          if (length >= nextGS + 4) {
            ai2 = code.substring(nextGS + 1, nextGS + 3);
            ai3 = code.substring(nextGS + 1, nextGS + 4);
            if (ai3.equals("240")) {
              result = result + ".240"; // 01.17.10.240
            } else if (ai2.equals("30")) {
              result = result + ".30"; // 01.17.10.30
            } else if (ai2.equals("91")) {
              result = result + ".91"; // 01.17.10.91
            } else if (ai2.equals("93")) {
              result = result + ".93"; // 01.17.10.93
            }
          }
        } else if (result.equals("01.17.30")) {
          nextGS = indexOfGS(code, 18);
          if (length >= nextGS + 3) {
            ai2 = code.substring(nextGS + 1, nextGS + 3);
            if (ai2.equals("10")) {
              result = result + ".10"; // 01.17.30.10
            }
          }
        } else if (result.equals("02.10")) {
          nextGS = indexOfGS(code, 16);
          if (length >= nextGS + 3) {
            ai2 = code.substring(nextGS + 1, nextGS + 3);
            if (ai2.equals("37")) {
              result = result + ".37"; // 02.10.37
            }
          }
        } else if (result.equals("02.17.37")) {
          nextGS = indexOfGS(code, 18);
          if (length >= nextGS + 3) {
            ai2 = code.substring(nextGS + 1, nextGS + 3);
            if (ai2.equals("10")) {
              result = result + ".10"; // 02.17.37.10
            }
          }
        } else if (result.equals("02.37")) {
          nextGS = indexOfGS(code, 18);
          if (length >= nextGS + 3) {
            ai2 = code.substring(nextGS + 1, nextGS + 3);
            if (ai2.equals("10")) {
              result = result + ".10"; // 02.37.10
            }
          }
        } else if (result.equals("10")) {
          nextGS = indexOfGS(code, 0);
          ai2 = code.substring(nextGS + 1, nextGS + 3);
          if (ai2.equals("11")) {
            result = result + ".11"; // 10.11
            if (length >= nextGS + 11) {
              ai2 = code.substring(nextGS + 9, nextGS + 11);
              if (ai2.equals("17")) {
                result = result + ".17"; // 10.11.17
                if (length >= nextGS + 9 + 8 + 2) {
                  ai2 = code.substring(nextGS + 9 + 8, nextGS + 9 + 8 + 2);
                  result = result + ".30"; // 10.11.17.30
                }
              }
            }
          } else if (ai2.equals("17")) {
            result = result + ".17"; // 10.17
            if (length > nextGS + 12) {
              ai2 = code.substring(nextGS + 9, nextGS + 11);
              if (ai2.equals("30")) {
                result = result + ".30"; // 10.17.30
              }
            }
          } else if (ai2.equals("21")) {
            result = result + ".21"; // 10.21
          }
        } else if (result.equals("17.10")) {
          nextGS = indexOfGS(code, 8);
          ai2 = code.substring(nextGS + 1, nextGS + 3);
          if (ai2.equals("30")) {
            result = result + ".30"; // 10.17.30
          } else if (ai2.equals("91")) {
            result = result + ".91"; // 10.17.91
          }
        } else if (result.equals("17.30")) {
          nextGS = indexOfGS(code, 0);
          if (length >= nextGS + 3) {
            ai2 = code.substring(nextGS + 1, nextGS + 3);
            if (ai2.equals("10")) {
              result = result + ".10"; // 17.30.10
            }
          }
        } else if (result.equals("240")) {
          nextGS = indexOfGS(code, 1);
          if (length >= nextGS + 3) {
            ai2 = code.substring(nextGS + 1, nextGS + 3);
            if (ai2.equals("21")) {
              result = result + ".21"; // 240.21
              nextGS = indexOfGS(code, nextGS + 1);
              if (nextGS != -1) {
                if (length >= nextGS + 3) {
                  ai2 = code.substring(nextGS + 1, nextGS + 3);
                  if (ai2.equals("30")) {
                    result = result + ".30"; // 240.21.30
                    nextGS = indexOfGS(code, nextGS + 1);
                    if (nextGS != -1) {
                      if (length >= nextGS + 3) {
                        ai2 = code.substring(nextGS + 1, nextGS + 3);
                        if (ai2.equals("10")) {
                          result = result + ".10"; // 240.21.30.10
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        } else if (result.equals("91")) {
          nextGS = indexOfGS(code, 1);
          if (length >= nextGS + 3) {
            ai2 = code.substring(nextGS + 1, nextGS + 3);
            if (ai2.equals("17")) {
              result = result + ".17"; // 91.17
              if (length > nextGS + 3) {
                int shift = nextGS + 9;
                if (length >= shift + 2) {
                  ai2 = code.substring(shift, shift + 2);
                  if (ai2.equals("10")) {
                    result = result + ".10"; // 91.17.10
                  }
                }
              }
            }
          }
        }
      }
    } // End GS1-128

    // Start NaS
    else if (type == Type.NaS) {
      if (code.length() == 7) {
        if (check7Car(code)) {
          result = "NaS7";
        }
      }

      if (code.length() == 19) {
        String subLeftCode = code.substring(0, 13);
        if (checkEan13Key(subLeftCode)) {
          result = "001"; // EAN 13 and LPP without checksum
        }
      }

      if (code.length() == 20) {
        String subLeftCode = code.substring(0, 13);
        String subRightCode = code.substring(13, 20);
        if (checkEan13Key(subLeftCode) && check7Car(subRightCode) && code.startsWith("3401")) {
          result = "002"; // ACL 13 and LPP
        }
      }

      if (code.length() == 21) {
        String subLeftCode = code.substring(0, 13);
        String subRightCode = code.substring(14, 21);
        if (checkEan13Key(subLeftCode) && check7Car(subRightCode) && code.startsWith("3401")) {
          result = "003"; // ACL 13 and LPP with Espace
        }
      }

      if (code.length() == 20) {
        String subLeftCode = code.substring(0, 13);
        String subRightCode = code.substring(13, 20);
        if (checkEan13Key(subLeftCode) && check7Car(subRightCode) && !code.startsWith("3401")) {
          result = "004"; // EAN 13 and LPP
        }
      }

      if (code.length() == 28) {
        if (code.substring(20, 21).equals(" ") && code.substring(25, 26).equals("-")) {
          result = "005"; // Chris Eyes Company (Example: ASK +20.0 1102745059 2016-05)
        }
      }

      if (code.length() == 17) {
        String maybeLot = code.substring(11, 17);
        boolean ok = StringUtils.isNumeric(maybeLot);
        if (ok && code.substring(10, 11).equals(" ")) {
          result = "006"; // COUSIN BIOSERV Company (Example: FBIOW8D160 102223)
        }
      }
      if (code.length() == 22) {
        String maybeRef = code.substring(0, 8);
        String maybeExpiry = code.substring(16, 22);
        String maybeLot = code.substring(8, 16);
        boolean ok1 = StringUtils.isNumeric(maybeRef);
        boolean ok2 = StringUtils.isNumeric(maybeExpiry);
        boolean ok3 = !StringUtils.isNumeric(maybeLot);
        if (ok1 && ok2 && ok3) {
          result = "007"; // BARD France Company (Example: 58562152ANTL0294122012)
        }
      }

      // if (code.length() == 28) {
      // boolean ok = numericString(code);
      // if (ok)
      // result = "008"; // PHYSIOL France Company (Example:
      // 2808123005365310060911306301)
      // } // 28081230 053653 10060911306301

      if (code.length() >= 8) {
        if (!StringUtils.isNumeric(code) && code.substring(0, 4).equals("PAR-")) {
          result = "009"; // Arthrex Company (Example: PAR-1234-AB)
        }
      }

      if (code.length() == 7) {
        if (StringUtils.isNumeric(code.substring(1, 7)) && code.substring(0, 1).equals("T")) {
          result = "010"; // Arthrex Company (Example: T123456)
        }
      }

      // if (code.length() == 2) {
      // if (numericString(code.substring(1, 2)) && code.substring(0, 1).equals("Q")) {
      // result = "011"; // Arthrex Company (Example: Q1)
      // }
      // }

      // if (code.length() > 10)
      // {
      // if (NumericString(code.substring(3, 6)) && (code.substring(0, 3) == "SEM") && (code.substring(9, 1).equals("^"))
      // result = "012"; // SEM (Sciences Et Medecine) Company (Example: SEM171252^P30778E4009A)
      // }

      if (code.length() > 10) {
        if (code.substring(0, 3).equals("SEM") && code.substring(9, 11).equals("^P") && code.substring(code.length() - 1, code.length()).matches("^[a-zA-Z]+$")) {
          result = "012"; // SEM (Sciences Et Medecine) Company (Example: SEM171252^P30778E4009A)
        }
      }

      if (code.length() == 14) {
        if (StringUtils.isNumeric(code.substring(6, 14)) && code.substring(0, 1).equals(" ")
            && code.substring(5, 6).equals("-")) {
          result = "013"; // ABS BOLTON Company (Example: BF01-11018180)
        }
      }

      if (code.length() == 10) {
        if (StringUtils.isNumeric(code.substring(5, 10)) && code.substring(0, 5).equals("CPDR ")) {
          result = "014"; // CHIRURGIE OUEST / EUROSILICONE / SORMED Company (Example: CPDR 24602)
        }
      }
      if (code.length() == 17) {
        if (code.substring(4, 5).equals("-") && code.substring(15, 16).equals("-")) {
          result = "015"; // Symbios Orthopédie (Example: H080-25.01.2014-1)
        }
      }
      if (code.length() == 24) {
        if (StringUtils.isNumeric(code.substring(18, 24))) {
          result = "016"; // Teleflex / Arrow (Example : ]C0FR04052CFZF3015237141231)
        }
      }
      if (code.length() == 14) {
        if (StringUtils.isNumeric(code.substring(0, 9)) && code.substring(10, 11).equals(" ")) {
          result = "017"; // FCI (Example : ]C01401788197 001)
        }
      }
    }
    // End NaS

    return result;
  }
  
  /**
   * 
   * @param barcode le code barre sous forme de chaîne de caractère
   * @return les infos structurées extraites du code barre
   */
  @Override
  public CodeBarreStructure parse(String barcode) {
    try {
      CodeBarreStructure codeBarreStructure = new CodeBarreStructure();
      ParserIO parserIO = new ParserIO();
      String codeBarreWithGSescaped = barcode.replace("\u001D", "&#x0022;");
      codeBarreStructure.setCodeBarreOrigine(codeBarreWithGSescaped);
      codeBarreStructure.setParserIOVersion(this.getVersion());
      
      Type type = parserIO.getType(barcode);
      codeBarreStructure.setType(type); // 26
      String SubType = parserIO.getSubType(barcode, type);
      codeBarreStructure.setSubType(SubType); // 25
      codeBarreStructure.setAcl(parserIO.getAcl(barcode, type, SubType)); // 1
      codeBarreStructure.setAdditionnalId(parserIO.getAdditionnalId(barcode, type, SubType)); // 2
      codeBarreStructure.setBestBefore(parserIO.getBestBefore(barcode, type, SubType)); // 3
      codeBarreStructure.setCip(parserIO.getCip(barcode, type, SubType)); // 4
      codeBarreStructure.setCompany(parserIO.getCompany(barcode, type, SubType)); // 5
      codeBarreStructure.setContainsOrMayContainId(parserIO.containsOrMayContainId(barcode, type, SubType));
      codeBarreStructure.setContent(parserIO.getContent(barcode, type, SubType)); // 6
      codeBarreStructure.setCount(parserIO.getCount(barcode, type, SubType)); // 7
      codeBarreStructure.setEan(parserIO.getEan(barcode, type, SubType));
      codeBarreStructure.setExpiry(parserIO.getExpiry(barcode, type, SubType)); // 8
      codeBarreStructure.setFamily(parserIO.getFamily(barcode, type, SubType)); // 9
      codeBarreStructure.setGtin(parserIO.getGtin(barcode, type, SubType)); // 10
      codeBarreStructure.setLic(parserIO.getLic(barcode, type, SubType)); // 11
      codeBarreStructure.setLot(parserIO.getLot(barcode, type, SubType)); // 12
      codeBarreStructure.setLpp(parserIO.getLpp(barcode, type, SubType)); // 13
      codeBarreStructure.setNas7(parserIO.getNaS7(barcode, type, SubType)); // 14
      codeBarreStructure.setNormalizedBestBefore(parserIO.getNormalizedBestBefore(barcode, type, SubType)); // 15
      codeBarreStructure.setNormalizedExpiry(parserIO.getNormalizedExpiry(barcode, type, SubType)); // 16
      codeBarreStructure.setNormalizedProdDate(parserIO.getNormalizedProdDate(barcode, type, SubType)); // 17
      codeBarreStructure.setPcn(parserIO.getPcn(barcode, type, SubType)); // 18
      codeBarreStructure.setProdDate(parserIO.getProdDate(barcode, type, SubType)); // 19
      codeBarreStructure.setProduct(parserIO.getProduct(barcode, type, SubType)); // 20
      codeBarreStructure.setQuantity(parserIO.getQuantity(barcode, type, SubType)); // 21
      codeBarreStructure.setReference(parserIO.getReference(barcode, type, SubType)); // 22
      codeBarreStructure.setNasIdParamName(parserIO.getNaSIdParamName(type, SubType));
      codeBarreStructure.setSerial(parserIO.getSerial(barcode, type, SubType)); // 23
      codeBarreStructure.setSscc(parserIO.getSscc(barcode, type, SubType)); // 24
      codeBarreStructure.setSymbologyId(parserIO.getSymbologyId(barcode));
      codeBarreStructure.setUdi(parserIO.getUdi(barcode, type, SubType));
      codeBarreStructure.setUom(parserIO.getUom(barcode, type, SubType)); // 28
      codeBarreStructure.setUpn(parserIO.getUpn(barcode, type, SubType));
      codeBarreStructure.setVarCount(parserIO.getVarCount(barcode, type, SubType)); // 29
      codeBarreStructure.setVariant(parserIO.getVariant(barcode, type, SubType)); // 30
      
//      if (codeBarreStructure.isContainsOrMayContainId()) {
//        codeBarreStructure.setIdentifiers(getIdentifiers(codeBarreStructure));
//      }
      if (codeBarreStructure.getAdditionalInformation().length() == 0) {
        codeBarreStructure.setAdditionalInformation("No errors detected!");
      } else {
        codeBarreStructure.setAdditionalInformation("Errors detected:" + codeBarreStructure.getAdditionalInformation().substring(1, codeBarreStructure.getAdditionalInformation().length()));
      }

      return codeBarreStructure;
    }
    catch (Exception e) {
      logger.log(Level.ERROR, "Erreur lors du décodage du code barre (barcode="+barcode+")");
      throw e;
    }
  }
}