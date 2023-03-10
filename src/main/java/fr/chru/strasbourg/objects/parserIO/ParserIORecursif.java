package fr.chru.strasbourg.objects.parserIO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.chru.strasbourg.enums.parserIO.Type;

//For more information, please consult the ParserIO web site at
//<http://parserio.codeplex.com>
//<https://github.com/reseauphast/ParserIO>
//

/**
 * Classe principale du parser
 * 
 * @author Guillaume Lefebvre
 */
public class ParserIORecursif implements IParserIO {

  private final static Logger logger = Logger.getLogger(ParserIORecursif.class);

//  private static String hibcASDlist = "(/14D|/16D|/S)";

  private static final String NonPrintableGS = "\u001D";
  
  private static int[] _month_days = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

  private static List<Character> nValueAssignements = new ArrayList<Character>(); 
  
  private List<String> gs1AIList = Arrays.asList("00",
      "01",
      "02",
      "10",
      "11",
      "12",
      "13",
      "15",
      "16",
      "17",
      "20",
      "21",
      "240",
      "241",
      "30",
      "90",
      "91",
      "92",
      "93",
      "94",
      "95",
      "96",
      "97",
      "98",
      "99");
  
  /**
   * Constructeur
   */
  public ParserIORecursif() {
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
    return "2.6.*";
  }

  /**
   * @param code le code barre brut ? analyser
   * @return
   */
//  private String getSymbologyId(String code) {
//    String result = "";
//    code = cleanse(code);
//    String id = "";
//    if (code.length() >= 3)
//      id = code.substring(0, 3);
//    if (id.equals("]A0"))
//      result = "A0";
//    else if (id.equals("]C0"))
//      result = "C0";
//    else if (id.equals("]C1"))
//      result = "C1";
//    else if (id.equals("]d1"))
//      result = "d1";
//    else if (id.equals("]d2"))
//      result = "d2";
//    return result;
//  }

