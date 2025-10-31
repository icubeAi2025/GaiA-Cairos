package kr.co.ideait.platform.gaiacairos.core.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class FileResource {
	private String diskFilePath;      // 파일 경로
    private String originalFileName;      // 실제 파일명
    private String diskFileName; // 디스크 파일명

    public FileResource(String diskFilePath, String originalFileName, String diskFileName) {
        this.diskFilePath = diskFilePath;
        this.originalFileName = originalFileName;
        this.diskFileName = diskFileName;
    }

}
