package kr.co.ideait.platform.gaiacairos.comp.document.dto;

import lombok.*;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(collection  = "doc-mgmt")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchVO {

    public static final String FIELD_ID = "id";
    public static final String FIELD_REF_SYS_KEY = "refSysKey";
    public static final String FIELD_NAVI_KEY = "naviKey";
    public static final String FIELD_NAVI_NO = "naviNo";
    public static final String FIELD_NAVI_ID = "naviId";
    public static final String FIELD_NAVI_PATH = "naviPath";
    public static final String FIELD_NAVI_NM = "naviNm";
    public static final String FIELD_NAVI_DIV = "naviDiv";
    public static final String FIELD_NAVI_FOLDER_TYPE = "naviFolderType";
    public static final String FIELD_DOC_NO = "docNo";
    public static final String FIELD_DOC_ID = "docId";
    public static final String FIELD_DOC_TYPE = "docType";
    public static final String FIELD_DOC_PATH = "docPath";
    public static final String FIELD_DOC_NM = "docNm";
    public static final String FIELD_PROPERTY_DATA = "propertyData";
    public static final String FIELD_RGSTR_NM = "rgstrNm";
    public static final String FIELD_RGSTR_ID = "rgstrId";
    public static final String FIELD_RGST_DT = "rgstDt";
    public static final String FIELD_CHG_DT = "chgDt";

    @Id
    @Indexed(name = "id")
    private String id;

    @Indexed(name = "refSysKey")
    private String refSysKey;

    @Indexed(name = "naviKey")
    private String naviKey;

    @Field("naviDiv")
    private String naviDiv;

    @Indexed(name = "naviPath")
    private String naviPath;

    @Field("upNaviNo")
    private String upNaviNo;

    @Field("upNaviId")
    private String upNaviId;

    @Field("naviLevel")
    private String naviLevel;

    @Field("naviType")
    private String naviType;

    @Field("naviSharYn")
    private String naviSharYn;

    @Field("naviFolderType")
    private String naviFolderType;

    @Field("naviNo")
    private String naviNo;

    @Field("naviId")
    private String naviId;

    @Indexed(name = "naviNm")
    private String naviNm;

    @Indexed(name = "docNo")
    private String docNo;

    @Indexed(name = "docId")
    private String docId;

    @Indexed(name = "docType")
    private String docType;

    @Indexed(name = "docPath")
    private String docPath;

    @Indexed(name = "docNm")
    private String docNm;

    @Field("upDocNo")
    private String upDocNo;

    @Field("upDocId")
    private String upDocId;

    @Field("docDiskNm")
    private String docDiskNm;

    @Field("docDiskPath")
    private String docDiskPath;

    @Field("docUrlPath")
    private String docUrlPath;

    @Field("docSize")
    private String docSize;

    @Field("docHitNum")
    private String docHitNum;

    @Indexed(name = "propertyData")
    private String propertyData;

    @Indexed(name = "rgstrId")
    private String rgstrId;

    @Indexed(name = "rgstrNm")
    private String rgstrNm;

    @Indexed(name = "rgstDt")
    private String rgstDt;

    @Field("chgId")
    private String chgId;

    @Field("chgNm")
    private String chgNm;

    @Field("chgDt")
    private String chgDt;

    @Field("cbgnKey")
    private String cbgnKey;
}
