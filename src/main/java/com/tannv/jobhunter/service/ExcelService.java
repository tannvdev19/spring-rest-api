package com.tannv.jobhunter.service;

import com.tannv.jobhunter.domain.User;
import com.tannv.jobhunter.repository.UserRepository;
import com.tannv.jobhunter.util.excel.ExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
