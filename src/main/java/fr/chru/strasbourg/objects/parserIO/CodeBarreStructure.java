package fr.chru.strasbourg.objects.parserIO;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.json.JSONObject;

import fr.chru.strasbourg.enums.parserIO.Type;

/**
 * Informations contenues dans un code barre
 * Elle sont extraite à partir de la méthode {@link ParserIO#parse(String)}
 * 
 * @author Guillaume Lefebvre
 */
public class CodeBarreStructure {
  
  private String codeBarreOrigine;
  private String acl;
  private String additionnalId;
  private String bestBefore;
  private String cip;
  private String company; // obsolete
  private boolean containsOrMayContainId;
  private String content;
  private String count;
  private String ean;
  private String expiry;
  private String family;
  private String gtin;
  private String lic;
  private String lot;
  private String lpp;
  private String nas7;
  private String normalizedBestBefore;
  private String normalizedExpiry;
  private String normalizedProdDate;
  private String pcn;
  private String prodDate;
  private String product; // obsolete
  private String quantity;
  private String reference;
  private String nasIdParamName;
  private String serial;
  private String sscc;
  private String subType;
  private String symbologyId;
  private String symbologyIdDesignation;
  private Type type;
  private String udi;
  private String udiDi;
  private String issuer;
  private String uom;
  private String upn;
  private String varCount;
  private String variant;

  private String custPartNo;
  private String additionalInformation;
  private String internal_91;
  private String internal_92;
  private String internal_93;
  private String internal_94;
  private String internal_95;
  private String internal_96;
  private String internal_97;
  private String internal_98;
  private String internal_99;
  private String storageLocation;
//  private List<Identifier> identifiers = new ArrayList<Identifier>();
  private String parserIOVersion;
  
  /**
   * Constructeur
   * On initialise les valeurs par défaut
   */
  public CodeBarreStructure() {
    this.acl = "";
    this.additionnalId = "";
    this.bestBefore = "";
    this.cip = "";
    this.company = "";
    this.containsOrMayContainId = false;
    this.content = "";
    this.count = "";
    this.ean = "";
    this.expiry = "";
    this.family = "";
    this.gtin = "";
    this.lic = "";
    this.lot = "";
    this.lpp = "";
    this.nas7 = "";
    this.normalizedBestBefore = "";
    this.normalizedExpiry = "";
    this.normalizedProdDate = "";
    this.pcn = "";
    this.prodDate = "";
    this.product = "";
    this.quantity = "";
    this.reference = "";
    this.nasIdParamName = "";
    this.serial = "";
    this.sscc = "";
    this.subType = "";
    this.symbologyId = "";
    this.type = Type.NaS;
    this.udi = "";
    this.uom = "";
    this.upn = "";
    this.varCount = "";
    this.variant = "";
    this.custPartNo = "";
    this.additionalInformation = "";
    this.internal_91 = "";
    this.internal_92 = "";
    this.internal_93 = "";
    this.internal_94 = "";
    this.internal_95 = "";
    this.internal_96 = "";
    this.internal_97 = "";
    this.internal_98 = "";
    this.internal_99 = "";
    this.storageLocation = "";
  }
  
  /**
   * @return the codeBarreOrigine
   */
  public String getCodeBarreOrigine() {
    return this.codeBarreOrigine;
  }

  /**
   * @param codeBarreOrigine the codeBarreOrigine to set
   */
  public void setCodeBarreOrigine(String codeBarreOrigine) {
    this.codeBarreOrigine = codeBarreOrigine;
  }

  /**
   * @return the acl
   */
  public String getAcl() {
    return this.acl;
  }

  /**
   * @param acl the acl to set
   */
  public void setAcl(String acl) {
    this.acl = acl;
  }

  /**
   * @return the additionnalId
   */
  public String getAdditionnalId() {
    return this.additionnalId;
  }

  /**
   * @param additionnalId the additionnalId to set
   */
  public void setAdditionnalId(String additionnalId) {
    this.additionnalId = additionnalId;
  }

  /**
   * @return the bestBefore
   */
  public String getBestBefore() {
    return this.bestBefore;
  }

  /**
   * @param bestBefore the bestBefore to set
   */
  public void setBestBefore(String bestBefore) {
    this.bestBefore = bestBefore;
  }

  /**
   * @return the cip
   */
  public String getCip() {
    return this.cip;
  }

  /**
   * @param cip the cip to set
   */
  public void setCip(String cip) {
    this.cip = cip;
  }

  /**
   * @return the company
   */
  public String getCompany() {
    return this.company;
  }

  /**
   * @param company the company to set
   */
  public void setCompany(String company) {
    this.company = company;
  }

