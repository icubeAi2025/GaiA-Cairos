package kr.co.ideait.platform.gaiacairos.core.type;

import java.nio.file.Path;

public enum FileUploadType {

    TEMP("temp"), DOCUMENT("document"), ITEM("item"), EAPPROVAL("eapproval"), ETC("etc"), PROJECT("project"), SYSTEM("system"),
    DailyReport("dailyreport"), Construction("construction"), QualityInspection("qualityinspection"),
    DEFICIENCY("deficiency"), DESIGN("design"), SAFETY("safety"), PERSONAL("personal"), DOCUMENT_FORM("document_form"), INSPECTION_REPORT("inspectionreport"),
    NOTICE("notice"), FAQ("faq"), MONTHLY_REPORT("monthlyreport"), MAINMTRL_REQFRM("mainmtrlreqfrm"), RECYCLE_BIN("recycle-bin");

    String mainPath;

    FileUploadType(String mainPath) {
        this.mainPath = mainPath;
    }

    public String getDirPath(String... subDirs) {
        return Path.of(mainPath, subDirs).toString();
    }

}
