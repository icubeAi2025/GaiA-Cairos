package kr.co.ideait.platform.gaiacairos.comp.projectcost.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.exception.C3RSAXErrorHandler;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.eii.common.Project;
import kr.co.ideait.eii.exception.ParserExceptionInformation;
import kr.co.ideait.eii.parser.Parser;
import kr.co.ideait.iframework.EtcUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Value;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.JDOMParseException;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.script.ScriptException;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.amqp.rabbit.connection.NodeLocator.LOGGER;

@Slf4j
@Service
public class C3RService extends AbstractGaiaCairosService {

    private static final String DEFAULT_MAPPER_PATH = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.c3r";

    private static final int CD_RSCE_LEN = 16;
    private static final int CD_UNIN_LEN = 12;
    private static final int CD_MCHNE_LEN = 17;

    private static final Map<String, String> ERR_MSG_INFO = Map.ofEntries(
        Map.entry("020101", "공종코드 길이 규칙 오류 - 짝수"),
        Map.entry("020102", "공종의 상위공종코드 오류 - 미존재"),
        Map.entry("020103", "공종의 공종구분 오류 - 최상위 이외는 일반공종만 가능"),

        Map.entry("020201", "자원구분이 텍스트인 세부공종에 코드값이 존재"),
        Map.entry("020202", "공종에 하위공종과 세부공종이 같이 존재 - 공종구조 오류"),
        Map.entry("020203", "내역서 수량 소수 자리 초과 오류"),
        Map.entry("020204", "내역서 수량과 작업분류수량의 합산 결과가 일치하지 않음"),

        Map.entry("020301", "자원코드 중복 오류"),
        Map.entry("020302", "항목코드 최대 길이 초과 오류"),
        // Map.entry("020303", "표준공사코드 미준수 오류 - 노무, 표준시장가격, 시장시공가격"),	// 조달발주 요청건만 대상이므로 일단 제외
        Map.entry("020304", "단가산출서 오류 - 산출내용이 없음"),

        Map.entry("020401", "단가산출서 오류 - 기계경비 산출서에 허용 불가 자원"),
        Map.entry("020402", "단가산출서 오류 - 등록되지 않은 자원"),
        Map.entry("020403", "단가산출서 오류 - 자원구분 값 오류"),
        Map.entry("020404", "단가산출서 오류 - 일위대가-산근 전역변수 중복 오류"),

        Map.entry("020501", "손료항목 코드규칙 오류 - 손료코드는 RENT로 시작"),
        Map.entry("020502", "손료항목 코드중복 오류 - 손료코드는 해당 산출서에 유일하여야 함"),
        Map.entry("020503", "손료항목 수량 오류 - 손료의 수량은 1로 작성"),
        Map.entry("020504", "손료항목 설정 오류 - 손료 소수 처리방법이 정의되지 않음"),
        Map.entry("020505", "손료 계산 루핑 오류 - 자신을 포함하고 있음"),
        Map.entry("020506", "손료항목 설정 오류 - 적용요율 소수 자릿수 초과"),
        Map.entry("020507", "손료항목 설정 오류 - 손료 계산 대상이 없음"),
        Map.entry("020508", "손료항목 설정 오류 - 손료 계산 루핑(순환 참조) 오류"),
        Map.entry("020509", "손료 계산 가능 범위 초과 오류 - 손료계산 대상 범위 500BYTE"),

        Map.entry("020601", "기계경비 시간당노임산출 항목코드 중복"),
        Map.entry("020602", "기계경비 시간당손료산출식 오류"),
        Map.entry("020603", "기계경비 시간당노임산출식 오류"),
        Map.entry("020604", "기계경비 산출정보 사용자 정의코드 오류"),
        Map.entry("020605", "기계경비 작업시간 구분 값 오류"),
        Map.entry("020606", "기계경비 작업장소 구분 값 오류 - 빈 값"),
        Map.entry("020607", "기계경비 작업장소 구분 값 오류 - 정의되지 않음")
    );

    @Autowired
    C3RCalService c3RCalService;

