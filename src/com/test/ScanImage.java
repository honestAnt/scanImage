package com.test;

import com.sun.jna.Platform;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.*;

/**
 * Created by honestAnt on 2016/6/28.
 */
public class ScanImage {
    public static void main(String[] args) {
        loadDLL("dlls/x86/sysntfy.dll");
        loadDLL("dlls/x86/ieshims.dll");
        loadDLL("dlls/x86/msvcr120.dll");
        loadDLL("dlls/x86/msvcp120.dll");
        loadDLL("dlls/x86/gpsvc.dll");
        loadDLL("dlls/x86/liblept173.dll");
        loadDLL("dlls/x86/libtesseract304.dll");
        System.out.println(Platform.isWindows()?"libtesseract304":"tesseract");
        File imageFile = new File(ScanImage.class.getResource("/samples/").getPath() + "static.jpg");
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        instance.setDatapath(new File(ScanImage.class.getResource("/resources/").getPath()).getPath());
//        instance.setLanguage("osd");  中文
        //如果设置简体中文或者繁体的识别比osd更高,需要去下载对应的traineddata文件（https://github.com/tesseract-ocr/tessdata）
        //instance.setLanguage("osd");  简体中文 	chi_sim.traineddata
        //instance.setLanguage("osd");  繁体中文 	chi_tra.traineddata
        
        try {
            String result = instance.doOCR(imageFile);
            System.out.println(result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }


    private static void loadDLL(String libFullName) {
        try {
            String nativeTempDir = System.getProperty("java.io.tmpdir");
            InputStream in = null;
            FileOutputStream writer = null;
            BufferedInputStream reader = null;
            File extractedLibFile = new File(ScanImage.class.getResource("/").getPath() + File.separator + libFullName);
            if (!extractedLibFile.exists()) {
                try {
                    in = Tesseract.class.getResourceAsStream("/" + libFullName);
                    Tesseract.class.getResource(libFullName);
                    reader = new BufferedInputStream(in);
                    try {
                        writer = new FileOutputStream(extractedLibFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    byte[] buffer = new byte[1024];
                    while (reader.read(buffer) > 0) {
                        writer.write(buffer);
                        buffer = new byte[1024];
                    }
                    in.close();
                    writer.close();
                    System.load(extractedLibFile.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (in != null) {
                        in.close();
                    }
                    if (writer != null) {
                        writer.close();
                    }
                }
            } else {
                System.load(extractedLibFile.toString());
            }
        } catch (IOException e) {
            System.out.println("初始化 " + libFullName + " DLL错误");
        }
    }
}