  /**
   * @return the containsOrMayContainId
   */
  public boolean isContainsOrMayContainId() {
    return this.containsOrMayContainId;
  }

  /**
   * @param containsOrMayContainId the containsOrMayContainId to set
   */
  public void setContainsOrMayContainId(boolean containsOrMayContainId) {
    this.containsOrMayContainId = containsOrMayContainId;
  }

  /**
   * @return the content
   */
  public String getContent() {
    return this.content;
  }

  /**
   * @param content the content to set
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * @return the count
   */
  public String getCount() {
    return this.count;
  }

  /**
   * @param count the count to set
   */
  public void setCount(String count) {
    this.count = count;
  }

  /**
   * @return the ean
   */
  public String getEan() {
    return this.ean;
  }

  /**
   * @param ean the ean to set
   */
  public void setEan(String ean) {
    this.ean = ean;
  }

  /**
   * @return the expiry
   */
  public String getExpiry() {
    return this.expiry;
  }

  /**
   * @param expiry the expiry to set
   */
  public void setExpiry(String expiry) {
    this.expiry = expiry;
  }

  /**
   * @return the family
   */
  public String getFamily() {
    return this.family;
  }

  /**
   * @param family the family to set
   */
  public void setFamily(String family) {
    this.family = family;
  }

  /**
   * @return the gtin
   */
  public String getGtin() {
    return this.gtin;
  }

  /**
   * @param gtin the gtin to set
   */
  public void setGtin(String gtin) {
    this.gtin = gtin;
  }

  /**
   * @return the lic
   */
  public String getLic() {
    return this.lic;
  }

  /**
   * @param lic the lic to set
   */
  public void setLic(String lic) {
    this.lic = lic;
  }

  /**
   * @return the lot
   */
  public String getLot() {
    return this.lot;
  }

  /**
   * @param lot the lot to set
   */
  public void setLot(String lot) {
    this.lot = lot;
  }

  /**
   * @return the lpp
   */
  public String getLpp() {
    return this.lpp;
  }

  /**
   * @param lpp the lpp to set
   */
  public void setLpp(String lpp) {
    this.lpp = lpp;
  }

  /**
   * @return the nas7
   */
  public String getNas7() {
    return this.nas7;
  }

  /**
   * @param nas7 the nas7 to set
   */
  public void setNas7(String nas7) {
    this.nas7 = nas7;
  }

  /**
   * @return the normalizedBestBefore
   */
  public String getNormalizedBestBefore() {
    return this.normalizedBestBefore;
  }

  /**
   * @param normalizedBestBefore the normalizedBestBefore to set
   */
  public void setNormalizedBestBefore(String normalizedBestBefore) {
    this.normalizedBestBefore = normalizedBestBefore;
  }

  /**
   * @return the normalizedExpiry
   */
  public String getNormalizedExpiry() {
    return this.normalizedExpiry;
  }

  /**
   * @param normalizedExpiry the normalizedExpiry to set
   */
  public void setNormalizedExpiry(String normalizedExpiry) {
    this.normalizedExpiry = normalizedExpiry;
  }

  /**
   * @return the normalizedProdDate
   */
  public String getNormalizedProdDate() {
    return this.normalizedProdDate;
  }

  /**
   * @param normalizedProdDate the normalizedProdDate to set
   */
  public void setNormalizedProdDate(String normalizedProdDate) {
    this.normalizedProdDate = normalizedProdDate;
  }

  /**
   * @return the pcn
   */
  public String getPcn() {
    return this.pcn;
  }

  /**
   * @param pcn the pcn to set
   */
  public void setPcn(String pcn) {
    this.pcn = pcn;
  }

  /**
   * @return the prodDate
   */
  public String getProdDate() {
    return this.prodDate;
  }

  /**
   * @param prodDate the prodDate to set
   */
  public void setProdDate(String prodDate) {
    this.prodDate = prodDate;
  }

  /**
   * @return the product
   */
  public String getProduct() {
    return this.product;
  }

  /**
   * @param product the product to set
   */
  public void setProduct(String product) {
    this.product = product;
  }

  /**
   * @return the quantity
   */
  public String getQuantity() {
    return this.quantity;
  }

