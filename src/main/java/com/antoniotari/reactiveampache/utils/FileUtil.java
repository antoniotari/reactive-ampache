package com.antoniotari.reactiveampache.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Antonio Tari
 */
public final class FileUtil {
    private volatile static FileUtil instance = null;
    private static boolean useExternalStorage = false;

    public void setUseExternalStorage(boolean useExternal) {
        useExternalStorage = useExternal;
    }

    public boolean isUseExternalStorage() {
        return useExternalStorage;
    }

    //------------------------------------------------------------------
    //--------------
    private FileUtil() {
    }

    //------------------------------------------------------------------
    //--------------
    public static FileUtil getInstance() {
        if (null == instance) {
            synchronized (FileUtil.class) {
                if (null == instance) {
                    instance = new FileUtil();
                }
            }
        }
        return instance;
    }

    //-----------------------------------------------------------------------------
    //-----------------
//	public static String storageDir(Context context,boolean useExternalStorage)
//	{
//		if(useExternalStorage){
//			return extStorageDir(context);
//		}
//		return storageDir(context);
//	}

    public static String storageDir(Context context) {
        if (useExternalStorage) {
            return extStorageDir(context);
        }
        File mediaStorageDir = context.getFilesDir();
        return mediaStorageDir.getPath() + File.separator;
    }

    //-----------------------------------------------------------------------------
    //-----------------
    public static String extStorageDir(Context context) {
        return extStorageDir(context.getPackageName());
    }

    //-----------------------------------------------------------------------------
    //-----------------
//	public static String extStorageDir()
//	{
//		return extStorageDir(ATUtil.getPackageName());
//	}