    /**
     *
     * @param c3rFileList C3R 파일목록
     * @param cntrctNo 계약번호
     * @param cntrctChgId 계약변경ID
     * @param majorCnsttyCd 공사구분코드
     * @param checkFileListJson 초기화 삭제여부
     * @return
     */
    @Transactional(rollbackFor = {DataAccessException.class, Exception.class})
    public boolean saveC3RFile(List<MultipartFile> c3rFileList, String cntrctNo, String cntrctChgId, String majorCnsttyCd, String checkFileListJson) {

        // FILE 확장자명 검사
        for (MultipartFile c3rFile : c3rFileList) {

            // 20250227 - 정적검사 수정 [Dodgy] NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE
            String originalFileName = c3rFile.getOriginalFilename();
            String fileExt = null;

            if (originalFileName != null) {
                fileExt = org.springframework.util.StringUtils.getFilenameExtension(originalFileName.toLowerCase());

                if (fileExt == null) {
                    // msg.026 - 허용되지 않은 파일 형식입니다.
                    String message = messageSource.getMessage("msg.026", null, LocaleContextHolder.getLocale());
                    throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, message);
                } else if (!fileExt.equals("xml")) {
                    // msg.026 - 허용되지 않은 파일 형식입니다.
                    String message = messageSource.getMessage("msg.026", null, LocaleContextHolder.getLocale());
                    throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, message);
                }
            }
        }

        boolean result = true;
        String errMsg = "";

        Map<String, Object> param = new HashMap<>();

        param.put("CNTRCT_NO", cntrctNo);
        param.put("CNTRCT_CHG_ID", cntrctChgId);
        param.put("USER_ID", Objects.requireNonNull(UserAuth.get(true)).getUsrId());
        param.put("MAJOR_CNSTTY_CD", majorCnsttyCd);


        // TODO Mybatis binding 을위한 임시변수 - 추후 주석 제거시 삭제필요
        param.put("UNIT_CNSTWK_NO", "TEMP");

        // JSON 데이터를 리스트로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> checkFileList = null;
        try {
            checkFileList = objectMapper.readValue(checkFileListJson, List.class);
        } catch (JsonProcessingException e) {
            result = false;
            throwsErrException(param, e.getLocalizedMessage());
        }

        // 파일명(fileName)을 key, isChecked 값을 value로 가지는 Map 으로 변환
        Map<String, Boolean> fileCheckMap = checkFileList.stream()
                .collect(Collectors.toMap(
                    file -> (String) file.get("fileName"),
                    file -> (Boolean) file.get("isChecked")
                ));

        for (MultipartFile c3rFile : c3rFileList) {

            // Validation
            if (c3rFile.isEmpty()) {
                result = false;
            }

            // 해당 C3R 파일이 형식에 맞는지 내용 검증
            Map<String, Object> chkMap = setSchemaChk(param, c3rFile);

            boolean JDOM_ERR = ObjectUtils.isEmpty(chkMap.get("JDOM_ERR"));
            boolean SAXP_ERR = ObjectUtils.isEmpty(chkMap.get("SAXP_ERR"));
            boolean ETC_ERR = ObjectUtils.isEmpty(chkMap.get("ETC_ERR"));
            
            String FILE_NAME = c3rFile.getOriginalFilename();

            if(JDOM_ERR && SAXP_ERR && ETC_ERR){
                Element root = (Element)chkMap.get("root");

                try {

                    // 20241126 - C3R 파일에서 공사정보를 가져와 단위공사 코드정보 SET
                    String constructionType = root
                            .getChild("공사내역서")
                            .getChild("단일공사내역서")
                            .getChild("단일공사정보")
                            .getChildText("공사구분");
                    List<Map<String, Object>> codeList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getMajorCnsttyCdList", param);

                    for (Map<String, Object> map : codeList) {
                        if (map.get("unit_cnstwk_nm").equals(constructionType))
                            param.put("MAJOR_CNSTTY_CD", map.get("cntrct_cnst_type"));
                    }


                    Boolean doDelete = fileCheckMap.get(FILE_NAME);
                    if (doDelete) {
                        // 계약내역서 갱신 & 연관 설계내역서 삭제
                        mybatisSession.delete(DEFAULT_MAPPER_PATH + ".deleteUnitC3R", param);
                    }

                    // 임시테이블 삭제
                    mybatisSession.delete(DEFAULT_MAPPER_PATH + ".deleteUnitC3RTmpDta", param);

                    // 임시테이블에 C3R 등록
                    insertCnstInfoTmp(root, param);

                    // mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCstCnstwkTmpDta");

                    // 임시테이블 -> 단위공사
                    moveTmpToRealCnstwk(root, param);

                } catch(GaiaBizException e) {
                    result = false;
                    throwsErrException(param, e.getLocalizedMessage());
                }
            } else {
                if (!JDOM_ERR) errMsg = EtcUtil.nullConvert(chkMap.get("JDOM_ERR"));
                else if (!SAXP_ERR) errMsg = EtcUtil.nullConvert(chkMap.get("SAXP_ERR"));
                else if (!ETC_ERR) errMsg = EtcUtil.nullConvert(chkMap.get("ETC_ERR"));
            }

            if (!errMsg.isEmpty()) {
                errMsg = FILE_NAME + " [설계내역서 등록 오류]<br /><br />" + errMsg;
                throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, errMsg);
            }

            // C3R 오류 항목 확인 후 등록 불가 처리
            List<Map<String, Object>> errList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getC3rDtaErrList", param);
            //List<Map<String, Object>> errList = new ArrayList<>();
            if (!errList.isEmpty()) {
                errMsg = "";

                int nCnt = 1;
                for(Map<String, Object> item : errList) {
                    errMsg += nCnt + ". " + EtcUtil.nullConvert(item.get("ERR_CNTNTS")) + (nCnt < errList.size() ? "<br />" : "");

                    nCnt++;
                }
                errMsg = FILE_NAME + " [설계내역서 등록 오류]<br /><br />" + errMsg;
                throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, errMsg);
            }

            // 계약내역서와 설계내역서 공종 및 세부공종 일치하는 정보조회
            /**
             * 20241209 주석
            List<Map<String, Object>> debug1 = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getDebugCmprList1", param);
            List<Map<String, Object>> debug2 = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getDebugCmprList2", param);
            List<Map<String, Object>> debug3 = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getDebugCmprList3", param);

            List<Map<String, Object>> cmprList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCtrCstDtlCnsttyCmprList", param);
            if(!cmprList.isEmpty()) {
                for(Map<String, Object> item : cmprList) {
                    if("N".equals(item.get("err_yn"))) {
                        item.put("USER_ID", param.get("USER_ID"));

                        // 일치하는 공종 및 세부공종에 대해 계약내역서에 단위공사번호, 공종순번, 세부공종순번 추가
                        mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateCtrDtlsttCstInfo", EtcUtil.changeToUpperMapKey(item));
                    } else {
                        errMsg = "계약내역서와 설계내역서가 일치하지 않습니다.<br />[ 계약내역서 세부공종 : "
                                + EtcUtil.nullConvert(item.get("PRDNM")) + " ] <br />[ 설계내역서 세부공종 : "
                                + EtcUtil.nullConvert(item.get("RSCE_NM")) + " ]";
                        break;
                    }
                }
            } else {
                errMsg = "계약내역서 정보가 없습니다.";
            }

            if (!errMsg.isEmpty()) {
                errMsg = FILE_NAME + " [설계내역서 등록 오류]<br /><br />" + errMsg;
                throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, errMsg);
            }
            */

            // 소요자원 계산
            c3RCalService.getCstReqreRsceRecalc(param);

            /**
             * @date: 2024-12-13
             * 공정 QDB 관련 비즈니스 로직 추가
             *
             * @date: 2024-12-23
             * 공정_PLAN_ACTIVITY 비즈니스 로직 추가
             */
            int revisionId = EtcUtil.zeroConvertInt(mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".getPrRevisionId", param));

            // 해당 계약 공정_REVISION 이 존재할 경우에만 수행
            if (revisionId > 0) {
                param.put("REVISION_ID", Integer.toString(revisionId));
                // CT_QDB TO PR_QDB 삭제 후 INSERT
                // 2024-12-27 QDB_RESOURCE

                mybatisSession.delete(DEFAULT_MAPPER_PATH + ".deletePrQdbResource", param);
                mybatisSession.delete(DEFAULT_MAPPER_PATH + ".deleteC3rToPrQDB", param);

                mybatisSession.insert(DEFAULT_MAPPER_PATH + ".moveC3rToPrQDB", param);
                mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertPrQdbResource", param);

                // 2024-12-23 추가
                mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateActivityExptCost", param);


                // 해당 계약 총 금액이 0보다 클때 수행
                double contractAmt = mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".getCntrctTotalAmt", param);
                if (contractAmt > 0) {

                    List<Map<String, Object>> periodList = EtcUtil.changeToUpperMapList(mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getActivityPlanPeriod", param));
                    String insertActivityPlan = DEFAULT_MAPPER_PATH + ".insertActivityPlan";

                    // PR_ACTIVITY_PLAN 삭제 후 INSERT
                    mybatisSession.delete(DEFAULT_MAPPER_PATH + ".deleteActivityPlan", param);
                    List<List<Map<String, Object>>> partedList = ListUtils.partition(periodList, 200);
                    for (List<Map<String, Object>> list1 : partedList) {
                        for (Map item : list1) {

                            mybatisSession.insert(insertActivityPlan, item);
                        }
                        mybatisSession.flushStatements();
                    }
                }

            }


        }

        return result;
    }

    /**
     * 임시테이블 삭제
     */
    private void throwsErrException(Map<String, Object> param, String errMsg) {
        //mybatisSession.delete(DEFAULT_MAPPER_PATH + ".deleteUnitCnstwk", param);
        mybatisSession.delete(DEFAULT_MAPPER_PATH + ".deleteUnitC3R", param);
        throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, errMsg);
    }

    /**
     * 스키마 validator
     */
    private Map<String, Object> setSchemaChk(Map<String, Object> param, MultipartFile c3rFile) {
        Map<String, Object> rtnMap = new HashMap<String, Object>();
        InputStream inputStream = null;
        File _c3rFile = null;
        String schemaFileNm = "validationcheck.xsd";

        // 1. XML에서 스키마버전 먼저 확인
        try {
            Path _c3rFilePath =  Files.createTempFile(null, c3rFile.getOriginalFilename());
            _c3rFile = _c3rFilePath.toFile();
            c3rFile.transferTo(_c3rFile.toPath());

            inputStream = new FileInputStream(_c3rFile);
            Document jdomdoc = new SAXBuilder().build(inputStream);
            String elVal = jdomdoc.getRootElement().getChild("스키마버전").getValue().replaceAll("[^\\d]", "");
            int version = EtcUtil.zeroConvertInt(elVal);
            if(version < 105) {
                schemaFileNm = "validationcheck_104.xsd";
            } else if(version >= 105 && version < 110) {
                schemaFileNm = "validationcheck_105.xsd";
            } else if(version >= 110) {
                schemaFileNm = "validationcheck_110.xsd";
            }
            // 20250227 - 정적검사 수정 useless_copndition
            /* else {
                schemaFileNm = "validationcheck.xsd";
            } */

            // 2. 스키마 유효성 체크
            /************* 스키마 유효성 체크 시작 *****************/
            File c3rXmlFile = _c3rFile;
            String resourcePath = "c3r/" + schemaFileNm;
            Resource resource = new ClassPathResource(resourcePath);
            log.debug("resourcePath: " + resourcePath);


            // JAR 내부의 리소스는 파일이 아니라 스트림으로 처리해야 한다.
            //File c3rSchemaFile = resource.getFile();
            File c3rSchemaFile = null;
            c3rSchemaFile = FileService.convertInputStreamToFile(resource.getInputStream(), schemaFileNm);
            if(c3rSchemaFile != null) {

                log.debug("File created at: " + c3rSchemaFile.getAbsolutePath());

                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = schemaFactory.newSchema(c3rSchemaFile);
                Validator validator = schema.newValidator();
                validator.setFeature("http://xml.org/sax/features/validation", true);
                validator.setFeature("http://apache.org/xml/features/validation/schema", true);
                //validator.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", SCHEMA_DIR + File.separator + schemaFileNm);
                validator.setErrorHandler(new C3RSAXErrorHandler());
                validator.validate(new StreamSource(c3rXmlFile));
                /************* 스키마 유효성 체크 끝 *****************/

                // 3. 스키마 유효성 오류 없으면 root 정보 얻기
                Element root = jdomdoc.getRootElement();
                rtnMap.put("root", root);

                // 20250227 - 정적검사 수정 [Bad practice] RV_RETURN_VALUE
                // 파일 삭제
                if (!c3rSchemaFile.delete()) {
                    log.warn("Failed to delete file: {}", c3rSchemaFile.getAbsolutePath());
                }
                log.debug("File deleted after use.");
            }

        } catch(JDOMParseException ex) {
            String message = ex.getMessage();
            if(message != null) {
                rtnMap.put("JDOM_ERR", message.replaceAll("'", "′"));
            }
        } catch(SAXException ex) {
            String message = ex.getMessage();
            if(message != null) {
                rtnMap.put("SAXP_ERR", message.replaceAll("'", "′"));
            }
        } catch(IOException | JDOMException | GaiaBizException ex){
            String message = ex.getMessage();
            if(message != null){
                rtnMap.put("ETC_ERR", message.replaceAll("'", "′"));
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
            FileUtils.deleteQuietly(_c3rFile);
        }

        return rtnMap;
    }

    /**
     * 임시테이블 등록
     *
     * 2025-02-11 건별 수행 -> 리스트 분할 입력으로 변경처리
     */
    private void insertCnstInfoTmp(Element root, Map<String, Object> param){
        Map<String, Object> tmpMapList3 = makeTmpMapList3(root, param);
        List<Map<String, Object>> tmpCnsttyDtaList = (List<Map<String, Object>>) tmpMapList3.get("tmpCnsttyDtaList");
        List<Map<String, Object>> tmpDtaList = (List<Map<String, Object>>) tmpMapList3.get("tmpDtaList");
        List<Map<String, Object>> tmpMidDtaList = (List<Map<String, Object>>) tmpMapList3.get("tmpMidDtaList");

        // AS-IS
//        for(List<Map<String, Object>> list : new ArrayList<>(Arrays.asList(tmpCnsttyDtaList, tmpDtaList, tmpMidDtaList))) {
//            int cnt = 0;
//            for(Map<String, Object> map : list) {
//                setTmpDataNoKeyEmptyStr(map);
//                mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertCstCnstwkTmpDta", map);
//                cnt++;
//
//                if(cnt > 1000) {
//                    mybatisSession.flushStatements();
//
//                    cnt = 0;
//                }
//            }
//
//            mybatisSession.flushStatements();
//        }

        List<Map<String, Object>> modifiedTmpCnsttyDtaList = tmpCnsttyDtaList.stream()
                .map(item -> {
                    Map<String, Object> newMap = new HashMap<>(item);
                    setTmpDataNoKeyEmptyStr(newMap);
                    return newMap;
                })
                .collect(Collectors.toList());

        List<List<Map<String, Object>>> partedList1 = ListUtils.partition(modifiedTmpCnsttyDtaList, 100);// 한꺼번에 하면 pgsql 에러
        for(List<Map<String, Object>> list: partedList1) {
            mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertCstCnstwkTmpDta", EtcUtil.map("regList", list));
        }

        List<Map<String, Object>> modifiedTmpDtaList = tmpDtaList.stream()
                .map(item -> {
                    Map<String, Object> newMap = new HashMap<>(item);
                    setTmpDataNoKeyEmptyStr(newMap);
                    return newMap;
                })
                .collect(Collectors.toList());

        List<List<Map<String, Object>>> partedList2 = ListUtils.partition(modifiedTmpDtaList, 100);// 한꺼번에 하면 pgsql 에러
        for(List<Map<String, Object>> list: partedList2) {
            mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertCstCnstwkTmpDta", EtcUtil.map("regList", list));
        }

        List<Map<String, Object>> modifiedTmpMidDtaList = tmpMidDtaList.stream()
                .map(item -> {
                    Map<String, Object> newMap = new HashMap<>(item);
                    setTmpDataNoKeyEmptyStr(newMap);
                    return newMap;
                })
                .collect(Collectors.toList());

        List<List<Map<String, Object>>> partedList3 = ListUtils.partition(modifiedTmpMidDtaList, 100);// 한꺼번에 하면 pgsql 에러
        for(List<Map<String, Object>> list: partedList3) {
            mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertCstCnstwkTmpDta", EtcUtil.map("regList", list));
        }
    }

    /**
     * C3R 파일에 있는 공사정보를 Map으로 생성
     */
    private Map<String, Object> makeTmpMapList3(Element root, Map<String, Object> param) {
        List<Map<String, Object>> tmpDtaList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> tmpMidDtaList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> tmpCnsttyDtaList = new ArrayList<Map<String, Object>>();
        Map<String, Object> tempMap = null;

        String[] arrMLEStr = { "MA", "LA", "EQ" };
//		String rgstrId = EtcUtil.nullConvert(param.get("USER_ID"));
        String rgstrId = "C3R";

        List depth2List = null;
        List depth3List = null;
        List depth4List = null;

        Element rootEle = null;
        Element depth2Ele = null;
        Element depth3Ele = null;
        Element depth4Ele = null;
        Element depth5Ele = null;

        /////////////////////////////////////////// 기계경비산출//////////////////////////////////////
        rootEle = root.getChild("공사정보");
        depth2Ele = rootEle.getChild("기계경비산출");
        if(depth2Ele != null) {
            // 환율 처리
            depth3Ele = depth2Ele.getChild("환율");
            if(depth3Ele != null) {
                tempMap = getNewMapDta(param, rgstrId);
                tempMap.put("TBL_NM", "CST_UNIT_UPJT_EXCRT");									// 원가_환율테이블
                tempMap.put("ATTRBT1_NM", "미국_1USD");											// 통화구분
                tempMap.put("FRGNCRNCY_GNRLEXPNS_UPRC", depth3Ele.getChildText("미국_1USD"));		// 기준환율
                tmpDtaList.add(tempMap);

                tempMap = getNewMapDta(param, rgstrId);
                tempMap.put("TBL_NM", "CST_UNIT_UPJT_EXCRT");									// 원가_환율테이블
                tempMap.put("ATTRBT1_NM", "유럽연합_1EUR");											// 통화구분
                tempMap.put("FRGNCRNCY_GNRLEXPNS_UPRC", depth3Ele.getChildText("유럽연합_1EUR"));	// 기준환율
                tmpDtaList.add(tempMap);

                tempMap = getNewMapDta(param, rgstrId);
                tempMap.put("TBL_NM", "CST_UNIT_UPJT_EXCRT");									// 원가_환율테이블
                tempMap.put("ATTRBT1_NM", "일본_100엔");											// 통화구분
                tempMap.put("FRGNCRNCY_GNRLEXPNS_UPRC", depth3Ele.getChildText("일본_100엔"));		// 기준환율
                tmpDtaList.add(tempMap);
            } else {
                tempMap = getNewMapDta(param, rgstrId);
                tempMap.put("TBL_NM", "CST_UNIT_UPJT_EXCRT");									// 원가_환율테이블
                tempMap.put("ATTRBT1_NM", "미화-1USD");											// 통화구분
                tempMap.put("FRGNCRNCY_GNRLEXPNS_UPRC", "0");									// 기준환율
                tmpDtaList.add(tempMap);

                tempMap = getNewMapDta(param, rgstrId);
                tempMap.put("TBL_NM", "CST_UNIT_UPJT_EXCRT");									// 원가_환율테이블
                tempMap.put("ATTRBT1_NM", "유로-1EUR");											// 통화구분
                tempMap.put("FRGNCRNCY_GNRLEXPNS_UPRC", "0");									// 기준환율
                tmpDtaList.add(tempMap);

                tempMap = getNewMapDta(param, rgstrId);
                tempMap.put("TBL_NM", "CST_UNIT_UPJT_EXCRT");									// 원가_환율테이블
                tempMap.put("ATTRBT1_NM", "엔화-100엔");											// 통화구분
                tempMap.put("FRGNCRNCY_GNRLEXPNS_UPRC", "0");									// 기준환율
                tmpDtaList.add(tempMap);
            }

            // 시간당 손료 산출
            depth3Ele = depth2Ele.getChild("시간당손료산출");
            if(depth3Ele != null) {
                depth4List = depth3Ele.getChildren();
                if(depth4List != null) {
                    for(int i = 0; i < depth4List.size(); i++) {
                        depth4Ele = (Element) depth4List.get(i);

                        tempMap = getNewMapDta(param, rgstrId);
                        tempMap.put("TBL_NM", "CST_MCHNE_CALC_INFO");			// 원가_기계경비산출정보 테이블
                        tempMap.put("ATTRBT1_NM", "1");							// 산출구분(1:손료산출)
                        tempMap.put("ATTRBT2_NM", "산출식");						// 적용구분(0)

                        if("E0-주간".equals(depth4Ele.getName())) {
                            tempMap.put("TMP_RSCE_UNTY_CD", "E0");				// 산출식코드
                            tempMap.put("RSCE_SPEC_NM", depth4Ele.getValue());	// 산출식
                            tempMap.put("RMRK_CNTNTS", "주간");					// 산출식설명
                            tmpDtaList.add(tempMap);

                        } else if("E1-야간".equals(depth4Ele.getName())) {
                            tempMap.put("TMP_RSCE_UNTY_CD", "E1");				// 산출식코드
                            tempMap.put("RSCE_SPEC_NM", depth4Ele.getValue());	// 산출식
                            tempMap.put("RMRK_CNTNTS", "야간");					// 산출식설명
                            tmpDtaList.add(tempMap);

                        } else if("E2-주야간2교대".equals(depth4Ele.getName())) {
                            tempMap.put("TMP_RSCE_UNTY_CD", "E2");				// 산출식코드
                            tempMap.put("RSCE_SPEC_NM", depth4Ele.getValue());	// 산출식
                            tempMap.put("RMRK_CNTNTS", "주야간2교대");				// 산출식설명
                            tmpDtaList.add(tempMap);

                        } else if("E3-주야간3교대".equals(depth4Ele.getName())) {
                            tempMap.put("TMP_RSCE_UNTY_CD", "E3");				// 산출식코드
                            tempMap.put("RSCE_SPEC_NM", depth4Ele.getValue());	// 산출식
                            tempMap.put("RMRK_CNTNTS", "주야간3교대");				// 산출식설명
                            tmpDtaList.add(tempMap);
                        }
                    }
                }
            }

            // 시간당노임산출 처리
            depth3Ele = depth2Ele.getChild("시간당노임산출");
            if(depth3Ele != null) {
                depth4List = depth3Ele.getChildren();
                if(depth4List != null) {
                    for(int i = 0; i < depth4List.size(); i++) {
                        depth4Ele = (Element) depth4List.get(i);

                        tempMap = getNewMapDta(param, rgstrId);
                        tempMap.put("TBL_NM", "CST_MCHNE_CALC_INFO");					// 원가_기계경비산출정보 테이블
                        tempMap.put("ATTRBT1_NM", "2");									// 산출구분(2:노임산출)

                        List<String> cdNmList = new ArrayList<>(Arrays.asList("H0-일반구간", "H1-터널구간", "H2-장대터널", "H3-펌프준설선", "H4-그래브준설선", "H5-버킷준설선"));
                        String cdNm = EtcUtil.nullConvert(depth4Ele.getName());
                        if(cdNmList.contains(cdNm)) {
                            tempMap.put("ATTRBT2_NM", depth4Ele.getChildText("적용구분"));	// 적용구분
                            tempMap.put("TMP_RSCE_UNTY_CD", cdNm.substring(0, 2));		// 산출식코드
                            tempMap.put("RSCE_SPEC_NM", depth4Ele.getChildText("산출식"));	// 산출식
                            tempMap.put("RSCE_QTY", depth4Ele.getChildText("계산값"));		// 계산값
                            tempMap.put("RMRK_CNTNTS", cdNm.substring(3));				// 산출식설명
                            tmpDtaList.add(tempMap);
                        }
                    }
                }

                depth4List = depth3Ele.getChildren("H_-사용자정의");
                if(depth4List != null) {
                    for(int i = 0; i < depth4List.size(); i++) {
                        depth4Ele = (Element) depth4List.get(i);
                        tempMap = getNewMapDta(param, rgstrId);
                        tempMap.put("TBL_NM", "CST_MCHNE_CALC_INFO");
                        tempMap.put("ATTRBT1_NM", "3");										// 산출구분(3:노임산출 사용자정의)
                        tempMap.put("TMP_RSCE_UNTY_CD", depth4Ele.getChildText("사용자정의코드"));	// 산출식코드

                        depth5Ele = depth4Ele.getChild("노임산출");
                        tempMap.put("ATTRBT2_NM", depth5Ele.getChildText("적용구분"));			// 적용구분
                        tempMap.put("RSCE_SPEC_NM", depth5Ele.getChildText("산출식"));			// 산출식
                        tempMap.put("RSCE_QTY", depth5Ele.getChildText("계산값"));				// 계산값
                        tempMap.put("RMRK_CNTNTS", "노임산출" + i);								// 산출식설명
                        tmpDtaList.add(tempMap);
                    }
                }
            }
        }

        /////////////////////////////////////////// 작업분류정보 //////////////////////////////////////
        depth2Ele = rootEle.getChild("작업분류정보");
        if(depth2Ele != null) {
            // 작업분류기준 처리
            depth3Ele = depth2Ele.getChild("작업분류기준");
            if(depth3Ele != null) {
                tempMap = getNewMapDta(param, rgstrId);
                tempMap.put("TBL_NM", "CST_WBS_INFO");								// 원가_WBS정보 테이블
                tempMap.put("ATTRBT1_NM", depth3Ele.getChildText("작업분류기준명"));		// 작업분류기준명
                tempMap.put("CALC_MLG_VAL", depth3Ele.getChildText("작업분류기준버전"));	// 작업분류기준버전
                tempMap.put("CNSTTY_CD", depth3Ele.getChildText("표준작업분류기준코드"));	// 표준작업분류기준코드
                tmpDtaList.add(tempMap);
            }

            // 작업분류단계 처리
            depth3List = depth2Ele.getChildren("작업분류단계");
            for(int i = 0; i < depth3List.size(); i++) {
                depth3Ele = (Element) depth3List.get(i);

                tempMap = getNewMapDta(param, rgstrId);
                tempMap.put("TBL_NM", "CST_WBS_OPER_CLSS");							// 원가_WBS작업분류 테이블
                tempMap.put("RSCE_QTY", depth3Ele.getChildText("작업분류단계수"));			// 작업분류단계수
                tempMap.put("ATTRBT1_NM", depth3Ele.getChildText("작업분류단계명"));		// 작업분류단계명
                tmpDtaList.add(tempMap);
            }

            // 작업분류 처리
            depth3List = depth2Ele.getChildren("작업분류");
            for(int i = 0; i < depth3List.size(); i++) {
                depth3Ele = (Element) depth3List.get(i);

                // 작업분류단계코드, 작업분류단계명 1 ~ 10 합침. 구분자 : |
                String WBS_IT_LVL_CD = "";
                String WBS_IT_LVL_NM = "";
                int lvlNum = 1;

                // 1단계 코드 및 코드명이 없을경우 작업분류 등록하지 않음.
                String stepCdRoot = depth3Ele.getChildText("작업분류1단계코드");
                String stepNmRoot = depth3Ele.getChildText("작업분류1단계명");
                if(StringUtils.isEmpty(stepCdRoot) && StringUtils.isEmpty(stepNmRoot)) {
                    break;
                }

                for(int j=1; j <= 10; j++) {
                    String stepCd = depth3Ele.getChildText("작업분류" + j + "단계코드");
                    String stepNm = depth3Ele.getChildText("작업분류" + j + "단계명");

                    WBS_IT_LVL_CD += (j > 1 ? "| " : "") + stepCd;
                    WBS_IT_LVL_NM += (j > 1 ? "| " : "") + stepNm;

                    if(!ObjectUtils.isEmpty(stepCd)) {
                        lvlNum = j;
                    }
                }

                tempMap = getNewMapDta(param, rgstrId);
                tempMap.put("TBL_NM", "CST_WBS"); 											// 원가_WBS 테이블
                tempMap.put("RSCE_SN_VAL", depth3Ele.getChildText("작업분류식별번호"));			// 작업분류식별번호
                tempMap.put("ERR_CNTNTS", WBS_IT_LVL_CD);									// 작업분류단계코드
                tempMap.put("ETC_CNTNTS", WBS_IT_LVL_NM);									// 작업분류단계명
                tempMap.put("CALC_EXCP_YN_VAL", depth3Ele.getChildText("작업예정시작일자"));		// 작업예정시작일자
                tempMap.put("GOVSPLY_MTRL_YN_VAL", depth3Ele.getChildText("작업예정종료일자"));	// 작업예정종료일자
                tempMap.put("LOSSCST_APLY_PT", depth3Ele.getChildText("작업예정일수"));			// 작업예정일수
                tempMap.put("STEP1_ORDR_VAL", String.valueOf(lvlNum));						// 레벨수
                tmpDtaList.add(tempMap);
            }
        }

        /////////////////////////////////////////// 공사내역서 //////////////////////////////////////
        rootEle = null;
        depth2Ele = null;
        rootEle = root.getChild("공사내역서");
        if(rootEle == null)throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "XML 에 항목이 없습니다.[공사내역서]"); // csap 보완();

        depth2List = rootEle.getChildren("단일공사내역서");// 단일공사내역서 가져오기
        if(depth2List == null)throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "XML 에 항목이 없습니다.[단일공사내역서]");// csap 보완

        // 등록한 공사와 동일한지 여부 체크
        for(int i = 0; i < depth2List.size(); i++) {
            depth2Ele = (Element) depth2List.get(i);
            depth3Ele = depth2Ele.getChild("단일공사정보");
        }

        // 중기단가산출식 => 중기단가산출중간코드 변환을 위한 객체생성
        Project project = new Project(String.valueOf(System.currentTimeMillis()) + EtcUtil.nullConvert(param.get("USER_ID")));

        // 원가_중기산출전역변수 테이블 세팅
        depth4List = depth3Ele.getChildren("전역변수");
        for(int i = 0; i < depth4List.size(); i++) {
            depth4Ele = (Element) depth4List.get(i);

            tempMap = getNewMapDta(param, rgstrId);
            tempMap.put("TBL_NM", "CST_HMUPC_VAR");
            tempMap.put("TMP_RSCE_UNTY_CD", depth4Ele.getChildText("변수명").toUpperCase());
            tempMap.put("RSCE_SPEC_NM", depth4Ele.getChildText("변수명").toUpperCase());
            tempMap.put("RSCE_QTY", depth4Ele.getChildText("실수값"));
            tmpDtaList.add(tempMap);

            // 중기단산출식 Parser를 위한 값을 저장
            project.addGlobalVariable(depth4Ele.getChildText("변수명").toUpperCase(), depth4Ele.getChildText("실수값"));
        }

        // 내역서 Element 처리
        depth3List = depth2Ele.getChildren("내역서");
        for(int i = 0; i < depth3List.size(); i++) {
            depth3Ele = (Element) depth3List.get(i);

            String CNSTTY_CD = depth3Ele.getChildText("내역서번호");	// 공종코드 = 내역서번호

            // 원가공종종등록
            tempMap = getNewMapDta(param, rgstrId);
            tempMap.put("TBL_NM", "CST_CNSTTY");
            tempMap.put("CNSTTY_CD", CNSTTY_CD);	// 공종코드 = 내역서번호
            tempMap.put("ATTRBT1_NM", depth3Ele.getChildText("공종구분"));
            tempMap.put("ATTRBT2_NM", null);
            tempMap.put("CALC_EXCP_YN_VAL", depth3Ele.getChildText("계산제외"));
            tempMap.put("RSCE_SN_VAL", depth3Ele.getChildText("공종번호"));
            tempMap.put("RSCE_NM", depth3Ele.getChildText("공종명"));
            tempMap.put("MTRL_UPRC", depth3Ele.getChildText("재료비"));
            tempMap.put("LBR_UPRC", depth3Ele.getChildText("노무비"));
            tempMap.put("GNRLEXPNS_UPRC", depth3Ele.getChildText("경비"));
            tempMap.put("RMRK_CNTNTS", depth3Ele.getChildText("비고"));
            tempMap.put("ETC_CNTNTS", depth3Ele.getChildText("기타"));
            tmpCnsttyDtaList.add(tempMap);

            // 세부공종 Element 처리
            depth4List = depth3Ele.getChildren("세부공종");
            for(int j = 0; j < depth4List.size(); j++) {
                depth4Ele = (Element) depth4List.get(j);

                String TMP_RSCE_UNTY_CD = depth4Ele.getChildText("세부공종코드");	// 세부공종코드
                String dsplOrdr = String.valueOf(j + 1);
                Double RSCE_QTY = EtcUtil.zeroConvertDouble(depth4Ele.getChildText("수량"));
                Double wbsTotRsceQty = 0.0;

                tempMap = getNewMapDta(param, rgstrId);
                tempMap.put("CNSTTY_CD", CNSTTY_CD);				// 공종코드
                tempMap.put("TBL_NM", "CST_DTL_CNSTTY");
                tempMap.put("TMP_RSCE_UNTY_CD", TMP_RSCE_UNTY_CD);	// 세부공종코드
                tempMap.put("RSCE_TP_VAL", depth4Ele.getChildText("자원구분"));
                tempMap.put("CALC_EXCP_YN_VAL", depth4Ele.getChildText("계산제외"));
                tempMap.put("RSCE_SN_VAL", depth4Ele.getChildText("세부공종번호"));
                tempMap.put("RSCE_NM", depth4Ele.getChildText("세부공종명"));
                tempMap.put("RSCE_SPEC_NM", depth4Ele.getChildText("규격"));
                tempMap.put("UNIT", depth4Ele.getChildText("단위"));
                tempMap.put("RSCE_QTY", String.valueOf(RSCE_QTY));	// 수량
                tempMap.put("MTRL_UPRC", depth4Ele.getChildText("재료비단가"));
                tempMap.put("LBR_UPRC", depth4Ele.getChildText("노무비단가"));
                tempMap.put("GNRLEXPNS_UPRC", depth4Ele.getChildText("경비단가"));
                tempMap.put("GOVSPLY_MTRL_YN_VAL", depth4Ele.getChildText("관급"));
                tempMap.put("ATTRBT1_NM", "구분없음");
                tempMap.put("ATTRBT2_NM", depth4Ele.getChildText("세부공종구분"));
                tempMap.put("LOSSCST_CD_LIST", depth4Ele.getChildText("손료코드군"));
                tempMap.put("CALC_MLG_VAL", depth4Ele.getChildText("손료계산비목구분"));
                tempMap.put("RSLT_MLG_VAL", depth4Ele.getChildText("손료결과비목구분"));
                tempMap.put("LOSSCST_APLY_PT", depth4Ele.getChildText("손료적용요율"));
                tempMap.put("DPRCTNCST", depth4Ele.getChildText("손료처리자리수"));
                tempMap.put("MLG_VAL", depth4Ele.getChildText("손료처리방법"));
                tempMap.put("RMRK_CNTNTS", depth4Ele.getChildText("비고"));
                tempMap.put("ETC_CNTNTS", depth4Ele.getChildText("기타"));
                tempMap.put("FRGNCRNCY_GNRLEXPNS_UPRC", dsplOrdr);

                // 작업분류수량
                List depth5List = depth4Ele.getChildren("작업분류수량");
                if(depth5List != null && depth5List.size() > 0) {
                    tempMap.put("ERR_CNTNTS", i + "_" + j);	//중복 세부공종 코드 구분을 위함(WBS1_NO)
                }

                for(int k = 0; k < depth5List.size(); k++) {
                    depth5Ele = (Element) depth5List.get(k);
                    String operGrpSn = EtcUtil.nullConvert(depth5Ele.getChildText("작업분류식별번호"));
                    String wbsRsceQty = depth5Ele.getChildText("수량");

                    if(!"".equals(operGrpSn)) {
                        Map wbsMap = getNewMapDta(param, rgstrId);
                        wbsMap.put("TBL_NM", "CST_QDB");										// 원가_QDB
                        wbsMap.put("CNSTTY_CD", CNSTTY_CD);										// 공종코드
                        wbsMap.put("TMP_RSCE_UNTY_CD", TMP_RSCE_UNTY_CD);						// 세부공종코드
                        wbsMap.put("FRGNCRNCY_GNRLEXPNS_UPRC", dsplOrdr);						// 세부공종 순번(세부공종순번 조회용)
                        wbsMap.put("RSCE_SN_VAL", depth5Ele.getChildText("작업분류식별번호"));			// 작업분류식별번호
                        wbsMap.put("RSCE_QTY", wbsRsceQty);										// 수량
                        wbsMap.put("CALC_EXCP_YN_VAL", depth5Ele.getChildText("작업예정시작일자"));	// 작업예정시작일자
                        wbsMap.put("GOVSPLY_MTRL_YN_VAL", depth5Ele.getChildText("작업예정종료일자"));	// 작업예정종료일자
                        wbsMap.put("LOSSCST_APLY_PT", depth5Ele.getChildText("작업예정일수"));		// 작업예정일수
                        wbsMap.put("ATTRBT1_NM", i + "_" + j);									// 중복 세부공종 코드 구분을 위함
                        tmpCnsttyDtaList.add(wbsMap);
                    }

                    wbsTotRsceQty += EtcUtil.zeroConvertDouble(wbsRsceQty);
                }

                // 작업분류수량 합산과 일치하지 않는 경우 0(false), 일치하는 경우 1(true)
                tempMap.put("STEP2_QTY", String.valueOf(wbsTotRsceQty != 0.0 && wbsTotRsceQty != RSCE_QTY ? 0 : 1));
                tmpCnsttyDtaList.add(tempMap);

                // 노임산출근거
                depth5Ele = depth4Ele.getChild("노임산출근거");
                if(depth5Ele != null) {
                    tempMap = getNewMapDta(param, rgstrId);
                    tempMap.put("TBL_NM", "CST_LBR_CALCFRMLA_BASE");					// 원가_노임산출근거
                    tempMap.put("CNSTTY_CD", CNSTTY_CD);								// 공종코드
                    tempMap.put("TMP_RSCE_UNTY_CD", TMP_RSCE_UNTY_CD);					// 세부공종코드
                    tempMap.put("FRGNCRNCY_GNRLEXPNS_UPRC", String.valueOf(dsplOrdr));	// 세부공종 순번(세부공종순번 조회용)
                    tempMap.put("RSCE_QTY", depth5Ele.getChildText("산출수량"));			// 산출수량
                    tempMap.put("STEP2_QTY", depth5Ele.getChildText("노임할증"));			// 노임할증
                    tempMap.put("RMRK_CNTNTS", depth5Ele.getChildText("공량품셈근거"));		// 공량품셈근거
                    tmpCnsttyDtaList.add(tempMap);

                    List depth6List = depth5Ele.getChildren("세부공종");
                    for(int k = 0; k < depth6List.size(); k++) {
                        Element depth6Ele = (Element) depth6List.get(k);

                        tempMap = getNewMapDta(param, rgstrId);
                        tempMap.put("TBL_NM", "CST_LBR_VLM");								// 원가_노임별공량
                        tempMap.put("CNSTTY_CD", CNSTTY_CD);								// 공종코드
                        tempMap.put("TMP_RSCE_UNTY_CD", TMP_RSCE_UNTY_CD);					// 세부공종코드
                        tempMap.put("FRGNCRNCY_GNRLEXPNS_UPRC", String.valueOf(dsplOrdr));	// 세부공종 순번(세부공종순번 조회용)
                        tempMap.put("RSCE_SN_VAL", depth6Ele.getChildText("노무코드"));			// 노무코드
                        tempMap.put("RSCE_QTY", depth6Ele.getChildText("공량"));				// 공량
                        tmpCnsttyDtaList.add(tempMap);
                    }
                }
            }
        }

        // 일위대가 Element 처리
        String ucostcode = "";
        depth3List = depth2Ele.getChildren("일위대가");
        for(int i = 0; i < depth3List.size(); i++) {
            depth3Ele = (Element) depth3List.get(i);
            ucostcode = depth3Ele.getChildText("일위대가코드");

            tempMap = getNewMapDta(param, rgstrId);
            tempMap.put("TBL_NM", "CST_UCOST");
            tempMap.put("CNSTTY_CD", "");
            tempMap.put("TMP_RSCE_UNTY_CD", ucostcode);
            tempMap.put("ATTRBT1_NM", "일위대가");
            tempMap.put("RSCE_SN_VAL", depth3Ele.getChildText("번호"));
            tempMap.put("RSCE_NM", depth3Ele.getChildText("품명"));
            tempMap.put("RSCE_SPEC_NM", depth3Ele.getChildText("규격"));
            tempMap.put("UNIT", depth3Ele.getChildText("단위"));
            tempMap.put("MTRL_UPRC", depth3Ele.getChildText("재료비"));
            tempMap.put("LBR_UPRC", depth3Ele.getChildText("노무비"));
            tempMap.put("GNRLEXPNS_UPRC", depth3Ele.getChildText("경비"));
            tempMap.put("MLG_VAL", depth3Ele.getChildText("비목구분"));
            tempMap.put("RMRK_CNTNTS", depth3Ele.getChildText("비고"));
            tempMap.put("ETC_CNTNTS", depth3Ele.getChildText("기타"));
            tmpDtaList.add(tempMap);

            // 일위대가상세 Element 처리
            depth4List = depth3Ele.getChildren("일위대가상세");
            for(int j = 0; j < depth4List.size(); j++) {
                depth4Ele = (Element) depth4List.get(j);

                tempMap = getNewMapDta(param, rgstrId);
                tempMap.put("TBL_NM", "CST_UCOST_DTL");
                tempMap.put("CNSTTY_CD", ucostcode);
                tempMap.put("RSCE_TP_VAL", depth4Ele.getChildText("자원구분"));
                tempMap.put("RSCE_SN_VAL", j + 1);
                tempMap.put("TMP_RSCE_UNTY_CD", depth4Ele.getChildText("자원코드"));
                tempMap.put("CALC_EXCP_YN_VAL", depth4Ele.getChildText("계산제외"));
                tempMap.put("RSCE_NM", depth4Ele.getChildText("품명"));
                tempMap.put("RSCE_SPEC_NM", depth4Ele.getChildText("규격"));
                tempMap.put("UNIT", depth4Ele.getChildText("단위"));
                tempMap.put("RSCE_QTY", depth4Ele.getChildText("수량"));
                tempMap.put("MTRL_UPRC", depth4Ele.getChildText("재료비단가"));
                tempMap.put("LBR_UPRC", depth4Ele.getChildText("노무비단가"));
                tempMap.put("GNRLEXPNS_UPRC", depth4Ele.getChildText("경비단가"));
                tempMap.put("GOVSPLY_MTRL_YN_VAL", depth4Ele.getChildText("관급"));
                tempMap.put("LOSSCST_CD_LIST", depth4Ele.getChildText("손료코드군"));
                tempMap.put("CALC_MLG_VAL", depth4Ele.getChildText("손료계산비목구분"));
                tempMap.put("RSLT_MLG_VAL", depth4Ele.getChildText("손료결과비목구분"));
                tempMap.put("LOSSCST_APLY_PT", depth4Ele.getChildText("손료적용요율"));
                tempMap.put("RMRK_CNTNTS", depth4Ele.getChildText("비고"));
                tempMap.put("ETC_CNTNTS", depth4Ele.getChildText("기타"));
                tempMap.put("FRGNCRNCY_GNRLEXPNS_UPRC", String.valueOf(j + 1));// 순번
                tmpDtaList.add(tempMap);
            }
        }

        // 중기단가산출 Element 처리
        depth3List = depth2Ele.getChildren("중기단가산출");
        for(int i = 0; i < depth3List.size(); i++) {
            depth3Ele = (Element) depth3List.get(i);

            tempMap = new HashMap<String, Object>();

            // 20241118 UNIT_CNSTWK_NO - CNTRCT_CHG_ID 변환
            //tempMap.put("UNIT_CNSTWK_NO", param.get("UNIT_CNSTWK_NO"));
            tempMap.put("CNTRCT_CHG_ID", param.get("CNTRCT_CHG_ID"));

            tempMap.put("RGSTR_ID", rgstrId);
            tempMap.put("TBL_NM", "CST_HMUPC");
            tempMap.put("CNSTTY_CD", "");
            tempMap.put("TMP_RSCE_UNTY_CD", depth3Ele.getChildText("중기단가산출코드"));
            tempMap.put("RSCE_SN_VAL", depth3Ele.getChildText("번호"));
            tempMap.put("RSCE_NM", depth3Ele.getChildText("품명"));
            tempMap.put("RSCE_SPEC_NM", depth3Ele.getChildText("규격"));
            tempMap.put("UNIT", depth3Ele.getChildText("단위"));
            tempMap.put("MTRL_UPRC", depth3Ele.getChildText("재료비"));
            tempMap.put("LBR_UPRC", depth3Ele.getChildText("노무비"));
            tempMap.put("GNRLEXPNS_UPRC", depth3Ele.getChildText("경비"));
            tempMap.put("MLG_VAL", depth3Ele.getChildText("비목구분"));
            tempMap.put("RMRK_CNTNTS", depth3Ele.getChildText("비고"));
            tempMap.put("ETC_CNTNTS", depth3Ele.getChildText("기타"));
            tempMap.put("HMUP_CALCFRMLA_CNTNTS", depth3Ele.getChildText("중기단가산출식"));
            tmpDtaList.add(tempMap);

            project.addSentence(depth3Ele.getChildText("중기단가산출코드"), new StringBuffer(depth3Ele.getChildText("중기단가산출식")));
        }

        int tempNo;
        String tempStr;
        Parser parser = new Parser(project, LOGGER.isDebugEnabled());
        Document doc = (Document) parser.parse();
        Map<String, Object> midTmpMap = null;

        Element element = doc.getRootElement();
        List<Element> midEl = element.getChildren("중기단가산출중간수식목록");
        for(Element el : midEl) {
            tempMap = new HashMap<String, Object>();

            // 20241118 UNIT_CNSTWK_NO - CNTRCT_CHG_ID 변환
            //tempMap.put("UNIT_CNSTWK_NO", param.get("UNIT_CNSTWK_NO"));
            tempMap.put("CNTRCT_CHG_ID", param.get("CNTRCT_CHG_ID"));

            tempMap.put("RGSTR_ID", rgstrId);
            tempMap.put("TBL_NM", "CST_HMUPC_MID_CD");
            tempMap.put("CNSTTY_CD", el.getChild("중기단가산출중간수식").getChildText("중기단가산출코드"));

            for(int j = 0; j < arrMLEStr.length; j++) {
                Element MLE = el.getChild("중기단가산출중간수식").getChild(arrMLEStr[j]);
                if(MLE != null) {
                    List<Element> hdList = MLE.getChildren("할당구문");
                    if(hdList != null && hdList.size() > 0) {
                        for(Element hd : hdList) {
                            List<Element> hdssElList = hd.getChild("할당수식목록").getChildren("할당수식");
                            for(Element hdssEl : hdssElList) {
                                midTmpMap = new HashMap<String, Object>();
                                midTmpMap.putAll(tempMap);

                                midTmpMap.put("MLG_VAL", arrMLEStr[j].substring(0, 1));				// 비목구분
                                midTmpMap.put("TMP_RSCE_UNTY_CD", hdssEl.getChildText("참조항목코드"));	// 자원코드
                                midTmpMap.put("RSCE_SN_VAL", hdssEl.getChildText("참조금액레벨"));		// 계산레벨

                                tempNo = 1;

                                // 계정
                                String typeMLE = hdssEl.getChildText("참조금액계정");
                                List<Element> cjbhElList = hdssEl.getChild("참조번호목록").getChildren();
                                for(Element cjbhEl : cjbhElList) {
                                    midTmpMap.put("STEP" + tempNo + "_ORDR_VAL", cjbhEl.getText());
                                    tempNo++;
                                }

                                tempNo = 1;

                                List<Element> yggsElList = hdssEl.getChild("연관계수목록").getChildren();
                                for(Element yggsEl : yggsElList) {
                                    if(tempNo == 1) {
                                        // 초기화 하고 넣자
                                        midTmpMap.put("MTRL_QTY", "0");
                                        midTmpMap.put("LBR_QTY", "0");
                                        midTmpMap.put("GNRLEXPNS_QTY", "0");

                                        tempStr = ObjectUtils.isEmpty(yggsEl.getText()) ? "0" : yggsEl.getText();

                                        if("MA".equals(typeMLE)) {
                                            midTmpMap.put("MTRL_QTY", tempStr);
                                        } else if("LA".equals(typeMLE)) {
                                            midTmpMap.put("LBR_QTY", tempStr);
                                        } else if("EQ".equals(typeMLE)) {
                                            midTmpMap.put("GNRLEXPNS_QTY", tempStr);
                                        }
                                    } else {
                                        midTmpMap.put("STEP" + tempNo + "_QTY", yggsEl.getText());
                                    }

                                    tempNo++;
                                }

                                tmpMidDtaList.add(midTmpMap);
                            }
                        }
                    }
                }
            }
        }

        // 변환된 에러코드 저장
        if(parser.errorcount > 0) {
            for(ParserExceptionInformation pi : parser.errors) {
                try {
                    tempMap = new HashMap<String, Object>();

                    // 20241118 UNIT_CNSTWK_NO - CNTRCT_CHG_ID 변환
                    //tempMap.put("UNIT_CNSTWK_NO", param.get("UNIT_CNSTWK_NO"));
                    tempMap.put("CNTRCT_CHG_ID", param.get("CNTRCT_CHG_ID"));

                    tempMap.put("TBL_NM", "CST_HMUPC_MID_CD");
                    tempMap.put("RGSTR_ID", rgstrId);
                    tempMap.put("ATTRBT1_NM", "PARSER ERROR");
                    tempMap.put("CNSTTY_CD", pi.sentencecode);
                    tempMap.put("TMP_RSCE_UNTY_CD", pi.errorcode);
                    tempMap.put("ETC_CNTNTS", "산출수식 오류");
                    tempMap.put("ERR_CNTNTS", pi.message);
                    tmpDtaList.add(tempMap);

                    if(LOGGER.isDebugEnabled()) {
                        LOGGER.debug("********debugParser**********" + pi.toString());
                    }
                } catch(GaiaBizException e) {
                    if(LOGGER.isDebugEnabled()) {
                        LOGGER.debug("********debugParser 에러코드저장 수행중 문제 **********");
                    }
                }
            }
        }

        // 기계경비 Element 처리
        depth3List = depth2Ele.getChildren("기계경비");
        for(int i = 0; i < depth3List.size(); i++) {
            depth3Ele = (Element) depth3List.get(i);

            tempMap = new HashMap<String, Object>();

            // 20241118 UNIT_CNSTWK_NO - CNTRCT_CHG_ID 변환
            //tempMap.put("UNIT_CNSTWK_NO", param.get("UNIT_CNSTWK_NO"));
            tempMap.put("CNTRCT_CHG_ID", param.get("CNTRCT_CHG_ID"));

            tempMap.put("RGSTR_ID", rgstrId);
            tempMap.put("TBL_NM", "CST_MCHNE");
            tempMap.put("CNSTTY_CD", "");
            tempMap.put("TMP_RSCE_UNTY_CD", depth3Ele.getChildText("기계경비코드"));
            tempMap.put("ATTRBT1_NM", depth3Ele.getChildText("작업시간"));
            tempMap.put("ATTRBT2_NM", depth3Ele.getChildText("작업장소"));
            tempMap.put("RSCE_SN_VAL", depth3Ele.getChildText("번호"));
            tempMap.put("RSCE_NM", depth3Ele.getChildText("품명"));
            tempMap.put("RSCE_SPEC_NM", depth3Ele.getChildText("규격"));
            tempMap.put("UNIT", depth3Ele.getChildText("단위"));
            tempMap.put("MTRL_UPRC", depth3Ele.getChildText("재료비"));
            tempMap.put("LBR_UPRC", depth3Ele.getChildText("노무비"));
            tempMap.put("GNRLEXPNS_UPRC", depth3Ele.getChildText("경비"));
            tempMap.put("RMRK_CNTNTS", depth3Ele.getChildText("비고"));
            tempMap.put("ETC_CNTNTS", depth3Ele.getChildText("기타"));
            tmpDtaList.add(tempMap);

            // 기계경비상세 Element 처리
            depth4List = depth3Ele.getChildren("기계경비상세");
            for(int j = 0; j < depth4List.size(); j++) {
                depth4Ele = (Element) depth4List.get(j);

                tempMap = new HashMap<String, Object>();

                // 20241118 UNIT_CNSTWK_NO - CNTRCT_CHG_ID 변환
                //tempMap.put("UNIT_CNSTWK_NO", param.get("UNIT_CNSTWK_NO"));
                tempMap.put("CNTRCT_CHG_ID", param.get("CNTRCT_CHG_ID"));

                tempMap.put("RGSTR_ID", rgstrId);
                tempMap.put("TBL_NM", "CST_MCHNE_DTL");
                tempMap.put("CNSTTY_CD", depth3Ele.getChildText("기계경비코드"));
                tempMap.put("RSCE_TP_VAL", depth4Ele.getChildText("자원구분"));
                tempMap.put("RSCE_SN_VAL", j + 1);
                tempMap.put("TMP_RSCE_UNTY_CD", depth4Ele.getChildText("자원코드"));
                tempMap.put("CALC_EXCP_YN_VAL", depth4Ele.getChildText("계산제외"));
                tempMap.put("RSCE_NM", depth4Ele.getChildText("품명"));
                tempMap.put("RSCE_SPEC_NM", depth4Ele.getChildText("규격"));
                tempMap.put("UNIT", depth4Ele.getChildText("단위"));
                tempMap.put("RSCE_QTY", depth4Ele.getChildText("수량"));
                tempMap.put("MTRL_UPRC", depth4Ele.getChildText("재료비단가"));
                tempMap.put("LBR_UPRC", depth4Ele.getChildText("노무비단가"));
                tempMap.put("GNRLEXPNS_UPRC", depth4Ele.getChildText("경비단가"));
                tempMap.put("GOVSPLY_MTRL_YN_VAL", depth4Ele.getChildText("관급"));
                tempMap.put("LOSSCST_CD_LIST", depth4Ele.getChildText("손료코드군"));
                tempMap.put("CALC_MLG_VAL", depth4Ele.getChildText("손료계산비목구분"));
                tempMap.put("RSLT_MLG_VAL", depth4Ele.getChildText("손료결과비목구분"));
                tempMap.put("LOSSCST_APLY_PT", depth4Ele.getChildText("손료적용요율"));
                tempMap.put("DPRCTNCST", depth4Ele.getChildText("상각비할증"));
                tempMap.put("RPRCST", depth4Ele.getChildText("정비비할증"));
                tempMap.put("MNGCST", depth4Ele.getChildText("관리비할증"));
                tempMap.put("RMRK_CNTNTS", depth4Ele.getChildText("비고"));
                tempMap.put("ETC_CNTNTS", depth4Ele.getChildText("기타"));
                tempMap.put("FRGNCRNCY_GNRLEXPNS_UPRC", String.valueOf(j + 1));// 순번
                tmpDtaList.add(tempMap);
            }
        }

        // 자재 Element 처리
        depth3List = depth2Ele.getChildren("자재");
        for(int i = 0; i < depth3List.size(); i++) {
            depth3Ele = (Element) depth3List.get(i);

            tempMap = new HashMap<String, Object>();

            // 20241118 UNIT_CNSTWK_NO - CNTRCT_CHG_ID 변환
            //tempMap.put("UNIT_CNSTWK_NO", param.get("UNIT_CNSTWK_NO"));
            tempMap.put("CNTRCT_CHG_ID", param.get("CNTRCT_CHG_ID"));

            tempMap.put("RGSTR_ID", rgstrId);
            tempMap.put("TBL_NM", "CST_MTRL");
            tempMap.put("CNSTTY_CD", "");
            tempMap.put("TMP_RSCE_UNTY_CD", depth3Ele.getChildText("자재코드"));
            tempMap.put("RSCE_NM", depth3Ele.getChildText("품명"));
            tempMap.put("RSCE_SPEC_NM", depth3Ele.getChildText("규격"));
            tempMap.put("UNIT", depth3Ele.getChildText("단위"));
            tempMap.put("MTRL_UPRC", depth3Ele.getChildText("재료비단가"));
            tempMap.put("ATTRBT1_NM", depth3Ele.getChildText("인도조건구분"));
            tempMap.put("RMRK_CNTNTS", depth3Ele.getChildText("비고"));
            tempMap.put("ETC_CNTNTS", depth3Ele.getChildText("기타"));
            tmpDtaList.add(tempMap);

            // 조사처별단가 Element 처리
            depth4List = depth3Ele.getChildren("조사처별단가");
            for(int j = 0; j < depth4List.size(); j++) {
                depth4Ele = (Element) depth4List.get(j);

                tempMap = new HashMap<String, Object>();

                // 20241118 UNIT_CNSTWK_NO - CNTRCT_CHG_ID 변환
                //tempMap.put("UNIT_CNSTWK_NO", param.get("UNIT_CNSTWK_NO"));
                tempMap.put("CNTRCT_CHG_ID", param.get("CNTRCT_CHG_ID"));

                tempMap.put("RGSTR_ID", rgstrId);
                tempMap.put("TBL_NM", "CST_ESTMN_UPRC");
                tempMap.put("CNSTTY_CD", "");
                tempMap.put("TMP_RSCE_UNTY_CD", depth3Ele.getChildText("자재코드"));
                tempMap.put("RSCE_NM", depth4Ele.getChildText("상호"));
                tempMap.put("RSCE_SN_VAL", depth4Ele.getChildText("전화번호"));
                tempMap.put("MTRL_UPRC", depth4Ele.getChildText("재료비단가"));
                tempMap.put("ATTRBT1_NM", depth4Ele.getChildText("인도조건구분"));
                tempMap.put("RMRK_CNTNTS", depth4Ele.getChildText("비고"));
                tmpDtaList.add(tempMap);
            }
        }

        // 노무 Element 처리
        depth3List = depth2Ele.getChildren("노무");
        for(int i = 0; i < depth3List.size(); i++) {
            depth3Ele = (Element) depth3List.get(i);

            tempMap = new HashMap<String, Object>();

            // 20241118 UNIT_CNSTWK_NO - CNTRCT_CHG_ID 변환
            //tempMap.put("UNIT_CNSTWK_NO", param.get("UNIT_CNSTWK_NO"));
            tempMap.put("CNTRCT_CHG_ID", param.get("CNTRCT_CHG_ID"));

            tempMap.put("RGSTR_ID", rgstrId);
            tempMap.put("TBL_NM", "CST_LBR");
            tempMap.put("CNSTTY_CD", "");
            tempMap.put("TMP_RSCE_UNTY_CD", depth3Ele.getChildText("노무코드"));
            tempMap.put("RSCE_NM", depth3Ele.getChildText("품명"));
            tempMap.put("RSCE_SPEC_NM", depth3Ele.getChildText("규격"));
            tempMap.put("UNIT", depth3Ele.getChildText("단위"));
            tempMap.put("LBR_UPRC", depth3Ele.getChildText("노무비단가"));
            tempMap.put("RMRK_CNTNTS", depth3Ele.getChildText("비고"));
            tempMap.put("ETC_CNTNTS", depth3Ele.getChildText("기타"));
            tmpDtaList.add(tempMap);
        }

        // 경비항목 Element 처리
        depth3List = depth2Ele.getChildren("경비항목");
        for(int i = 0; i < depth3List.size(); i++) {
            depth3Ele = (Element) depth3List.get(i);

            tempMap = new HashMap<String, Object>();

            // 20241118 UNIT_CNSTWK_NO - CNTRCT_CHG_ID 변환
            //tempMap.put("UNIT_CNSTWK_NO", param.get("UNIT_CNSTWK_NO"));
            tempMap.put("CNTRCT_CHG_ID", param.get("CNTRCT_CHG_ID"));

            tempMap.put("RGSTR_ID", rgstrId);
            tempMap.put("TBL_NM", "CST_GNRLEXPNS");
            tempMap.put("CNSTTY_CD", "");
            tempMap.put("TMP_RSCE_UNTY_CD", depth3Ele.getChildText("경비항목코드"));
            tempMap.put("ATTRBT2_NM", depth3Ele.getChildText("경비구분"));
            tempMap.put("RSCE_NM", depth3Ele.getChildText("품명"));
            tempMap.put("RSCE_SPEC_NM", depth3Ele.getChildText("규격"));
            tempMap.put("UNIT", depth3Ele.getChildText("단위"));
            tempMap.put("ATTRBT1_NM", depth3Ele.getChildText("통화구분"));
            tempMap.put("MTRL_UPRC", depth3Ele.getChildText("재료비단가"));
            tempMap.put("LBR_UPRC", depth3Ele.getChildText("노무비단가"));
            tempMap.put("GNRLEXPNS_UPRC", depth3Ele.getChildText("경비단가"));
            tempMap.put("FRGNCRNCY_GNRLEXPNS_UPRC", depth3Ele.getChildText("외화경비단가"));
            tempMap.put("DPRCTNCST", depth3Ele.getChildText("상각비계수"));
            tempMap.put("RPRCST", depth3Ele.getChildText("정비비계수"));
            tempMap.put("MNGCST", depth3Ele.getChildText("관리비계수"));
            tempMap.put("RMRK_CNTNTS", depth3Ele.getChildText("비고"));
            tempMap.put("ETC_CNTNTS", depth3Ele.getChildText("기타"));
            tmpDtaList.add(tempMap);

            // 조사처별단가 Element 처리
            depth4List = depth3Ele.getChildren("조사처별단가");
            for(int j = 0; j < depth4List.size(); j++) {
                depth4Ele = (Element) depth4List.get(j);

                tempMap = new HashMap<String, Object>();

                // 20241118 UNIT_CNSTWK_NO - CNTRCT_CHG_ID 변환
                //tempMap.put("UNIT_CNSTWK_NO", param.get("UNIT_CNSTWK_NO"));
                tempMap.put("CNTRCT_CHG_ID", param.get("CNTRCT_CHG_ID"));

                tempMap.put("RGSTR_ID", rgstrId);
                tempMap.put("TBL_NM", "CST_ESTMN_UPRC");
                tempMap.put("CNSTTY_CD", "");
                tempMap.put("TMP_RSCE_UNTY_CD", depth3Ele.getChildText("경비항목코드"));
                tempMap.put("RSCE_NM", depth4Ele.getChildText("상호"));
                tempMap.put("RSCE_SN_VAL", depth4Ele.getChildText("전화번호"));
                tempMap.put("MTRL_UPRC", depth4Ele.getChildText("재료비단가"));
                tempMap.put("LBR_UPRC", depth4Ele.getChildText("노무비단가"));
                tempMap.put("GNRLEXPNS_UPRC", depth4Ele.getChildText("경비단가"));
                tempMap.put("ATTRBT1_NM", depth4Ele.getChildText("인도조건구분"));
                tempMap.put("RMRK_CNTNTS", depth4Ele.getChildText("비고"));
                tmpDtaList.add(tempMap);
            }
        }

        // 실적단가 Element 처리
        depth3List = depth2Ele.getChildren("실적단가");
        for(int i = 0; i < depth3List.size(); i++) {
            depth3Ele = (Element) depth3List.get(i);

            tempMap = new HashMap<String, Object>();

            // 20241118 UNIT_CNSTWK_NO - CNTRCT_CHG_ID 변환
            //tempMap.put("UNIT_CNSTWK_NO", param.get("UNIT_CNSTWK_NO"));
            tempMap.put("CNTRCT_CHG_ID", param.get("CNTRCT_CHG_ID"));

            tempMap.put("RGSTR_ID", rgstrId);
            tempMap.put("TBL_NM", "CST_UCOST");// 실적단가 Element 처리는 "원가_일위대가"테이블명으로 등록
            tempMap.put("CNSTTY_CD", "");
            tempMap.put("ATTRBT1_NM", depth3Ele.getChildText("항목구분"));
            tempMap.put("ATTRBT2_NM", depth3Ele.getChildText("공종구분"));
            tempMap.put("TMP_RSCE_UNTY_CD", depth3Ele.getChildText("실적공사비코드"));
            tempMap.put("RSCE_NM", depth3Ele.getChildText("품명"));
            tempMap.put("RSCE_SPEC_NM", depth3Ele.getChildText("규격"));
            tempMap.put("UNIT", depth3Ele.getChildText("단위"));
            tempMap.put("MTRL_UPRC", depth3Ele.getChildText("재료비단가"));
            tempMap.put("LBR_UPRC", depth3Ele.getChildText("노무비단가"));
            tempMap.put("GNRLEXPNS_UPRC", depth3Ele.getChildText("경비단가"));
            tempMap.put("RMRK_CNTNTS", depth3Ele.getChildText("비고"));
            tempMap.put("ETC_CNTNTS", depth3Ele.getChildText("기타"));
            tmpDtaList.add(tempMap);
        }

        // 공사제경비 Element 처리
        depth3Ele = depth2Ele.getChild("제경비");
        if(depth3Ele != null) {
            tempMap = new HashMap<String, Object>();

            // 20241118 UNIT_CNSTWK_NO - CNTRCT_CHG_ID 변환
            //tempMap.put("UNIT_CNSTWK_NO", param.get("UNIT_CNSTWK_NO"));
            tempMap.put("CNTRCT_CHG_ID", param.get("CNTRCT_CHG_ID"));

            tempMap.put("RGSTR_ID", rgstrId);

            depth4List = depth3Ele.getChildren("제경비항목");
            if(depth4List != null) {
                tempMap.put("TBL_NM", "원가_원가계산서");// 원가_원가계산서 테이블
                tempMap.put("CNSTTY_CD", "");

                for(int i = 0; i < depth4List.size(); i++) {
                    depth4Ele = (Element) depth4List.get(i);

                    tempMap.put("TMP_RSCE_UNTY_CD", depth4Ele.getChildText("세부공종코드"));
                    tempMap.put("CAL_EXCP_YN", depth4Ele.getChildText("집계항목여부"));
                    tempMap.put("RSCE_NM", depth4Ele.getChildText("비목명"));
                    tempMap.put("RSCE_SPEC_NM", depth4Ele.getChildText("적용산식"));
                    tempMap.put("MTRL_UPRC", depth4Ele.getChildText("추가금액"));
                    tempMap.put("LBR_UPRC", depth4Ele.getChildText("산출금액"));
                    tempMap.put("LOSSCST_APLY_PT", depth4Ele.getChildText("적용요율"));
                    tempMap.put("RMRK_CNTNTS", depth4Ele.getChildText("적용산식설명"));
                    tempMap.put("RSCE_SN_VAL", String.valueOf(i + 1));
                    tmpDtaList.add(tempMap);
                }
            }
        }

        return Map.of("tmpCnsttyDtaList", tmpCnsttyDtaList, "tmpDtaList", tmpDtaList, "tmpMidDtaList", tmpMidDtaList);
    }

    /**
     * 신규 Map 생성 및 초기값 설정
     */
    protected Map<String, Object> getNewMapDta(Map<String, Object> param, String rgstrId) {
        Map<String, Object> rtnMap = new HashMap<String, Object>();

        // 20241118 UNIT_CNSTWK_NO - CNTRCT_CHG_ID 변환
        //rtnMap.put("UNIT_CNSTWK_NO", param.get("UNIT_CNSTWK_NO"));
        rtnMap.put("CNTRCT_CHG_ID", param.get("CNTRCT_CHG_ID"));

        rtnMap.put("RGSTR_ID", rgstrId);
        return rtnMap;
    }

    /**
     * 임시정보 테이블 등록을 위한 Map 생성
     */
    private void setTmpDataNoKeyEmptyStr(Map<String, Object> map1) {
        // SEQ_TMP_DTA_SN

        // 20241118 UNIT_CNSTWK_NO - CNTRCT_CHG_ID 변환
        //setKeyIfNoKey(map1, "UNIT_CNSTWK_NO");
        setKeyIfNoKey(map1, "CNTRCT_CHG_ID");
        setKeyIfNoKey(map1, "TBL_NM");
        setKeyIfNoKey(map1, "ATTRBT1_NM");
        setKeyIfNoKey(map1, "ATTRBT2_NM");
        setKeyIfNoKey(map1, "CNSTTY_CD");
        setKeyIfNoKey(map1, "TMP_RSCE_UNTY_CD");
        setKeyIfNoKey(map1, "CALC_EXCP_YN_VAL");
        setKeyIfNoKey(map1, "RSCE_SN_VAL");
        setKeyIfNoKey(map1, "RSCE_NM");
        setKeyIfNoKey(map1, "RSCE_SPEC_NM");
        setKeyIfNoKey(map1, "UNIT");
        setKeyIfNoKey(map1, "RSCE_QTY");
        setKeyIfNoKey(map1, "MTRL_UPRC");
        setKeyIfNoKey(map1, "LBR_UPRC");
        setKeyIfNoKey(map1, "GNRLEXPNS_UPRC");
        setKeyIfNoKey(map1, "FRGNCRNCY_GNRLEXPNS_UPRC");
        setKeyIfNoKey(map1, "RSCE_TP_VAL");
        setKeyIfNoKey(map1, "MLG_VAL");
        setKeyIfNoKey(map1, "GOVSPLY_MTRL_YN_VAL");
        setKeyIfNoKey(map1, "LOSSCST_CD_LIST");
        setKeyIfNoKey(map1, "CALC_MLG_VAL");
        setKeyIfNoKey(map1, "RSLT_MLG_VAL");
        setKeyIfNoKey(map1, "RMRK_CNTNTS");
        setKeyIfNoKey(map1, "HMUP_CALCFRMLA_CNTNTS");
        setKeyIfNoKey(map1, "LOSSCST_APLY_PT");
        setKeyIfNoKey(map1, "MNGCST");
        setKeyIfNoKey(map1, "DPRCTNCST");
        setKeyIfNoKey(map1, "RPRCST");
        setKeyIfNoKey(map1, "ERR_CNTNTS");
        setKeyIfNoKey(map1, "ETC_CNTNTS");
        setKeyIfNoKey(map1, "STEP1_ORDR_VAL");
        setKeyIfNoKey(map1, "STEP2_ORDR_VAL");
        setKeyIfNoKey(map1, "STEP3_ORDR_VAL");
        setKeyIfNoKey(map1, "STEP4_ORDR_VAL");
        setKeyIfNoKey(map1, "STEP5_ORDR_VAL");
        setKeyIfNoKey(map1, "STEP6_ORDR_VAL");
        setKeyIfNoKey(map1, "STEP7_ORDR_VAL");
        setKeyIfNoKey(map1, "STEP8_ORDR_VAL");
        setKeyIfNoKey(map1, "STEP9_ORDR_VAL");
        setKeyIfNoKey(map1, "STEP10_ORDR_VAL");
        setKeyIfNoKey(map1, "MTRL_QTY");
        setKeyIfNoKey(map1, "LBR_QTY");
        setKeyIfNoKey(map1, "GNRLEXPNS_QTY");
        setKeyIfNoKey(map1, "STEP2_QTY");
        setKeyIfNoKey(map1, "STEP3_QTY");
        setKeyIfNoKey(map1, "STEP4_QTY");
        setKeyIfNoKey(map1, "STEP5_QTY");
        setKeyIfNoKey(map1, "STEP6_QTY");
        setKeyIfNoKey(map1, "STEP7_QTY");
        setKeyIfNoKey(map1, "STEP8_QTY");
        setKeyIfNoKey(map1, "STEP9_QTY");
        setKeyIfNoKey(map1, "STEP10_QTY");
        setKeyIfNoKey(map1, "RGSTR_ID");
    }

    /**
     * Map에 키에 해당하는 값이 없는 경우 추가
     */
    void setKeyIfNoKey(Map<String, Object> map1, String key) {
        if(!map1.containsKey(key))
            map1.put(key, "");
    }

    /**
     * 임시테이블에 등록된 C3R 정보를 단위공사로 복사
     */
    private Map<String, Object> moveTmpToRealCnstwk(Element root, Map<String, Object> param) {
        String errMsg = "";

        // FIXME 연관된 다른 테이블 필요정보 DB 반영필요
        // 단위공사정보 등록

        // 20241115 - 현 시스템에서 사용하지 않는 "CST_UNIT_CNSTWK" 작업 수행
//        saveUnitCsntwkInfo(root, param);
//        List<Map<String, Object>> delNoList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getDelUnitCnstwkNoList", param);
//        for(Map<String, Object> map : delNoList) {
//            mybatisSession.delete(DEFAULT_MAPPER_PATH + ".deleteUnitCnstwk", map);
//        }

        String MAJOR_CNSTTY_CD = (String) param.get("MAJOR_CNSTTY_CD");
        if(StringUtils.isEmpty(MAJOR_CNSTTY_CD)) {
            param.put("MAJOR_CNSTTY_CD", "C");
        }

        // 20240210 - 기존에 같은 종류의 공사구분 데이터가 있는지 검사 - 대상: 원가_자원
        Map<String, String> unitParamMap = Map.of(
                "TBL_NM", "CT_RESOURCE",
                "CNTRCT_CHG_ID", param.get("CNTRCT_CHG_ID").toString(),
                "MAJOR_CNSTTY_CD", param.get("MAJOR_CNSTTY_CD").toString()
        );
        Map<String, Object> checkedCnstType =
                mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".checkExistUnitCnstType", unitParamMap);

        // 20240210 - 기존에 같은 종류의 공사구분 있다면 카운트 + 1
        // e.g. A2
        if (!ObjectUtils.isEmpty(checkedCnstType)) {
            int cnt = Integer.parseInt(checkedCnstType.get("cnt").toString()) + 1;
            String updatedMajorCnsttyCd = param.get("MAJOR_CNSTTY_CD").toString() + cnt;
            param.put("MAJOR_CNSTTY_CD", updatedMajorCnsttyCd);
        }

        param.put("CNSTWK_UPJT_APLY_YN", "N");
        param.put("RVW_CMPLT_YN", "N");

        // 2024-12-03 디버깅을 위해 하나의 쿼리 단위 -> 대상별 쿼리로 변경 처리
        //mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertTmpDtaToUnitCnstwkDta", param);
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertTmpDtaToResourceMaterial", param);  // 자원 - 자재
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertTmpDtaToResourceLabor", param);     // 자원 - 노무
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertTmpDtaToResourceEquip", param);     // 자원 - 장비
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertTmpDtaToUnitCost", param);          // 일위대가
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertTmpDtaToMchne", param);             // 일위대가 - 기계경비
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertTmpDtaToHmupc", param);             // 일위대가 - 중기단가산출

        // FOR. 디버깅
