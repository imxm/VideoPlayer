package mxm.videoplayer.helperClass;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.LinearLayout;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import mxm.videoplayer.MediaFileInfo;

public class VideoData {
    private ArrayList<File> fileList = new ArrayList();
    private File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
    private LinearLayout view;
    private Context context;

    public VideoData(Context context) {
        this.context = context;
    }

    public static String size(long l) {
        if (l <= 0) {
            return "0";
        }
        String[] arrstring = new String[]{"B", "kB", "MB", "GB", "TB"};
        int n = (int)(Math.log10((double)l) / Math.log10((double)1024.0));
        return new DecimalFormat("#,##0.#").format((double)l / Math.pow((double)1024.0, (double)n)) + " " + arrstring[n];
    }

    public File[] getAllFiles(int n) {
        File[] arrfile = ((File)this.getDirectory().get(n)).listFiles();
        File[] arrfile2 = new File[this.getNumberOfFiles(n)];
        int n2 = 0;
        for (int i = 0; i < arrfile.length; ++i) {
            if (!arrfile[i].getName().endsWith(".mp4")) continue;
            arrfile2[n2] = arrfile[i];
            ++n2;
        }
        return arrfile2;
    }

    public ArrayList<File> getDirectory() {
        ArrayList<File> arrayList = this.getfile(this.root);
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < arrayList.size(); ++i) {
            if (!((File)arrayList.get(i)).isDirectory()) continue;
            arrayList2.add(arrayList.get(i));
        }
        return arrayList2;
    }

    public ArrayList<File> getFile() {
        Log.e((String)"+++++++++++++++++++++", (String)this.root.getName());
        return this.getfile(this.root);
    }

    public ArrayList<File> getListview() {
        ArrayList<File> arrayList = this.getDirectory();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        for (int i = 0; i < arrayList.size(); ++i) {
            arrayList2.add((Object)((File)arrayList.get(i)).listFiles());
        }
        for (int j = 0; j < arrayList2.size(); ++j) {
            for (int k = 0; k < ((File[])arrayList2.get(j)).length; ++k) {
                if (!((File[])arrayList2.get(j))[k].getName().endsWith(".mp4")) continue;
                arrayList3.add((Object)((File[])arrayList2.get(j))[k]);
            }
        }
        return arrayList3;
    }

    public int getNumberOfFiles(int n) {
        int n2 = 0;
        File[] arrfile = ((File)this.getDirectory().get(n)).listFiles();
        for (int i = 0; i < arrfile.length; ++i) {
            if (!arrfile[i].getName().endsWith(".mp4")) continue;
            ++n2;
        }
        return n2;
    }

    /*
     * Enabled aggressive block sorting
     */
    public ArrayList<File> getfile(File file) {
        File[] arrfile = file.listFiles();
        if (arrfile != null && arrfile.length > 0) {
            for (int i = 0; i < arrfile.length; ++i) {
                if (arrfile[i].isDirectory()) {
                    this.getfile(arrfile[i]);
                    continue;
                }
                if (!arrfile[i].getName().endsWith(".mp4")) continue;
                this.fileList.add((File) arrfile[i].getParentFile());
                this.fileList.add((File) arrfile[i]);
            }
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet(this.fileList);
        linkedHashSet.addAll(this.fileList);
        this.fileList.clear();
        this.fileList.addAll((Collection)linkedHashSet);
        return this.fileList;
    }



    public ArrayList<MediaFileInfo> getListFromFolder(String folderName) {

        ArrayList<MediaFileInfo> list=new ArrayList<>();


        String[] parameters = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE};
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArgs = new String[]{"%"+folderName+"%"};

        Cursor videocursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                parameters, selection, selectionArgs, MediaStore.Video.Media.DATE_TAKEN + " DESC");
        //Cursor videocursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        //      parameters, null, null, null);
        int count = videocursor.getCount();
        Log.d("No of video", "" + count);
        for (int i = 0; i < count; i++) {
            MediaFileInfo mediaFileInfo=new MediaFileInfo();
            int video_column_index = videocursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int column_index = videocursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            videocursor.moveToPosition(i);
            String name = videocursor.getString(video_column_index);
            String filepath = videocursor.getString(column_index);
            mediaFileInfo.setFileName(name);
            mediaFileInfo.setFilePath(filepath);
            mediaFileInfo.setFileType("video");
            mediaFileInfo.setVideoiID(videocursor.getInt(videocursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)));
            list.add(mediaFileInfo);



        }
        return list;
    }



}

