package com.tannv.jobhunter.service;

import com.tannv.jobhunter.domain.User;
import com.tannv.jobhunter.model.excel.ExcelWorkbook;
import com.tannv.jobhunter.repository.UserRepository;
import com.tannv.jobhunter.util.ExcelUtils;
import com.tannv.jobhunter.util.excel.ExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelService {
    private final UserRepository userRepository;

    public ExcelService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ByteArrayInputStream getActualData() throws IOException {
        List<User> users = userRepository.findAll();

        return ExcelHelper.dataToExcel(users);
    }

    public List<ExcelWorkbook> getExcelWorkbook(List<MultipartFile> files) throws IOException {
        return ExcelUtils.parseExcelFiles(files);
    }


}