  /**
   * @param quantity the quantity to set
   */
  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }

  /**
   * @return the reference
   */
  public String getReference() {
    return this.reference;
  }

  /**
   * @param reference the reference to set
   */
  public void setReference(String reference) {
    this.reference = reference;
  }

  /**
   * @return the nasIdParamName
   */
  public String getNasIdParamName() {
    return this.nasIdParamName;
  }

  /**
   * @param nasIdParamName the nasIdParamName to set
   */
  public void setNasIdParamName(String nasIdParamName) {
    this.nasIdParamName = nasIdParamName;
  }

  /**
   * @return the serial
   */
  public String getSerial() {
    return this.serial;
  }

  /**
   * @param serial the serial to set
   */
  public void setSerial(String serial) {
    this.serial = serial;
  }

  /**
   * @return the sscc
   */
  public String getSscc() {
    return this.sscc;
  }

  /**
   * @param sscc the sscc to set
   */
  public void setSscc(String sscc) {
    this.sscc = sscc;
  }

  /**
   * @return the subType
   */
  public String getSubType() {
    return this.subType;
  }

  /**
   * @param subType the subType to set
   */
  public void setSubType(String subType) {
    this.subType = subType;
  }

  /**
   * @param subType sous-type à ajouter
   * @return le sous-type du code-barre
   */
  public String addSubType(String subType) {
    this.subType += subType;
    return this.subType;
  }
  
  /**
   * @return the symbologyId
   */
  public String getSymbologyId() {
    return this.symbologyId;
  }

  /**
   * @param symbologyId the symbologyId to set
   */
  public void setSymbologyId(String symbologyId) {
    this.symbologyId = symbologyId;
  }

  /**
   * @return the type
   */
  public Type getType() {
    return this.type;
  }

  /**
   * @param type the type to set
   */
  public void setType(Type type) {
    this.type = type;
  }

  /**
   * @return the uom
   */
  public String getUom() {
    return this.uom;
  }

  /**
   * @param uom the uom to set
   */
  public void setUom(String uom) {
    this.uom = uom;
  }

  /**
   * @return the upn
   */
  public String getUpn() {
    return this.upn;
  }

  /**
   * @param upn the upn to set
   */
  public void setUpn(String upn) {
    this.upn = upn;
  }

  /**
   * @return the varCount
   */
  public String getVarCount() {
    return this.varCount;
  }

  /**
   * @param varCount the varCount to set
   */
  public void setVarCount(String varCount) {
    this.varCount = varCount;
  }

  /**
   * @return the variant
   */
  public String getVariant() {
    return this.variant;
  }

  /**
   * @param variant the variant to set
   */
  public void setVariant(String variant) {
    this.variant = variant;
  }

  /**
   * @return the udi
   */
  public String getUdi() {
    return this.udi;
  }

  /**
   * @param udi the udi to set
   */
  public void setUdi(String udi) {
    this.udi = udi;
  }

  /**
   * @return the custPartNo
   */
  public String getCustPartNo() {
    return this.custPartNo;
  }

  /**
   * @param custPartNo the custPartNo to set
   */
  public void setCustPartNo(String custPartNo) {
    this.custPartNo = custPartNo;
  }

  /**
   * @return the additionalInformation
   */
  public String getAdditionalInformation() {
    return this.additionalInformation;
  }

  /**
   * @param additionalInformation the additionalInformation to set
   */
  public void setAdditionalInformation(String additionalInformation) {
    this.additionalInformation = additionalInformation;
  }

  /**
   * @return the internal_91
   */
  public String getInternal_91() {
    return this.internal_91;
  }

  /**
   * @param internal_91 the internal_91 to set
   */
  public void setInternal_91(String internal_91) {
    this.internal_91 = internal_91;
  }

  /**
   * @return the internal_92
   */
  public String getInternal_92() {
    return this.internal_92;
  }

  /**
   * @param internal_92 the internal_92 to set
   */
  public void setInternal_92(String internal_92) {
    this.internal_92 = internal_92;
  }

  /**
   * @return the internal_93
   */
  public String getInternal_93() {
    return this.internal_93;
  }

  /**
   * @param internal_93 the internal_93 to set
   */
  public void setInternal_93(String internal_93) {
    this.internal_93 = internal_93;
  }

  /**
   * @return the internal_94
   */
  public String getInternal_94() {
    return this.internal_94;
  }

  /**
   * @param internal_94 the internal_94 to set
   */
  public void setInternal_94(String internal_94) {
    this.internal_94 = internal_94;
  }

  /**
   * @return the internal_95
   */
  public String getInternal_95() {
    return this.internal_95;
  }

  /**
   * @param internal_95 the internal_95 to set
   */
  public void setInternal_95(String internal_95) {
    this.internal_95 = internal_95;
  }

  /**
   * @return the internal_96
   */
  public String getInternal_96() {
    return this.internal_96;
  }

  /**
   * @param internal_96 the internal_96 to set
   */
  public void setInternal_96(String internal_96) {
    this.internal_96 = internal_96;
  }

  /**
   * @return the internal_97
   */
  public String getInternal_97() {
    return this.internal_97;
  }

  /**
   * @param internal_97 the internal_97 to set
   */
  public void setInternal_97(String internal_97) {
    this.internal_97 = internal_97;
  }

  /**
   * @return the internal_98
   */
  public String getInternal_98() {
    return this.internal_98;
  }

  /**
   * @param internal_98 the internal_98 to set
   */
  public void setInternal_98(String internal_98) {
    this.internal_98 = internal_98;
  }

  /**
   * @return the internal_99
   */
  public String getInternal_99() {
    return this.internal_99;
  }

  /**
   * @param internal_99 the internal_99 to set
   */
  public void setInternal_99(String internal_99) {
    this.internal_99 = internal_99;
  }

  /**
   * @return the storageLocation
   */
  public String getStorageLocation() {
    return this.storageLocation;
  }

  /**
   * @param storageLocation the storageLocation to set
   */
  public void setStorageLocation(String storageLocation) {
    this.storageLocation = storageLocation;
  }

