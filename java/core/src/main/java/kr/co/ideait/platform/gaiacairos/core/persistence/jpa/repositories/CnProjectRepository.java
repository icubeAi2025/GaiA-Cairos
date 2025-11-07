package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProject;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CnProjectRepository extends JpaRepository<CnProject, String>, JpaSpecificationExecutor<CnProject>,
                JpaLogicalDeleteable<CnProject> {
        @Query(
                value = """
                        SELECT
                            CASE
                                WHEN MAX_VAL = 0 THEN '001'
                                ELSE LPAD((MAX_VAL+1)::TEXT, 3, '0')
                            END AS MAX_SEQ_STR
                        FROM (
                            SELECT COALESCE(MAX(CAST(SUBSTRING(P.PJT_NO, 8, 3) AS INTEGER)), 0) AS MAX_VAL
                            FROM CN_PROJECT P
                            WHERE P.PJT_NO LIKE CONCAT('P', :yearMonth, '%')
                            AND CAST(SUBSTRING(P.PJT_NO, 8, 3) AS INTEGER) < 500
                        ) AS SUB;
                    """, nativeQuery = true
        )
        String findMaxPgaiaSerialByYearMonth(@Param("yearMonth") String yearMonth);

        @Query(
                value = """
                        SELECT
                            CASE
                                WHEN MAX_VAL = 0 THEN '501'
                                ELSE LPAD((MAX_VAL+1)::TEXT, 3, '0')
                            END AS MAX_SEQ_STR
                        FROM (
                            SELECT COALESCE(MAX(CAST(SUBSTRING(P.PJT_NO, 8, 3) AS INTEGER)), 0) AS MAX_VAL
                            FROM CN_PROJECT P
                            WHERE P.PJT_NO LIKE CONCAT('P', :yearMonth, '%')
                            AND CAST(SUBSTRING(P.PJT_NO, 8, 3) AS INTEGER) > 500
                        ) AS SUB;
                    """, nativeQuery = true
        )
        String findMaxGaiaSerialByYearMonth(@Param("yearMonth") String yearMonth);

        String findPjtNoByPjtNm(@Param("pjtNm") String pjtNm);
}
