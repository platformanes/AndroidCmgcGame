/*
 * @(#) NetThread.java Created Date: 2009-3-5 Copyright (c) Jiangsu Ecode Co., Ltd This software is the confidential and
 * proprietary information of Jiangsu Ecode Co., Ltd. ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into
 * with Jiangsu Ecode Co., Ltd.
 */
package com.cmgc.func;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

/**
 * Thread to access network.
 * 
 * @author tianlu
 * @version 1.0 Create At : 2010-2-16 PM 09:37:31
 */
public class NetThread extends Thread {

	/** Tag or name of this class. */
	private final static String TAG = "CmgcPay_NetThread";

	/** All running threads. */
	private static final List<NetThread> ALL_RUNNING_THREADS = new ArrayList<NetThread>();

	/** Message flag of Establishing. */
	public static final int NET_ESTABLISHING = 0x1001;

	/** Message flag of Sending. */
//	public static final int NET_SENDING = 0x1002;

	/** Message flag of Requesting. */
//	public static final int NET_REQUESTING = 0x1003;

	/** Message flag of success. */
	public static final int NET_SUCCESS = 0x1004;

	/** Message flag of download success. */
//	public static final int NET_SUCCESS_DOWNLOAD = 0x1005;

	/** Message flag of upload success. */
//	public static final int NET_SUCCESS_UPLOAD = 0x1006;

	/** Message flag of uploading. */
	public static final int NET_UPLOADING = 0x1007;

	/** Message flag of downloading. */
	public static final int NET_DOWNLOADING = 0x1008;

	/** Message flag of access error. */
	public static final int NET_ERROR = 0x1010;

	/** Message flag of server error. */
	public static final int NET_SERVER_ERROR = 0x1011;

	/** Thread status flag of actived. */
	public static final int THREAD_ACTIVE = 1;

	/** Thread status flag of interruped. */
	public static final int THREAD_INTERRUPED = 2;

	/** Thread status flag of uploading. */
	public static final int THREAD_UPLOAD = 4;

	/** Thread status flag of downloading. */
	public static final int THREAD_DOWNLOAD = 8;

	/** Thread status flag of downloading image. */
	public static final int THREAD_DOWNLOAD_IMG = 16;

	/** Http method of GET. */
	public static final String METHOD_GET = "GET";

	/** Http method of POST. */
	public static final String METHOD_POST = "POST";

	/** Wrong number of retrying. */
	public static final int ERROR_RETRY_COUNT = 3;

	/** Max count of redirection */
	public static final int REDIRECTION_MAX_COUNT = 6;

	/** Buffer size for writing data to stream or reading from stream. */
	protected final static int BUF_SIZE = 512;

	/** Session of current logged user. */
	public static String session = null;

	/** Timeout for establishing connection */
	private static int connectionTimeout = 5000;

	/** Timeout for reading data */
	private static int readDataTimeout = 8000;

	/** Timeout for reading images data */
	private static int readImgTimeout = 15000;

	/** Time interval for retrying making connection */
	private static int retryInterval = 2000;

	private int mErrorRetryCount = ERROR_RETRY_COUNT;

	/** Information's id relative to this thread, for example download images of specified information. */
	protected int mRecId = -1;

	/** Http url connection */
	protected HttpURLConnection mConnection = null;

	/** Index of request action. */
	protected int mAction = -1;

	/**
	 * Set request action.
	 * 
	 * @param action
	 *            Index of request action.
	 */
	public void setAction(int action) {
		this.mAction = action;
	}

	protected boolean mStopped = false;

	/**
	 * Whether the thread has been stopped.
	 * 
	 * @return
	 */
	public boolean isStopped() {
		return mStopped;
	}

	/** Set thread status flag as 1. */
	protected int mActiveFlag = THREAD_ACTIVE;

