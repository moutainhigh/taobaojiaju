package com.xinshan.utils.fileServer;


/**
 * Created by jonson.xu on 15-4-28.
 */
public class FastDFSUtils {
    /*private StorageClient storageClient;
    private StorageServer storageServer;
    private FastDHTClient fastDHTClient;
    private String namespace;
    private String object;

    public FastDFSUtils(String namespace, String object) {
        this.namespace = namespace;
        this.object = object;
        this.dfs_conn();
        this.dht_conn();
    }

    //TODO 建立fast dfs连接
    private void dfs_conn() {
        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer;
            trackerServer = trackerClient.getConnection();
            this.storageClient = new StorageClient(trackerServer, storageServer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //TODO 建立 fast dht连接
    private void dht_conn() {
        fastDHTClient = new FastDHTClient(true);
    }

    *//**
     * @return string
     * 生成Key
     *//*
    public String generateKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    *//**
     * FastDHT Key信息
     *
     * @param key
     * @return
     *//*
    private DfsModel getInfo(String key) {
        try {
            KeyInfo keyInfo = new KeyInfo(namespace, object, key);
            String info = this.fastDHTClient.get(keyInfo);
            DfsModel dfsModel = JSON.parseObject(info, DfsModel.class);
            return dfsModel;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    *//**
     * 获取文件的mine
     *
     * @param nameValuePairs
     * @param key
     * @return
     *//*
    public String getNameValuePair(NameValuePair[] nameValuePairs, String key) {
        for (NameValuePair nameValuePair : nameValuePairs) {
            if (nameValuePair.getName().equals(key)) {
                return nameValuePair.getValue();
            }
        }
        return null;
    }

    *//**
     * 读取master文件信息及内容
     *
     * @param key
     * @return
     *//*
    public com.xinshan.pojo.fastdfs.FileModel read(String key) {
        try {
            DfsModel dfsModel = this.getInfo(key);
            //TODO Master
            if (dfsModel != null) {
                DfsFileInfo masterFileInfo = dfsModel.getMaster();
                if (masterFileInfo != null) {
                    byte[] fileContent = this.storageClient.download_file(masterFileInfo.getGroup_name(), masterFileInfo.getFilename());
                    NameValuePair[] nameValuePairs = this.storageClient.get_metadata(masterFileInfo.getGroup_name(), masterFileInfo.getFilename());
                    String mime = this.getNameValuePair(nameValuePairs, "mime");
                    String filename = this.getNameValuePair(nameValuePairs, "filename");
                    Long size = Long.parseLong(this.getNameValuePair(nameValuePairs, "size"));
                    if (mime == null || filename == null) {
                        return null;
                    }
                    com.xinshan.pojo.fastdfs.FileModel fileModel = new com.xinshan.pojo.fastdfs.FileModel();
                    fileModel.setContent(fileContent);
                    fileModel.setFilename(filename);
                    fileModel.setMime(mime);
                    fileModel.setSize(size);
                    return fileModel;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    *//**
     * 仅上传文件
     *
     * @param file
     * @return
     *//*
    public ResultFileInfo upload(File file) {
        try {
            String ext = FileUtils.getExtName(file.getName());
            String mime = FileUtils.getMime(file.getName());
            long size = file.length();
            NameValuePair[] nameValuePairs = new NameValuePair[]{
                    new NameValuePair("mime", mime),
                    new NameValuePair("size", String.valueOf(size)),
                    new NameValuePair("filename", file.getName())
            };
            boolean isImage = mime.contains("image");
            byte[] buffer = null;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream((int) size);
            byte[] tmpBytes = new byte[1024];
            int n;
            InputStream inputStream = new FileInputStream(file);
            while ((n = inputStream.read(tmpBytes)) != -1) {
                byteArrayOutputStream.write(tmpBytes, 0, n);
            }
            inputStream.close();
            byteArrayOutputStream.close();
            buffer = byteArrayOutputStream.toByteArray();
            String[] strings = storageClient.upload_appender_file(buffer, ext, nameValuePairs);
            DfsFileInfo dfsFileInfo = new DfsFileInfo();
            dfsFileInfo.setFilename(strings[1]);
            dfsFileInfo.setGroup_name(strings[0]);
            dfsFileInfo.setNameValuePairs(nameValuePairs);
            DfsModel dfsModel = new DfsModel();
            dfsModel.setMaster(dfsFileInfo);
            if (isImage) {
                //TODO 压缩图片，并把压缩图片做为从文件
//                FileModel imageModel = new FileModel(file.getPath());

            }

            String key = generateKey();
            KeyInfo keyInfo = new KeyInfo(namespace, object, key);
            fastDHTClient.set(keyInfo, JSON.toJSONString(dfsModel));
            ResultFileInfo resultFileInfo = new ResultFileInfo();
            resultFileInfo.setFilename(file.getName());
            resultFileInfo.setFilesize(file.length());
            resultFileInfo.setKey(key);
            return resultFileInfo;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    *//**
     * 下载
     *
     * @param key
     *//*
    public FileModel download(String key) {
        try {
            String downloadPath = CommonUtils.FILE_DOWNLOAD_TMP;
            DfsModel dfsModel = getInfo(key);
            DfsFileInfo dfsFileInfo = dfsModel.getMaster();
            NameValuePair[] nameValuePairs = this.storageClient.get_metadata(dfsModel.getMaster().getGroup_name(), dfsModel.getMaster().getFilename());
            mkDir(downloadPath + dfsFileInfo.getFilename());
            File file = new File(downloadPath + dfsFileInfo.getFilename());
            if (!file.exists()) {
                storageClient.download_file(dfsFileInfo.getGroup_name(), dfsFileInfo.getFilename(), downloadPath + dfsFileInfo.getFilename());
            }
            FileModel fileModel = new FileModel(downloadPath,dfsFileInfo.getFilename());
            fileModel.setFileSize(Long.parseLong(this.getNameValuePair(nameValuePairs, "size")));
            fileModel.setFileName(this.getNameValuePair(nameValuePairs,"filename"));
            return fileModel;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void mkDir(String path) {
        File file = new File(path);
        try {
            if (!file.exists()) {
                try {
                    file.mkdirs();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (file.isDirectory()) {
            file.delete();
        }
    }

    *//**
     * 通过表单直接上传
     *
     * @param file
     * @return
     *//*
    public String upload(MultipartFile file) {
        try {
            String ext = FileUtils.getExtName(file.getOriginalFilename());
            String mime = file.getContentType();
            long size = file.getSize();
            NameValuePair[] nameValuePairs = new NameValuePair[]{
                    new NameValuePair("mime", mime),
                    new NameValuePair("size", String.valueOf(size)),
                    new NameValuePair("filename", file.getOriginalFilename())
            };
            String[] strings = storageClient.upload_appender_file(file.getBytes(), ext, nameValuePairs);
//            [group1, M00/00/00/wKgBRFWdNbKAKNyVAAvqH_kipG8077.jpg]
            boolean isImage = mime.contains("image");
            return strings[0] + "/" + strings[1];
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public NameValuePair[] get_metadata(String groupName, String remoteFileName) throws IOException, MyException {
        NameValuePair[] nameValuePairs = storageClient.get_metadata(groupName, remoteFileName);
        return nameValuePairs;
    }

    private static byte[] getFileContent(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            int available = fis.available();
            byte[] bytes = new byte[available];
            fis.read(bytes);
            fis.close();
            return bytes;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }*/
}
