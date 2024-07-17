package com.tannv.jobhunter.service;

import com.tannv.jobhunter.model.ProjectBOM;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class XMLService {

    public void readFile() {
        try {
            File inputFile = new File("./src/main/java/com/tannv/jobhunter/sample/data/legacy.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            Element rootElement = doc.getDocumentElement();
            ProjectBOM projectBOM = parseElement(rootElement);

            // Print or use the projectBOM object as needed
            System.out.println("Root Product: " + projectBOM.getProduct());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ProjectBOM parseElement(Element element) {
        ProjectBOM projectBOM = new ProjectBOM();
        projectBOM.setProduct(element.getTagName());

        NamedNodeMap attributes = element.getAttributes();
        Map<String, String> attributesMap = new HashMap<>();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attr = attributes.item(i);
            attributesMap.put(attr.getNodeName(), attr.getNodeValue());
        }
        projectBOM.setAttributes(attributesMap);

        NodeList childNodes = element.getChildNodes();
        List<ProjectBOM> children = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                children.add(parseElement((Element) node));
            }
        }
        projectBOM.setChildrens(children);

        return projectBOM;
    }
}
