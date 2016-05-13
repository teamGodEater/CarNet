package g.xdroidrequest;

import java.io.File;

import g.xdroidrequest.base.Request;
import g.xdroidrequest.cache.CacheConfig;
import g.xdroidrequest.config.HttpMethod;
import g.xdroidrequest.config.RequestCacheConfig;
import g.xdroidrequest.ex.DownloadRequest;
import g.xdroidrequest.ex.MultipartGsonRequest;
import g.xdroidrequest.ex.RequestParams;
import g.xdroidrequest.interfaces.IXRequest;
import g.xdroidrequest.interfaces.OnRequestListener;
import g.xdroidrequest.queue.RequestQueue;
import g.xdroidrequest.utils.AppUtils;

import android.content.Context;

/**
 * Encapsulates the request，for the convenience of call
 * 
 * @author Robin
 * @since 2015-08-12 17:43:03
 *
 */
public class XRequest implements IXRequest {

	private static volatile XRequest INSTANCE = null;

	public static XRequest getInstance() {
		if (INSTANCE == null) {
			synchronized (XRequest.class) {
				if (INSTANCE == null) {
					INSTANCE = new XRequest();
				}
			}
		}
		return INSTANCE;
	}

	public static void initXRequest(Context context, long diskCacheMaxSize, File diskCacheDir, int diskCacheAppVersion, int memoryCacheMaxSize) {
		RequestContext.init(context);

		CacheConfig.DISK_CACHE_MAX_SIZE = diskCacheMaxSize;

		CacheConfig.DISK_CACHE_DIRECTORY = diskCacheDir;

		CacheConfig.DISK_CACHE_APP_VERSION = diskCacheAppVersion;

		CacheConfig.MEMORY_CACHE_MAX_SIZE = memoryCacheMaxSize;
	}

	public static void initXRequest(Context context, long diskCacheMaxSize, File diskCacheDir, int diskCacheAppVersion) {
		initXRequest(context, diskCacheMaxSize, diskCacheDir, diskCacheAppVersion, (int) Runtime.getRuntime().maxMemory() / 8);
	}

	public static void initXRequest(Context context, long diskCacheMaxSize, File diskCacheDir) {
		initXRequest(context, diskCacheMaxSize, diskCacheDir, AppUtils.getAppVersion(context), (int) Runtime.getRuntime().maxMemory() / 8);
	}

	public static void initXRequest(Context context, long diskCacheMaxSize) {
		initXRequest(context, diskCacheMaxSize, AppUtils.getDiskCacheDir(context, "xrequest"), AppUtils.getAppVersion(context), (int) Runtime.getRuntime().maxMemory() / 8);
	}

	public static void initXRequest(Context context) {
		RequestContext.init(context);
		CacheConfig.DISK_CACHE_MAX_SIZE = CacheConfig.DEFAULT_MAX_SIZE;
		CacheConfig.DISK_CACHE_DIRECTORY = AppUtils.getDiskCacheDir(context, "xrequest");
		CacheConfig.DISK_CACHE_APP_VERSION = AppUtils.getAppVersion(context);
		CacheConfig.MEMORY_CACHE_MAX_SIZE = (int) Runtime.getRuntime().maxMemory() / 8;
	}

	private RequestQueue queue;

	/**
	 * Best during application initialization calls only once
	 * 
	 * @param threadPoolSize
	 */
	@Override
	public void setRequestThreadPoolSize(int threadPoolSize) {
		if (queue != null) {
			queue.stop();
			queue = null;
		}
		queue = new RequestQueue(threadPoolSize);
		queue.start();
	}

	/**
	 * Add a request to queue to execute
	 * 
	 * @param request
	 *            Target request
	 */
	@Override
	public void addToRequestQueue(Request<?> request) {
		if (queue == null) {
			queue = new RequestQueue();
			queue.start();
		}
		queue.add(request);
	}

	/**
	 * Create a default cache configuration
	 * 
	 * @return
	 */
	@Override
	public RequestCacheConfig getDefaultCacheConfig() {
		return RequestCacheConfig.buildDefaultCacheConfig();
	}

	@Override
	public RequestCacheConfig getNoCacheConfig() {
		return RequestCacheConfig.buildNoCacheConfig();
	}

	/**
	 * To cancel a request that is requesting
	 * 
	 * @param request
	 */
	@Override
	public void cancelRequest(Request<?> request) {
		if (null != request) {
			request.cancel();
		}
	}

	/**
	 * Cancel all of this request in the request queue , not including is
	 * requested
	 * 
	 * @param request
	 *            Current instance of request
	 * @param tag
	 *            If there is no special Settings, then introduction the
	 *            instance of activity
	 */
	@Override
	public void cancelAllRequestInQueueByTag(Object tag) {
		if (queue != null) {
			queue.cancelAll(tag);
		}
	}

	/**
	 * Start the request，start the thread pool
	 */
	@Override
	public void start() {
		if (queue != null) {
			queue.start();
		}
	}

