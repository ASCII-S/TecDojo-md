package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;

@RestController
public class FileUploadController {

    @PostMapping("/upload")
    // MultipartFile 是 Spring 框架提供的一个类，用于处理文件上传。
    // HttpServletRequest 是 Servlet 提供的一个类，用于获取请求信息。
        // HttpServletRequest 如何在前端中传递进来
        // 在 form 表单中，添加 enctype="multipart/form-data" 属性
    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取文件类型
        String contentType = file.getContentType();
        // 获取文件大小
        long size = file.getSize();
        // 设置本地保存路径
            // getServletContext() 是 HttpServletRequest 提供的一个方法，用于获取 ServletContext 对象。
                // getRealPath() 是 ServletContext 提供的一个方法，用于获取文件的实际保存路径。
                    // path 是文件的保存路径，相对于 web 应用的根目录。
        String savePath = request.getServletContext().getRealPath("/upload");
        // 调用方法
        saveFile(file, savePath);


        // 打印文件信息
        System.out.println("文件名：" + fileName);
        System.out.println("文件类型：" + contentType);
        System.out.println("文件大小：" + size);
        System.out.println("保存路径：" + savePath);

        return "上传成功";
    }

    // 保存文件
    public void saveFile(MultipartFile file, String savePath) throws IOException {
        // 判断文件夹是否存在
        File folder = new File(savePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // 创建文件
        File saveFile = new File(savePath, file.getOriginalFilename());
        // 保存文件
        file.transferTo(saveFile);
    }
}
