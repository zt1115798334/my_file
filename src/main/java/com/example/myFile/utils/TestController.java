package com.example.myFile.utils;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhang
 * date: 2019/7/11 14:37
 * description:
 */
@Controller
public class TestController {

    @GetMapping(value = "downFile",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downFile(HttpServletRequest request,
                         HttpServletResponse response) {
        try {
            FileUtil.download(request, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
