package com.tannv.jobhunter.service;

import com.tannv.jobhunter.model.ProjectBOM;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class XMLService {
    public boolean readFile() {
        try {
            File legacyFile = new File("./src/main/java/com/tannv/jobhunter/sample/data/legacy.xml");
            File nxgFile = new File("./src/main/java/com/tannv/jobhunter/sample/data/nxg.xml");
            Element rootLegacy = getRootElement(legacyFile);
            Element rootNxg = getRootElement(nxgFile);
            ProjectBOM boomLegacy = parseElement(rootLegacy, "");
            ProjectBOM boomNxg = parseElement(rootNxg, "");
            boolean isSameStructure = compares(boomLegacy, boomNxg);
            // Print or use the projectBOM object as needed
            return isSameStructure;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Element getRootElement(File fileXML) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fileXML);
        doc.getDocumentElement().normalize();

        return doc.getDocumentElement();
    }
    public ProjectBOM parseElement(Element element, String productParent) {
        ProjectBOM projectBOM = new ProjectBOM();
        projectBOM.setProduct(element.getTagName());
        String productFullParent = (productParent.isEmpty() ? "" : productParent + ".") + element.getTagName();
        projectBOM.setProductFullParent(productFullParent);
        NodeList childNodes = element.getChildNodes();
        List<ProjectBOM> children = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elementOfNode = ((Element) node);
                String ATTRIBUTES_TAG = "Attributes";
                if(elementOfNode.getTagName().equals(ATTRIBUTES_TAG)) {
                    NamedNodeMap attributes = node.getAttributes();
                    Map<String, String> attributesMap = new LinkedHashMap<>();
                    for (int j = 0; j < attributes.getLength(); j++) {
                        Node attr = attributes.item(j);
                        attributesMap.put(attr.getNodeName(), attr.getNodeValue());

                    }
                    projectBOM.setAttributes(attributesMap);
                } else {
                    children.add(parseElement(elementOfNode, productFullParent));
                }
            }
        }
        projectBOM.setChildrens(children);

        return projectBOM;
    }

    public boolean compares(ProjectBOM bom1, ProjectBOM bom2) {
        // Base case: if both objects are null, they are equal
        if (bom1 == null && bom2 == null) {
            return true;
        }

        // If only one of the objects is null, they are not equal
        if (bom1 == null || bom2 == null) {
            return false;
        }

        // Check if the products are not the same
        if (!bom1.getProduct().equals(bom2.getProduct())) {
            return false;
        }

        // Check if the sizes of children lists are different
        if (bom1.getChildrens().size() != bom2.getChildrens().size()) {
            return false;
        }

        // Recursively compare children lists
        for (int i = 0; i < bom1.getChildrens().size(); i++) {
            ProjectBOM child1 = bom1.getChildrens().get(i);
            ProjectBOM child2 = bom2.getChildrens().get(i);
            if (!compares(child1, child2)) {
                return false;
            }
        }

        // If all checks pass, objects are considered equal
        return true;
    }
}
