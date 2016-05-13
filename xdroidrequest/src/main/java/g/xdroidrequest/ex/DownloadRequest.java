package g.xdroidrequest.ex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import g.xdroidrequest.config.HttpMethod;
import g.xdroidrequest.config.Priority;
import g.xdroidrequest.config.RequestCacheConfig;
import g.xdroidrequest.interfaces.OnRequestListener;
import g.xdroidrequest.response.NetworkResponse;
import g.xdroidrequest.response.Response;
import g.xdroidrequest.utils.CLog;

import android.text.TextUtils;

/**
 * Download request
 * @author Robin
 * @since 2016-1-14 18:53:03
 *
 */
public class DownloadRequest extends MultipartRequest<File> {
	
	private String mDownloadPath;
	private String mFileName;

	public DownloadRequest(String downloadPath,String fileName) {
		super();
		this.mDownloadPath = downloadPath;
		this.mFileName = fileName;
		
		setPriority(Priority.NORMAL);
		setHttpMethod(HttpMethod.GET);
	}

	public DownloadRequest(RequestCacheConfig cacheConfig, String url, String cacheKey, String downloadPath,String fileName,
			OnRequestListener<File> onRequestListener) {
		super(cacheConfig, url, cacheKey, onRequestListener);
		
		setPriority(Priority.NORMAL);
		setHttpMethod(HttpMethod.GET);
		setRequestParams(new RequestParams());
		
		this.mDownloadPath = downloadPath;
		this.mFileName = fileName;
	}

	@Override
	public Response<File> parseNetworkResponse(NetworkResponse response) {
		File downloadFile = null;
        try {
            byte[] data = response.data;
		    //convert array of bytes into file
            File directory = new File(mDownloadPath);
            if (!directory.exists()) {
				directory.mkdir();
			}
            
            String path = mDownloadPath;
            if (!TextUtils.isEmpty(mFileName)) {
            	   path = mDownloadPath+File.separator+mFileName;
			}
		    FileOutputStream fileOuputStream = new FileOutputStream(path); 
		    fileOuputStream.write(data);
		    fileOuputStream.close();
		    downloadFile = new File(path);
        } catch (FileNotFoundException e) {
			e.printStackTrace();
			CLog.e("Download directory %s is not exsit",mDownloadPath);
		}  catch (IOException e) {
			e.printStackTrace();
		}
        
        return Response.success(downloadFile, response.headers);
	}

}
