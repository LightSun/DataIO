package com.heaven7.java.data.io.utils;

import com.heaven7.java.base.util.Logger;
import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.base.util.Throwables;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * the file internal
 */
public class FileUtils {

    /**
     * the filename transformer
     */
    public interface FilenameTransformer{
        /**
         * transform the name
         * @param name the dir name or file name, exclude "/" and "\"
         * @return the result.
         */
        String transform(String name);
    }

    /** create file path. */
    public static String createFilePath(String baseDir, String simpleFileName, String...subDirs){
        StringBuilder sb = new StringBuilder();
        sb.append(baseDir);
        if (subDirs != null) {
            for (String dir : subDirs) {
                sb.append(File.separator).append(dir);
            }
        }
        return sb.append(File.separator).append(simpleFileName).toString();
    }

    /** get the file name only. exclude extension and dir. */
    public static String getFileName(String path) {
        int index = path.lastIndexOf("/");
        if(index == -1){
            index = path.lastIndexOf("\\");
        }
        return path.substring(index + 1, path.lastIndexOf("."));
    }

    public static String getSimpleName(String path){
        String extension = getFileExtension(path);
        if(extension != null){
            return getFileName(path) + "." + extension;
        }
        return getFileName(path);
    }

    public static String getLastPath(String path){
        int index = path.lastIndexOf("/");
        if(index == -1){
            index = path.lastIndexOf("\\");
        }
        return path.substring(0, index);
    }

    /**
     * 视频文件的关键目录 为2级目录模式,比如:
     empty/dinner/xxx.mp4 , empty/white/xxx2.mp4.
     那么empty就是文件的关键目录
     */
    public static String getFileDir(String filepath, int depth, boolean fullPath){
        if(depth < 1) throw new IllegalArgumentException("depth must > 0");
        File file = new File(filepath);
        if(file.exists() && file.isFile()){
            File parent = file;
            while (depth > 0){
                depth --;
                parent = parent.getParentFile();
                if(parent == null){
                    return null;
                }
            }
            return fullPath ? parent.getAbsolutePath() :parent.getName();
        }
        Logger.d("FileUtils", "getFileDir", "file not exist. filepath = " + filepath);
        return null;
    }
    public static String getParentDir(String dir, int depth, boolean fullPath){
        if(depth < 1) throw new IllegalArgumentException("depth must > 0");
        File file = new File(dir);
        if(file.exists() && file.isDirectory()){
            File parent = file;
            while (depth > 0){
                depth --;
                parent = parent.getParentFile();
                if(parent == null){
                    return null;
                }
            }
            return fullPath ? parent.getAbsolutePath() :parent.getName();
        }
        Logger.d("FileUtils", "getParentDir", "dir not exist. dir = " + dir);
        return null;
    }

    public static String encodeChinesePath(String path){
        return transformPath(path, new FilenameTransformer() {
            @Override
            public String transform(String name) {
                try {
                    return URLEncoder.encode(name, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    return name;
                }
            }
        });
    }
    public static String decodeChinesePath(String path){
        return transformPath(path, new FilenameTransformer() {
            @Override
            public String transform(String name) {
                try {
                    return URLDecoder.decode(name, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    return name;
                }
            }
        });
    }

    /** get file path which is encode by path */
    public static String transformPath(String path, final FilenameTransformer transformer){
        List<String> names = new ArrayList<>();
        //c:\xxx\xxx1\xxx2.jpg -> c:\xxx\xxx1\xxx2
        String tmpPath = path.contains(".") ? path.substring(0, path.lastIndexOf(".")) : path;
        File file = new File(tmpPath);
        if(!file.exists()){
            Logger.d("", "transformPath", "path not exists. path = " + path);
        }
        File parent = file;
        do {
            names.add(parent.getName());
            parent = parent.getParentFile();
            if(parent == null){
                break;
            }
        }while (true);

        final StringBuilder sb = new StringBuilder();
        int index = path.indexOf(File.separator);
        if(index == -1){
            throw new IllegalArgumentException();
        }
        sb.append(path.substring(0, index));
        VisitServices.from(names).reverseService().fire(new FireVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                sb.append(File.separator).append(transformer.transform(s));
                return false;
            }
        });
        if(path.contains(".")){
            sb.append(".").append(getFileExtension(path));

        }
        String s = sb.toString();
        if(s.contains(":\\\\")){
            s = s.replace(":\\\\", ":\\");
        }
        return s;
    }

