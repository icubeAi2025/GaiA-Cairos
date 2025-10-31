package kr.co.ideait.platform.gaiacairos.core.util;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class StringHelper extends StringUtils {
    private final static String[][] RPL_CMD_INJECT_STR = new String[][]{{"&", "&amp;"}, {"`", "&#96;"}, {"|", "&#124;"}, {";", "&#59;"}, {"/", "&#47;"}, {"<", "&lt;"}, {">", "&gt;"}};

    private final static Pattern[] PATTERNS = new Pattern[] {
            Pattern.compile("<script>(.*?)</script>", 2),
            Pattern.compile("src[\r\n]*=[\r\n]*\\'(.*?)\\'", 42),
            Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", 42),
            Pattern.compile("</script>", 2),
            Pattern.compile("<script(.*?)>", 42),
            Pattern.compile("eval\\((.*?)\\)", 42),
            Pattern.compile("expression\\((.*?)\\)", 42),
            Pattern.compile("javascript:", 2),
            Pattern.compile("vbscript:", 2),
            Pattern.compile("onload(.*?)=", 42)
    };

    public static String getBody(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        InputStreamReader isr = null;
        try {
            if (inputStream != null) {
                isr = new InputStreamReader(inputStream, "UTF-8");

                bufferedReader = new BufferedReader(isr);
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        return stringBuilder.toString();
    }

    public static String getSafeParamData(String value) {
        value = convertSecurityCharacter(value);

        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            switch (c) {
//                case '"':
//                    stringBuilder.append("&quot;");
//                    break;
                case '&':
                    stringBuilder.append("&amp;");
                    break;
                case '\'':
                    stringBuilder.append("&apos;");
                    break;
                case '<':
                    stringBuilder.append("&lt;");
                    break;
                case '>':
                    stringBuilder.append("&gt;");
                    break;
                default:
                    stringBuilder.append(c);
            }
        }

        return stringBuilder.toString();
    }

    public static String getSafeParamDataForJson(String value) {
        value = convertSecurityCharacter(value);

        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            switch (c) {
//                case '&':
//                    stringBuilder.append("&amp;");
//                    break;
                case '<':
                    stringBuilder.append("&lt;");
                    break;
                case '>':
                    stringBuilder.append("&gt;");
                    break;
                default:
                    stringBuilder.append(c);
            }
        }

        return stringBuilder.toString();
    }

    public static String convertSecurityCharacter(String value) {
/*
        // 겹치는 문자 script -> x_script로 치환
        // value = value.replaceAll("javascript", "x_javascript");
        value= value.replaceAll("--", "&#45;&#45;");
        value= value.replaceAll("\\.\\.", "&#46;&#46;");
        value = value.replaceAll(" vbscript ", "x_vbscript");
        value = value.replaceAll(" applet ", "x_applet");
        value = value.replaceAll(" meta ", "x_meta");
        value = value.replaceAll(" xml ", "x_xml");
        value = value.replaceAll(" blink ", "x_blink");
        value = value.replaceAll(" style ", "x_style");
        value = value.replaceAll(" script ", "x_script");
        value = value.replaceAll(" embed ", "x_embed");
        value = value.replaceAll(" object ", "x_object");
        value = value.replaceAll(" iframe ", "x_iframe");
        value = value.replaceAll(" frame ", "x_frame");
        value = value.replaceAll(" frameset ", "x_frameset");
        value = value.replaceAll(" ilayer ", "x_ilayer");
        value = value.replaceAll(" title ", "x_title");
        value = value.replaceAll(" base ", "x_base");
        value = value.replaceAll(" eval ", "x_eval");
        value = value.replaceAll(" innnerHTML ", "x_innnerHTML");
        value = value.replaceAll(" charset ", "x_charset");
        value = value.replaceAll(" document ", "x_document");
        value = value.replaceAll(" string ", "x_string");
        value = value.replaceAll(" create ", "x_create");
        value = value.replaceAll(" append ", "x_append");
        value = value.replaceAll(" binding ", "x_binding");
        value = value.replaceAll(" alert ", "x_alert");
        value = value.replaceAll(" refresh ", "x_refresh");
        value = value.replaceAll(" embed ", "x_embed");
        value = value.replaceAll(" cookie ", "x_cookie");
        value = value.replaceAll(" void ", "x_void");
        value = value.replaceAll(" href ", "x_href");
        value = value.replaceAll(" onload ", "x_onload");
        value = value.replaceAll(" onsubmit ", "x_onsubmit");
        value = value.replaceAll(" onunload ", "x_onunload");
        value = value.replaceAll(" union ", "q_union");
        value = value.replaceAll(" select ", "q_select");
        value = value.replaceAll(" insert ", "q_insert");
        value = value.replaceAll(" drop ", "q_drop");
        value = value.replaceAll(" update ", "q_update");
        value = value.replaceAll(" join ", "q_join");
        value = value.replaceAll(" substring ", "q_substring");
        value = value.replaceAll(" from ", "q_from");
        value = value.replaceAll(" where ", "q_where");
        value = value.replaceAll(" declare ", "q_declare");
        value = value.replaceAll(" substr ", "q_substr");
        value = value.replaceAll(" delete ", "q_delete");
        value = value.replaceAll(" having ", "q_having");
        value = value.replaceAll(" rownum ", "q_rownum");
        //value = value.replaceAll("link", "x_link");
        //value = value.replaceAll("all", "q_all");
        //value = value.replaceAll("order", "q_order");

        //value = value.replaceAll("and", "q_and");
        //value = value.replaceAll("or", "q_or");
        //value = value.replaceAll("if", "q_if");
        value = value.replaceAll("' or", "' q_or");
        value = value.replaceAll(" or", " q_or");
        value = value.replaceAll("';", "");
*/

//        value = value.replaceAll("script ?", "x_script");
//        value = value.replaceAll("iframe ", "x_iframe");
//        value = value.replaceAll("frame ", "x_frame");
//        value = value.replaceAll("frameset ", "x_frameset");
//        value = value.replaceAll("innnerHTML", "x_innnerHTML");
//
//        value = value.replaceAll("union ", "q_union");
//        value = value.replaceAll("select ", "q_select");
//        value = value.replaceAll("insert ", "q_insert");
//        value = value.replaceAll("drop ", "q_drop");
//        value = value.replaceAll("update ", "q_update");
//        value = value.replaceAll("and", "q_and");
//        value = value.replaceAll("or", "q_or");
//        value = value.replaceAll("if", "q_if");
//        value = value.replaceAll("join ", "q_join");
//        value = value.replaceAll(" substring", "q_substring");
//        value = value.replaceAll("from ", "q_from");
//        value = value.replaceAll("where ", "q_where");
//        value = value.replaceAll("declare ", "q_declare");
//        value = value.replaceAll(" substr", "q_substr");
//        //value = value.replaceAll("all", "q_all");
//        value = value.replaceAll("delete ", "q_delete");
//        //value = value.replaceAll("order", "q_order");
//        value = value.replaceAll("having ", "q_having");
//        value = value.replaceAll(" rownum", "q_rownum");
//
//        value = value.replaceAll("' or", "' q_or");
//        value = value.replaceAll(" or", " q_or");

        return value;
    }

    public static String getUnSafeParamDataForJson(String value) {
        if (StringUtils.isEmpty(value)) {
            return "{result: null}";
        }

        return value.replaceAll("&amp;", "&")
                .replaceAll("&apos;", "\'")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">");
    }

    public static String stripXSS(String value) {
        boolean isChange = false;
        String result = value;

        if (value != null) {
            result = result.replaceAll("\u0000", "");
            Pattern[] patterns;
            int length = (patterns = PATTERNS).length;

            for(int i = 0; i < length; ++i) {
                Pattern scriptPattern = patterns[i];
                if (scriptPattern.matcher(result).find()) {
                    result = result.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
                }
            }

            StringBuffer dest = new StringBuffer("");

            for(int i = 0; i < result.length(); ++i) {
                char oldChar = result.charAt(i);

                for(int j = 0; j < RPL_CMD_INJECT_STR.length; ++j) {
                    if (oldChar == RPL_CMD_INJECT_STR[j][0].charAt(0)) {
                        dest.append(RPL_CMD_INJECT_STR[j][1]);
                        isChange = true;
                        break;
                    }

                    isChange = false;
                }

                if (!isChange) {
                    dest.append(oldChar);
                }
            }

            result = dest.toString();
        }

        return result;
    }
    public static String decodeSafeText(String text){
        if(StringUtils.isEmpty(text)){
            return "";
        }
        return text.replaceAll("&amp;","&").replaceAll("&lt;","<").replaceAll("&gt;",">").replaceAll("&quot;","\"").replaceAll("&apos;","'").replaceAll("&#x27;","'").replaceAll("&#x2F;","/");
    }
}
