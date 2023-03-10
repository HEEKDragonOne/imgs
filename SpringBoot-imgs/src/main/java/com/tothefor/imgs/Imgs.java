package com.tothefor.imgs;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.http.HttpUtil;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @Author DragonOne
 * @Date 2023/3/10 11:06
 * @墨水记忆 www.tothefor.com
 */
public class Imgs {
    public static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(
            4,
            6,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(100),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );
    public static Set<String> imgs = new HashSet<>();
    public static String basePath = "/Users/dragonone/ToTheFor/blog/source/_posts";

    public static void main(String[] args) throws Exception {

        File baseFile = new File(basePath);

        if (baseFile.isDirectory()) {
            //显示目录下面的文件或者文件夹
            File folder[] = baseFile.listFiles();
            for (int i = 0; i < folder.length; i++) {
                String baseFolderName = folder[i].getName();
                String sonFolder = basePath + "/" + baseFolderName;
                if (folder[i].isDirectory()) {
                    File[] listFiles = folder[i].listFiles();
                    for (int j = 0; j < listFiles.length; ++j) {
                        if (listFiles[j].isDirectory()) {
                            String lastPath = sonFolder + "/" + listFiles[j].getName();
                            File[] lastListFiles = listFiles[j].listFiles();
                            for (int k = 0; k < lastListFiles.length; ++k) {
                                if (lastListFiles[k].isDirectory()) {
                                    String lastFilePath = lastPath + "/" + lastListFiles[k].getName();
                                    File[] files = lastListFiles[k].listFiles();
                                    for (int n = 0; n < files.length; ++n) {
                                        if (files[n].isDirectory()) {
                                            String end = lastFilePath + "/" + files[n].getName();
                                            System.out.println("底层文件夹为：" + end);
                                        } else {
                                            if (files[n].getName().contains("Store")) continue;
                                            String dealFilePath = lastPath + "/" + files[n].getName();
                                            deal(dealFilePath);
                                        }
                                    }
                                } else {
                                    if (lastListFiles[k].getName().contains("Store")) continue;
                                    String dealFilePath = lastPath + "/" + lastListFiles[k].getName();
                                    deal(dealFilePath);
                                }
                            }
                        } else {
                            if (listFiles[j].getName().contains("Store")) continue;
                            String dealFilePath = sonFolder + "/" + listFiles[j].getName();
                            System.out.println(dealFilePath);
                            deal(dealFilePath);
                        }
                    }
                }
            }

        }


    }

    public static void deal(String fileName) {
        try {
            // 读取文件内容到Stream流中，按行读取
            Stream<String> lines = Files.lines(Paths.get(fileName));
            // 随机行顺序进行数据处理
            lines.forEach(ele -> {
//                EXECUTOR_SERVICE.submit(() -> {
                    if (ele.contains("https://img-blog.csdnimg.cn/")) {
                        imgs.add(ele);
                        down(ele);
                    }
//                });
            });
        } catch (Exception e) {
            System.out.println(fileName + "处理出错=================================");
        }

    }

    public static void down(String url) {
        int indexOf = url.indexOf("https:");
        int i = url.lastIndexOf(".png");
        String fileUrl = url.substring(indexOf, i + 1);
        fileUrl+="png";
        long l = 0L;
        String path = null;
        String staticAndMksDir = null;
        if (fileUrl != null) {
            //下载时文件名称
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("."));
            try {
                String dataStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
                String uuidName = UUID.randomUUID().toString();
                path = "/Users/dragonone/Desktop/imgs/src/main/resources/images/" + uuidName + fileName;
//                staticAndMksDir = Paths.get(ResourceUtils.getURL("classpath:").getPath(),"resources", "images",dataStr).toString();
//                HttpUtil.downloadFile(fileUrl, staticAndMksDir + File.separator  + fileName);
                HttpUtil.downloadFile(fileUrl, path);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }
//        System.out.println(printlnSystem.currentTimeMillis()-l);
        System.out.println(fileUrl + " <<<=====>>> " + path);
    }
}