//        List<Map<String, Object>> ucostList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCtUnitCost", param);
//        List<Map<String, Object>> ucostDetailList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCtUnitCostDetail2", param);

//        mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getTmpCtCbsDetail", param);

        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertTmpDtaToUnitCbs", param);           // 공종
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertTmpDtaToUnitCbsDetail", param);     // 세부공종
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertTmpDtaToUnitWbsInfo", param);       // 원가 - WBS 정보
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertTmpDtaToUnitWbsOperClss", param);   // 원가 - WBS 작업분류
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertTmpDtaToUnitWbs", param);           // 원가 - WBS


//        mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCtWbsInfo", param);
//        mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCtWbsOperClss", param);
//        mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCtWbs", param);

//        mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCtUnitCostDetail", param);
//        mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCtCbsDetail", param);
//        mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCtResource", param);

        mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateTmpDtaToUnit1", param);              // 경비항목 자원구분코드 업데이트
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateTmpDtaToUnit2", param);              // 경비항목 자원구분코드 업데이트

        // 동일공종/동일내역 오류 항목 QDB 조회

//        List<Map<String, Object>> sameQdbCnsttyList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getSameQdbCnsttyList", param);
//        int i = 1, listCnt = sameQdbCnsttyList.size();
//        for(Map<String, Object> map : sameQdbCnsttyList) {
//            if( i==1 ) errMsg = "동일CBS 다중 물량분개 오류 : <br />";
//
//			errMsg = "동일CBS 다중 물량분개 오류 : <br />  [ 세부공종명 :"+ map.get("DTL_CNSTTY_NM") + " ]  <br />"
//			+  "[ 세부공종코드 :"+ map.get("TMP_RSCE_UNTY_CD") + " ]"
//			+  " 포함 " + EtcUtil.zeroConvertInt(map.get("ERR_CNT")) + " 건";
//
//            errMsg += map.get("TMP_RSCE_UNTY_CD") + "<br/>";
//
//            if( i>=5 || i==listCnt ) {
//                if( listCnt > 5 ) errMsg += "(... 중략 ...)<br/>";
//                errMsg += "----------------------<br/>총 " + listCnt + " 건";
//                break;
//            }
//            i++;
//        }
//        if(!errMsg.isEmpty()) {
//            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "[설계내역서 등록 오류]<br /><br />" + errMsg);
//        }


        /**
         * 20241212 - WBS ~ 세부공종 > QDB
         */

