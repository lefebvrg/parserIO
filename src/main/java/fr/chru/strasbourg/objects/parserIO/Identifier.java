package fr.chru.strasbourg.objects.parserIO;

/**
 * 
 * @author Guillaume Lefebvre
 */
public class Identifier {
  private String value;

  /**
   * Constructeur par défaut
   */
  public Identifier() {
    
  }
  
  /**
   * @param value
   */
  public Identifier(String value) {
    this.value = value;
  }
  
  /**
   * @return the value
   */
  public String getValue() {
    return this.value;
  }

  /**
   * @param value the value to set
   */
  public void setValue(String value) {
    this.value = value;
  }
  
  
}
