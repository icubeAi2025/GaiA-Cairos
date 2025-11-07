-- DROP PROCEDURE gaia_cmis.sm_department_dc_navigation_set(bpchar, bpchar, bpchar, bpchar, bpchar, bpchar, bpchar);

CREATE OR REPLACE PROCEDURE gaia_cmis.sm_department_dc_navigation_set(p_inserttype character, p_pjttype character, p_pjtno character, p_cntrctno character, p_itemname character, p_itemdesc character, p_corpno character)
 LANGUAGE plpgsql
AS $procedure$

	DECLARE
		i_checkCount integer;
		DECLARE NAVIGATION_DIV_CURSOR record;
	BEGIN
		IF p_insertType = 'ADD' THEN			
			
			IF p_pjtType = 'P' THEN

				INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'PGAIA', 'G'||p_pjtNo, p_itemName, p_itemDesc, 'PG', 1, (SELECT COALESCE(MAX(DSPLY_ORDR)+1, 1) FROM SM_DEPARTMENT WHERE PJT_TYPE = 'PGAIA' AND DEPT_LVL = 1), null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', null, 'N');
				SELECT COUNT(*) INTO i_checkCount FROM SM_DEPARTMENT WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_cntrctNo AND DEPT_ID = 'G'||p_pjtNo;

				IF i_checkCount > 0 THEN

					INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'PGAIA', 'G'||p_pjtNo||'.M1', '관리관', '프로젝트 관리관', 'G'||p_pjtNo, 2, 1, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '07', 'Y');
					INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'PGAIA', 'G'||p_pjtNo||'.M2', '수요기관', '프로젝트 수요기관', 'G'||p_pjtNo, 2, 2, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '10', 'Y');
					INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_pjtNo, p_itemName, p_itemDesc, 'C', 1, (SELECT MAX(DSPLY_ORDR)+1 FROM SM_DEPARTMENT WHERE PJT_TYPE = 'CMIS' AND DEPT_LVL = 1), null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', null, 'N');
				
				ELSE
					RAISE EXCEPTION '[code 01] 프로젝트번호 %의 기본 데이터가 존재하지 않습니다.', p_pjtNo;
				END IF;				

				FOR NAVIGATION_DIV_CURSOR IN
					SELECT CMN_CD FROM SM_COM_CODE WHERE CMN_GRP_CD = 'd4bf74e8-b862-4389-a8f1-8c167b877556'
				LOOP
					INSERT INTO DC_NAVIGATION (NAVI_NO, NAVI_ID, PJT_NO, CNTRCT_NO, NAVI_DIV, NAVI_PATH, NAVI_NM, UP_NAVI_NO, UP_NAVI_ID, NAVI_LEVEL, NAVI_TYPE, NAVI_SHAR_YN, DLT_YN, RGSTR_ID, RGST_DT, CHG_ID, CHG_DT, DLT_ID, DLT_DT) VALUES(NEXTVAL('DC_NAVIGATION_NAVI_NO_SEQ'::REGCLASS), NAVIGATION_DIV_CURSOR.CMN_CD||'_'||p_cntrctNo, p_pjtNo, p_cntrctNo, NAVIGATION_DIV_CURSOR.CMN_CD, p_itemname, p_itemname, 0, '', 0, 'F', '', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null);
				END LOOP;

			ELSE

				SELECT COUNT(*) INTO i_checkCount FROM SM_DEPARTMENT WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_pjtNo AND DEPT_ID = 'C'||p_pjtNo;

				IF i_checkCount > 0 THEN

					INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo, p_itemName, p_itemDesc, 'C'||p_pjtNo, 2, (SELECT COALESCE(MAX(DSPLY_ORDR)+1, 1) FROM SM_DEPARTMENT WHERE PJT_TYPE = 'CMIS' AND DEPT_LVL = 2 and up_dept_id = 'C'||p_pjtNo), null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', null, 'N');
					
					SELECT COUNT(*) INTO i_checkCount FROM SM_DEPARTMENT WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_cntrctNo AND DEPT_ID = 'C'||p_cntrctNo;

					IF i_checkCount > 0 THEN

						INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.m1', '관리관', '계약별 관리관', 'C'||p_cntrctNo, 3, 1, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'N', '07', 'Y');
						INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.m2', '현장소장', '계약별 현장소장', 'C'||p_cntrctNo, 3, 2, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '01', 'Y');
						INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s1', '품질', '품질담당', 'C'||p_cntrctNo||'.m2', 4, 1, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '02', 'Y');
						INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s2', '안전', '안전담당', 'C'||p_cntrctNo||'.m2', 4, 2, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '03', 'Y');
						INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s3', '환경', '환경담당', 'C'||p_cntrctNo||'.m2', 4, 3, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '04', 'Y');
						INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s4', '감리', '감리담당', 'C'||p_cntrctNo||'.m2', 4, 4, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '05', 'Y');
						INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s5', '공무', '공무담당', 'C'||p_cntrctNo||'.m2', 4, 5, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '06', 'Y');
					
					ELSE 
						RAISE EXCEPTION '[code 02] 계약번호 %의 기본 데이터가 존재하지 않습니다.', p_cntrctNo;
					END IF;
				ELSE

					SELECT COUNT(*) INTO i_checkCount FROM SM_DEPARTMENT WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_pjtNo AND DEPT_ID = 'G'||p_pjtNo;

					IF i_checkCount > 0 THEN

						INSERT INTO SM_DEPARTMENT  
        				SELECT (SELECT nextval('sm_department_dept_no_seq'::regclass)), CORP_NO, PJT_NO, CNTRCT_NO, 'CMIS', 'C'||p_pjtNo, DEPT_NM, DEPT_DSCRPT, 'C', DEPT_LVL, (SELECT MAX(DSPLY_ORDR)+1 FROM SM_DEPARTMENT WHERE PJT_TYPE = 'CMIS' AND DEPT_LVL = 1), PSTN_NM, MNG_NM, USE_YN, DLT_YN, RGSTR_ID, now(), CHG_ID, now(), DLT_ID, DLT_DT, DSPLY_YN, SVR_TYPE, DEPT_YN
						FROM SM_DEPARTMENT WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_pjtNo AND  PJT_TYPE = 'PGAIA' AND DEPT_ID = 'G'||p_pjtNo;

						INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo, p_itemName, p_itemDesc, 'C'||p_pjtNo, 2, (SELECT COALESCE(MAX(DSPLY_ORDR)+1, 1) FROM SM_DEPARTMENT WHERE PJT_TYPE = 'CMIS' AND DEPT_LVL = 2 and up_dept_id = 'C'||p_pjtNo), null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', null, 'N');
					
						SELECT COUNT(*) INTO i_checkCount FROM SM_DEPARTMENT WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_cntrctNo AND DEPT_ID = 'C'||p_cntrctNo;
	
						IF i_checkCount > 0 THEN
	
							INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.m1', '관리관', '계약별 관리관', 'C'||p_cntrctNo, 3, 1, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'N', '07', 'Y');
							INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.m2', '현장소장', '계약별 현장소장', 'C'||p_cntrctNo, 3, 2, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '01', 'Y');
							INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s1', '품질', '품질담당', 'C'||p_cntrctNo||'.m2', 4, 1, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '02', 'Y');
							INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s2', '안전', '안전담당', 'C'||p_cntrctNo||'.m2', 4, 2, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '03', 'Y');
							INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s3', '환경', '환경담당', 'C'||p_cntrctNo||'.m2', 4, 3, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '04', 'Y');
							INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s4', '감리', '감리담당', 'C'||p_cntrctNo||'.m2', 4, 4, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '05', 'Y');
							INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s5', '공무', '공무담당', 'C'||p_cntrctNo||'.m2', 4, 5, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '06', 'Y');
						
						ELSE 
							RAISE EXCEPTION '[code 03] 계약번호 %의 기본 데이터가 존재하지 않습니다.', p_cntrctNo;
						END IF;
						

					ELSE
						RAISE EXCEPTION '[code 04] 프로젝트번호 %의 기본 데이터가 존재하지 않습니다.', p_pjtNo;
					END IF;
				END IF;

				FOR NAVIGATION_DIV_CURSOR IN
					SELECT CMN_CD FROM SM_COM_CODE WHERE CMN_GRP_CD = 'd4bf74e8-b862-4389-a8f1-8c167b877556'
				LOOP
					INSERT INTO DC_NAVIGATION (NAVI_NO, NAVI_ID, PJT_NO, CNTRCT_NO, NAVI_DIV, NAVI_PATH, NAVI_NM, UP_NAVI_NO, UP_NAVI_ID, NAVI_LEVEL, NAVI_TYPE, NAVI_SHAR_YN, DLT_YN, RGSTR_ID, RGST_DT, CHG_ID, CHG_DT, DLT_ID, DLT_DT) VALUES(NEXTVAL('DC_NAVIGATION_NAVI_NO_SEQ'::REGCLASS), NAVIGATION_DIV_CURSOR.CMN_CD||'_'||p_cntrctNo, p_pjtNo, p_cntrctNo, NAVIGATION_DIV_CURSOR.CMN_CD, p_itemname, p_itemname, 0, '', 0, 'F', '', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null);
				END LOOP;

			END IF;

		ELSEIF p_insertType = 'UPDATE' THEN
			
			IF p_pjtType = 'P' THEN

				SELECT COUNT(*) INTO i_checkCount FROM SM_DEPARTMENT WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_cntrctNo AND DEPT_ID = 'G'||p_pjtNo;

				IF i_checkCount > 0 THEN

					UPDATE SM_DEPARTMENT SET DEPT_NM = p_itemname, CHG_DT = NOW() WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_cntrctNo and DEPT_LVL = 1;		
				
				ELSE
					INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'PGAIA', 'G'||p_pjtNo, p_itemName, p_itemDesc, 'PG', 1, (SELECT COALESCE(MAX(DSPLY_ORDR)+1, 1) FROM SM_DEPARTMENT WHERE PJT_TYPE = 'PGAIA' AND DEPT_LVL = 1), null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', null, 'N');
					SELECT COUNT(*) INTO i_checkCount FROM SM_DEPARTMENT WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_cntrctNo AND DEPT_ID = 'G'||p_pjtNo;
	
					IF i_checkCount > 0 THEN
	
						INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'PGAIA', 'G'||p_pjtNo||'.M1', '관리관', '프로젝트 관리관', 'G'||p_pjtNo, 2, 1, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '07', 'Y');
						INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'PGAIA', 'G'||p_pjtNo||'.M2', '수요기관', '프로젝트 수요기관', 'G'||p_pjtNo, 2, 2, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '10', 'Y');
						INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_pjtNo, p_itemName, p_itemDesc, 'C', 1, (SELECT MAX(DSPLY_ORDR)+1 FROM SM_DEPARTMENT WHERE PJT_TYPE = 'CMIS' AND DEPT_LVL = 1), null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', null, 'N');
					
					ELSE
						RAISE EXCEPTION '[code 05] 프로젝트번호 %의 기본 데이터가 존재하지 않습니다.', p_pjtNo;
					END IF;

				END IF;
			ELSE
				SELECT COUNT(*) INTO i_checkCount FROM SM_DEPARTMENT WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_cntrctNo AND DEPT_ID = 'C'||p_cntrctNo;

				IF i_checkCount > 0 THEN
					UPDATE SM_DEPARTMENT SET DEPT_NM = p_itemname, CHG_DT = NOW() WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_cntrctNo and DEPT_LVL = 2;		
				ELSE
					
					SELECT COUNT(*) INTO i_checkCount FROM SM_DEPARTMENT WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_pjtNo AND DEPT_ID = 'C'||p_pjtNo;

					IF i_checkCount > 0 THEN
	
						INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo, p_itemName, p_itemDesc, 'C'||p_pjtNo, 2, (SELECT COALESCE(MAX(DSPLY_ORDR)+1, 1) FROM SM_DEPARTMENT WHERE PJT_TYPE = 'CMIS' AND DEPT_LVL = 2 and up_dept_id = 'C'||p_pjtNo), null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', null, 'N');
						
						SELECT COUNT(*) INTO i_checkCount FROM SM_DEPARTMENT WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_cntrctNo AND DEPT_ID = 'C'||p_cntrctNo;
	
						IF i_checkCount > 0 THEN
	
							INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.m1', '관리관', '계약별 관리관', 'C'||p_cntrctNo, 3, 1, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'N', '07', 'Y');
							INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.m2', '현장소장', '계약별 현장소장', 'C'||p_cntrctNo, 3, 2, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '01', 'Y');
							INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s1', '품질', '품질담당', 'C'||p_cntrctNo||'.m2', 4, 1, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '02', 'Y');
							INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s2', '안전', '안전담당', 'C'||p_cntrctNo||'.m2', 4, 2, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '03', 'Y');
							INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s3', '환경', '환경담당', 'C'||p_cntrctNo||'.m2', 4, 3, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '04', 'Y');
							INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s4', '감리', '감리담당', 'C'||p_cntrctNo||'.m2', 4, 4, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '05', 'Y');
							INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s5', '공무', '공무담당', 'C'||p_cntrctNo||'.m2', 4, 5, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '06', 'Y');
						
						ELSE 
							RAISE EXCEPTION '[code 06] 계약번호 %의 기본 데이터가 존재하지 않습니다.', p_cntrctNo;
						END IF;
					ELSE
	
						SELECT COUNT(*) INTO i_checkCount FROM SM_DEPARTMENT WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_pjtNo AND DEPT_ID = 'G'||p_pjtNo;
	
						IF i_checkCount > 0 THEN
	
							INSERT INTO SM_DEPARTMENT  
	        				SELECT (SELECT nextval('sm_department_dept_no_seq'::regclass)), CORP_NO, PJT_NO, CNTRCT_NO, 'CMIS', 'C'||p_pjtNo, DEPT_NM, DEPT_DSCRPT, 'C', DEPT_LVL, (SELECT MAX(DSPLY_ORDR)+1 FROM SM_DEPARTMENT WHERE PJT_TYPE = 'CMIS' AND DEPT_LVL = 1), PSTN_NM, MNG_NM, USE_YN, DLT_YN, RGSTR_ID, now(), CHG_ID, now(), DLT_ID, DLT_DT, DSPLY_YN, SVR_TYPE, DEPT_YN
							FROM SM_DEPARTMENT WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_pjtNo AND  PJT_TYPE = 'PGAIA' AND DEPT_ID = 'G'||p_pjtNo;
	
							INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo, p_itemName, p_itemDesc, 'C'||p_pjtNo, 2, (SELECT COALESCE(MAX(DSPLY_ORDR)+1, 1) FROM SM_DEPARTMENT WHERE PJT_TYPE = 'CMIS' AND DEPT_LVL = 2 and up_dept_id = 'C'||p_pjtNo), null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', null, 'N');
						
							SELECT COUNT(*) INTO i_checkCount FROM SM_DEPARTMENT WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_cntrctNo AND DEPT_ID = 'C'||p_cntrctNo;
		
							IF i_checkCount > 0 THEN
		
								INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.m1', '관리관', '계약별 관리관', 'C'||p_cntrctNo, 3, 1, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'N', '07', 'Y');
								INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.m2', '현장소장', '계약별 현장소장', 'C'||p_cntrctNo, 3, 2, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '01', 'Y');
								INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s1', '품질', '품질담당', 'C'||p_cntrctNo||'.m2', 4, 1, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '02', 'Y');
								INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s2', '안전', '안전담당', 'C'||p_cntrctNo||'.m2', 4, 2, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '03', 'Y');
								INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s3', '환경', '환경담당', 'C'||p_cntrctNo||'.m2', 4, 3, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '04', 'Y');
								INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s4', '감리', '감리담당', 'C'||p_cntrctNo||'.m2', 4, 4, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '05', 'Y');
								INSERT INTO SM_DEPARTMENT VALUES (nextval('sm_department_dept_no_seq'::regclass), p_corpNo, p_pjtNo, p_cntrctNo, 'CMIS', 'C'||p_cntrctNo||'.s5', '공무', '공무담당', 'C'||p_cntrctNo||'.m2', 4, 5, null, null, 'Y', 'N', 'SYSTEM', now(), 'SYSTEM', now(), null, null, 'Y', '06', 'Y');
							
							ELSE 
								RAISE EXCEPTION '[code 07] 계약번호 %의 기본 데이터가 존재하지 않습니다.', p_cntrctNo;
							END IF;
							
	
						ELSE
							RAISE EXCEPTION '[code 08] 프로젝트번호 %의 기본 데이터가 존재하지 않습니다.', p_pjtNo;
						END IF;
					END IF;
				END IF;

			END IF;

			UPDATE DC_NAVIGATION SET NAVI_PATH = p_itemname, NAVI_NM = p_itemname, CHG_DT = NOW() WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_cntrctNo AND NAVI_ID like '%_'||p_cntrctNo;	

		ELSEIF p_insertType = 'DEL' THEN
			
			IF p_pjtType = 'P' THEN
				
				UPDATE SM_DEPARTMENT SET DLT_YN = 'Y', DLT_ID = 'SYSTEM', DLT_DT = NOW() WHERE PJT_NO = p_pjtNo;
			
				UPDATE DC_NAVIGATION SET DLT_YN = 'Y', DLT_ID = 'SYSTEM', DLT_DT = NOW() WHERE PJT_NO = p_pjtNo;
			ELSE
				UPDATE SM_DEPARTMENT SET DLT_YN = 'Y', DLT_ID = 'SYSTEM', DLT_DT = NOW() WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_cntrctNo;

				UPDATE DC_NAVIGATION SET DLT_YN = 'Y', DLT_ID = 'SYSTEM', DLT_DT = NOW() WHERE PJT_NO = p_pjtNo AND CNTRCT_NO = p_cntrctNo;
			END IF;
		END IF;
	END;
$procedure$
;

-- Permissions

ALTER PROCEDURE gaia_cmis.sm_department_dc_navigation_set(bpchar, bpchar, bpchar, bpchar, bpchar, bpchar, bpchar) OWNER TO gaia;
GRANT ALL ON PROCEDURE gaia_cmis.sm_department_dc_navigation_set(bpchar, bpchar, bpchar, bpchar, bpchar, bpchar, bpchar) TO public;
GRANT ALL ON PROCEDURE gaia_cmis.sm_department_dc_navigation_set(bpchar, bpchar, bpchar, bpchar, bpchar, bpchar, bpchar) TO gaia;
