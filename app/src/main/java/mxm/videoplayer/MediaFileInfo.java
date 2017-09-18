package mxm.videoplayer;

/**
 * Created by root on 9/9/17.
 */

public class MediaFileInfo {
    private String fileName,filePath,fileType;
    int videoiID;

    public String getFileName() {
        return fileName;
    }

    public int getVideoiID() {
        return videoiID;
    }

    public void setVideoiID(int videoiID) {

        this.videoiID = videoiID;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
