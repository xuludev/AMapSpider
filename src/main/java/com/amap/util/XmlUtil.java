package com.amap.util;

import com.amap.entity.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/6/3.
 * <p>
 * unfinished
 */
public class XmlUtil {

    private static final Logger logger = LogManager.getLogger();

    public static org.dom4j.Document parse(String xmlPath) throws DocumentException {
        File inputFile = new File(xmlPath);
        SAXReader reader = new SAXReader();
        org.dom4j.Document document = reader.read(inputFile);

        return document;
    }

    public static org.dom4j.Document createNodeIfNotExist(org.dom4j.Document document, String typeName, String
            levelName) {
        org.dom4j.Element rootElement = document.getRootElement();
        // iterate through child elements of root
        if (hasThisTypeNode(rootElement, typeName)) {

        } else {
            rootElement.addElement("type").addAttribute("name", typeName);
        }

        return document;
    }

    private static boolean hasThisTypeNode(org.dom4j.Element rootElement, String typeName) {

        boolean flag = false;
        // iterate through child elements of root
        for (Iterator i = rootElement.elementIterator(); i.hasNext(); ) {
            org.dom4j.Element element = (org.dom4j.Element) i.next();
            if (typeName.equals(element.valueOf("@name"))) {
                logger.info("存在 type 为 " + typeName + " 的节点...");

                flag = true;
            }
        }
        return flag;
    }

    private static boolean hasThisLevelNode(org.dom4j.Element levelElement, String levelName) {

        boolean flag = false;
        // iterate through child elements of root
        for (Iterator i = levelElement.elementIterator(); i.hasNext(); ) {
            org.dom4j.Element element = (org.dom4j.Element) i.next();
            if (levelName.equals(element.valueOf("@name"))) {
                logger.info("存在 level 为 " + levelName + " 的节点...");

                flag = true;
            }
        }
        return flag;
    }

    public static void write(org.dom4j.Document document) throws IOException {

        // lets write to a file
        XMLWriter writer = new XMLWriter(
                new FileWriter("output.xml")
        );
        writer.write(document);
        writer.close();

        // Pretty print the document to System.out
        OutputFormat format = OutputFormat.createPrettyPrint();
        writer = new XMLWriter(System.out, format);
        writer.write(document);

        // Compact format to System.out
        format = OutputFormat.createCompactFormat();
        writer = new XMLWriter(System.out, format);
        writer.write(document);
    }

    /**
     * read location info from config.xml
     *
     * @param xmlConfigPath
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static List<Config> readCityXmlConfig(String appKey, String xmlConfigPath) throws ParserConfigurationException,
            IOException, SAXException {
        List<Config> configList = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
//        Document document = builder.parse(new FileInputStream(new File(xmlConfigPath)));
        Document document = builder.parse(XmlUtil.class.getResourceAsStream("/" + xmlConfigPath));
        Element element = document.getDocumentElement();

        NodeList nodeList = element.getChildNodes();
        int len = nodeList.getLength();
        for (int i = 0; i < len; i++) {
            if (nodeList.item(i) instanceof Element) {
                String name = ((Element) nodeList.item(i)).getAttribute("name").trim();
                String distance = ((Element) nodeList.item(i)).getAttribute("range").trim();

                String[] infos = GeoUtils.getRestfulLocationInfo(appKey, name);
                NodeList typeNodeList = nodeList.item(i).getChildNodes();
                for (int j = 0; j < typeNodeList.getLength(); j++) {
                    if (typeNodeList.item(j) instanceof Element) {
                        String type = typeNodeList.item(j).getTextContent();
                        Config config = new Config(infos[0], distance, infos[2], infos[1], type);
                        configList.add(config);
                    }
                }

            }
        }

        return configList;
    }
}