    public static final FileFilter TRUE_FILE_FILTER =
            new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return true;
                }
            };

    public static void writeTo(String file, String content) {
        writeTo(new File(file), content);
    }

    public static void writeTo(File dst, String content) {
        if (dst.isDirectory()) {
            throw new IllegalStateException();
        }
        if(!dst.getParentFile().exists()){
            dst.getParentFile().mkdirs();
        }
        if (dst.exists()) {
            dst.delete();
        }
        //empty
        if(content == null || content.length() == 0){
            System.out.println("no content for: " + dst.getAbsolutePath());
            return;
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(dst);
            fw.write(content);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void createFile(String path, boolean deleteIfExist){
        File file = new File(path);
        if(file.exists()){
            if(deleteIfExist) {
                file.delete();
            }else {
                return;
            }
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void checkDir(String dir, boolean mustExist) {
        File file = new File(dir);
        if ((mustExist && !file.exists()) || file.isFile()) {
            throw new IllegalStateException("must be dir");
        }
    }

    public static List<String> getFiles(File dir, String extension,
                                        FileFilter filter) {
        List<String> paths = new ArrayList<>();
        getFiles(dir, extension, filter, paths);
        return paths;
    }
    public static List<String> getFiles(File dir, String extension) {
        List<String> paths = new ArrayList<>();
        getFiles(dir, extension, TRUE_FILE_FILTER, paths);
        return paths;
    }

    public static void getVideos(File dir, List<String> outVideos) {
        getFiles(dir, "mp4", outVideos);
    }

    public static void getFiles(File dir, String extension, List<String> outFiles) {
        getFiles(dir, extension, TRUE_FILE_FILTER, outFiles);
    }

    public static void getFiles(File dir, final String extension,
                                final FileFilter filter, List<String> outFiles) {
        File[] videoFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (!pathname.isFile()) {
                    return false;
                }
                if (!filter.accept(pathname)) {
                    return false;
                }
                String extension2 = getFileExtension(pathname);
                return extension.equalsIgnoreCase(extension2);
            }
        });
        if (!Predicates.isEmpty(videoFiles)) {
            for (File file : videoFiles) {
                outFiles.add(file.getAbsolutePath());
            }
        }
        File[] dirs = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        if (!Predicates.isEmpty(dirs)) {
            for (File dir1 : dirs) {
                getFiles(dir1, extension, filter, outFiles);
            }
        }
    }

    public static void getFiles(File dir, FileFilter filter, List<String> outFiles) {
        File[] videoFiles = dir.listFiles(filter);
        if (!Predicates.isEmpty(videoFiles)) {
            for (File file : videoFiles) {
                outFiles.add(file.getAbsolutePath());
            }
        }
        File[] dirs = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        if (!Predicates.isEmpty(dirs)) {
            for (File dir1 : dirs) {
                getFiles(dir1, filter, outFiles);
            }
        }
    }
    public static List<String> getFiles(File dir, FileFilter filter) {
        List<String> files = new ArrayList<>();
        getFiles(dir, filter, files);
        return files;
    }


    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    /**
     * exclude "."
     */
    public static String getFileExtension(String filename) {
        try {
            return filename.substring(filename.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * exclude "."
     */
    public static String getFileExtension(File file) {
        return getFileExtension(file.getAbsolutePath());
    }

    public static boolean copyFilesFromDir(File srcDir, final File dstDir) {
        if(!srcDir.exists()){
            return false;
        }
        if(!dstDir.exists()){
            dstDir.mkdirs();
        }
        final String srcPath = srcDir.getAbsolutePath();
        List<String> files = getFiles(srcDir, "jpg");
        VisitServices.from(files).fire(new FireVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                int index = s.indexOf(srcPath);
                String target = dstDir.getAbsolutePath() + s.substring(index + srcPath.length());
                copyFile(new File(s), new File(target));
                return null;
            }
        });
        return true;
    }

    public static boolean copyFile(File src, File dst) {
        if(!src.exists()){
            return false;
        }
        if(!dst.getParentFile().exists()){
            dst.getParentFile().mkdirs();
        }else{
            if (dst.exists()) {
                dst.delete();
            }
        }
        try {
            dst.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        try {
            in = new BufferedInputStream(new FileInputStream(src));
            out = new BufferedOutputStream(new FileOutputStream(dst));
            byte[] buffer = new byte[1024 * 4];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return true;
    }
    public static FileFilter ofDirFileFilter(String dir){
        Throwables.checkNull(dir);
        return new DirFileFilter(dir);
    }

    private static final class DirFileFilter implements FileFilter{
        private final String dir;

        DirFileFilter(String dir) {
            this.dir = dir;
        }
        @Override
        public boolean accept(File pathname) {
            return dir.equals(FileUtils.getFileDir(pathname.getAbsolutePath(), 1, false));
        }
    }
}
