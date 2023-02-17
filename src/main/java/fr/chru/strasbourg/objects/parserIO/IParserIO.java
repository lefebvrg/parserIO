package fr.chru.strasbourg.objects.parserIO;

import fr.chru.strasbourg.enums.parserIO.Type;

/**
 * 
 * @author Guillaume Lefebvre
 */
public interface IParserIO {

  /**
   * @param code le code barre brut à analyser
   * @return le type de code barre {@link Type}
   */
  Type getType(String code);

  /**
   * 
   * @param barcode le code barre sous forme de chaîne de caractère
   * @return les infos structurées extraites du code barre
   */
  CodeBarreStructure parse(String barcode);

  /**
   * @return la version du parserIO
   */
  String getVersion();

}