  /**
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return NaS7 ou EAN ou ACL
   */
  private String getNaSIdParamName(Type type, String subType) {
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

//  private String getKey7Car(String code) {
//    String result = "-1";
//    boolean ok = true;
//    int sum = 0;
//    char[] array = code.toCharArray();
//    int n = -1;
//    for (int i = 0; i < code.length(); i++) {
//      if (!Character.isDigit(array[i])) {
//        ok = false;
//        break;
//      }
//    }
//    if (ok) {
//      for (int i = 0; i < 6; i++) {
//        char c = code.charAt(i);
//        try {
//          n = Integer.parseInt(Character.toString(c));
//          sum = sum + n * (i + 2);
//        } catch (NumberFormatException e) {
//        }
//      }
//      int n1 = (sum % 11) % 10;
//      result = new Integer(n1).toString();
//    }
//    return result;
//  }
  
  /**
   * 
   * @param code le code barre brut ? analyser
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return indique si le code barre contient ou pourrait contenir un identifiant
   */
  private boolean containsOrMayContainId(String code, Type type, String subType) {
    boolean result = false;
    code = cleanse(code);
    code = cleanSymbologyId(code);
    if (type == Type.GS1_128 || type == Type.GS1_Datamatrix) {
      if (subType.contains("01") ||
          subType.contains("02") ||
          subType.contains("240") ||
          subType.contains("241") ||
          subType.contains("90") ||
          subType.contains("91") ||
          subType.contains("92") ||
          subType.contains("93"))
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
      result = result.substring(3, result.length());
    }
    return result;
  }

  private String getSymbologyID(String code) {
    String result = "";
    code = cleanse(code);
    String id = "";
    if (code.length() >= 3) {
      id = code.substring(0, 3);
    }
    if (id.equals("]A0")) {
      result = "A0";
    } else if (id.equals("]C0")) {
      result = "C0";
    } else if (id.equals("]C1")) {
      result = "C1";
    } else if (id.equals("]d1")) {
      result = "d1";
    } else if (id.equals("]d2")) {
      result = "d2";
    }
    return result;
  }

  /**
   * 
   * @param str la cha?ne de caract?re contenant la date
   * @param typeDate le type de date (1 ? 9)
   * @return la date convertie depuis la cha?ne de caract?re
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
      dt.setDate(typeDate);
      if (h > 0) {
        dt.setHours(h);
      }
    }
    return dt;
  }
  
  /**
   * @param code le code barre brut ? analyser 
   * @param type le type de code barre
   * @param subType le sous type de code barre
   * @return Code famille pour le code ACL. La famille sur 1 caract?re pour le ACL 13
   * <ul>
   * <li>0 pour un accessoire d'usage m?dical</li>
   * <li>1 pour une sp?cialit? v?t?rinaire</li>
   * <li>2 pour une sp?cialit? di?t?tique</li>
   * <li>3 pour une sp?cialit? cosm?tique</li>
   * <li>5 pour une sp?cialit? parapharmaceutique</li>
   * </ul>
   */
  private String getFamily(String code, Type type, String subType) {
    code = cleanse(code);
    code = cleanSymbologyId(code);
    String result = "";
    if (code.length() >= 7) {
      if (subType.equals("ACL 13")) {
        result = code.substring(4, 5);
      }
      else if ((type == Type.GS1_128 || type == Type.GS1_Datamatrix) && subType.startsWith("01") && code.substring(3, 7).equals("3401")) {
        result = code.substring(7, 8);
      }
      else if (type == Type.NaS && (subType.equals("002") || subType.equals("003"))) {
        result = code.substring(4, 5);
      }
    }
    return result;
  }
  
  public static List<Identifier> getIdentifiers(CodeBarreStructure codeBarreStructure) {
    List<Identifier> result = new ArrayList<Identifier>();

    if (codeBarreStructure.getType() == Type.EAN13) {
      result.add(new Identifier(codeBarreStructure.getUdi()));
    }
    else if (codeBarreStructure.getType() == Type.GS1_128 || codeBarreStructure.getType() == Type.GS1_Datamatrix) {
      if (codeBarreStructure.getSubType().contains("01")) {
        result.add(new Identifier(codeBarreStructure.getUdi()));
      }
      if (codeBarreStructure.getSubType().contains("02")) {
        result.add(new Identifier(codeBarreStructure.getContent()));
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
    }
    else if (codeBarreStructure.getType() == Type.HIBC) {
      if (codeBarreStructure.getSubType().startsWith("Primary")) {
        result.add(new Identifier(codeBarreStructure.getUdi()));
      }
    }
    else if (codeBarreStructure.getType() == Type.NaS) {
      if (/* codeBarreStructure.getSubType().equals("001") || */ codeBarreStructure.getSubType().equals("004")) {
        result.add(new Identifier(codeBarreStructure.getUdi()));
      }
      else if (codeBarreStructure.getSubType().equals("002") || codeBarreStructure.getSubType().equals("003")) {
        result.add(new Identifier(codeBarreStructure.getAcl()));
      }
      else if (codeBarreStructure.getSubType().equals("NaS7")) {
        result.add(new Identifier(codeBarreStructure.getNas7()));
      }
      else {
        result.add(new Identifier(codeBarreStructure.getReference()));
      }
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
  
  /* (non-Javadoc)
   * @see fr.chru.strasbourg.objects.parserIO.IParserIO#getType(java.lang.String)
   */
  @Override
  public Type getType(String code) {
    Type result = Type.NaS;
    int length = code.length();
    code = cleanse(code);
    if ((length > 5) && code.startsWith("]C1")) {
      String ai2 = code.substring(3, 5);
      String ai3 = code.substring(3, 6);
      if (this.gs1AIList.contains(ai2) | this.gs1AIList.contains(ai3)) {
        result = Type.GS1_128;
      }
    }
    else if ((length > 5) && code.startsWith("]d2")) {
      String ai2 = code.substring(3, 5);
      String ai3 = code.substring(3, 6);
      if (this.gs1AIList.contains(ai2) | this.gs1AIList.contains(ai3)) {
        result = Type.GS1_Datamatrix;
      }
    }
    else if ((code.startsWith("]d1+") || (code.startsWith("]C0+") || code.startsWith("]A0+") || code.startsWith("+")))) {
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
    if (code.contains(NonPrintableGS)) {
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
      result = code.indexOf(NonPrintableGS, start);
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

  private CodeBarreStructure parse(String barcode, CodeBarreStructure codeBarreStructure) {
    try {
      if (barcode.length() > 0) {
        if ((codeBarreStructure.getType() == Type.GS1_128) || (codeBarreStructure.getType() == Type.GS1_Datamatrix)) {
          if (barcode.startsWith("@") || barcode.startsWith(NonPrintableGS)) {
            barcode = barcode.substring(1, barcode.length());
          }
          else if (barcode.startsWith("[GS]")) {
            barcode = barcode.substring(4, barcode.length()-3);
          }
          if (barcode.startsWith("00")) {
            codeBarreStructure.addSubType(".00");
            codeBarreStructure.setSscc(barcode.substring(2, 20));
            barcode = barcode.substring(20, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("01")) {
            codeBarreStructure.addSubType(".01");
            // Check if GTIN-13 or GTIN-14
            String gtin = barcode.substring(2, 16);
            if (checkGTINKey(gtin)) {
              if (gtin.startsWith("03400")) {
                codeBarreStructure.setGtin(gtin);
                codeBarreStructure.setCip(gtin.substring(1, 14));
                codeBarreStructure.setUdi(codeBarreStructure.getCip());
              }
              else if (gtin.startsWith("03401")) {
                codeBarreStructure.setGtin(gtin);
                codeBarreStructure.setAcl(gtin.substring(1, 14));
                codeBarreStructure.setUdi(codeBarreStructure.getAcl());
              }
              else {
                codeBarreStructure.setGtin(gtin);
                codeBarreStructure.setUdi(gtin);
              }
            } else {
              // Warning
            }
            barcode = barcode.substring(16, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("02")) {
            codeBarreStructure.addSubType(".02");
            String content = barcode.substring(2, 16);
            if (checkGTINKey(content)) {
              codeBarreStructure.setContent(content);
              codeBarreStructure.setUdi(content);
            } else {
              // Warning
            }
            barcode = barcode.substring(16, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("10")) {
            codeBarreStructure.addSubType(".10");
            int skip = 0;
            if (containsGS(barcode)) {
              skip = indexOfGS(barcode, 0);
            } else {
              skip = barcode.length();
            }
            codeBarreStructure.setLot(barcode.substring(2, skip));
            barcode = barcode.substring(skip, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("11")) {
            codeBarreStructure.addSubType(".11");
            codeBarreStructure.setProdDate(barcode.substring(2, 8));
            barcode = barcode.substring(8, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("15")) {
            codeBarreStructure.addSubType(".15");
            codeBarreStructure.setBestBefore(barcode.substring(2, 8));
            barcode = barcode.substring(8, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("17")) {
            codeBarreStructure.addSubType(".17");
            codeBarreStructure.setExpiry(barcode.substring(2, 8));
            barcode = barcode.substring(8, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("20")) {
            codeBarreStructure.addSubType(".20");
            codeBarreStructure.setVariant(barcode.substring(2, 4));
            barcode = barcode.substring(4, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("21")) {
            codeBarreStructure.addSubType(".21");
            int skip = 0;
            if (containsGS(barcode)) {
              skip = indexOfGS(barcode, 0);
            } else {
              skip = barcode.length();
            }
            codeBarreStructure.setSerial(barcode.substring(2, skip));
            barcode = barcode.substring(skip, barcode.length());
            parse(barcode, codeBarreStructure);
          }
          // Obsolete in v16
          else if (barcode.startsWith("22")) {

          }
          else if (barcode.startsWith("30")) {
            codeBarreStructure.addSubType(".30");
            int skip = 0;
            if (containsGS(barcode)) {
              skip = indexOfGS(barcode, 0);
            } else {
              skip = barcode.length();
            }
            codeBarreStructure.setVarCount(barcode.substring(2, skip));
            barcode = barcode.substring(skip, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("37")) {
            codeBarreStructure.addSubType(".37");
            int skip = 0;
            if (containsGS(barcode)) {
              skip = indexOfGS(barcode, 0);
            } else {
              skip = barcode.length();
            }
            codeBarreStructure.setCount(barcode.substring(2, skip));
            barcode = barcode.substring(skip, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("91")) {
            codeBarreStructure.addSubType(".91");
            int skip = 0;
            if (containsGS(barcode)) {
              skip = indexOfGS(barcode, 0);
            } else {
              skip = barcode.length();
            }
            codeBarreStructure.setInternal_91(barcode.substring(2, skip));
            barcode = barcode.substring(skip, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("92")) {
            codeBarreStructure.addSubType(".92");
            int skip = 0;
            if (containsGS(barcode)) {
              skip = indexOfGS(barcode, 0);
            } else {
              skip = barcode.length();
            }
            codeBarreStructure.setInternal_92(barcode.substring(2, skip));
            barcode = barcode.substring(skip, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("93")) {
            codeBarreStructure.addSubType(".93");
            int skip = 0;
            if (containsGS(barcode)) {
              skip = indexOfGS(barcode, 0);
            } else {
              skip = barcode.length();
            }
            codeBarreStructure.setInternal_93(barcode.substring(2, skip));
            barcode = barcode.substring(skip, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("94")) {
            codeBarreStructure.addSubType(".94");
            int skip = 0;
            if (containsGS(barcode)) {
              skip = indexOfGS(barcode, 0);
            } else {
              skip = barcode.length();
            }
            codeBarreStructure.setInternal_94(barcode.substring(2, skip));
            barcode = barcode.substring(skip, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("95")) {
            codeBarreStructure.addSubType(".95");
            int skip = 0;
            if (containsGS(barcode)) {
              skip = indexOfGS(barcode, 0);
            } else {
              skip = barcode.length();
            }
            codeBarreStructure.setInternal_95(barcode.substring(2, skip));
            barcode = barcode.substring(skip, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("96")) {
            codeBarreStructure.addSubType(".96");
            int skip = 0;
            if (containsGS(barcode)) {
              skip = indexOfGS(barcode, 0);
            } else {
              skip = barcode.length();
            }
            codeBarreStructure.setInternal_96(barcode.substring(2, skip));
            barcode = barcode.substring(skip, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("97")) {
            codeBarreStructure.addSubType(".97");
            int skip = 0;
            if (containsGS(barcode)) {
              skip = indexOfGS(barcode, 0);
            } else {
              skip = barcode.length();
            }
            codeBarreStructure.setInternal_97(barcode.substring(2, skip));
            barcode = barcode.substring(skip, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("98")) {
            codeBarreStructure.addSubType(".98");
            int skip = 0;
            if (containsGS(barcode)) {
              skip = indexOfGS(barcode, 0);
            } else {
              skip = barcode.length();
            }
            codeBarreStructure.setInternal_98(barcode.substring(2, skip));
            barcode = barcode.substring(skip, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("99")) {
            codeBarreStructure.addSubType(".99");
            int skip = 0;
            if (containsGS(barcode)) {
              skip = indexOfGS(barcode, 0);
            } else {
              skip = barcode.length();
            }
            codeBarreStructure.setInternal_99(barcode.substring(2, skip));
            barcode = barcode.substring(skip, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("240")) {
            codeBarreStructure.addSubType(".240");
            int skip = 0;
            if (containsGS(barcode)) {
              skip = indexOfGS(barcode, 0);
            } else {
              skip = barcode.length();
            }
            codeBarreStructure.setAdditionnalId(barcode.substring(3, skip));
            barcode = barcode.substring(skip, barcode.length());
            parse(barcode, codeBarreStructure);
          } else if (barcode.startsWith("241")) {
            codeBarreStructure.addSubType(".241");
            int skip = 0;
            if (containsGS(barcode)) {
              skip = indexOfGS(barcode, 0);
            } else {
              skip = barcode.length();
            }
            codeBarreStructure.setCustPartNo(barcode.substring(3, skip));
            barcode = barcode.substring(skip, barcode.length());
            parse(barcode, codeBarreStructure);
          }
          else {
            codeBarreStructure.setAdditionalInformation(codeBarreStructure.getAdditionalInformation() + ";" + barcode);
          }
          if (codeBarreStructure.getSubType().startsWith(".")) {
            codeBarreStructure.setSubType(codeBarreStructure.getSubType().substring(1, codeBarreStructure.getSubType().length()));
          }
        }
        else if (codeBarreStructure.getType() == Type.HIBC) {
          if (codeBarreStructure.getSubType().equals("")) {
            barcode = barcode.substring(1, barcode.length());
            char[] array = barcode.toCharArray();
            if (Character.isLetter(array[0])) { // ...with the first character always being alphabetic.
              codeBarreStructure.setSubType("Primary");
              int position = barcode.indexOf('/');
              if ((position != -1) && (position != barcode.length() - 1)) {
                codeBarreStructure.addSubType("/Secondary");
              }
            }
            else {
              codeBarreStructure.addSubType("Secondary");
            }
            parse(barcode, codeBarreStructure);
          }
          else {
            String secondaryASD = "";
            if (codeBarreStructure.getSubType().startsWith("Primary")) {
              int endPrimary = barcode.length(); // Just Primary
              int position = barcode.indexOf("/");

              if (position != -1) {
                if (position < barcode.length() - 1) {
                  endPrimary = position + 1;
                }
              }

              codeBarreStructure.setLic(barcode.substring(0, 4));
              codeBarreStructure.setPcn(barcode.substring(4, endPrimary - 2));
              codeBarreStructure.setUom(barcode.substring(endPrimary - 2, endPrimary - 1));
              codeBarreStructure.setUpn(codeBarreStructure.getLic() + codeBarreStructure.getPcn() + codeBarreStructure.getUom());
              codeBarreStructure.setUdi(codeBarreStructure.getUpn());

              barcode = barcode.substring(endPrimary, barcode.length());
            }
            if (codeBarreStructure.getSubType().contains("Secondary")) {
              String secondaryF1 = "";
              int endSecondaryF1 = barcode.length() - 1;
              barcode = barcode.substring(0, endSecondaryF1); // Delete Check Digit
              if (!codeBarreStructure.getSubType().startsWith("Primary")) {
                endSecondaryF1 = endSecondaryF1 - 1;
                barcode = barcode.substring(0, endSecondaryF1); // Delete eventually Link Digit
              }

              int position = barcode.indexOf("/");

              if (position != -1) {
                if (position < barcode.length() - 1) {
                  endSecondaryF1 = position;
                }
              }
              secondaryF1 = barcode.substring(0, endSecondaryF1);
              secondaryASD = barcode.substring(endSecondaryF1, barcode.length());

              if (secondaryF1.substring(0, 3).equals("$$+")) {
                codeBarreStructure.addSubType(".$$+");
                secondaryF1 = secondaryF1.substring(3, secondaryF1.length());
                // Date & S/N
              } else if (secondaryF1.substring(0, 2).equals("$$")) {
                codeBarreStructure.addSubType(".$$");
                secondaryF1 = secondaryF1.substring(2, secondaryF1.length());
                if (secondaryF1.substring(0, 1).equals("8")) {
                  codeBarreStructure.addSubType(".8");
                  codeBarreStructure.setQuantity(secondaryF1.substring(1, 3));
                  secondaryF1 = secondaryF1.substring(3, secondaryF1.length());
                }
                else if (secondaryF1.substring(0, 1).equals("9")) {
                  codeBarreStructure.addSubType(".9");
                  codeBarreStructure.setQuantity(secondaryF1.substring(1, 6));
                  secondaryF1 = secondaryF1.substring(6, secondaryF1.length());
                }
                // Date & Lot
              }
              else if (secondaryF1.substring(0, 1).equals("$")) {
                codeBarreStructure.addSubType(".$");
                secondaryF1 = secondaryF1.substring(1, secondaryF1.length());
              }
              else {
                codeBarreStructure.addSubType(".N");
                codeBarreStructure.setExpiry(secondaryF1.substring(0, 5));
                codeBarreStructure.setLot(secondaryF1.substring(5, secondaryF1.length()));
              }
              if (codeBarreStructure.getSubType().contains(".$$")) {
                String expDateFlag = secondaryF1.substring(0, 1);
                int dateLength = 4;
                int shift = 1;
                switch (expDateFlag) {
                case "2": {
                  codeBarreStructure.addSubType("." + expDateFlag);
                  dateLength = 6;
                  break;
                }
                case "3": {
                  codeBarreStructure.addSubType("." + expDateFlag);
                  dateLength = 6;
                  break;
                }
                case "4": {
                  codeBarreStructure.addSubType("." + expDateFlag);
                  dateLength = 8;
                  break;
                }
                case "5": {
                  codeBarreStructure.addSubType("." + expDateFlag);
                  dateLength = 5;
                  break;
                }
                case "6": {
                  codeBarreStructure.addSubType("." + expDateFlag);
                  dateLength = 7;
                  break;
                }
                default: {
                  shift = 0;
                  break;
                }
                }
                codeBarreStructure.setExpiry(secondaryF1.substring(shift, dateLength + shift));
                secondaryF1 = secondaryF1.substring(shift + dateLength, secondaryF1.length());
              }

              if (codeBarreStructure.getSubType().contains("$$+")) {
                codeBarreStructure.setSerial(secondaryF1);
              } else if ((codeBarreStructure.getSubType().contains("$$")) || codeBarreStructure.getSubType().contains(".$")) {
                codeBarreStructure.setLot(secondaryF1);
              }
            }

            if (secondaryASD.length() > 0) { // code ASD
              String[] parties = secondaryASD.split("/");
//              int count = parties.length;

              for (String item : parties) {
                if (item != null && !item.isEmpty()) {
                  String asd1 = item.substring(0, 1);
                  String asd2 = "";
                  String asd3 = "";
  
                  if (item.length() > 1) {
                    asd2 = item.substring(0, 2);
                  }
  
                  if (item.length() > 2) {
                    asd3 = item.substring(0, 3);
                  }
  
                  int endData = item.length();
  
                  if (asd1.equals("L")) {
                    codeBarreStructure.addSubType(".L");
                    codeBarreStructure.setStorageLocation(item.substring(1, endData));
                  } else if (asd1.equals("S")) {
                    codeBarreStructure.addSubType(".S");
                    codeBarreStructure.setSerial(item.substring(1, endData));
                  }
                  // else if(asd2.equals("1N"))
                  // {
  
                  // }
                  else if (asd3.equals("14D")) {
                    codeBarreStructure.addSubType(".14D");
                    codeBarreStructure.setExpiry(item.substring(3, endData));
                  } else if (asd3.equals("16D")) {
                    codeBarreStructure.addSubType(".16D");
                    codeBarreStructure.setProdDate(item.substring(3, endData));
                  } else {
                    codeBarreStructure.setAdditionalInformation(codeBarreStructure.getAdditionalInformation() + ";" + item);
                  }
                  /*
                   * switch (asd1) { case "L": //Storage location { codeBarreStructure.SubType = codeBarreStructure.SubType + ".L"; codeBarreStructure.StorageLocation = item.substring(1, endData - 1); break; } case "S": { codeBarreStructure.SubType = codeBarreStructure.SubType + ".S";
                   * codeBarreStructure.Serial = item.substring(1, endData - 1); break; } } switch (asd3) { case "14D": { codeBarreStructure.SubType = codeBarreStructure.SubType + ".14D"; codeBarreStructure.Expiry = item.substring(3, endData - 3); break; } case "16D": { codeBarreStructure.SubType =
                   * codeBarreStructure.SubType + ".16D"; codeBarreStructure.PRODDATE = item.substring(3, endData - 3); break; } }
                   */
                }
              }
            }
          }
        } else if (codeBarreStructure.getType() == Type.EAN13) {
          codeBarreStructure.setEan(barcode);

          if (barcode.substring(0, 4).equals("3401")) {
            codeBarreStructure.setSubType("ACL 13");
            codeBarreStructure.setAcl(barcode);
            codeBarreStructure.setUdi(barcode);
          }
          else if (barcode.substring(0, 4).equals("3400")) {
            codeBarreStructure.setSubType("CIP 13");
            codeBarreStructure.setCip(barcode);
            codeBarreStructure.setUdi(barcode);
          }
          else {
            codeBarreStructure.setGtin(String.format("%14s", barcode));
            codeBarreStructure.setUdi(String.format("%14s", barcode));
          }

          // Obsolete
          /*
           * if (code.substring(0, 4).equals("3401")) { codeBarreStructure.SubType = "ACL 13"; codeBarreStructure.ACL = code; } else if (code.substring(0, 4).equals("3400")) { codeBarreStructure.SubType = "CIP 13"; codeBarreStructure.CIP = code; } else { codeBarreStructure.Company = code.substring(0, 7);
           * codeBarreStructure.Product = code.substring(7, 5); }
           */
        }
        else if (codeBarreStructure.getType() == Type.NaS) {
//          if (barcode.length() == 19) {
//            String subLeftCode = barcode.substring(0, 13);
//            if (checkEan13Key(subLeftCode)) {
//              // 4022495007216119361
//              codeBarreStructure.setSubType("001"); // EAN 13 and LPP without checksum
//              codeBarreStructure.setEan(subLeftCode);
//              codeBarreStructure.setGtin(subLeftCode);
//              codeBarreStructure.setUdi(subLeftCode);
//              // codeBarreStructure.Company = code.substring(0, 7);
//              // codeBarreStructure.Product = code.substring(7, 5);
//              codeBarreStructure.setLpp(barcode.substring(13, 19) + getKey7Car(barcode.substring(13, 19)));
//            }
//          }
          if (barcode.length() == 20) {
            String subLeftCode = barcode.substring(0, 13);
            String subRightCode = barcode.substring(13, 20);
            if (checkEan13Key(subLeftCode) & check7Car(subRightCode) && barcode.startsWith("3401")) {
              // 34010798755871393295
              codeBarreStructure.setSubType("002"); // ACL 13 and LPP
              codeBarreStructure.setAcl(subLeftCode);
              codeBarreStructure.setEan(subLeftCode);
              codeBarreStructure.setUdi(subLeftCode);
              codeBarreStructure.setLpp(barcode.substring(13, 20));
            }
          }
          if (barcode.length() == 21) {
            String subLeftCode = barcode.substring(0, 13);
            String subRightCode = barcode.substring(14, 21);
            if (checkEan13Key(subLeftCode) & check7Car(subRightCode) && barcode.startsWith("3401")) {
              // 3401079875587 1393295
              codeBarreStructure.setSubType("003"); // ACL 13 and LPP with Espace
              codeBarreStructure.setAcl(subLeftCode);
              codeBarreStructure.setEan(subLeftCode);
              codeBarreStructure.setGtin(subLeftCode);
              codeBarreStructure.setUdi(subLeftCode);
              codeBarreStructure.setLpp(subRightCode);
            }
          }
          if (barcode.length() == 20) {
            String subLeftCode = barcode.substring(0, 13);
            String subRightCode = barcode.substring(13, 20);
            if (checkEan13Key(subLeftCode) & check7Car(subRightCode) && !barcode.startsWith("3401")) {
              // 40224950072161139964
              codeBarreStructure.setSubType("004"); // EAN 13 and LPP
              // codeBarreStructure.Company = code.substring(0, 7);
              // codeBarreStructure.Product = code.substring(7, 5);
              codeBarreStructure.setEan(subLeftCode);
              codeBarreStructure.setGtin(String.format("14%s", subLeftCode));
              codeBarreStructure.setUdi(String.format("14%s", subLeftCode));
              codeBarreStructure.setLpp(subRightCode);
            }
          }
          if (barcode.length() == 28) {
            if ((barcode.substring(20, 1).equals(" ")) && (barcode.substring(25, 1).equals("-"))) {
              // ASK +20.0 1102745059 2016-05
              codeBarreStructure.setSubType("005"); // Chris Eyes Company
              codeBarreStructure.setReference(barcode.substring(0, 9));
              codeBarreStructure.setSerial(barcode.substring(10, 20));
              codeBarreStructure.setExpiry(barcode.substring(21, 28));
            }
          }
          // Obsolete
//          if (barcode.length() == 17) {
//            String maybeLot = barcode.substring(11, 17);
//            boolean ok = StringUtils.isNumeric(maybeLot);
//            if (ok && barcode.substring(10, 11).equals(" ")) {
//              // FBIOW8D160 102223
//              codeBarreStructure.setSubType("006"); // COUSIN BIOSERV Company
//              codeBarreStructure.setReference(barcode.substring(0, 10));
//              codeBarreStructure.setLot(maybeLot);
//            }
//          }
          // Obsolete
          // if (code.Length == 22)
          // {
          // string maybeRef = code.substring(0, 8);
          // string maybeLot = code.substring(8, 8);
          // string maybeExpiry = code.substring(16, 6);
          // bool ok1 = NumericString(maybeRef);
          // bool ok2 = NumericString(maybeExpiry);
          // bool ok3 = !NumericString(maybeLot);
          // if (ok1 & ok2 & ok3)
          // {
          // // 58562152ANTL0294122012
          // codeBarreStructure.SubType = "007"; // BARD France Company
          // codeBarreStructure.Reference = maybeRef;
          // codeBarreStructure.Lot = maybeLot;
          // codeBarreStructure.Expiry = maybeExpiry;
          // }
          // }
          // Obsolete
          // if (code.Length == 28)
          // {
          // bool ok = NumericString(code);
          // if (ok)
          // codeBarreStructure = "008"; // PHYSIOL France Company (Example: 2808123005365310060911306301)
          // } // 28081230 053653 10060911306301
          // if (code.Length >= 8)
          // {
          // if (!NumericString(code) & (code.substring(0, 4).equals("PAR-")))
          // {
          // // PAR-1234-AB
          // codeBarreStructure.SubType = "009"; // Arthrex Company
          // codeBarreStructure.Reference = code.substring(1, code.Length - 1);
          // }
          // }
          // Obsolete
          // if (code.Length == 7)
          // {
          // if (NumericString(code.substring(1, 6)) & (code.substring(0, 1).equals("T")))
          // {
          // codeBarreStructure.SubType = "010"; // Arthrex Company (Example: T123456)
          // codeBarreStructure.Reference = "";
          // }
          // }
          // Obsolete
          // if (code.Length == 2)
          // {
          // if (NumericString(code.substring(1, 1)) & (code.substring(0, 1).equals("Q")))
          // codeBarreStructure = "011"; // Arthrex Company (Example: Q1)
          // }
//          if (barcode.length() > 10) {
//            if (barcode.substring(0, 3).equals("SEM") && barcode.substring(9, 11).equals("^P") && barcode.substring(barcode.length() - 1, barcode.length()).matches("^[a-zA-Z]+$")) {
//              // SEM171252^P30778E4009A
//              codeBarreStructure.setSubType("012"); // SEM (Sciences Et Medecine)
//              codeBarreStructure.setReference(barcode.substring(3, 9));
//              codeBarreStructure.setLot(barcode.substring(barcode.indexOf("^") + 1, barcode.length() - 1));
//            }
//          }
//          if (barcode.length() == 14) {
//            if (StringUtils.isNumeric(barcode.substring(6, 14)) && barcode.substring(0, 1).equals(" ") && barcode.substring(5, 6).equals("-")) {
//              // BF01-11018180
//              codeBarreStructure.setSubType("013"); // ABS BOLTON Company
//              codeBarreStructure.setReference(barcode.substring(1, 14));
//            }
//          }
//          if (barcode.length() == 10) {
//            if (StringUtils.isNumeric(barcode.substring(5, 10)) && barcode.substring(0, 5).equals("CPDR ")) {
//              // CPDR 24602
//              codeBarreStructure.setSubType("014"); // CHIRURGIE OUEST / EUROSILICONE / SORMED Company
//              codeBarreStructure.setReference(barcode.substring(0, 4) + barcode.substring(5, 10));
//            }
//          }
          if (codeBarreStructure.getSubType().equals("")) {
            codeBarreStructure.setSubType("NaS");
//            codeBarreStructure.setReference(barcode);
          }
        }
      }
      return codeBarreStructure;
    } catch (Exception e) {
      logger.log(Level.ERROR, "Erreur lors du d?codage du code barre (barcode=" + barcode + ")");
      throw e;
    }
  }
  
  /* (non-Javadoc)
   * @see fr.chru.strasbourg.objects.parserIO.IParserIO#parse(java.lang.String)
   */
  @Override
  public CodeBarreStructure parse(String barcode) {
    try {
      CodeBarreStructure codeBarreStructure = new CodeBarreStructure();
      String codeBarreWithGSescaped = barcode.replace(NonPrintableGS, "&#x0022;");
      codeBarreStructure.setCodeBarreOrigine(codeBarreWithGSescaped);
      codeBarreStructure.setParserIOVersion(this.getVersion());

      barcode = cleanse(barcode);
      codeBarreStructure.setType(getType(barcode));
      codeBarreStructure.setSymbologyId(getSymbologyID(barcode));
      barcode = cleanSymbologyId(barcode);

      codeBarreStructure = parse(barcode, codeBarreStructure);

      codeBarreStructure.setContainsOrMayContainId(containsOrMayContainId(barcode, codeBarreStructure.getType(), codeBarreStructure.getSubType()));
      codeBarreStructure.setNasIdParamName(getNaSIdParamName(codeBarreStructure.getType(), codeBarreStructure.getSubType()));
      // codeBarreStructure.ParserIOVersion = Assembly.Load("ParserIO.Core").GetName().Version.ToString(); ;

//      if (codeBarreStructure.isContainsOrMayContainId()) {
//        codeBarreStructure.setIdentifiers(getIdentifiers(codeBarreStructure));
//      }

      if (!codeBarreStructure.getExpiry().equals("")) {
        codeBarreStructure.setNormalizedExpiry(getNormalizedDate(codeBarreStructure.getExpiry(), codeBarreStructure.getType(), codeBarreStructure.getSubType()));
      }
      if (codeBarreStructure.getProdDate() != "") {
        codeBarreStructure.setNormalizedProdDate(getNormalizedDate(codeBarreStructure.getProdDate(), codeBarreStructure.getType(), codeBarreStructure.getSubType()));
      }

      if (codeBarreStructure.getBestBefore() != "") {
        codeBarreStructure.setNormalizedBestBefore(getNormalizedDate(codeBarreStructure.getBestBefore(), codeBarreStructure.getType(), codeBarreStructure.getSubType()));
      }
      codeBarreStructure.setFamily(getFamily(barcode, codeBarreStructure.getType(), codeBarreStructure.getSubType()));

      // UDI_DI
      if (codeBarreStructure.getUpn() != null && !codeBarreStructure.getUpn().trim().equals("")) {
        codeBarreStructure.setUdiDi(codeBarreStructure.getUpn());
      }
      else if (codeBarreStructure.getGtin() != null && !codeBarreStructure.getGtin().trim().equals("")) {
        codeBarreStructure.setUdiDi(codeBarreStructure.getGtin());
      }
      
      // Issuer
      if (codeBarreStructure.getUdiDi() != null && !codeBarreStructure.getUdiDi().trim().equals("")) {
        if (codeBarreStructure.getGtin() != null && !codeBarreStructure.getGtin().trim().equals("")) {
          codeBarreStructure.setIssuer("GS1");
        }
        else if (codeBarreStructure.getUpn() != null && !codeBarreStructure.getUpn().trim().equals("")) {
          codeBarreStructure.setIssuer("HIBCC");
        }
      }
      
      if (codeBarreStructure.getAdditionalInformation().length() == 0) {
        codeBarreStructure.setAdditionalInformation("No errors detected!");
      }
      else {
        codeBarreStructure.setAdditionalInformation("Errors detected:" + codeBarreStructure.getAdditionalInformation().substring(1, codeBarreStructure.getAdditionalInformation().length()));
      }

      return codeBarreStructure;
    } catch (Exception e) {
      logger.log(Level.ERROR, "Erreur lors du d?codage du code barre (barcode=" + barcode + ")");
      throw e;
    }
  }
}