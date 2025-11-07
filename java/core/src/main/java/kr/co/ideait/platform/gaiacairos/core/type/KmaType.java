package kr.co.ideait.platform.gaiacairos.core.type;

public enum KmaType {

    // 공통코드(코드명, 기상청코드, 위도, 경도, 지역코드)
	KMA15("15", "부산", "159", "129.03203", "35.10468", "11H20201"),
	KMA14("14", "광주", "156", "126.89156", "35.17294", "11F20501"),
	KMA17("17", "제주", "184", "126.52969", "33.51411", "11G00201"),
	KMA2("02", "경기", "119", "126.983", "37.25746", "11B20601"),
	KMA11("11", "경북", "136", "128.70733", "36.57293", "11H10501"),
	KMA12("12", "울산", "152", "129.33469", "35.58237", "11H20101"),
	KMA5("05", "세종", "239", "127.24438", "36.48522", "11C20404"),
	KMA6("06", "충북", "131", "127.44066", "36.63924", "11C10301"),
	KMA3("03", "인천", "112", "126.6249", "37.47772", "11B20201"),
	KMA8("08", "충남", "177", "126.68772", "36.65759", "11C20104"),
	KMA1("01", "서울", "108", "126.9658", "37.57142", "11B10101"),
	KMA13("13", "경남", "155", "128.57282", "35.17019", "11H20301"),
	KMA4("04", "강원", "101", "127.7357", "37.90262", "11D10301"),
	KMA10("10", "대구", "143", "128.65296", "35.87797", "11H10701"),
	KMA9("09", "전북", "146", "127.11718", "35.84092", "11F10201"),
	KMA16("16", "전남", "165", "126.38151", "34.81732", "21F20801"),
	KMA7("07", "대전", "133", "127.3721", "36.37199", "11C20401");

	private String key;          // 코드(회사내 코드)
	private String codeName;     // 코드명
	private String weatherCode;  // 기상청코드
	private String wido;         // 위도
	private String kyngdo;       // 경도
	private String areaCode;     // 지역코드
	   
	KmaType(String key, String codeName, String weatherCode, String wido, String kyngdo, String areaCode) {
		this.key = key;
	    this.codeName = codeName;
	    this.weatherCode = weatherCode;
	    this.wido = wido;
	    this.kyngdo = kyngdo;
	    this.areaCode = areaCode;
	}
	   
	public String getKey() {
		return key;
	}
   
	public String getCodeName() {
		return codeName;
	}
	   
	public String getWeatherCode() {
		return weatherCode;
	}
	   
	public String getWido() {
		return wido;
	}
	   
	public String getKyngdo() {
		return kyngdo;
	}
	   
	public String getAreaCode() {
		return areaCode;
	}

}

