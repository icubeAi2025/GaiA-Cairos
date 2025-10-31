package kr.co.ideait.platform.gaiacairos.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService extends AbstractGaiaCairosService {

    /**
     *
     * @param path ({@link String}) 파일을 생성할 위치 경로
     * @param fileName ({@link String}) 생성될 파일의 이름
     * @param file ({@link MultipartFile}) 생성될 파일 데이터
     * @return {@link FileMeta} 생성된 파일의 정보를 담고 있는 메타데이터 객체
     * @throws IOException
     */
    private FileMeta generate(String path, String fileName, MultipartFile file) throws IOException {
        Path dirPath = Path.of(path);
        Path filePath = Path.of(path, fileName);
        Files.createDirectories(dirPath);
        file.transferTo(filePath);

        FileMeta fileMeta = new FileMeta();
        fileMeta.setOriginalFilename(file.getOriginalFilename());
        fileMeta.setFileName(fileName);
        fileMeta.setDirPath(dirPath.toString().replaceAll("\\\\", "/"));
        fileMeta.setFilePath(filePath.toString().replaceAll("\\\\", "/"));
        fileMeta.setSize((int) file.getSize());

        return fileMeta;
    }

    public FileMeta build(String meta, String transferPath) {
        FileMeta fileMeta = null;

        try {
//            BeanUtils.copyProperties(fileMeta, meta);
            fileMeta = objectMapper.readValue(meta, FileMeta.class);

            fileMeta.setDirPath(transferPath);
            fileMeta.setFilePath(String.format("%s/%s", transferPath, fileMeta.getFileName()));
        } catch (JsonProcessingException e) {
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "build fail", e);
        }

        return fileMeta;
    }
    /**
     *
     * @param subPath ({@linke String}) 저장될 파일의 업무 디렉토리 명
     * @param file ({@link MultipartFile}) 저장될 파일 데이터
     * @return {@link FileMeta} 저장된 파일의 정보를 담고 있는 메타데이터 객체
     */
    public FileMeta save(String subPath, MultipartFile file) {
        try {
            String fileName = Random.id() + getFileExtension(file.getOriginalFilename());

            return generate(String.format("%s%s%s", uploadPath, File.separator, subPath), fileName, file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *
     * @param subPath ({@linke String}) 저장될 파일의 업무 디렉토리 명
     * @param file ({@link MultipartFile}) 저장될 파일 데이터
     * @param specifiedFileName ({@link String}) 지정하여 저장될 파일명
     * @return {@link FileMeta} 저장된 파일의 정보를 담고 있는 메타데이터 객체
     */
    public FileMeta save(String subPath, MultipartFile file, String specifiedFileName) {
        try {
            String fileName = specifiedFileName;
            String extension = getFileExtension(file.getOriginalFilename());
            if(StringUtils.isEmpty(fileName)){
                fileName =  Random.id()+extension;
            }

            return generate(String.format("%s%s%s", uploadPath, File.separator, subPath), fileName, file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     *
     * @param fullPath ({@linke String}) 저장될 파일의 디렉토리 경로
     * @param file ({@link MultipartFile}) 저장될 파일 데이터
     * @return {@link FileMeta} 저장된 파일의 정보를 담고 있는 메타데이터 객체
     */
    public FileMeta saveByFullPath(String fullPath,  MultipartFile file) {
        try {
            String fileName = Random.id() + getFileExtension(file.getOriginalFilename());
            return generate(fullPath, fileName, file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     *
     * @param fullPath ({@linke String}) 저장될 파일의 디렉토리 경로
     * @param file ({@link MultipartFile}) 저장될 파일 데이터
     * @param specifiedFileName ({@link String}) 지정하여 저장될 파일명
     * @return {@link FileMeta} 저장된 파일의 정보를 담고 있는 메타데이터 객체
     */
    public FileMeta saveByFullPath(String fullPath,  MultipartFile file, String specifiedFileName) {
        try {
            String fileName = specifiedFileName;
            String extension = getFileExtension(file.getOriginalFilename());
            if(StringUtils.isEmpty(fileName)){
                fileName =  Random.id()+extension;
            }

            return generate(fullPath, fileName, file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR);
        }
    }

    public Resource getFile(String dirPath, String fileName) {
        Resource result = null;
        try {
            Path filePath = Path.of(dirPath, fileName);
            if(Files.exists(filePath)){
                result = new UrlResource(filePath.toUri());
            }
        } catch (MalformedURLException e) {
            log.error("MalformedURLException : {}",e.getMessage());
        } catch(GaiaBizException e){
            log.error("exception : {}",e.getMessage());
        }
        return result;
    }
    public Resource getFile(String fullPath) {
        Resource result = null;
        try {
            Path filePath = Path.of(fullPath);
            if(Files.exists(filePath)){
                result = new UrlResource(filePath.toUri());
            }
        } catch (MalformedURLException e) {
            log.error("MalformedURLException : {}",e.getMessage());
        } catch(GaiaBizException e){
            log.error("exception : {}",e.getMessage());
        }
        return result;
    }
    public Resource getFile(Path filePath) {
        Resource result = null;
        try {
            if(Files.exists(filePath)){
                result = new UrlResource(filePath.toUri());
            }
        } catch (MalformedURLException e) {
            log.error("MalformedURLException : {}",e.getMessage());
        } catch(GaiaBizException e){
            log.error("exception : {}",e.getMessage());
        }
        return result;
    }

    public String getFileExtension(String fileName) {
        return this.getFileExtension(fileName, true);
    }

    public String getFileExtension(String fileName, boolean hasDot) {
        if (hasDot) {
            if (fileName != null && fileName.contains(".")) {
                return fileName.substring(fileName.lastIndexOf("."));
            }
        } else {
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
                return ""; // 확장자가 없는 경우
            }
            return fileName.substring(dotIndex + 1).toLowerCase();
        }

        return "";
    }

    public FileMeta createFolder(String subPath) {
        try {
            Path dirPath = Path.of(uploadPath, subPath);
            Files.createDirectories(dirPath);

            int lastIndex = dirPath.toString().lastIndexOf("\\");
            String docDiskPath = dirPath.toString().substring(0, lastIndex);

            FileMeta fileMeta = new FileMeta();
            fileMeta.setDirPath(docDiskPath.replaceAll("\\\\", "/"));
            Path dirPathFileName = dirPath.getFileName();
            if(dirPathFileName != null){
                fileMeta.setFileName(dirPathFileName.toString());
                return fileMeta;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new GaiaBizException(e);
        }
        return null;
    }

    public void downloadFilesAsZip(List<FileResource> fileResourceList, HttpServletResponse response)
            throws GaiaBizException {

        if (fileResourceList == null || response == null) {
            throw new GaiaBizException(ErrorType.NOT_FOUND,"File list or response cannot be null.");
        }

        // 응답 헤더 설정: 파일 이름과 콘텐츠 타입
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"documents.zip\"");
        response.setContentType("application/zip");

        // ZIP 파일 생성 및 응답에 직접 쓰기
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
            for (FileResource fileResource : fileResourceList) {
                File file = new File(fileResource.getDiskFilePath(), fileResource.getDiskFileName());

                if (!file.exists()) {
                    log.error("File not found: " + file.getAbsolutePath());
                    continue; // 파일이 없는 경우 해당 파일은 패스.
                }

                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    String docNm = fileResource.getOriginalFileName();

                    // 경로 탐색 및 특수문자 검증: "..", "/", "\" 등이 포함되면 예외를 발생.
                    if (docNm.contains("..") || docNm.contains("/") || docNm.contains("\\")) {
                        throw new GaiaBizException(ErrorType.BAD_REQUEST, "Invalid file name.");
                    }

                    ZipEntry zipEntry = new ZipEntry(docNm); // 실제 파일명으로 저장.
                    zipOutputStream.putNextEntry(zipEntry);

                    byte[] buffer = new byte[8192]; //8KB 버퍼
                    int len;
                    while ((len = fileInputStream.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, len);
                    }

                    zipOutputStream.closeEntry();

                } catch (IOException e) {
                    throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "파일을 읽는 중 오류가 발생했습니다: " + file.getName(), e);
                }
            }
        } catch (IOException e) {
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "ZIP 파일을 생성하는 중 오류가 발생했습니다.", e);
        }
    }


    public String createDirPath(String subPath) {
        return Path.of(uploadPath, subPath).toString();
    }

    public File saveExcelToFile(ByteArrayOutputStream baos, String filePath) {
        if (baos == null) {
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "baos is null");
        }
        if (filePath == null || filePath.isBlank()) {
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "filePath is blank");
        }

        File file = new File(filePath);

        // 부모 디렉터리 보장
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "상위 디렉터리를 생성하지 못했습니다: " + parent.getAbsolutePath());
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            baos.writeTo(fos);
        } catch (IOException e) {
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "파일을 생성하는 중 오류가 발생했습니다.", e);
        }
        return file;
    }

    public boolean deleteFile(String dirPath, String fileName) {
        Path filePath = Path.of(dirPath, fileName);
        boolean result = false;
        try {
            result = Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR,"File Delete",e);
        }
        return result;
    }

    public void moveFile(String oldPath, String newPath){
        Path source = Paths.get(oldPath);
        Path target = Paths.get(newPath);

        try {
            if (!Files.exists(source)) {
                throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA,"원본 파일이 존재하지 않습니다: " + oldPath);
            }

            Path targetDir = target.getParent();
            if (targetDir != null && !Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new GaiaBizException(ErrorType.ETC,"@@파일 이동 실패!"+e.getMessage());
        }
    }

    @Data
    public static class FileMeta {
    	@Description(name = "원본 파일명", description = "원본 파일 이름")
        String originalFilename;
    	@Description(name = "파일명", description = "시스템에 저장된 파일의 이름(ex:apple.png)")
        String fileName;
    	@Description(name = "파일경로", description = "시스템에 저장된 파일의 경로(ex:D:\\apple.png)")
        String filePath;
    	@Description(name = "폴더경로", description = "시스템에 저장된 파일의 폴더경로(ex:D:\\)")
        String dirPath;
    	@Description(name = "파일크기", description = "시스템에 저장된 파일의 바이트 크기(ex:1000)")
        Integer size; // TODO: 파일사이즈가 int로 가능할지 확인 필요
        @Description(name = "파일 확장자", description = "jpg, png, txt 등등")
        String ext;
        @Description(name = "파일 액션 상태", description = "C: 추가, D: 삭제")
        String mode;
    }


    public static byte[] fileToByteArrayWithNIO(String filePath) {
        byte[] fileData = null;
        try {
            fileData = Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "Change To Byte Array", e);
        }
        return fileData;
    }





















    // public FileMeta moveFolder(String originalFolderPath, String newFolderPath) {
    //     try {
    //         Path sourcePath = Path.of(originalFolderPath);
    //         Path targetPath = Path.of(newFolderPath);

    //         // 폴더 이동
    //         Files.move(sourcePath, targetPath);

    //         int lastIndex = targetPath.toString().lastIndexOf("\\");
    //         String docDiskPath = targetPath.toString().substring(0, lastIndex);

    //         System.out.println("Folder moved successfully!");

    //         // 이동 후 새로운 경로 정보를 반환
    //         FileMeta folderInfo = new FileMeta();
    //         folderInfo.setDirPath(docDiskPath);
    //         folderInfo.setFileName(targetPath.getFileName().toString());

    //         return folderInfo;
    //     } catch (IOException e) {
    //         System.out.println("Error moving folder: " + e.getMessage());
    //         throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "Internal Server Error");
    //     }
    // }

    // public int copyDocument(String originalFilePath, String copyFilePath, String copyDocDiskPath) {

    //     File originalFile = new File(originalFilePath);
    //     File copyFile = new File(copyFilePath);
    //     File copyFileDir = new File(copyDocDiskPath);

    //     // 복사될 장소의 디렉토리가 존재하지 않으면 만들어준다.
    //     if (!copyFileDir.exists()) {
    //         copyFileDir.mkdirs();
    //     }

    //     try {
    //         // 파일의 내용을 읽어오기위한 준비
    //         FileInputStream fis = new FileInputStream(originalFile);

    //         // 파일의 내용을 쓰기 위한 준비
    //         FileOutputStream fos = new FileOutputStream(copyFile);

    //         // 파일을 읽고 쓰기를 합니다.
    //         int nRealByte = 0;
    //         while ((nRealByte = fis.read()) != -1) {
    //             fos.write(nRealByte);
    //         }

    //         // 파일스트림을 닫아줍니다.
    //         fis.close();
    //         fos.close();

    //     } catch (Exception e) {
    //         // 파일 처리 실패시 -1를 리턴합니다.
    //         System.out.println(e.getLocalizedMessage());
    //         throw new GaiaBizException(ErrorType.NO_DATA, "File not found.");
    //         // return -1;
    //     }
    //     // 성공시에 메세지 출력후 1을 리턴합니다.
    //     System.out.println("copy succeed !!");
    //     return 1;
    // }

    // public int moveDocument(String originalFilePath, String moveFilePath) {
    //     try {
    //         Path sourcePath = Path.of(originalFilePath);
    //         Path targetPath = Path.of(moveFilePath);

    //         // 파일 이동
    //         Files.move(sourcePath, targetPath);

    //         System.out.println("File moved successfully!");
    //         return 1; // 성공 시 1 반환
    //     } catch (IOException e) {
    //         System.out.println("Error moving file: " + e.getMessage());
    //         throw new GaiaBizException(ErrorType.NO_DATA, "File not found.");
    //         // return -1; // 실패 시 -1 반환
    //     }
    // }

    // public void downloadFile(String fileDiskPath, String fileDiskName, HttpServletResponse response) {
    //     try {
    //         // 파일 경로 설정
    //         Path filePath = Path.of(fileDiskPath, fileDiskName);
    //         File file = filePath.toFile();

    //         // 파일이 존재하지 않는 경우 처리
    //         if (!file.exists()) {
    //             response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
    //             return;
    //         }

    //         // 응답 헤더 설정: 파일 이름과 콘텐츠 타입
    //         response.setContentType(Files.probeContentType(filePath));
    //         response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDiskName + "\"");

    //         // 파일을 응답에 직접 쓰기
    //         try (FileInputStream fileInputStream = new FileInputStream(file);
    //                 ServletOutputStream outputStream = response.getOutputStream()) {

    //             byte[] buffer = new byte[1024];
    //             int bytesRead;
    //             while ((bytesRead = fileInputStream.read(buffer)) != -1) {
    //                 outputStream.write(buffer, 0, bytesRead);
    //             }
    //         }
    //     } catch (IOException e) {
    //         log.error("Error downloading file: {}", e.getMessage());
    //         throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR);
    //     }
    // }

    /**
     * InputStream to File
     * @param inputStream
     * @param fileName
     * @return
     * @throws Exception
     */
    public static File convertInputStreamToFile(InputStream inputStream, String fileName) throws IOException {
        if(inputStream == null){
            return null;
        }
        // 임시 파일 생성
        File tempFile = File.createTempFile(fileName, ".tmp");

        // InputStream 데이터를 임시 파일로 복사
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return tempFile; // 변환된 File 객체 반환
    }
}
