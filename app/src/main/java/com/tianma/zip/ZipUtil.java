package com.tianma.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Tianma at 2016/10/24
 */

public class ZipUtil {


    public static void unzip() {
        new ZipDecompress().unzip();
    }

    /**
     * 将多个(1~n)个文件或者目录压缩至目标文件
     * @param destZipFile 目标压缩文件
     * @param fileOrDirs 多个被压缩的文件或目录
     * @throws IOException
     */
    public static void zip(File destZipFile, File... fileOrDirs) throws IOException {
        new ZipCompress().zip(destZipFile, fileOrDirs);
    }

    /**
     * Zip解压缩
     */
    public static class ZipDecompress {

        public void unzip() {
        }

    }

    /**
     * Zip压缩
     */
    public static class ZipCompress {

        private static int buf = 4096;

        /**
         * 将多个(1~n)个文件或者目录压缩至目标文件
         * @param destZipFile 目标压缩文件
         * @param fileOrDirs 多个被压缩的文件或目录
         * @throws IOException
         */
        public void zip(File destZipFile, File... fileOrDirs) throws IOException{
            ZipOutputStream zos = null;
            try {
                zos = new ZipOutputStream(new FileOutputStream(destZipFile));
                for(File fileOrDir : fileOrDirs) {
                    if (fileOrDir.isDirectory()) {
                        zipDir(fileOrDir, fileOrDir, zos);
                    } else {
                        zipFile(fileOrDir, fileOrDir.getParentFile(), zos);
                    }
                }
            } finally {
                if (zos != null) {
                    zos.close();
                }
            }
        }

        /**
         * 压缩目录
         *
         * @param dir     被压缩的目录
         * @param baseDir 被压缩的文件的根目录(用于记录压缩文件的层次结构)
         * @param zos     压缩文件流
         * @throws IOException
         */
        private void zipDir(File dir, File baseDir, ZipOutputStream zos) throws IOException {
            File[] subFiles = dir.listFiles();
            for (File subFile : subFiles) {
                if (subFile.isDirectory()) {
                    zipDir(subFile, baseDir, zos);
                } else {
                    zipFile(subFile, baseDir, zos);
                }
            }
        }

        /**
         * 压缩文件
         *
         * @param srcFile 被压缩的文件
         * @param baseDir 被压缩的文件的根目录(用于记录压缩文件层次结构)
         * @param zos     压缩文件流
         * @throws IOException
         */
        private void zipFile(File srcFile, File baseDir, ZipOutputStream zos) throws IOException {
            InputStream in = new FileInputStream(srcFile);
            String entry = srcFile.getPath().substring(baseDir.getPath().length() + 1);
            ZipEntry zipEntry = new ZipEntry(entry);
            zos.putNextEntry(zipEntry);
            byte[] data = new byte[buf];
            int len;
            while ((len = in.read(data)) != -1) {
                zos.write(data, 0, len);
            }
            zos.flush();
            in.close();
        }


    }

}