//  /**
//   * @return the identifiers
//   */
//  public List<Identifier> getIdentifiers() {
//    return this.identifiers;
//  }
//
//  /**
//   * @param identifiers the identifiers to set
//   */
//  public void setIdentifiers(List<Identifier> identifiers) {
//    this.identifiers = identifiers;
//  }

  /**
   * @return the parserIOVersion
   */
  public String getParserIOVersion() {
    return this.parserIOVersion;
  }

  /**
   * @return the symbologyIdDesignation
   */
  public String getSymbologyIdDesignation() {
    return this.symbologyIdDesignation;
  }

  /**
   * @param symbologyIdDesignation the symbologyIdDesignation to set
   */
  public void setSymbologyIdDesignation(String symbologyIdDesignation) {
    this.symbologyIdDesignation = symbologyIdDesignation;
  }

  /**
   * @return the udiDi
   */
  public String getUdiDi() {
    return this.udiDi;
  }

  /**
   * @param udiDi the udiDi to set
   */
  public void setUdiDi(String udiDi) {
    this.udiDi = udiDi;
  }

  /**
   * @return the issuer
   */
  public String getIssuer() {
    return this.issuer;
  }

  /**
   * @param issuer the issuer to set
   */
  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  /**
   * @param parserIOVersion the parserIOVersion to set
   */
  public void setParserIOVersion(String parserIOVersion) {
    this.parserIOVersion = parserIOVersion;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
  }

  /**
   * @return l'objet JSON correspondant
   */
  public JSONObject toJSON() {
    JSONObject jsonObject = new JSONObject()
        .put("acl", this.getAcl())
        .put("additionnalId", this.getAdditionnalId())
        .put("bestBefore", this.getBestBefore())
        .put("cip", this.getCip())
        .put("company", this.getCompany())
        .put("containsOrMayContainId", this.isContainsOrMayContainId())
        .put("content", this.getContent())
        .put("count", this.getCount())
        .put("ean", this.getEan())
        .put("expiry", this.getExpiry())
        .put("family", this.getFamily())
        .put("gtin", this.getGtin())
        .put("lic", this.getLic())
        .put("lot", this.getLot())
        .put("lpp", this.getLpp())
        .put("nas7", this.getNas7())
        .put("normalizedBestBefore", this.getNormalizedBestBefore())
        .put("normalizedExpiry", this.getNormalizedExpiry())
        .put("normalizedProdDate", this.getNormalizedProdDate())
        .put("pcn", this.getPcn())
        .put("prodDate", this.getProdDate())
        .put("product", this.getProduct())
        .put("quantity", this.getQuantity())
        .put("reference", this.getReference())
        .put("nasIdParamName", this.getNasIdParamName())
        .put("serial", this.getSerial())
        .put("sscc", this.getSscc())
        .put("subType", this.getSubType())
        .put("symbologyId", this.getSymbologyId())
        .put("type", this.getType())
        .put("udi", this.getUdi())
        .put("uom", this.getUom())
        .put("upn", this.getUpn())
        .put("varCount", this.getVarCount())
        .put("variant", this.getVariant())
        .put("custPartNo", this.getCustPartNo())
        .put("additionalInformation", this.getAdditionalInformation())
        .put("internal_91", this.getInternal_91())
        .put("internal_92", this.getInternal_92())
        .put("internal_93", this.getInternal_93())
        .put("internal_94", this.getInternal_94())
        .put("internal_95", this.getInternal_95())
        .put("internal_96", this.getInternal_96())
        .put("internal_97", this.getInternal_97())
        .put("internal_98", this.getInternal_98())
        .put("internal_99", this.getInternal_99())
        .put("storageLocation", this.getStorageLocation())
//        .put("identifiers", this.getIdentifiers())
        .put("parserIOVersion", this.getParserIOVersion());
    
    return jsonObject;
  }
}