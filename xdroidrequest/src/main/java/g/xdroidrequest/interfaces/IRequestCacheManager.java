package g.xdroidrequest.interfaces;

import java.io.File;

import g.xdroidrequest.base.Request;

/**
 * Request cache manager interface
 * 
 * @author Robin
 * @since 2015-12-30 20:34:42
 *
 */
public interface IRequestCacheManager {

	// ----------------------------------------Disk-------------------------------------------

	public File getDiskCacheDirectory();

	public long getDiskCacheMaxSize();

	public void setDiskCacheMaxSize(long maxSize);

	public long getAllDiskCacheSize();

	public void deleteAllDiskCacheData();

	public Boolean deleteOneDiskCacheData(String originalKey);

	public Boolean deleteOneDiskCacheData(Request<?> request);

	public Boolean deleteOneDiskCacheData(Request<?> request, String originalKey);

	public <T> T getDataFromDiskCache(String originalKey);

	// ----------------------------------------Memory-------------------------------------------

	public void deleteOneMemoryCacheData(String key);

	public void deleteOneMemoryCacheData(Request<?> request);

	public void deleteOneMemoryCacheData(Request<?> request, String key);

	public void deleteAllMemoryCacheData();

	public <T> T getDataFromMemoryCache(String key);

}
