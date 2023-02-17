package fr.chru.strasbourg.enums.parserIO;

/**
 * Type du code barre
 * Actuellement 3 types sont gérés : GS1 128, HIBC et EAN 13
 */
public enum Type {
  /**
   * code barre non standard
   */
  NaS,
  /**
   * GS1 128
   */
  GS1_128,
  /**
   * GS1 en datamatrix
   */
  GS1_Datamatrix,
  /**
   * HIBC
   */
  HIBC,
  /**
   * EAN 13
   */
  EAN13;
//  EAN14;
  
  @Override
  public String toString() {
    switch (this) {
    case NaS:
      return "NaS";
    case GS1_128:
      return "GS1-128";
    case GS1_Datamatrix:
      return "GS1_Datamatrix";
    case HIBC:
      return "HIBC";
    case EAN13:
      return "EAN13";
//    case EAN14:
//      return "EAN14";
    default:
      return null;
    }
  }
}