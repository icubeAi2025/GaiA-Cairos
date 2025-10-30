package kr.co.ideait.platform.gaiacairos.core.util.excel;

import lombok.AllArgsConstructor;
import lombok.Data;

public interface ExcelUtil {
	@Data
	@AllArgsConstructor
	class ExcelRow {
		String step1;
		String step2;
		String step3;
		int rowIndex;
	}

	@Data
	@AllArgsConstructor
	class MtrlRow {
		String rsceNm;
		double rsceQty;
		double monthlyQuantity;
		String year;
		String month;
		int rowIndex;
	}
}