//        mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCtCbs", param);
//        mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCtCbsDetail", param);

        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertTmpDtaToQdbDta", param);

        // 원가_일위대가 손료 수정 -> 20241124_미사용으로 인한 생략
//        int v_loop1 = 2;
//        while(v_loop1 > 0) {
//            param.put("LVL_NUM", v_loop1);
//
//            int updated_cnt = mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateCstUcostLosscst", param);
//            if(updated_cnt > 0) {
//                v_loop1 = v_loop1 + 1;
//            } else {
//                v_loop1 = 0;
//            }
//        }

        // 원가_기계경비 손료 수정 -> 20241124_미사용으로 인한 생략
//        int v_loop2 = 2;
//        while(v_loop2 > 0) {
//            param.put("LVL_NUM", v_loop2);
//
//            int updated_cnt = mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateCstMchneLosscst", param);
//            if(updated_cnt > 0) {
//                v_loop2 = v_loop2 + 1;
//            } else {
//                v_loop2 = 0;
//            }
//        }

        // 원가_세부공종 손료 수정 -> 20241124_미사용으로 인한 생략
//        int v_loop3 = 2;
//        while(v_loop3 > 0) {
//            param.put("LVL_NUM", v_loop3);
//
//            int updated_cnt = mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateCstDtlCnsttyLosscst", param);
//            if(updated_cnt > 0) {
//                v_loop3 = v_loop3 + 1;
//            } else {
//                v_loop3 = 0;
//            }
//        }


        // 일위대가상세 (원가_중기단가산출중간코드) 수정
        // FIXME 1120
