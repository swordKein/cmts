package com.kthcorp.cmts.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XmlUtil {

    public static NodeList readXmlFile(String filePath, String tag) {
        NodeList result = null;
        try {
            File fXmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            //System.out.println("### XML:: Root element :" + doc.getDocumentElement().getNodeName());

            result = doc.getElementsByTagName(tag);

            /*
            //for (int temp = 0; temp < nList.getLength(); temp++) {
            for (int temp = 0; temp < 10; temp++) {
                Node nNode = result.item(temp);
                System.out.println("### XML:: Current Element :" + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    //System.out.println("CONTENT_ID : " + eElement.getAttribute("CONTENT_ID"));

                    System.out.println("CONTENT_ID : " + eElement.getElementsByTagName("CONTENT_ID").item(0).getTextContent());
                    System.out.println("CONTENT_TITLE : " + eElement.getElementsByTagName("CONTENT_TITLE").item(0).getTextContent());
                    System.out.println("DIRECTOR : " + eElement.getElementsByTagName("DIRECTOR").item(0).getTextContent());
                    System.out.println("YEAR : " + eElement.getElementsByTagName("YEAR").item(0).getTextContent());
                    System.out.println("KT_RATING : " + eElement.getElementsByTagName("KT_RATING").item(0).getTextContent());

                }
            }
            */
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
