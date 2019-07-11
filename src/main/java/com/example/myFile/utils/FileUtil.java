package com.example.myFile.utils;

import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhang
 * date: 2019/7/11 14:17
 * description:
 */
public class FileUtil {

    /**
     * 文件下载
     *
     * @param request
     * @param response
     * @throws UnsupportedEncodingException
     */
    public static void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        File file = new File("E:\\Game\\3DMGAME-Rage.Update.2-SKIDROW.rar");
        long position = FileUtil.headerSetting(file, request, response);
        //  log.info("跳过"+pos);
        //NIO 实现
        int bufferSize = 131072;
        //读出文件到i/o流
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel fileChannel = fileInputStream.getChannel();

        // 6x128 KB = 768KB byte buffer
        ByteBuffer buff = ByteBuffer.allocateDirect(786432);
        byte[] byteArr = new byte[bufferSize];
        int nRead, nGet;
        try {
            fileChannel.position(position);
            while ((nRead = fileChannel.read(buff)) != -1) {
                if (nRead == 0) {
                    continue;
                }
                buff.position(0);
                buff.limit(nRead);
                while (buff.hasRemaining()) {
                    nGet = Math.min(buff.remaining(), bufferSize);
                    // read bytes from disk
                    buff.get(byteArr, 0, nGet);
                    // write bytes to output
                    response.getOutputStream().write(byteArr);
                }
                buff.clear();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            buff.clear();
            fileChannel.close();
            fileInputStream.close();
        }
    }

    /**
     * 断点续传支持
     *
     * @param file
     * @param request
     * @param response
     * @return 跳过多少字节
     */
    public static long headerSetting(File file, HttpServletRequest request, HttpServletResponse response) {
        long len = file.length();//文件长度
        if (null == request.getHeader("Range")) {
            setResponse(new RangeSettings(len), file.getName(), response);
            return 0;
        }
        String range = request.getHeader("Range").replaceAll("bytes=", "");
        RangeSettings settings = getSettings(len, range);
        setResponse(settings, file.getName(), response);
        return settings.getStart();
    }

    private static void setResponse(RangeSettings settings, String fileName, HttpServletResponse response) {
        response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);// set the MIME type.
        if (!settings.isRange()) {
            response.addHeader("Content-Length", String.valueOf(settings.getTotalLength()));
        } else {
            long start = settings.getStart();
            long end = settings.getEnd();
            long contentLength = settings.getContentLength();
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);
            response.addHeader("Content-Length", String.valueOf(contentLength));
            String contentRange = "bytes " + start + "-" + end + "/" + settings.getTotalLength();
            response.setHeader("Content-Range", contentRange);
        }
    }

    private static RangeSettings getSettings(long len, String range) {
        long contentLength;
        long start;
        long end = 0;
        if (range.startsWith("-"))// -500，最后500个
        {
            contentLength = Long.parseLong(range.substring(1));//要下载的量
            end = len - 1;
            start = len - contentLength;
        } else if (range.endsWith("-"))//从哪个开始
        {
            start = Long.parseLong(range.replace("-", ""));
            end = len - 1;
            contentLength = len - start;
        } else//从a到b
        {
            String[] se = range.split("-");
            start = Long.parseLong(se[0]);
            end = Long.parseLong(se[1]);
            contentLength = end - start + 1;
        }
        return new RangeSettings(start, end, contentLength, len);
    }
}