//        String updateHmupcMidCdRsce = ".updateHmupcMidCdRsce";
//        mybatisSession.update(updateHmupcMidCdRsce, param);

        // C3R오류검토
        int errCnt = getChkC3rError(param);

        // 20241118 UNIT_CNSTWK_NO - CNTRCT_CHG_ID 변환
        //String no = param.get("UNIT_CNSTWK_NO");
        Object no = param.get("CNTRCT_CHG_ID");
        Map<String, Object> rtnMap = EtcUtil.map(
                "data",
                Map.of("errCnt", errCnt, "unit_no", no)
        );
        return rtnMap;
    }

    /**
     * C3R 오류 확인
     */
    private int getChkC3rError(Map<String, Object> param) {
        String strErrList = "";
        String upCnsttyCd = "";
        StringBuilder strCnsttyCdList = new StringBuilder();
        Map<String, Object> rsceCdMap = new HashMap<String, Object>();
        Map<String, Object> rsceTpRescCdMap = new HashMap<String, Object>();
        List<Map<String, Object>> errList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> calcInfoList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> rsceInfoList = new ArrayList<Map<String, Object>>();

        // 1. 기존 오류정보 삭제 후, 신규 등록
        mybatisSession.delete(DEFAULT_MAPPER_PATH + ".insertC3rCopyErrInfo", param);

        // 2. 공종 오류 확인
        param.put("TBL_NM", "CST_CNSTTY");
        List<Map<String, Object>> cnsttyList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getC3rTblInfoList", param);

        for(Map<String, Object> dtaMap : cnsttyList) {
            String cnstty_cd = EtcUtil.nullConvert(dtaMap.get("cnstty_cd"));
            int cdLen = cnstty_cd.length();

            strErrList = "";

            // 2-1. 공종코드 길이가 2가 아닌 경우(최상위 공종이 아닌 경우)
            if(cdLen != 2) {
                // 2-1-1. 상위 공종코드 생성
                if(cdLen % 2 == 0) {
                    // 	2-1-1-1. 공종코드 길이가 짝수인 경우
                    upCnsttyCd = cnstty_cd.substring(0, cdLen - 2);
                } else {
                    // 2-1-1-2. 공종코드 길이가 짝수가 아닌 경우, 오류 목록 추가 후 상위 공종코드 생성
                    strErrList = strErrList + "," + "020101";	// 공종코드 길이 규칙 오류(짝수X)
                    upCnsttyCd = cnstty_cd.substring(0, cdLen - 1);
                }

                // 2-1-2. 공종코드 길이가 1이 아닌 경우
                if(cdLen != 1) {
                    // 2-1-2-1. 공종코드목록에 상위 공종코드가 존재하지 않는 경우 오류 목록 추가
                    if(strCnsttyCdList.indexOf("," + upCnsttyCd + ",") < 0) {
                        strErrList = strErrList + "," + "020102";	// 공종의 상위공종코드 오류(존재X)
                    }

                    // 2-1-2-2. 하위 공종의 경우 공종구분명이 '일반공종'이 아니면 오류 목록 추가
                    if(!"일반공종".equals(dtaMap.get("attrbt1_nm"))) {
                        strErrList = strErrList + "," + "020103";	// 공종의 공종구분 오류
                    }
                }
            }

            // 2-2. 공종코드 목록 생성(상위코드분터 순차적으로 추가)
            // 20250227 - 정적검사 수정 [Performance Warnings] SBSC_USE_STRINGBUFFER_CONCATENATION
//            strCnsttyCdList = strCnsttyCdList + "," + cnstty_cd + ",";
            strCnsttyCdList.append(",");
            strCnsttyCdList.append(cnstty_cd);
            strCnsttyCdList.append(",");

            // 2-3. 오류 목록이 있는 경우
            if(!"".equals(strErrList.trim())) {
                errList = addErrList(dtaMap, strErrList, errList);
            }
        }

        // 3. 코드 오류 확인
        // 3-1. 표준 준수 오류 확인
        // List<Map<String, Object>> stdRscecList = pckgC3RMapper.getC3rStdLbrRsceInfoList(param);	// 표준 준수여부는 조달발주 요청건만 대상이므로 일단 제외
        List<Map<String, Object>> stdRscecList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getC3rRsceInfoList", param);
        for(Map<String, Object> rsceInfo : stdRscecList) {
            String rsceTpCd = "";
            String tbl_nm = EtcUtil.nullConvert(rsceInfo.get("tbl_nm"));
            String resc_code = EtcUtil.nullConvert(rsceInfo.get("resc_code"));
            int cdLen = resc_code.length();

            strErrList = "";

            // 3-1-1. 자원유형코드 설정
            if("CST_LBR".equals(tbl_nm)) {
                rsceTpCd = "L=";
            } else {
                if("표준시장단가".equals(rsceInfo.get("attrbt1_nm"))) {
                    rsceTpCd = "K=";
                } else {
                    rsceTpCd = "P=";
                }
            }

            // 3-1-2. 코드 길이가 초과한 경우 오류 목록 추가
            if(("CST_UCOST".equals(tbl_nm) && cdLen > CD_UNIN_LEN) || ("CST_LBR".equals(tbl_nm) && cdLen > CD_RSCE_LEN)) {
                strErrList = strErrList + "," + "020302";	// 항목코드 최대 길이 초과 오류
            }

            // 3-1-3. 중복된 코드정보 확인 후 오류 목록 추가
            if(!ObjectUtils.isEmpty(rsceCdMap.get("CD=" + resc_code))) {
                strErrList = strErrList + "," + "020301";	// 자원코드 중복 오류
            } else {
                rsceCdMap.put("CD=" + resc_code, resc_code);
            }

            // 3-1-4. 유형자원코드목록에 해당 자원유형코드 + 자원코드를 추가
            rsceTpRescCdMap.put(rsceTpCd + resc_code, rsceTpCd + resc_code);
/* 표준 준수여부는 조달발주 요청건만 대상이므로 일단 제외
			// 3-1-5. 표준자원코드가 없는 경우 오류 목록 추가
			if("".equals(EtcUtil.nullConvert(rsceInfo.get("STD_RESC_CODE")).trim())) {
				strErrList = strErrList + "," + "020303";	// 표준공사코드 미준수 오류
			}
*/
            // 3-1-6. 오류 목록이 있는 경우
            if(!"".equals(strErrList.trim())) {
                errList = addErrList(rsceInfo, strErrList, errList);
            }
        }

        // 3-2. 기계경비산출정보 오류 확인
        param.put("TBL_NM", "CST_MCHNE_CALC_INFO");
        List<Map<String, Object>> dtaList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getC3rTblInfoList", param);
        for(Map<String, Object> dtaMap : dtaList) {
            String formula = EtcUtil.nullConvert(dtaMap.get("rsce_spec_nm")).trim();
            String resc_code = EtcUtil.nullConvert(dtaMap.get("resc_code"));
            String rsceTpCd = resc_code.substring(0, 1);
            List<Map<String, Object>> existCdList = calcInfoList.stream().filter(it -> resc_code.equals(it.get("resc_code"))).collect(Collectors.toList());

            strErrList = "";

            // 3-2-1. 중복된 항목코드가 있는 경우 오류 목록 추가
            if(!ObjectUtils.isEmpty(existCdList)) {
                strErrList = strErrList + "," + "020601";	// 기계경비 시간당노임산출 항목코드 중복
            } else {
                calcInfoList.add(dtaMap);
            }

            // 3-2-2. 산출식 오류 확인
            if("E".equals(rsceTpCd)) {
                // 3-2-2-1. 시간당손료산출식 계산 실패 시 오류 목록 추가
                if(!"".equals(formula)) {
                    formula = getChgCaratToPower(formula);

                    if("ERROR".equals(getFormulaCalcRslt(formula))) {
                        strErrList = strErrList + "," + "020602";	// 기계경비 시간당손료산출식 오류
                    }
                }
            } else if("H".equals(rsceTpCd)) {
                // 3-2-2-2. 시간당노임산출식 계산 실패 시 오류 목록 추가
                if(!"".equals(formula)) {
                    formula = getChgCaratToPower(formula);

                    if("ERROR".equals(getFormulaCalcRslt(formula))) {
                        strErrList = strErrList + "," + "020603";	// 기계경비 시간당노임산출식 오류
                    }
                }
            } else {
                strErrList = strErrList + "," + "020604";	// 기계경비 산출정보 사용자 정의코드 오류
            }

            // 3-2-3. 오류 목록이 있는 경우
            if(!"".equals(strErrList.trim())) {
                errList = addErrList(dtaMap, strErrList, errList);
            }
        }

        // 3-2-4. 자원코드 오류 확인
        stdRscecList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getC3rStdRsceInfoList", param);
        for(Map<String, Object> rsceInfo : stdRscecList) {
            String rsceTpCd = "";
            String tbl_nm = EtcUtil.nullConvert(rsceInfo.get("tbl_nm"));
            String resc_code = EtcUtil.nullConvert(rsceInfo.get("resc_code"));
            int cdLen = resc_code.length();
            List<Map<String, Object>> existList;

            strErrList = "";

            // 3-2-4-1. 자원유형코드 설정
            switch(tbl_nm) {
                case "CST_UCOST":
                    rsceTpCd = "I=";
                    break;
                case "CST_HMUPC":
                    rsceTpCd = "J=";
                    break;
                case "CST_MCHNE":
                    rsceTpCd = "G=";
                    break;
                case "CST_MTRL":
                    rsceTpCd = "M=";
                    break;
                case "CST_GNRLEXPNS":
                    rsceTpCd = "E=";
                    break;
                default:
                    // 20250227 - 정적검사 수정 SF_SWITCH_NO_DEFAULT
                    log.warn("No matching case found for value :: {}", rsceTpCd);
                    break;
            }

            // 3-2-4-2. 자원코드가 중복된 경우 오류 목록 추가
            if(!ObjectUtils.isEmpty(rsceCdMap.get("CD=" + resc_code))) {
                strErrList = strErrList + "," + "020301";	// 자원코드 중복 오류
            } else {
                if("CST_UCOST|CST_HMUPC|CST_MCHNE".indexOf(tbl_nm) > -1) {
                    Map<String, Object> tMap = Map.of(
                            "SORT_CD", rsceTpCd + resc_code,
                            "TBL_NM", tbl_nm,
                            "CNSTWK_DTA_SN", rsceInfo.get("cnstwk_dta_sn"),
                            "DTL_CNT", rsceInfo.get("std_resc_code")
                    );

                    rsceInfoList.add(tMap);
                }

                rsceCdMap.put("CD=" + resc_code, resc_code);
            }

            // 3-2-4-3. 유형자원코드목록에 해당 자원유형코드 + 자원코드를 추가
            rsceTpRescCdMap.put(rsceTpCd + resc_code, rsceTpCd + resc_code);

            // 3-2-4-4. 코드 최대 길이 오류 여부 확인(일위대가, 중기단가 : 12자리, 자재 : 경비항목 : 16자리, 기계경비 : 17자리)
            if(("CST_UCOST|CST_HMUPC".indexOf(tbl_nm) > -1 && cdLen > CD_UNIN_LEN) || ("CST_MTRL|CST_GNRLEXPNS".indexOf(tbl_nm) > -1 && cdLen > CD_RSCE_LEN)) {
                strErrList = strErrList + "," + "020302";	// 항목코드 최대 길이 초과 오류
            } else if("CST_MCHNE".equals(tbl_nm)) {
                if(cdLen > CD_MCHNE_LEN) {
                    strErrList = strErrList + "," + "020302";	// 항목코드 최대 길이 초과 오류
                }

                existList = calcInfoList.stream().filter(it -> rsceInfo.get("attrbt1_nm").equals(it.get("resc_code"))).collect(Collectors.toList());
                if(ObjectUtils.isEmpty(existList)) {
                    strErrList = strErrList + "," + "020605";	// 기계경비 작업시간 구분 값 오류
                }

                if("".equals(EtcUtil.nullConvert(rsceInfo.get("attrbt2_nm")).trim())) {
                    strErrList = strErrList + "," + "020606";	// 기계경비 작업장소 구분 값 오류 - 빈 값
                } else {
                    existList = calcInfoList.stream().filter(it -> rsceInfo.get("attrbt2_nm").equals(it.get("resc_code"))).collect(Collectors.toList());
                    if(ObjectUtils.isEmpty(existList)) {
                        strErrList = strErrList + "," + "020607";	// 기계경비 작업장소 구분 값 오류 - 정의되지 않음
                    }
                }
            }

            // 3-2-4-5. 오류 목록이 있는 경우
            if(!"".equals(strErrList.trim())) {
                errList = addErrList(rsceInfo, strErrList, errList);
            }
        }

        // 4. 상세정보 오류 확인
        Map<String, Object> chkParam = new HashMap<String, Object>();
        chkParam.putAll(param);
        chkParam.put("cnsttyList", cnsttyList);
        chkParam.put("errList", errList);
        chkParam.put("rsceInfoList", rsceInfoList);
        chkParam.put("rsceCdMap", rsceCdMap);
        chkParam.put("rsceTpRescCdMap", rsceTpRescCdMap);

        // 4-1. 세부공종 오류 확인
        Map<String, Object> rsltMap = getChkTblC3rErrInfo(chkParam, "CST_DTL_CNSTTY");

        // 4-2. 일위대가 상세정보 오류 확인
        chkParam.put("errList", rsltMap.get("errList"));
        chkParam.put("rsceInfoList", rsltMap.get("rsceInfoList"));
        rsltMap = getChkTblC3rErrInfo(chkParam, "CST_UCOST_DTL");

        // 4-3. 기계경비 상세정보 오류 확인
        chkParam.put("errList", rsltMap.get("errList"));
        chkParam.put("rsceInfoList", rsltMap.get("rsceInfoList"));
        rsltMap = getChkTblC3rErrInfo(chkParam, "CST_MCHNE_DTL");

        errList = (List<Map<String, Object>>) rsltMap.get("errList");
        rsceInfoList = (List<Map<String, Object>>) rsltMap.get("rsceInfoList");

        // 4-4. 중기단가산출 중간코드 오류 확인
        param.put("TBL_NM", "CST_HMUPC_MID_CD");
        param.put("searchErrYn", "Y");
        dtaList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getC3rTblInfoList", param);
        for(Map<String, Object> dtaMap : dtaList) {
            if(ObjectUtils.isEmpty(rsceCdMap.get("CD=" + EtcUtil.nullConvert(dtaMap.get("resc_code"))))) {
                errList = addErrList(dtaMap, "020402", errList);	// 등록되지 않은 자원
            }
        }

        // 4-5. 산출 내용 존재 여부 확인
        for(Map<String, Object> rsceInfo : rsceInfoList) {
            if(EtcUtil.zeroConvertInt(rsceInfo.get("dtl_cnt")) <= 0) {
                errList = addErrList(rsceInfo, "020304", errList);	// 산출내용이 없음
            }
        }

        // 5. 중기단가산출 전역변수 중복 오류 확인
        param.put("TBL_NM", "CST_HMUPC_VAR");
        param.put("searchErrYn", "N");
        dtaList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getC3rTblInfoList", param);

        List<Map<String, Object>> errVarList = dtaList.stream().filter(it -> "ERR".equals(it.get("err"))).collect(Collectors.toList());
        for(Map<String, Object> errMap : errVarList) {
            errList = addErrList(errMap, "020404", errList);	// 일위대가-산근 전역변수 중복 오류
        }

        // 6. 순환 참조 오류 확인
        dtaList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getC3rCycleRsceList", param);
        for(Map<String, Object> dtaMap : dtaList) {
            List<Map<String, Object>> tblList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getC3rRsceTblList", dtaMap);
            for(Map<String, Object> tblMap : tblList) {
                errList = addErrList(tblMap, "020901", errList);	// 루핑(순환 참조) 오류
            }
        }

        // 7. 오류정보 등록
        List<Map<String, Object>> regList = new ArrayList<Map<String, Object>>();
        for(int i=0; i < errList.size(); i++) {
            Map<String, Object> errMap = new HashMap<String, Object>();
            errMap.putAll(errList.get(i));
            errMap.put("ERR_CNTNTS", getFrontEndEnterTrim(EtcUtil.nullConvert(errMap.get("ERR_CNTNTS"))));

            regList.add(errMap);

            if(regList.size() == 100 || i == errList.size() - 1) {
                // 20241118 UNIT_CNSTWK_NO - CNTRCT_CHG_ID 변환
                //mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertC3rErrInfo", Map.of("UNIT_CNSTWK_NO", param.get("UNIT_CNSTWK_NO"), "regList", regList));
                mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertC3rErrInfo", Map.of("CNTRCT_CHG_ID", param.get("CNTRCT_CHG_ID"), "regList", regList));

                regList.clear();
            }
        }

        // 8. 오류목록 변수 건수를 반환
        return errList.size();
    }



    /**
     * 오류 목록 추가
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> addErrList(Map<String, Object> param, String strErrList, List<Map<String, Object>> errList) {
        param = EtcUtil.changeToUpperMapKey(param);
        int cnstwk_dta_sn = EtcUtil.zeroConvertInt(param.get("CNSTWK_DTA_SN"));
        String tbl_nm = EtcUtil.nullConvert(param.get("TBL_NM"));
        String sortCd = tbl_nm + "_" + cnstwk_dta_sn + "_";

        List<String> cdList = getCdStringToList(strErrList);
        for(String cdVal : cdList) {
            // 오류 목록에 존재하지 않는 경우 추가하기
            if(ObjectUtils.isEmpty(errList.stream().filter(it -> (sortCd + cdVal).equals(it.get("SORT_CD"))).collect(Collectors.toList()))) {
                errList.add(Map.of("SORT_CD", sortCd + cdVal, "TBL_NM", tbl_nm, "CNSTWK_DTA_SN", cnstwk_dta_sn, "ERR_CD", cdVal, "ERR_CNTNTS", ERR_MSG_INFO.get(cdVal)));
            }
        }

        return errList;
    }

    /**
     * 문자열 to List
     */
    private List<String> getCdStringToList(String strCdList) {
        String cdListVal = strCdList != null ? strCdList.replaceAll(",,", ",").replaceAll(" ", "").trim() : "";
        String[] cdList = cdListVal.split(",");
        List<String> rsltList = new ArrayList<String>();

        // 20250227 - 정적검사 수정 [Performance Warnings] SBSC_USE_STRINGBUFFER_CONCATENATION
        StringBuilder strNewCdList = new StringBuilder();
        for(String cdVal : cdList) {
            if(!cdVal.isEmpty() && strNewCdList.indexOf(cdVal) < 0) {
                if (strNewCdList.length() > 0) {
                    strNewCdList.append(",");
                }
                strNewCdList.append(cdVal);

                rsltList.add(cdVal);
            }
        }

        return rsltList;
    }




    /**
     * 계산식 문자열을 실제 계산식 형태로 변환
     */
    private String getChgCaratToPower(String formula) {
        String newFormula = !"".equals(formula) ? formula.toUpperCase().replaceAll(" ", "") : "";
        String strNum = "";
        String strPower = "";
        String curChar = "";
        int index = !"".equals(newFormula) ? newFormula.indexOf("^") : -1;
        int len = 0, stIndex = -1, edIndex = -1;

        if(index < 0) return newFormula;

        while(index > -1) {
            int paran = 0;

            // 1. ^ 앞에 있는 숫자 얻기
            for(int i=index - 1; i > 0; i--) {
                curChar = newFormula.substring(i, i + 1);
                stIndex = 0;

                if(")".equals(curChar)) {
                    paran++;
                    strNum = curChar + strNum;
                } else if(".0123456789".indexOf(curChar) > -1) {
                    strNum = curChar + strNum;
                } else if("(".equals(curChar)) {
                    paran--;
                    strNum = curChar + strNum;

                    if(paran <= 0) {
                        stIndex = i;
                        break;
                    }
                } else {
                    if(paran <= 0) {
                        if("+".equals(curChar) || "-".equals(curChar)) {
                            strNum = curChar + strNum;
                            stIndex = i;
                        } else {
                            stIndex = i + 1;
                        }

                        break;
                    } else {
                        strNum = curChar + strNum;
                    }
                }
            }

            len = newFormula.length();
            paran = 0;
            edIndex = len - 1;

            // ^ 뒤에 있는 숫자 얻기
            for(int i=index + 1; i < len; i++) {
                curChar = newFormula.substring(i, i + 1);

                if("(".equals(curChar)) {
                    paran++;
                    strPower = curChar + strPower;
                } else if(".0123456789".indexOf(curChar) > -1) {
                    strPower = strPower + curChar;
                } else if(")".equals(curChar)) {
                    paran--;
                    strPower = strPower + curChar;

                    if(paran <= 0) {
                        edIndex = i;
                        break;
                    }
                } else {
                    if(paran <= 0) {
                        if(("+".equals(curChar) || "-".equals(curChar)) && i == index + 1) {
                            strPower = strPower + curChar;
                        } else {
                            edIndex = i - 1;
                            break;
                        }
                    } else {
                        strPower = strPower + curChar;
                    }
                }
            }

            // ^ 앞 뒤에 있는 숫자를 제곱근 함수에 파라미터로 적용
            newFormula = newFormula.substring(0, stIndex) + "Math.pow(" + strNum + ", " + strPower + ")" + newFormula.substring(edIndex + 1, len);
            index = newFormula.indexOf("^");
            strNum = "";
            strPower = "";
        }

        return newFormula;
    }

    /**
     * 계산식 실행하여 결과 생성
     */
    private String getFormulaCalcRslt(String calcFormula) {

//        ScriptEngineManager sEngMng = new ScriptEngineManager();
//        ScriptEngine sEng = sEngMng.getEngineByName("graal.js");

        String result = "";
        String function = "";

        // 내림 함수
        function = function + "function toFloor(num, point) {";
        function = function + "	if(point == null) {";
        function = function + "		point = 0;";
        function = function + "	}";
        function = function + "	var pt = parseInt( '1'.padEnd(point + 1, '0') );";
        function = function + "	return (Math.floor(num * pt) / pt);";
        function = function + "};";

        Engine engine = Engine.newBuilder()
                .option("engine.WarnInterpreterOnly", "false")
                .build();
        Context ctx = Context.newBuilder("js").engine(engine).build();

        calcFormula = calcFormula.replaceAll("MAX", "Math.max").replaceAll("GREATEST", "Math.max");
        calcFormula = calcFormula.replaceAll("MIN", "Math.min").replaceAll("LEAST", "Math.min");
        calcFormula = calcFormula.replaceAll("SANG", "100").replaceAll("KWAN", "100").replaceAll("JUNG", "100");

        if(calcFormula.contains("INT")) {
            calcFormula = function + calcFormula.replaceAll("INT", "toFloor");
        }

//      if (sEng == null) throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "ScriptEngineManager is null"); // csap 보완
        if (ctx == null) throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "ScriptEngineManager is null"); // csap 보완
        Value evaded = ctx.eval("js", calcFormula);
        result = !ObjectUtils.isEmpty(evaded) ? String.valueOf(evaded) : "";

        return result;
    }

    /**
     * 문자열 개행문자 제거
     */
    private String getFrontEndEnterTrim(String cntnts) {
        String newCntnts = cntnts.trim();
        int nPosition = cntnts.length();

        if(nPosition <= 0) {
            return newCntnts;
        }

        for(int i=nPosition - 1; i >= 0; i--) {
            if(cntnts.substring(i, i + 1).equals("\n")) {
                newCntnts = newCntnts.substring(0, nPosition - 1);
            } else {
                break;
            }
        }

        nPosition = -1;
        for(int i=0; i < newCntnts.length(); i++) {
            if(newCntnts.substring(i, i + 1).equals("\n")) {
                nPosition = i;
            } else {
                break;
            }
        }

        return newCntnts.substring(nPosition + 1);
    }

    /**
     * C3R 오류정보 확인
     */
    private Map<String, Object> getChkTblC3rErrInfo(Map<String, Object> param, String pTblNm) {
        Map<String, Object> rsltMap = new HashMap<String, Object>();
        Map<String, Object> losscstMap = new HashMap<String, Object>();
        Map<String, Object> losscstSrcMap = new HashMap<String, Object>();
        Map<String, Object> rsceCdMap = (Map<String, Object>) param.get("rsceCdMap");
        Map<String, Object> rsceTpRescCdMap = (Map<String, Object>) param.get("rsceTpRescCdMap");
        List<Map<String, Object>> cnsttyList = (List<Map<String, Object>>) param.get("cnsttyList");
        List<Map<String, Object>> errList = (List<Map<String, Object>>) param.get("errList");
        List<Map<String, Object>> rsceInfoList = (List<Map<String, Object>>) param.get("rsceInfoList");
        List<Map<String, Object>> losscstCalcList = new ArrayList<Map<String, Object>>();

        // 1. 오류 정보 조회
        param.put("TBL_NM", pTblNm);
        param.put("searchErrYn", "Y");
        List<Map<String, Object>> dtaList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getC3rTblInfoList", param);

        String oldCnsttyCd = "";
        int nCnsttyIdx = 0;
        for(int i=0; i < dtaList.size(); i++) {
            Map<String, Object> dMap = dtaList.get(i);
            Map<String, Object> dtaMap = new HashMap<String, Object>();
            dtaMap.putAll(dMap);

            String cnstty_cd = EtcUtil.nullConvert(dtaMap.get("cnstty_cd")).trim();
            String resc_code = EtcUtil.nullConvert(dtaMap.get("resc_code")).trim();
            String tbl_nm = EtcUtil.nullConvert(dtaMap.get("tbl_nm")).trim();
            String rsce_tp_val = EtcUtil.nullConvert(dtaMap.get("rsce_tp_val")).trim();
            String losscst_cd_list = EtcUtil.nullConvert(dtaMap.get("losscst_cd_list")).trim();

            // 1-1. 이전 공종코드와 현재 공종코드가 다르고, 자원유형명이 '텍스트'가 아닌 경우
            if(!oldCnsttyCd.equals(cnstty_cd) && !"텍스트".equals(rsce_tp_val)) {
                if("CST_DTL_CNSTTY".equals(pTblNm)) {
                    // 1-1-1. 세부공종의 경우, 최초 조회한 공종 목록과 비교
                    for(int j=nCnsttyIdx; j < cnsttyList.size(); j++) {
                        Map<String, Object> cnsttyInfo = cnsttyList.get(j);

                        // 1-1-1-1. 오류정보의 공종코드와 동일하고, 마지막 레벨인 경우, 오류 목록 추가
                        if(cnstty_cd.equals(cnsttyInfo.get("CNSTTY_CD"))) {
                            if("N".equals(cnsttyInfo.get("IS_LEAF_YN"))) {
                                errList = addErrList(cnsttyInfo, "020202", errList);	// 공종구조 오류
                            }

                            break;
                        }
                    }
                } else {
                    // 1-1-2. 세부공종이 아닌 경우
                    String rsceTpCd = "CST_MCHNE_DTL".equals(pTblNm) ? "G=" : ("CST_UCOST_DTL".equals(pTblNm) ? "I=" : "");
                    for(int j=0; j < rsceInfoList.size(); j++) {
                        Map<String, Object> tmpMap = new HashMap<String, Object>();
                        tmpMap.putAll(rsceInfoList.get(j));

                        // 1-1-2-1. 자원 정보 목록의 건수 정보 수정
                        if((rsceTpCd + cnstty_cd).equals(tmpMap.get("sort_cd"))) {
                            tmpMap.put("DTL_CNT", 1);
                            rsceInfoList.set(j, tmpMap);
                            break;
                        }
                    }
                }

                // 1-1-3. 손료 목록이 있는 경우 손료 정보 확인
                if(i > 0 && !ObjectUtils.isEmpty(losscstMap)) {
                    Map<String, Object> pMap = new HashMap<String, Object>();

                    pMap.put("dtaList", dtaList);
                    pMap.put("losscstSrcMap", losscstSrcMap);
                    pMap.put("losscstCalcList", losscstCalcList);
                    pMap.put("cnsttyList", cnsttyList);
                    pMap.put("errList", errList);
                    errList = getChkLosscstErrInfo(i - 1, pMap);
                }

                oldCnsttyCd = cnstty_cd;
            }

            if("텍스트".equals(rsce_tp_val)) {
                // 1-2. 자원유형이 '텍스트'인 경우
                if(!"".equals(resc_code)) {
                    errList = addErrList(dtaMap, "020201", errList);	// 자원구분 : 텍스트 - 코드값 존재 오류 확인
                }
            } else if("손료".equals(rsce_tp_val)) {
                // 1-3. 자원유형이 '손료'인 경우
                // 1-3-1. 손료 오류 확인 후 오류 목록 추가
                if(!"RENT".equals(resc_code.substring(0, 4))) {
                    errList = addErrList(dtaMap, "020501", errList);	// 손료항목 코드규칙 오류
                }

                if(!ObjectUtils.isEmpty(losscstMap.get(cnstty_cd + resc_code))) {
                    errList = addErrList(dtaMap, "020502", errList);	// 손료항목 코드중복 오류
                } else {
                    losscstMap.put("losscstCnt", EtcUtil.zeroConvertInt(losscstMap.get("losscstCnt")) + 1);
                    losscstMap.put(cnstty_cd + resc_code, cnstty_cd + resc_code);
                }

                if(EtcUtil.zeroConvertDouble(dtaMap.get("rsce_qty")) != 1) {
                    errList = addErrList(dtaMap, "020503", errList);	// 손료항목 수량 오류
                }

                if("CST_DTL_CNSTTY".equals(pTblNm) && "".equals(EtcUtil.nullConvert(dtaMap.get("mlg_val")).trim())) {
                    errList = addErrList(dtaMap, "020504", errList);	// 손료항목 소수 처리방법 미정의 오류(내역서)
                }

                if(("," + losscst_cd_list + ",").indexOf("," + resc_code + ",") > -1) {
                    errList = addErrList(dtaMap, "020505", errList);	// 손료 계산 루핑 오류

                    dtaMap.put("LOSSCST_CD_LIST", losscst_cd_list.replaceAll(resc_code, ""));
                    dtaList.set(i, dtaMap);
                }

                if(!chkDecimalLen(EtcUtil.expToBig(dtaMap.get("losscst_aply_pt")), 5)) {
                    errList = addErrList(dtaMap, "020506", errList);	// 손료항목 설정 : 적용요율 소수 자릿수 초과 오류
                }

                // 손료대상 목록 오류 확인 후 오류 목록 추가
                if(!"".equals(losscst_cd_list)) {
                    if(losscst_cd_list.length()> 500) {
                        errList = addErrList(dtaMap, "020509", errList);	// 손료 계산 가능 범위 초과 오류
                    }

                    List<String> cdList = getCdStringToList(losscst_cd_list);
                    for(String cdVal : cdList) {
                        String sortCd = cnstty_cd + "_" + cdVal;
                        List<Map<String, Object>> calcExistList = losscstCalcList.stream().filter(it -> sortCd.equals(it.get("SORT_CD"))).collect(Collectors.toList());

                        if(ObjectUtils.isEmpty(calcExistList)) {
                            Map<String, Object> tMap = Map.of(
                                    "LIST_IDX", losscstCalcList.size() > 0 ? losscstCalcList.size() - 1 : 0,
                                    "SORT_CD", sortCd,
                                    "TBL_NM", tbl_nm,
                                    "CNSTWK_DTA_SN", dtaMap.get("cnstwk_dta_sn"),
                                    "LOSSCST_CD_LIST", "," + cnstty_cd + "_" + resc_code
                            );

                            losscstCalcList.add(tMap);
                        } else {
                            Map<String, Object> tMap = new HashMap<String, Object>();
                            tMap.putAll(calcExistList.get(0));
                            tMap.put("LOSSCST_CD_LIST", tMap.get("losscst_cd_list") + "," + cnstty_cd + "_" + resc_code);

                            losscstCalcList.set(EtcUtil.zeroConvertInt(tMap.get("list_idx")), tMap);
                        }

                        if(ObjectUtils.isEmpty(losscstSrcMap.get(sortCd))) {
                            losscstSrcMap.put(sortCd, sortCd);
                        }
                    }
                }
            } else {
                // 1-4. 그 외 경우
                // 1-4-1. 오류 정보 확인 후 오류 목록 추가
                if("CST_MCHNE_DTL".equals(pTblNm) && "자재|노무|경비항목|기계경비".indexOf(rsce_tp_val) < 0) {
                    errList = addErrList(dtaMap, "020401", errList);	// 기계경비 산출서에 허용 불가 자원
                }

                if(ObjectUtils.isEmpty(rsceCdMap.get("CD=" + resc_code))) {
                    errList = addErrList(dtaMap, "020402", errList);	// 등록되지 않은 자원
                } else {
                    String rsceTpCd;
                    switch(rsce_tp_val) {
                        case "자재":		rsceTpCd = "M="; break;
                        case "노무":		rsceTpCd = "L="; break;
                        case "경비항목":	rsceTpCd = "E="; break;
                        case "일위대가":	rsceTpCd = "I="; break;
                        case "중기단가산출":	rsceTpCd = "J="; break;
                        case "기계경비":	rsceTpCd = "G="; break;
                        case "실적공사비": 	rsceTpCd = "S="; break;
                        case "시장시공가격":	rsceTpCd = "P="; break;
                        case "표준시장단가":	rsceTpCd = "K="; break;
                        default:		rsceTpCd = ""; break;
                    }

                    if(ObjectUtils.isEmpty(rsceTpRescCdMap.get(rsceTpCd + resc_code))) {
                        errList = addErrList(dtaMap, "020403", errList);	// 자원구분 값 오류
                    }
                }

                if(!chkDecimalLen(EtcUtil.expToBig(dtaMap.get("rsce_qty")), ("CST_DTL_CNSTTY".equals(pTblNm) ? 4 : 7))) {
                    errList = addErrList(dtaMap, "020203", errList);	// 내역서 수량 소수 자리 초과
                }

                // 손료대상 목록 오류 확인 후 오류 목록 추가
                if(!"".equals(losscst_cd_list)) {
                    if(losscst_cd_list.length()> 500) {
                        errList = addErrList(dtaMap, "020509", errList);	// 손료 계산 가능 범위 초과 오류
                    } else {
                        List<String> cdList = getCdStringToList(losscst_cd_list);
                        for(String cdVal : cdList) {
                            String sortCd = cnstty_cd + "_" + cdVal;

                            if(ObjectUtils.isEmpty(losscstSrcMap.get(sortCd))) {
                                losscstSrcMap.put(sortCd, sortCd);
                            }
                        }
                    }
                }
            }

            // 1-5. 작업분류 관련 오류 확인 후 오류 목록 추가
            if(!ObjectUtils.isEmpty(dtaMap.get("steq2_qty")) && EtcUtil.zeroConvertDouble(dtaMap.get("steq2_qty")) <= 0) {
                errList = addErrList(dtaMap, "020204", errList);	// 작업분류 수량 확인
            }
        }

        if(!ObjectUtils.isEmpty(losscstMap)) {
            Map<String, Object> pMap = new HashMap<String, Object>();

            pMap.put("losscstSrcMap", losscstSrcMap);
            pMap.put("losscstCalcList", losscstCalcList);
            pMap.put("cnsttyList", cnsttyList);
            pMap.put("errList", errList);
            errList = getChkLosscstErrInfo(EtcUtil.zeroConvertInt(losscstMap.get("losscstCnt")) - 1, pMap);
        }

        rsltMap.put("errList", errList);
        rsltMap.put("rsceInfoList", rsceInfoList);
        return rsltMap;
    }

    /**
     * 손료 오류 정보 확인
     */
    private List<Map<String, Object>> getChkLosscstErrInfo(int edIndex, Map<String, Object> param) {
        Map<String, Object> losscstSrcMap = (Map<String, Object>) param.get("losscstSrcMap");
        List<Map<String, Object>> dtaList = (List<Map<String, Object>>) param.get("dtaList");
        List<Map<String, Object>> errList = (List<Map<String, Object>>) param.get("errList");
        List<Map<String, Object>> losscstCalcList = (List<Map<String, Object>>) param.get("losscstCalcList");

        // 1. 확인 대상 목록이 없는 경우 더 이상 진행 안 함
        if(ObjectUtils.isEmpty(dtaList)) return errList;

        // 2. 확인 대상 목록에서 오류 정보 확인
        String chkCnsttyCd = EtcUtil.nullConvert(dtaList.get(edIndex).get("cnstty_cd"));
        for(int i=edIndex; i>=0; i--) {
            Map<String, Object> dtaMap = dtaList.get(i);
            String cnstty_cd = EtcUtil.nullConvert(dtaMap.get("cnstty_cd"));
            String resc_code = EtcUtil.nullConvert(dtaMap.get("resc_code"));

            // 2-1. 비교 대상 공종코드와 현재 공종코드가 다른 경우 더 이상 진행하지 않음
            if(!chkCnsttyCd.equals(cnstty_cd)) break;

            // 2-2. 자원유형이 '손료'인 항목이 계산 대상에 없는 경우 오류 목록 추가
            if("손료".equals(dtaMap.get("cnstty_cd"))) {
                if(ObjectUtils.isEmpty(losscstSrcMap.get(cnstty_cd + resc_code))) {
                    errList = addErrList(dtaMap, "020507", errList);	// 손료항목 설정 : 손료 계산 대상이 없음
                }
            }
        }

        // 3. 손료 계산 목록에서 오류 정보 확인
        for(Map<String, Object> losscstInfo : losscstCalcList) {
            if(getChkLoopLosscstInfo(EtcUtil.nullConvert(losscstInfo.get("sort_cd")), "", losscstCalcList)) {
                errList = addErrList(losscstInfo, "020508", errList);	// 손료항목 설정 : 손료 계산 루핑(순환 참조) 오류
            }
        }

        return errList;
    }

    /**
     * 손료 순환참조 확인
     */
    private boolean getChkLoopLosscstInfo(String losscst_cd, String parent_path, List<Map<String, Object>> losscstCalcList) {
        List<Map<String, Object>> existList = losscstCalcList.stream().filter(it -> losscst_cd.equals(it.get("SORT_CD"))).collect(Collectors.toList());

        if(!ObjectUtils.isEmpty((existList))) {
            List<String> cdList = getCdStringToList(EtcUtil.nullConvert(existList.get(0).get("losscst_cd_list")));

            for(String cdVal : cdList) {
                if(("," + parent_path + ",").indexOf("," + cdVal + ",") > -1) {
                    return true;
                }

                List<Map<String, Object>> existSubList = losscstCalcList.stream().filter(it -> cdVal.equals(it.get("SORT_CD"))).collect(Collectors.toList());
                if(!ObjectUtils.isEmpty(existSubList)) {
                    return getChkLoopLosscstInfo(cdVal, parent_path + "," + losscst_cd, losscstCalcList);
                }
            }
        }

        return false;
    }

    /**
     * 소수점 길이 확인
     */
    private boolean chkDecimalLen(BigDecimal pVal, int decimalLen)  {
        BigDecimal vVal = pVal.stripTrailingZeros();
        BigDecimal rsltVal = BigDecimal.TEN.pow(decimalLen);

        // 소수점 버린 값과 버리지 않은 값이 같으면 true, 아니면 false
        return vVal.multiply(rsltVal).setScale(0, RoundingMode.DOWN).compareTo(vVal.multiply(rsltVal)) == 0 ? true : false;
    }

    /* ==================================================================================================================
     *
     * EURECA 수신
     *
     * ==================================================================================================================
     */

    /**
     * 유레카 연동 수행 전 초기화
     *
     * 대상 테이블
     * 1. CN_CONTRACT_BID
     * 2. CN_CONTRACT_CALCULATOR
     * 3. CT_CBS
     * 4. CT_CBS_DETAIL
     * 5. CT_CBS_RESOURCE
     * 6. CT_RESOURCE
     * 7. CT_UNIT_COST (제거)
     * 8. CT_UNIT_COST_DETAIL (제거)
     * @param vo
     * @return
     */
    public void clearCostDataBeforeSync (Map<String, Object> vo) {
        mybatisSession.delete(DEFAULT_MAPPER_PATH + ".clearCostDataBeforeSync", vo);
    }

    /**
     * 유레카 - 계약 변경ID 가 파라미터에 없을 경우
     *
     * 총차(최초 - 1회) 에 대한 계약변경 ID 가져오기 에서 최종 계약변경 ID 가져오기로 수정
     * @param vo
     * @return
     */
    public String getLastCntrctChgId(Map<String, Object> vo) {
        return mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".getLastCntrctChgId", vo);
    }

    /**
     * 계약내역서_최초 등록 Fr. Eureca
     * @param voList
     * @return
     */
    public int insertCnContractBidFromEureca(Map<String, Object> voList) {
        int result = mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertCnContractBidFromEureca", voList);
        return result;
    }

    /**
     * 원가계산서_최초 등록 Fr. Eureca
     * @param voList
     * @return
     */
    public int insertCnContractCalculatorFromEureca(Map<String, Object> voList) {
        int result = mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertCnContractCalculatorFromEureca", voList);
        return result;
    }

    /**
     * 총차인 경우,
     * 공종 등록 Fr. Eureca
     * @param voList
     * @return
     */
    public int insertCtCbsFromEureca(Map<String, Object> voList) {
        int result = mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertCtCbsFromEureca", voList);
        return result;
    }

    /**
     * 차수별 인 경우,
     * 공종 업데이트 Fr. Eureca
     * @param voList
     * @return
     */
    public int updateCtCbsFromEureca(Map<String, Object> voList) {
        int result = mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateCtCbsFromEureca", voList);
        return result;
    }

    /**
     * 총차인 경우,
     * 세부공종 등록 Fr. Eureca
     * @param voList
     * @return
     */
    public int insertCtCbsDetailFromEureca(Map<String, Object> voList) {
        int result = mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertCtCbsDetailFromEureca", voList);
        return result;
    }

    /**
     * 차수별 인 경우,
     * 세부공종 업데이트 Fr. Eureca
     * @param voList
     * @return
     */
    public int updateCtCbsDetailFromEureca(Map<String, Object> voList) {
        int result = mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateCtCbsDetailFromEureca", voList);
        return result;
    }

    /**
     * 공종자원 등록 Fr. Eureca
     * @param voList
     * @return
     */
    public int insertCtCbsResourceFromEureca(Map<String, Object> voList) {
        int result = mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertCtCbsResourceFromEureca", voList);
        return result;
    }


    /**
     * 공종자원 수량 변경 Fr.Eureca
     * 단위수량 = 자원수량 / 총 수량
     *  unit_qty = cbsDetail.rsceQty / total_qty
     *
     * @param vo
     * @return
     */
    public int updateCtCbsResourceQtyFromEureca(Map<String, Object> vo) {
        int result = mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateCtCbsResourceQtyFromEureca", vo);
        return result;
    }

    /* ==================================================================================================================
     *
     * Revisiion 관리
     *
     * ==================================================================================================================
     */

    /**
     * 이전 Revision 기준 QDB 존재여부 체크
     * @param vo
     * @return
     */
    public boolean checkQdbExists(Map<String, Object> vo) {
        return mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".checkQdbExists", vo);
    }

    /**
     * 이전 Revision 기준 신규 Revision 작업 수행
     *
     * 입력: PR_QDB, PR_QDB_RESOURCE, PR_ACTIVITY_PLAN
     * 수정: PR_ACTIVITY
     * @param vo
     */
    @Transactional
    public void insertQdbActivityPlanDataWithRevision(Map<String, Object> vo) {

        // 01. PR_QDB, PR_QDB_RESOURCE 생성
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".copyQdbFromBeforeRevision", vo);
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertPrQdbResource", vo);

        // 02. Activity 예상금액 업데이트 (필수)
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateActivityExptCost", vo);


        // 03. (조건) 해당 계약 총 금액이 0보다 클때 수행
        double contractAmt = mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".getCntrctTotalAmt", vo);
        if (contractAmt > 0) {

            List<Map<String, Object>> periodList = EtcUtil.changeToUpperMapList(mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getActivityPlanPeriod", vo));
            String insertActivityPlan = DEFAULT_MAPPER_PATH + ".insertActivityPlan";

            // 04. PR_ACTIVITY_PLAN 삭제 후 생성
            mybatisSession.delete(DEFAULT_MAPPER_PATH + ".deleteActivityPlan", vo);
            List<List<Map<String, Object>>> partedList = ListUtils.partition(periodList, 200);
            for (List<Map<String, Object>> list1 : partedList) {
                for (Map item : list1) {
                    mybatisSession.insert(insertActivityPlan, item);
                }
                mybatisSession.flushStatements();
            }
        }
    }

    /**
     * Revision 관리
     * Activity 초기화 전 참조 QDB_RESOURCE 삭제 처리
     *
     * @param vo
     */
    public void deleteQdbResourceWithRevision(Map<String, Object> vo) {
        mybatisSession.delete(DEFAULT_MAPPER_PATH + ".deletePrQdbResource", vo);
    }

    /**
     * Revision 관리
     * Activity 초기화 전 참조 QDB 삭제 처리
     *
     * @param vo
     */
    public void deleteQdbWithRevision(Map<String, Object> vo) {
        mybatisSession.delete(DEFAULT_MAPPER_PATH + ".deleteC3rToPrQDB", vo);
    }

    /* ==================================================================================================================
     *
     * 계약관리
     *
     * ==================================================================================================================
     */

    /**
     * 계약관리 - 계약변경 추가
     */
    public boolean checkCostDataExists(Map<String, Object> vo) {
        return mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".checkCostDataExists", vo);
    }

    /**
     * 계약관리 - 계약변경 추가
     */
    public boolean checkProcessDataExists(Map<String, Object> vo) {
        return mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".checkProcessDataExists", vo);
    }

    /**
     * 계약관리 - 계약변경 추가
     * 이전 계약정보를 기준으로 원가 및 공정 연관테이블 초기화 (사용여부 'N' 변경)
     * @param vo
     */
    public void initCostAndProcessFromContract(Map<String, Object> vo) {
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".initCostAndProcessFromContract", vo);
    }

    /**
     * 계약관리 - 계약변경 추가
     * 이전 계약정보를 기준으로 원가 데이터 연관테이블 작성
     *
     * 대상
     * CT_CBS
     * CT_CBS_DETAIL
     * CT_CBS_RESOURCE
     * @param vo
     */
    @Transactional
    public void copyCostDataFromContract(Map<String, Object> vo) {
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".copyCostDataFromContract", vo);       // 원가정보 복사
    }

    /**
     * 계약관리 - 계약변경 추가
     * 이전 계약정보를 기준으로 공정 데이터 연관테이블 작성
     *
     * PR_REVISION
     * PR_WBS
     * PR_ACTIVITY
     * PR_QDB
     * PR_QDB_RESOURCE
     * PR_ACTIVITY_PLAN
     * @param vo
     */
    @Transactional
    public void copyProcessDataFromContract(Map<String, Object> vo) {
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".copyProcessDataFromContract", vo);    // 공정정보 복사
    }

}
