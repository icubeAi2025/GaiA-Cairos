package kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.approval;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApShare;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;
import java.util.List;

public interface ApprovalMybatisParam {

	@Data
    @Alias("approvalListInput")
	public class ApprovalListInput extends MybatisPageable {
		String data;
		String keyword;
		String apUsrId;
		String apLoginId;
		String apDocTitle;
		String apDocTxt;
		String startAppDt;
		String endAppDt;
		String startCmpltDt;
		String endCmpltDt;
		String cmnGrpCdDcsts;
		String cmnGrpCdType;
		String pjtNo; 			   
		String cntrctNo;
		String pjtType;
		List<String> selectedApType;
		String selectedStatus;
		Integer selectedForm;
	}
	
	
    @Data
    @Alias("approvalListOutput")
    public class ApprovalListOutput extends MybatisPageable {
    	int rnum;
	    int apDocNo;
	    String apDocId;
		String pjtNo;
		String cntrctNo;
		String apDocTitle;
		int frmNo;
		String frmId;
		String frmUrl;
		String apRgstId;
		String apAppDt;
		String apCmpltDt;
		String rgstDt;
		String apDocStats;
		String apDocStatsKrn;
		String usrNm;
		String frmNmKrn;
		String upFrmNmKrn;
	    String apUsrId;
	    String apStats;
	    String apDiv;
	    String apTypeKrn;
	    String apType;
    }
    
    @Data
    @Alias("approvalDetailOutput")
    public class ApprovalDetailOutput extends MybatisPageable {
	    int apDocNo;
	    String apDocId;
		String pjtNo;
		String cntrctNo;
		String pjtType;
		String apDocStats;
		String apDocStatsKrn;
		String apUsrId;
		String apDocTitle;
		String apDocEdtr;
		String apDocTxt;
		String usrNm;
		String telNo;
        String phoneNo;
        String emailAdrs;
		String rantgKrn;
		String pstnKrn;
		String frmUrl;
		String apAppDt;
		String apCmpltDt;
		String deptNm;
		Integer frmNo;
		String frmId;
		String recipientNm;
		String uuid;
		String naviId;
		String senderNm;
    }
	
	@Data
	@Alias("approvalShareListOutput")
	public class ApprovalShareListOutput {
		Integer apCnrsNo;
		Integer apDocNo;
		String apDocId;
		String apCnrsDiv;
		String apCnrsRng;
		String apCnrsId;
		String loginId;
		String usrNm;
		int deptNo;
		String deptNm;
	}

    
    @Data
    @Alias("approvalFormListOutput")
    public class ApprovalFormListOutput {
    	int frmNo;
		String pjtNo;
		String cntrctNo;
		String pjtType;
		String frmNmKrn;
		String frmNmEng;
		String upFrmNmKrn;
		String upFrmNmEng;
		int  upFrmNo;
    }

	@Data
    @Alias("apLineUpdate")
    public class ApLineUpdate {
    	Integer apDocNo;
		String apDocId;
		String apUsrId;
		String apStats;
		String apDocStats;
		String apUsrOpnin;
		String apType;
    }

    
	@Data
    public class ApproveListInput {
    	String apStats;
		List<ApLineUpdate> approveDocList;
    }

    
    @Data
    public class ApproveOneInput {
    	String apStats;
    	ApLineUpdate apLine;
		List<ApShare> apShareList;
		List<ApShare> delShareList;
		String pjtNo;
		String cntrctNo;
		String apUsrId;
    }

    
    @Data
    @Alias("nextApprovalInput")
    public class NextApprovalInput {
    	String apDocId;
    	String apUsrId;

    	public NextApprovalInput(String apDocId, String apUsrId) {
    		this.apDocId = apDocId;
    		this.apUsrId = apUsrId;
    	}
    }


    @Data
    @Alias("alarmInput")
    public class AlarmInput {
    	Integer keysn;
    	String knd;
    	String imp;
    	String tarid;
    	String contit;
    	String context;
    	String url;
    	String transtm;
    	String chktm;
    	String useyn;
    	String usrId;
    	String usrIp;
    	LocalDateTime insDat;
    	LocalDateTime uptDat;
    }


    @Data
    @Alias("searchPjtInfo")
    public class SearchPjtInfo {
    	
		String pjtNo;
		String cntrctNo; 
		String pjtNm;
		String cntrctNm;
		
		public SearchPjtInfo(String pjtNo, String cntrctNo) {
			this.pjtNo = pjtNo;
	        this.cntrctNo = cntrctNo;
		}
    }

}


