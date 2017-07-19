package cn.com.cis.common.utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import java.net.URL;

/**
 * XML DOM解析器辅助类。
 * 
 */
public class DomUtil {

    private static Logger _logger = Logger.getLogger(DomUtil.class);
    
    /**
     * 生成XML文件的{@link org.w3c.dom.Document}实例。
     * 
     * @param classPathXmlFile 相对于CLASSPATH的文件路径，以"/"开头。例："/threadpool4j.xml"
     * @return XML文件的{@link Document}实例。如果文件找不到或XML格式错误将返回null。
     */
    public static Document createDocument(String classPathXmlFile) {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            DocumentBuilder dom = domFactory.newDocumentBuilder();
            document = dom.parse(DomUtil.class.getResourceAsStream(classPathXmlFile));
        } catch (Exception e) {
            _logger.error( String.format("create Document of xml file[%s] occurs error", classPathXmlFile), e);
        }
        
        return document;
    }
    public static Document createDocument(URL url) {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            DocumentBuilder dom = domFactory.newDocumentBuilder();
            document = dom.parse(url.getFile());
        } catch (Exception e) {
            _logger.error( String.format("create Document of xml file[%s] occurs error", url.getFile()), e);
        }

        return document;
    }
}
