package kr.co.ideait.platform.gaiacairos.core.util;

import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class PdfUtil {

//    public static void main(String... args) {
//        boolean result = false;
//
//        List<File> files = List.of(
//            new File("D:/1.pdf")
//            , new File("D:/2.pdf")
//            , new File("D:/3.pdf")
//        );
//
//        System.out.println( PdfUtil.mergeToBytes(files,"D:/merge.pdf") );
//
////        separate(new File("D:/merge.pdf"), 3);
//    }

    public static boolean separate(File file, int page) {
        boolean result = false;

        try {
            PDDocument document = Loader.loadPDF(file);
            int pages = document.getNumberOfPages();

            if (pages > page) {
                separate(file,1,page);
                result = separate(file,page+1,pages);
            }
        } catch (IOException e) {
            result = false;
        }

        return result;
    }

    public static boolean separate(File file, int startPage, int endPage) {
        return separate(file, endPage-startPage + 1, startPage, endPage);
    }

    public static boolean separate(File file, int page, int startPage, int endPage) {
        boolean result = true;

        try {
            PDDocument document = Loader.loadPDF(file);
            Splitter splitter = new Splitter();
            splitter.setStartPage(startPage);
            splitter.setSplitAtPage(page);
            splitter.setEndPage(endPage);

            List<PDDocument> Pages = splitter.split(document);
            Iterator<PDDocument> iterator = Pages.listIterator();

            int i = 1;

            while (iterator.hasNext()) {
                PDDocument pd = iterator.next();
                String folderPath = file.getParent();
                String fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
                int start = startPage + (i-1) * page;
                int end = i * page > endPage ? i * page : endPage;
                pd.save(folderPath+File.separator + fileName + (start + "~" + end) + ".pdf");
            }

            document.close();
        } catch (GaiaBizException | IOException e){
            result = false;
        }

        return result;
    }

    public static File mergeToFile(List<File> files, String path, String fileName) {
        try {
            File dir = new File(path);

            if (!dir.isDirectory()) {
                //RV	Exceptional return value of java.io.File.mkdirs() ignored in kr.co.ideait.platform.gaiacairos.core.util.PdfUtil.mergeToBytes(List, String, String)
                if(!dir.mkdirs()){
                    log.error("folder make failed");
                }
            }

            final String fullPath = String.format("%s/%s", path, fileName);

            if (doMerge(files, fullPath)) {
                return new File(fullPath);
            }
        } catch (GaiaBizException e) {
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "pdf merge failed", e);
        }

        throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "pdf merge failed");
    }

    public static byte[] mergeToBytes(List<File> files, String path, String fileName) {
        try {
            File dir = new File(path);

            if (!dir.isDirectory()) {

                if(!dir.mkdirs()){
                    log.error("folder make failed");
                }
            }

            final String fullPath = String.format("%s/%s", path, fileName);

            if (doMerge(files, fullPath)) {
                return FileUtils.readFileToByteArray(new File(fullPath));
            }
        } catch (IOException e) {
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "pdf merge failed", e);
        }

        throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "pdf merge failed");
    }

    public static boolean doMerge(List<File> files, String fullPath) {
        boolean result = true;

        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(fullPath);

//        for (File file : files) {
//            try {
//                merger.addSource(file);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
        files.forEach(f -> {
            try {
                merger.addSource(f);
            } catch (FileNotFoundException e) {
                throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR,"File Not Found Exception",e);
            }
        });

        try {
            merger.mergeDocuments(null);
        } catch (IOException e) {
            log.error("document merge failed : {}",e.getMessage());
            result = false;
        }

        return result;
    }
}
