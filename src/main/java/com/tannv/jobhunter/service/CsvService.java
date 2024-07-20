package com.tannv.jobhunter.service;

import com.tannv.jobhunter.model.BOMCsvItemModel;
import com.tannv.jobhunter.model.BOMCsvModel;
import com.tannv.jobhunter.util.CsvUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Service
public class CsvService {
    public void readFile() {
        try {
            File legacyFile = new File("./src/main/java/com/tannv/jobhunter/sample/data/legacy_BOM.csv");
            List<List<String>> scannedData = CsvUtils.scanFile(legacyFile);
            List<BOMCsvItemModel> items = new ArrayList<>();
            for (List<String> item : scannedData) {
                BOMCsvItemModel bomCsvItemModel = new BOMCsvItemModel(item);
                items.add(bomCsvItemModel);
            }
            items.remove(0); // remove header
            items.get(0).setLevel("0");
            BOMCsvModel bomCsvModel = buildBOMCsvModel(items);
            System.out.println(bomCsvModel);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static BOMCsvModel buildBOMCsvModel(List<BOMCsvItemModel> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        Stack<BOMCsvModel> stack = new Stack<BOMCsvModel>();
        BOMCsvModel root = new BOMCsvModel(items.get(0));
        stack.push(root);

        for (int i = 1; i < items.size(); i++) {
            BOMCsvItemModel currentItem = items.get(i);
            BOMCsvModel currentModel = new BOMCsvModel(currentItem);
            int currentLevel = Integer.parseInt(currentItem.getLevel());

            // Pop stack to find the appropriate parent
            while (!stack.isEmpty() && Integer.parseInt(stack.peek().getBomCsvItemModel().getLevel()) >= currentLevel) {
                stack.pop();
            }

            // Add current model as child to the appropriate parent
            if (!stack.isEmpty()) {
                stack.peek().addChild(currentModel);
            }

            // Push current model onto stack
            stack.push(currentModel);
        }

        return root;
    }
}
