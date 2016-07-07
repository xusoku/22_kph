package com.davis.kangpinhui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import junit.framework.Assert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;

/**
 * 微信分享 需要正式签名打包可用
 * 
 * @author bobo
 * 
 */
public class WeiXinUtil {
	private static String TAG = WeiXinUtil.class.getSimpleName();
	private static IWXAPI api;
	private static String APP_ID = "wx962774b475625f93";
//	AppID：wx962774b475625f93
	private static Context context;

	public static IWXAPI WXInit(Context context) {
		WeiXinUtil.context = context;
		api = WXAPIFactory.createWXAPI(context, APP_ID);
		api.registerApp(APP_ID);
		return api;
	}

	public static void WXShare(final Context activity, String title,String description,Bitmap decodeResource,
			String targetUrl) {
		if (!api.isWXAppInstalled()) {
			Toast.makeText(context, "亲,你还未安装微信~~", Toast.LENGTH_SHORT).show();
			return;
		}
		WXWebpageObject webpageObject = new WXWebpageObject(targetUrl);

		final WXMediaMessage msg = new WXMediaMessage();
		
		// msg.setThumbImage(extractThumbNail(filePath, 150, 150, true));
		msg.title = title;
		msg.description = description;
		msg.mediaObject = webpageObject;
		msg.thumbData=Bitmap2Bytes(decodeResource);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("appdata");
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
	}
	public static void WXSharePengyou(final Context activity,String title,
			String description, Bitmap decodeResource,String targetUrl) {
		if (!api.isWXAppInstalled()) {
			Toast.makeText(context, "亲,你还未安装微信~~", Toast.LENGTH_SHORT).show();
			return;
		}
		WXWebpageObject webpageObject = new WXWebpageObject(targetUrl);
		
		final WXMediaMessage msg = new WXMediaMessage();
		
		// msg.setThumbImage(extractThumbNail(filePath, 150, 150, true));
		msg.title = title;
		msg.description = description;
		msg.mediaObject = webpageObject;
		msg.thumbData=Bitmap2Bytes(decodeResource);
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("appdata");
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);
	}
	
	public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

	private static String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;

	@SuppressWarnings("unused")
	private static Bitmap extractThumbNail(final String path, final int height,
			final int width, final boolean crop) {
		Assert.assertTrue(path != null && !path.equals("") && height > 0
				&& width > 0);

		BitmapFactory.Options options = new BitmapFactory.Options();

		try {
			options.inJustDecodeBounds = true;
			Bitmap tmp = BitmapFactory.decodeFile(path, options);
			if (tmp != null) {
				tmp.recycle();
				tmp = null;
			}

			Log.d(TAG, "extractThumbNail: round=" + width + "x" + height
					+ ", crop=" + crop);
			final double beY = options.outHeight * 1.0 / height;
			final double beX = options.outWidth * 1.0 / width;
			Log.d(TAG, "extractThumbNail: extract beX = " + beX + ", beY = "
					+ beY);
			options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY)
					: (beY < beX ? beX : beY));
			if (options.inSampleSize <= 1) {
				options.inSampleSize = 1;
			}

			// NOTE: out of memory error
			while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
				options.inSampleSize++;
			}

			int newHeight = height;
			int newWidth = width;
			if (crop) {
				if (beY > beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			} else {
				if (beY < beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			}

			options.inJustDecodeBounds = false;

			Log.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight
					+ ", orig=" + options.outWidth + "x" + options.outHeight
					+ ", sample=" + options.inSampleSize);
			Bitmap bm = BitmapFactory.decodeFile(path, options);
			if (bm == null) {
				Log.e(TAG, "bitmap decode failed");
				return null;
			}

			Log.i(TAG,
					"bitmap decoded size=" + bm.getWidth() + "x"
							+ bm.getHeight());
			final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth,
					newHeight, true);
			if (scale != null) {
				bm.recycle();
				bm = scale;
			}

			if (crop) {
				final Bitmap cropped = Bitmap.createBitmap(bm,
						(bm.getWidth() - width) >> 1,
						(bm.getHeight() - height) >> 1, width, height);
				if (cropped == null) {
					return bm;
				}

				bm.recycle();
				bm = cropped;
				Log.i(TAG,
						"bitmap croped size=" + bm.getWidth() + "x"
								+ bm.getHeight());
			}
			return bm;

		} catch (final OutOfMemoryError e) {
			Log.e(TAG, "decode bitmap failed: " + e.getMessage());
			options = null;
		}

		return null;
	}

	@SuppressWarnings("unused")
	private static byte[] readFromFile(String fileName, int offset, int len) {
		if (fileName == null) {
			return null;
		}

		File file = new File(fileName);
		if (!file.exists()) {
			Log.i(TAG, "readFromFile: file not found");
			return null;
		}

		if (len == -1) {
			len = (int) file.length();
		}

		Log.d(TAG, "readFromFile : offset = " + offset + " len = " + len
				+ " offset + len = " + (offset + len));

		if (offset < 0) {
			Log.e(TAG, "readFromFile invalid offset:" + offset);
			return null;
		}
		if (len <= 0) {
			Log.e(TAG, "readFromFile invalid len:" + len);
			return null;
		}
		if (offset + len > (int) file.length()) {
			Log.e(TAG, "readFromFile invalid file len:" + file.length());
			return null;
		}

		byte[] b = null;
		try {
			RandomAccessFile in = new RandomAccessFile(fileName, "r");
			b = new byte[len]; // 创建合适文件大小的数组
			in.seek(offset);
			in.readFully(b);
			in.close();

		} catch (Exception e) {
			Log.e(TAG, "readFromFile : errMsg = " + e.getMessage());
			e.printStackTrace();
		}
		return b;
	}
	
//	public static void handleGetShareImage(String imagePath){
//		LogX.getInstance().d(TAG, "分享二维码,下载iamgePath="+imagePath);
//		if (imagePath == null) {
//			return;
//		}
//		imagePath = imagePath.trim();
//		if (TextUtils.isEmpty(imagePath)) {
//			return;
//		}
//		File erweima_cache_dir=new File(Constants.ERWEIMA_CACHE_DIR);
//		if(!erweima_cache_dir.exists())
//		{
//			erweima_cache_dir.mkdirs();
//		}
//		try {
//			URL url = new URL(imagePath); 
//			HttpURLConnection rulConnection = (HttpURLConnection) url.openConnection();
//			InputStream inputStream = rulConnection.getInputStream();
//			FileOutputStream fileOutputStream= new FileOutputStream(Constants.ERWEIMA_CACHE_DIR+File.separator+QianbaoApplication.getInstance().getUser().getUsername()+".jpg");
//			int i=0;
//			byte[] b=new byte[1024];
//			while((i=inputStream.read(b))!=-1)
//			{
//				fileOutputStream.write(b, 0, i);
//			}
//			fileOutputStream.flush();
//			fileOutputStream.close();
//			inputStream.close();
//		} catch (Exception e) {
//			LogX.getInstance().d(TAG, "分享二维码,下载失败");
//			e.printStackTrace();
//		}
//	}
	

}
