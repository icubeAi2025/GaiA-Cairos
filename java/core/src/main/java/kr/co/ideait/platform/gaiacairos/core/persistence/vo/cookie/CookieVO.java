package kr.co.ideait.platform.gaiacairos.core.persistence.vo.cookie;

import kr.co.ideait.platform.gaiacairos.core.constant.KeyConstants;

public class CookieVO {
	
	// 토큰 정보를 담는 쿠키명
	String tokenCookieName;
	// portal 정보를 담는 쿠키명
    String portalCookieName;
    // 선택 프로젝트 코드를 담는 쿠키명
    String selectCookieName;
    
	public String getTokenCookieName() {
		return tokenCookieName;
	}
	public void setTokenCookieName(String tokenCookieName) {
		this.tokenCookieName = tokenCookieName;
	}
	public String getPortalCookieName() {
		return portalCookieName;
	}
	public void setPortalCookieName(String portalCookieName) {
		this.portalCookieName = portalCookieName;
	}
	public String getSelectCookieName() {
		return selectCookieName;
	}
	public void setSelectCookieName(String selectCookieName) {
		this.selectCookieName = selectCookieName;
	}
    
	public CookieVO(String type) {		
		
		if(type.toUpperCase().equals("GAIA")) {
			this.tokenCookieName 	= KeyConstants.GAIA_X_AUTH_KEY;
			this.portalCookieName	= KeyConstants.PORTAL_GAIA_AUTH_KEY;
			this.selectCookieName	= KeyConstants.GAIA_SELECTED_KEY;
        }else if (type.toUpperCase().equals("PGAIA")) {
        	this.tokenCookieName 	= KeyConstants.PGAIA_X_AUTH_KEY;
        	this.portalCookieName 	= KeyConstants.PORTAL_PGAIA_AUTH_KEY;
			this.selectCookieName	= KeyConstants.PGAIA_SELECTED_KEY;
        }else if (type.toUpperCase().equals("CAIROS")) {
        	this.tokenCookieName 	= KeyConstants.CAIROS_X_AUTH_KEY;
        	this.portalCookieName 	= KeyConstants.PORTAL_CAIROS_AUTH_KEY;
			this.selectCookieName	= KeyConstants.CAIROS_SELECTED_KEY;
        }else {
        	this.tokenCookieName 	= KeyConstants.WBSGEN_X_AUTH_KEY;
        	this.portalCookieName 	= KeyConstants.PORTAL_WBSGEN_AUTH_KEY;
			this.selectCookieName	= KeyConstants.WBSGEN_SELECTED_KEY;
        }
	}

}