	/**
	 * Close the request, quit all threads, release the request queue
	 */
	@Override
	public void shutdown() {
		if (queue != null) {
			queue.stop();
			queue = null;
		}
	}

	/*
	 * =======================================================================
	 * GET
	 * =======================================================================
	 */

	@Override
	public <T> Request<?> sendGet(Object tag, String url, String cacheKey, RequestParams params, RequestCacheConfig cacheConfig, OnRequestListener<T> onRequestListener) {
		url += params.buildQueryParameters();

		MultipartGsonRequest<T> request = new MultipartGsonRequest<T>(cacheConfig, url, cacheKey, onRequestListener);
		request.setRequestParams(params);
		request.setHttpMethod(HttpMethod.GET);
		request.setTag(tag);

		addToRequestQueue(request);

		return request;

	}

	@Override
	public <T> Request<?> sendGet(Object tag, String url, RequestParams params, OnRequestListener<T> onRequestListener) {
		String cacheKey = url;
		return sendGet(tag, url, cacheKey, params, getDefaultCacheConfig(), onRequestListener);
	}

	@Override
	public <T> Request<?> sendGet(Object tag, String url, String cacheKey, RequestParams params, OnRequestListener<T> onRequestListener) {
		return sendGet(tag, url, cacheKey, params, getDefaultCacheConfig(), onRequestListener);
	}

	@Override
	public <T> Request<?> sendGet(Object tag, String url, OnRequestListener<T> onRequestListener) {
		return sendGet(tag, url, new RequestParams(), onRequestListener);
	}

	@Override
	public <T> Request<?> sendGet(Object tag, String url, String cacheKey, OnRequestListener<T> onRequestListener) {
		return sendGet(tag, url, cacheKey, new RequestParams(), onRequestListener);
	}
	
	@Override
	public <T> Request<?> sendGet(Object tag, String url, RequestParams params, RequestCacheConfig cacheConfig, OnRequestListener<T> onRequestListener) {
		return sendGet(tag, url, url, params, cacheConfig, onRequestListener);
	}

	/*
	 * =======================================================================
	 * POST
	 * =======================================================================
	 */

	@Override
	public <T> Request<?> sendPost(Object tag, String url, String cacheKey, RequestParams params, RequestCacheConfig cacheConfig, OnRequestListener<T> onRequestListener) {
		MultipartGsonRequest<T> request = new MultipartGsonRequest<T>(cacheConfig, url, cacheKey, onRequestListener);
		request.setRequestParams(params);
		request.setHttpMethod(HttpMethod.POST);
		request.setTag(tag);

		addToRequestQueue(request);

		return request;
	}
	
	@Override
	public <T> Request<?> sendPost(Object tag, String url, RequestParams params, RequestCacheConfig cacheConfig, OnRequestListener<T> onRequestListener) {
		return sendPost(tag, url, url, params, cacheConfig, onRequestListener);
	}

	@Override
	public <T> Request<?> sendPost(Object tag, String url, RequestParams params, OnRequestListener<T> onRequestListener) {
		String cacheKey = url;
		return sendPost(tag, url, cacheKey, params, getDefaultCacheConfig(), onRequestListener);
	}

	@Override
	public <T> Request<?> sendPost(Object tag, String url, String cacheKey, RequestParams params, OnRequestListener<T> onRequestListener) {
		return sendPost(tag, url, cacheKey, params, getDefaultCacheConfig(), onRequestListener);
	}

	/*
	 * =======================================================================
	 * Upload
	 * =======================================================================
	 */

	@Override
	public <T> Request<?> upload(Object tag, String url, String cacheKey, RequestParams params, RequestCacheConfig cacheConfig, OnRequestListener<T> onRequestListener) {
		return sendPost(tag, url, cacheKey, params, cacheConfig, onRequestListener);
	}

	@Override
	public <T> Request<?> upload(Object tag, String url, RequestParams params, OnRequestListener<T> onRequestListener) {
		String cacheKey = url;
		return sendPost(tag, url, cacheKey, params, getNoCacheConfig(), onRequestListener);
	}

	@Override
	public <T> Request<?> upload(Object tag, String url, String cacheKey, RequestParams params, OnRequestListener<T> onRequestListener) {
		return sendPost(tag, url, cacheKey, params, getNoCacheConfig(), onRequestListener);
	}

	/*
	 * =======================================================================
	 * Download
	 * =======================================================================
	 */
	@Override
	public Request<?> download(Object tag, String url, String cacheKey, String downloadPath, String fileName, RequestCacheConfig cacheConfig, OnRequestListener<File> onRequestListener) {
		DownloadRequest request = new DownloadRequest(cacheConfig, url, cacheKey, downloadPath, fileName, onRequestListener);
		request.setTag(tag);

		addToRequestQueue(request);

		return request;
	}

	@Override
	public Request<?> download(Object tag, String url, String downloadPath, String fileName, OnRequestListener<File> onRequestListener) {
		return download(tag, url, url, downloadPath, fileName, getNoCacheConfig(), onRequestListener);
	}

}