    //-----------------------------------------------------------------------------
    //-----------------
    public static String extStorageDir(String packageName) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                //+ getApplicationContext().getPackageName()
                + packageName
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        return mediaStorageDir.getPath() + File.separator;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if external storage is available to at least read
     */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * return all the files contained in the specified directory
     */
    public static void fileList(String directoryName, ArrayList<File> files) {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                fileList(file.getAbsolutePath(), files);
            }
        }
    }

    //------------------------------------------------------------------
    //--------------
    public synchronized boolean writeStringFile(Context context, String filename, String string) throws FileNotFoundException, IOException {
        if (string == null) return false;

        //if the saved file is the same as the file we want to save return
        String actualFile = readStringFile(context, filename);
        if (actualFile != null && compare(string, actualFile)) {
            return false;
        }

        FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
        outputStream.write(string.getBytes());
        outputStream.close();
        return true;
    }

    //------------------------------------------------------------------
    //--------------
    public static String filename(String url) {
        return MD5.md5(url);// Uri.parse(url).getLastPathSegment();
    }

	/*public synchronized boolean deleteFile(Context context,String filename)
    {
		try{
			context.deleteFile(filename);
			return true;
		}catch(Exception d){
			return false;
		}
	}*/

    //------------------------------------------------------------------
    //--------------
    private boolean compare(String newFile, String actualFile) {
        // every char takes 16 bits representation i.e. 2 bytes in order to sustain unicode
        int maxLength = 100;
        //if strings are too big we use the hash to compare them
        Log.d(Log.tagHttp, "hash present file:" + MD5.md5(actualFile), "hash new file:" + MD5.md5(newFile));

        if (newFile.length() > maxLength || actualFile.length() > maxLength) {
            return MD5.md5(newFile).equalsIgnoreCase(MD5.md5(actualFile));
        }

        return actualFile.equalsIgnoreCase(newFile);
    }

    //------------------------------------------------------------------
    //--------------
    public String readStringFile(Context context, String filename) {
        try {
            //int ch;
            //			StringBuffer fileContent = new StringBuffer("");
            FileInputStream fis = context.openFileInput(filename);

            InputStreamReader in = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(in);
            StringBuilder data = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                data.append(line);
            }
            br.close();
            in.close();
            fis.close();
            return data.toString();
			/*byte[] buffer = new byte[1];
			//int length;
			while ((//length =
					fis.read(buffer)) != -1)
			{
				fileContent.append(new String(buffer));
			}
			return new String(fileContent);*/
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteFiles(Context context, String... filenames) {
        Log.blu("delete files");
        if (filenames == null) return;
        if (filenames.length == 0) return;
        for (String name: filenames) {
            if (!TextUtils.isEmpty(name)) {
                try {
                    context.deleteFile(name);
                    Log.blu("delete "+name);
                } catch (Exception e) {
                    Log.error(e);
                }
            }
        }
    }

    //------------------------------------------------------------------
    //--------------
    public boolean saveCache_(Context context, String url, String string) {
        if (string == null) {
            return false;
        }
        //if the saved file is the same as the file we want to save return
        String actualFile = readCache(context, url);
        if (actualFile != null && compare(string, actualFile)) {
            return false;
        }

        try {
            File chacheFile = getTempFile(context, url);
            FileOutputStream fos = new FileOutputStream(chacheFile);
            fos.write(string.getBytes());
            fos.close();
            return true;
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
        return false;
    }

    //------------------------------------------------------------------
    //--------------
    public String readCache_(Context context, String url) {
        try {
            File chacheFile = getTempFile(context, url);
            FileInputStream fis = new FileInputStream(chacheFile);// context.openFileInput( chacheFile );

            InputStreamReader in = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(in);
            String data = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                data += line;
            }
            br.close();
            in.close();
            return data;
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
        return "";
    }

    //------------------------------------------------------------------
    //--------------
    public File getTempFile(Context context, String url) {
        File file = null;
        try {
            String fileName = filename(url);// .replace("/", "").replace("\\", "").replace(".", "").replace(":", "");//Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        } catch (IOException e) {
            // Error while creating file
        }
        return file;
    }

    //------------------------------------------------------------------
    //--------------
    public boolean saveCache(Context context, String url, String string) {
        try {
            String fileName = filename(url);// Uri.parse(url).getLastPathSegment();
            Log.d(Log.tagHttp, "saveCache , filename:" + fileName);
            return writeStringFile(context, fileName, string);
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
        return false;
    }

    //------------------------------------------------------------------
    //--------------
    public String readCache(Context context, String url) {
        String fileName = filename(url);// Uri.parse(url).getLastPathSegment();
        return readStringFile(context, fileName);
    }

    //------------------------------------------------------------------
    //--------------
    public JSONObject returnOrUpdateCache(Context context, JSONObject returnedJSON, String url) {
        String toPassS = null;
        if (returnedJSON != null) {
            toPassS = returnedJSON.toString();
        }
        return returnOrUpdateCache(context, toPassS, url);
    }

    //------------------------------------------------------------------
    //--------------
    public JSONObject returnOrUpdateCache(Context context, String returnedString, String url) {
        //check if it's a valid JSONObject
        if (returnedString != null) {
            try {
                Log.d(Log.tagHttp, "1");
                JSONObject returnedJ = new JSONObject(returnedString);
                Log.d(Log.tagHttp, "2");
                if (saveCache(context, url, returnedString)) {
                    Log.d(Log.tagHttp, "3");
                    return returnedJ;
                }
                Log.d(Log.tagHttp, "4");
                String saved = readCache(context, url);
                if (saved == null) {
                    return new JSONObject();
                }
                return new JSONObject(saved);
            } catch (JSONException je) {
                Log.d(Log.tagHttp, "5");
            } catch (Exception je) {
                Log.d(Log.tagHttp, "5");
            }
        }

        JSONObject retJ = getCachedJson(context, url);

        return (retJ == null ? (new JSONObject()) : retJ);
    }

    //------------------------------------------------------------------
    //--------------
    public JSONObject getCachedJson(Context context, String url) {
        try {
            String saved = readCache(context, url);
            if (saved == null) {
                return null;
            }
            return new JSONObject(saved);
        } catch (JSONException je) {
            Log.d(Log.tagHttp, "6");
        }
        Log.d(Log.tagHttp, "7");
        return null;
    }

    //-----------------------------------------------------------------------------
    //-----------------
    public synchronized boolean storeSerializable(Context context, String filename, Serializable object) {
        try {
            //use buffering
            OutputStream file = new FileOutputStream(storageDir(context) + filename);
            OutputStream buffer = new BufferedOutputStream(file);
            ObjectOutput output = new ObjectOutputStream(buffer);
            try {
                output.writeObject(object);
            } finally {
                //file.flush();
                //file.close();
                //buffer.flush();
                //buffer.close();
                //output.flush();
                output.close();
                return true;
            }
        } catch (IOException ex) {
            Log.error("storeSerializable", ex);
        }
        return false;
    }

    /**
     * checks if the file exists and is readable
     */
    public static boolean fileExistsAndCanRead(String filePath) {
        File f = new File(filePath);
        return (f.exists() && !f.isDirectory() && f.canRead());
    }

    //-----------------------------------------------------------------------------
    //-----------------
    public <T extends Serializable> T readSerializable(Context context, String filename) {
        if (filename == null || context == null) {
            return null;
        }

        String filePath = storageDir(context) + filename;
        if (!fileExistsAndCanRead(filePath)) {
            return null;
        }

        T recoveredQuarks = null;
        try {
            //use buffering
            InputStream fis = new FileInputStream(filePath);
            InputStream buffer = new BufferedInputStream(fis);
            ObjectInput ois = new ObjectInputStream(buffer);
            try {
                //deserialize the List
                recoveredQuarks = (T) ois.readObject();
            } finally {
                ois.close();
            }
        } catch (ClassNotFoundException ex) {
            Log.error("readSerializable", ex);
        } catch (EOFException ex) {
            Log.error("readSerializable", "EOFException", ex);
        } catch (IOException ex) {
            Log.error("readSerializable", ex);
        } catch (ClassCastException ex){
            Log.error("ClassCastException", ex);
        }
        return recoveredQuarks;
    }

    /**
     *
     * @param context
     * @param filename
     * @param parcelable
     */
    public synchronized void storeParcelable(Context context, String filename, Parcelable parcelable) {
        Parcel parcel2 = Parcel.obtain();

        try {
            parcel2.writeParcelable(parcelable, 0);
            FileOutputStream bos = new FileOutputStream(FileUtil.storageDir(context) + filename);
            final GZIPOutputStream zos = new GZIPOutputStream(new BufferedOutputStream(bos));
            zos.write(parcel2.marshall());
            zos.close();
            bos.close();
        } catch (FileNotFoundException e) {
            Log.error("storeParcelableArray, FileNotFoundException", e);
        } catch (IOException e) {
            Log.error("storeParcelableArray, IOException", e);
        } finally {
            parcel2.recycle();
        }
    }

    /**
     *
     * @param context
     * @param filename
     * @param theClass
     * @return
     */
    public synchronized <T extends Parcelable> T readParcelable(Context context, String filename, Class<T> theClass) {
        T retParcel = null;
        final Parcel parcel = Parcel.obtain();
        try {
            final ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            final byte[] buffer = new byte[1024];
            final GZIPInputStream zis = new GZIPInputStream(new FileInputStream(storageDir(context) + filename));
            int len = 0;
            while ((len = zis.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            zis.close();
            parcel.unmarshall(byteBuffer.toByteArray(), 0, byteBuffer.size());
            parcel.setDataPosition(0);
            byteBuffer.close();

            retParcel = parcel.readParcelable(theClass.getClassLoader());
        } catch (FileNotFoundException e) {
            Log.error("deserializeBundle", e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.error("deserializeBundle", e);
        } catch (Exception e) {
            Log.error("deserializeBundle", e);
        } finally {
            parcel.recycle();
        }

        return retParcel;
    }

    //-----------------------------------------------------------------------------
    //-----------------

    /**
     * to get a parcelable array from a List: Parcelable[] parArray=(theTypeOfTheList[]) theList.toArray(new
     * theTypeOfTheList[theList.size()]);
     * <p>
     * Example: FileUtil.getInstance().storeParcelableArray(getApplicationContext(),"parcelablearray",(VideaAsset[])results.toArray(new
     * VideaAsset[results.size()]));
     */
    public synchronized void storeParcelableArray(Context context, String filename, Parcelable[] parcelableArray) {
        Parcel parcel2 = Parcel.obtain();

        try {
            parcel2.writeParcelableArray(parcelableArray, 0);
            FileOutputStream bos = new FileOutputStream(FileUtil.storageDir(context) + filename);
            final GZIPOutputStream zos = new GZIPOutputStream(new BufferedOutputStream(bos));
            zos.write(parcel2.marshall());
            zos.close();
            bos.close();
        } catch (FileNotFoundException e) {
            Log.error("storeParcelableArray, FileNotFoundException", e);
        } catch (IOException e) {
            Log.error("storeParcelableArray, IOException", e);
        } finally {
            parcel2.recycle();
        }
    }

    //-----------------------------------------------------------------------------
    //-----------------

    /**
     * to get the list from the parcelable array: Parcelable[] parcelableArray=FileUtil.getInstance().readParcelableArray(getApplicationContext(),"parcelablearray",VideaAsset[].class);
     * List<VideaAsset> results2=Arrays.asList((VideaAsset[])parcelableArray);
     */
    public synchronized Parcelable[] readParcelableArray(Context context, String filename, Class theClass) {
        Parcelable[] parcelableArray = null;
        final Parcel parcel = Parcel.obtain();
        try {
            final ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            final byte[] buffer = new byte[1024];
            final GZIPInputStream zis = new GZIPInputStream(new FileInputStream(storageDir(context) + filename));
            int len = 0;
            while ((len = zis.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            zis.close();
            parcel.unmarshall(byteBuffer.toByteArray(), 0, byteBuffer.size());
            parcel.setDataPosition(0);
            byteBuffer.close();

            Parcelable[] array = parcel.readParcelableArray(theClass.getClassLoader());
            parcelableArray = (Parcelable[]) Arrays.copyOf(array, (array).length, theClass);
        } catch (FileNotFoundException e) {
            Log.error("deserializeBundle, FileNotFoundException", e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.error("deserializeBundle, IOException", e);
        } catch (Exception e) {
            Log.error("deserializeBundle, Exception", e);
        } finally {
            parcel.recycle();
        }

        return parcelableArray;
    }

}