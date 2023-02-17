package fr.chru.strasbourg.objects.parserIO;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import fr.chru.strasbourg.objects.parserIO.CodeBarreStructure;

/**
 * 
 * @author Guillaume Lefebvre
 */
@XmlRootElement
public class ParserIOReponse {

  private CodeBarreStructure codeBarreStructure;
  
  private List<IdentifiantReponse> identifiants;

  /**
   * Cosntructeur par défaut
   */
  public ParserIOReponse() {
  }

  /**
   * @return the codeBarreStructure
   */
  public CodeBarreStructure getCodeBarreStructure() {
    return this.codeBarreStructure;
  }

  /**
   * @param codeBarreStructure the codeBarreStructure to set
   */
  public void setCodeBarreStructure(CodeBarreStructure codeBarreStructure) {
    this.codeBarreStructure = codeBarreStructure;
  }

  /**
   * @return the identifiants
   */
  public List<IdentifiantReponse> getIdentifiants() {
    return this.identifiants;
  }

  /**
   * @param identifiants the identifiants to set
   */
  public void setIdentifiants(List<IdentifiantReponse> identifiants) {
    this.identifiants = identifiants;
  }

}