	/**
	 * Set the thread's current work status, it can be a multi-status by bit arithmetic.
	 * 
	 * @param activeFlag
	 *            Thread's active flag.
	 */
	public void setActiveFlag(int activeFlag) {
		this.mActiveFlag = activeFlag;

		// If activeFlag is interrupted,then interrupt this thread.
		if ((activeFlag & THREAD_INTERRUPED) == THREAD_INTERRUPED) {
			try {
				mStopped = true;
				interrupt();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			mStopped = false;
		}
	}

	/** Command of request action,like "cmd=JPDanji" */
	protected String mCmd = null;

	/**
	 * Set command of request action.
	 * 
	 * @param cmd
	 *            Command value.
	 */
	public void setCmd(String cmd) {
		this.mCmd = cmd;
	}

	/** Command key of request action,like "JPDanji". */
	protected String mCmdKey = null;

	/**
	 * Set command key of request action.
	 * 
	 * @param cmdKey
	 *            Command key.
	 */
	public void setCmdKey(String cmdKey) {
		this.mCmdKey = cmdKey;
	}

	/** Request data,use byte array. */
	protected byte[] mContent = null;

	/**
	 * Set then content need upload to server.
	 * 
	 * @param content
	 */
	public void setContent(byte[] content) {
		this.mContent = content;
	}

	/**
	 * Define a handler to handle response message.
	 */
	protected Handler mHandler = null;

	/** HashMap to save http headers. */
	protected HashMap<String, String> mHeaders = null;

	/** Set default request method. */
	protected String mRequestMethod = METHOD_GET;

	/**
	 * Add a request header to headers.
	 * 
	 * @param key
	 *            RSAUtils of header.
	 * @param value
	 *            Value of header.
	 */
	public void setRequestHeader(String key, String value) {
		getHeaders().put(key, value);
	}

	/**
	 * Set request method.
	 * 
	 * @param requestMethod
	 *            Request method.
	 */
	public void setRequestMethod(String requestMethod) {
		this.mRequestMethod = requestMethod;
	}

	/**
	 * Request url address.
	 */
	protected CharSequence mUrl = null;

	/**
	 * Byte array of data.
	 */
	protected byte[] mData = new byte[BUF_SIZE];

	/**
	 * InputStream for uploading.
	 */
	private InputStream mInputStream = null;

	/**
	 * OutputStream for downloading.
	 */
	private OutputStream mOutputStream = null;

	/** The count of redirection */
	private int mRedirectCount = 0;

  private IServerRsp mResponse;

	public NetThread(CharSequence url, IServerRsp response) {
		this.mUrl = url;
    mResponse = response;
	}

	/**
	 * Get http headers.
	 */
	public HashMap<String, String> getHeaders() {
		if (mHeaders == null) {
			mHeaders = new HashMap<String, String>();
		}
		return mHeaders;
	}

	/**
	 * Get response http headers.
	 */
	protected Map<String, List<String>> getResponseHeader() {
		if (mConnection != null)
			return mConnection.getHeaderFields();
		return null;
	}

	@Override
	public void run() {
		if (mUrl == null || "".equals(mUrl)) {
			Log.e(TAG, "URL is null");
			sendMessage(NET_ERROR, null);
			return;
		}

		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		HttpURLConnection.setFollowRedirects(false);
		ALL_RUNNING_THREADS.add(this);

		// Number of errors.
		int count = 0;
		// HTTP server result status code.
		Integer serverResultCode = null;
		// If error happened,retry three times.
		while (count < mErrorRetryCount && !mStopped) {
			int timeoutN = count + 1;

			// If an error occurs, retry in one second, for avoiding errors caused by bad signal.
			if (count > 0) {
				try {
					Thread.sleep(retryInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// Too much redirections
			if (mRedirectCount > REDIRECTION_MAX_COUNT) {
				sendMessage(NET_ERROR, null);
				return;
			}

			byte[] results = null;
			try {
				// Send a message to notify network access is establishing.
				sendMessage(NET_ESTABLISHING, null);
				URL originalUrl = new URL(mUrl.toString());
				URL u = null;

//				if (GameBillingMain.isUseProxy()) {
//					String query = originalUrl.getQuery();
//					u = new URL("http://10.0.0.172" + originalUrl.getPath()
//							+ (query != null ? "?" + query : ""));
//				} else {
//					u = originalUrl;
//				}

        u = originalUrl;
				Log.i(TAG, ""+u.toString());
				mConnection = (HttpURLConnection) u.openConnection();

				if (mContent != null) {
					mConnection.setRequestProperty("Content-Length", "" + mContent.length);
				}

				if (mHeaders != null && !mHeaders.isEmpty()) {
					Set<Map.Entry<String, String>> set = mHeaders.entrySet();
					for (Entry<String, String> entry : set) {
						if (mConnection.getRequestProperty(entry.getKey()) == null) {
							mConnection.setRequestProperty(entry.getKey(), entry.getValue());
						}
					}
				}
				// Whether use proxy.
//				if (GameBillingMain.isUseProxy()) {
//					mConnection.setRequestProperty("X-Online-Host", originalUrl.getHost());
//				}

				// Set request method.
				mConnection.setRequestMethod(mRequestMethod);
				// Set time of connection timeout(millisecond).
				mConnection.setConnectTimeout(connectionTimeout * timeoutN);

				// Set time of read data timeout(millisecond).
				if ((mActiveFlag & THREAD_DOWNLOAD) != THREAD_DOWNLOAD
						&& (mActiveFlag & THREAD_UPLOAD) != THREAD_UPLOAD) {
					mConnection.setReadTimeout(readDataTimeout * timeoutN);
				}
				// Set time of read image data timeout(millisecond).
				if ((mActiveFlag & THREAD_DOWNLOAD_IMG) != THREAD_DOWNLOAD_IMG) {
					mConnection.setReadTimeout(readImgTimeout * timeoutN);
				}
				mConnection.setDoInput(true);
				mConnection.setDoOutput(true);

				// Create a connection if not have a connection.
				mConnection.connect();
				// If exist data, send it out.
				if ((mContent != null || mInputStream != null)
						&& mRequestMethod.equals(METHOD_POST)) {
//					sendMessage(NET_SENDING, null);
					if (mInputStream != null) {
						uploadFile(mConnection.getOutputStream(), mInputStream);
					} else {
						uploadBytes(mConnection.getOutputStream(), mContent);
					}
//					sendMessage(NET_SUCCESS_UPLOAD, "");
				}
//				sendMessage(NET_REQUESTING, null);
				// Get response status code.
				int resultCode = mConnection.getResponseCode();
				Log.i(TAG, ""+resultCode);
				switch (resultCode) {
				case HttpURLConnection.HTTP_OK:
					// Read data from connection.
					InputStream in = mConnection.getInputStream();
					String cookies = mConnection.getHeaderField("Set-Cookie");
					if (cookies != null) {
						int n = cookies.indexOf(';');
						if (n > -1) {
							session = cookies.substring(0, n);
						}
					}
          Log.i(TAG, "mActivateFlag="+mActiveFlag);
					if ((mActiveFlag & THREAD_DOWNLOAD) == THREAD_DOWNLOAD && mOutputStream != null) {
						downloadFile(mOutputStream, in, mConnection.getContentLength());

						// Send a message represents success to download data.
//						sendMessage(NET_SUCCESS_DOWNLOAD, "");
					} else {

						results = downloadBytes(in, mConnection.getContentLength());
            Log.i(TAG, "downloadBytes1="+new String(results));
            mResponse.onResult(NET_SUCCESS, new String(results));

						// Send a message represents successfully request with
						// response message(byte[]).
						//sendMessage(NET_SUCCESS, results);
//            mHandler.obtainMessage(NET_SUCCESS, 0, 0, results).sendToTarget();
					}
					mStopped = true;
					break;
				case HttpURLConnection.HTTP_MOVED_PERM:
				case HttpURLConnection.HTTP_MOVED_TEMP:
				case HttpURLConnection.HTTP_SEE_OTHER:
					mRedirectCount++;
					String header = mConnection.getHeaderField("Location");
					if (header != null) {
						if (header.toLowerCase().indexOf(originalUrl.getProtocol() + "://") < 0) {
							mUrl = originalUrl.getProtocol() + "://" + originalUrl.getHost()
									+ header;
						} else {
							mUrl = header;
						}
					}
					break;
				case HttpURLConnection.HTTP_NOT_FOUND:
					mStopped = true;
					sendMessage(NET_ERROR, null);
				case -1:
					// No valid response code
					count++;
					break;
				default:
					// Other error response code from server.
					serverResultCode = resultCode;
					count++;
					break;
				}

			} catch (Exception e) {
				serverResultCode = null;
				count++;
			}

			if (mConnection != null) {
				try {
					mConnection.disconnect();
					mConnection = null;
				} catch (Exception e) {

				}
			}
		}

		if (count > ERROR_RETRY_COUNT - 1) {
			if (serverResultCode != null) {
				sendMessage(NET_SERVER_ERROR, serverResultCode);
			} else {
				sendMessage(NET_ERROR, null);
			}
		}

		ALL_RUNNING_THREADS.remove(this);
	}

	/**
	 * Send a message to handler.
	 * 
	 * @param what
	 *            User-defined message code so that the recipient can identify what this message is about.
	 * @param obj
	 *            Message data.
	 */
	protected void sendMessage(int what, Object obj) {
		// Don't send any messages if thread status is not active or is
		// interrupted.
		if (mHandler == null || (mActiveFlag & THREAD_ACTIVE) != THREAD_ACTIVE
				|| (mActiveFlag & THREAD_INTERRUPED) == THREAD_INTERRUPED)
			return;

		// Don't send upload messages if thread is not a upload thread.
		if ((mActiveFlag & THREAD_UPLOAD) != THREAD_UPLOAD && what == NET_UPLOADING)
			return;

		// Don't send download messages if thread is not a download thread.
		if ((mActiveFlag & THREAD_DOWNLOAD) != THREAD_DOWNLOAD && what == NET_DOWNLOADING)
			return;

		Message msg = mHandler.obtainMessage(what, mAction, mRecId, obj);
		// Add response headers.
		if (what == NET_SUCCESS) {
			Map<String, List<String>> responseHeaders = getResponseHeader();
			if (responseHeaders != null && responseHeaders.size() > 0) {
				Set<Entry<String, List<String>>> set = responseHeaders.entrySet();
				for (Entry<String, List<String>> entry : set) {
					List<String> values = entry.getValue();
					if (values != null && values.size() > 0) {
						msg.getData().putString(entry.getKey().toLowerCase(), values.get(0));
					}
				}
			}
		}

		/*
		 * Calling sendMessage would enqueue this message, send it asynchronous, and this message can update UI thread.
		 * Calling dispatchMessage would send it now synchronized, and this message cannot update UI thread.
		 */
		mHandler.sendMessage(msg);
	}

	/**
	 * Read bytes from InputStream and send a message with params of downloaded size and total size.
	 * 
	 * @param in
	 *            Object of InputStream.
	 * @return Byte array.
	 * @throws IOException
	 */
	protected byte[] downloadBytes(InputStream in, int contentLength) throws IOException {
		// Read data length.
		int length = 0;
		// Downloaded size.
		int size = contentLength;
		// Uploaded size.
		int hasDownloaded = 0;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		while ((length = in.read(mData)) != -1 && !mStopped) {
			bos.write(mData, 0, length);
			hasDownloaded += length;
			sendMessage(NET_DOWNLOADING, new int[] { hasDownloaded, size });
		}
		// Close input stream.
		in.close();
		byte[] result = bos.toByteArray();
		// Close byte stream.
		bos.close();

		return result;

	}

	/**
	 * Download file from output stream and send a message with params of downloaded size and total size. First read
	 * data from input stream to byte array,then write to output stream,last download it.
	 * 
	 * @param out
	 *            Object of output stream.
	 * @param in
	 *            Object of input stream.
	 * @param contentLength
	 *            length of download content.
	 * @throws IOException
	 */
	protected void downloadFile(OutputStream out, InputStream in, int contentLength)
			throws IOException {
		// Read data length.
		int length = 0;
		// Downloaded size.
		int size = contentLength;
		// Uploaded size.
		int hasDownloaded = 0;

		while ((length = in.read(mData)) != -1 && !mStopped) {
			out.write(mData, 0, length);
			hasDownloaded += length;
			sendMessage(NET_DOWNLOADING, new int[] { hasDownloaded, size });
		}
		// Close input stream.
		in.close();

		out.flush();
		out.close();

	}

	/**
	 * Stop access network.
	 */
	public void stopSelf() {
		setActiveFlag(THREAD_INTERRUPED);
	}

	/**
	 * Write byte array data to output stream, and send a message with uploaded size and total size.
	 * 
	 * @param out
	 *            OutputStream for writing data.
	 * @param datas
	 *            Byte array data.
	 * @throws IOException
	 */
	protected void uploadBytes(OutputStream out, byte[] datas) throws IOException {
		// Read data length.
		int length = 0;
		// Uploaded size.
		int hasUploaded = 0;
		// Upload size needed.
		int size = datas.length;

		ByteArrayInputStream bis = new ByteArrayInputStream(datas);

		while ((length = bis.read(mData)) != -1 && !mStopped) {
			out.write(mData, 0, length);
			hasUploaded += length;
			sendMessage(NET_UPLOADING, new int[] { hasUploaded, size });
		}
		// Close upload output stream.
		out.flush();
		out.close();
		// Close byte input stream.
		bis.close();
	}

	/**
	 * Upload file to server and send a uploading message with uploaded size.
	 * 
	 * @param out
	 *            Object of OutputStream.
	 * @throws IOException
	 */
	protected void uploadFile(OutputStream out, InputStream in) throws IOException {
		// Read data length.
		int length = 0;
		// Uploaded size.
		int hasUploaded = 0;

		while ((length = in.read(mData)) != -1 && !mStopped) {
			out.write(mData, 0, length);
			hasUploaded += length;
			sendMessage(NET_UPLOADING, new int[] { hasUploaded });
		}
		// Close uploading output stream.
		out.flush();
		out.close();
		// Close byte input stream.
		in.close();
	}
}
