package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcStorageMain;

@Repository
public interface DcStorageMainRepository
        extends JpaRepository<DcStorageMain, Integer>, JpaSpecificationExecutor<DcStorageMain>,
        JpaLogicalDeleteable<DcStorageMain> {

                Optional<DcStorageMain> findByDocIdAndDltYn(String docId, String dltYn);

		List<DcStorageMain> findByUpDocId(String docId);

                //문서명 중복여부 체크
		boolean existsByDocNmAndUpDocIdAndNaviIdAndDltYn(String newName, String upDocId, String naviId, String dltYn);

                //복사할 경로 disk_path 조회
                @Query(value = "SELECT DISTINCT doc_disk_path FROM dc_storage_main WHERE doc_id = :upDocId AND navi_id = :naviId AND dlt_yn = :dltYn LIMIT 1", nativeQuery = true)
                Optional<String> findDocDiskPathByDocIdAndNaviIdAndDltYnNative(@Param("upDocId") String upDocId, @Param("naviId") String naviId, @Param("dltYn") String dltYn);

                Optional<DcStorageMain> findByDocNoAndDltYn(Integer docNo, String dltYn);

	        List<DcStorageMain> findAllByDocIdInAndDltYn(List<String> trashDocIdList, String string);

}
