package com.cmgc.func;

import java.util.Date;
import java.util.Random;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.cmgame.billing.api.BillingResult;
import cn.cmgame.billing.api.GameInterface;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

/**
 * 执行付费
 * @author Rect
 * @see rectvv@gmail.com<p>www.shadowkong.com
 * @version  Time：2013-5-8 
 */
public class CmgcPay implements FREFunction {

	private String TAG = "CmgcPay";
	private FREContext _context;
	private Handler mHandler;

	private IServerRsp mResult;
	@Override
	public FREObject call(final FREContext context, FREObject[] arg1) {
		// TODO Auto-generated method stub
		_context = context;
		FREObject result = null; 
		// TODO Auto-generated method stub
		//--------------------------------
		mResult = new IServerRsp() {
			@Override
			public void onResult(int code, String result) {
				callBack("msg="+result);
			}
		};

		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				//		        if(msg.what==0x1004){
				callBack("msg="+msg.what + ", obj="+msg.obj);
				//		        }
			}
		};

		//GameBilling.setExtraArguments(new String[]{"0","0","1"});

		final GameInterface.IPayCallback payCallback = new GameInterface.IPayCallback() {
			@Override
			public void onResult(int resultCode, String billingIndex, Object obj) {
				String result = "";
				switch (resultCode) {
				case BillingResult.SUCCESS:
					result = "购买道具：[" + billingIndex + "] 成功！";
					getPurchaseInfo();
					break;
				case BillingResult.FAILED:
					result = "购买道具：[" + billingIndex + "] 失败！";
					break;
				default:
					result = "购买道具：[" + billingIndex + "] 取消！";
					break;
				}
				callBack(result);
			}
		};

		
		Boolean isUseSms = false;
		Boolean isRepeated = false;
		String billingIndex = "001";
		String cpParam = getPaySerialNo();
		try
		{
			isUseSms = arg1[0].getAsBool();
			isRepeated = arg1[1].getAsBool();
			billingIndex = arg1[2].getAsString();
		}
		catch (Exception e) {
			// TODO: handle exception
			callBack("pay error:"+e.getMessage());
			return null;
		}
		
		GameInterface.doBilling(
				_context.getActivity(), 
				isUseSms, 		//选择采用短信计费还是联网计费方式
				isRepeated, 	//该计费点是否是非强制计费点
				billingIndex, 	//计费点索引
				cpParam, 		//透传参数，此参数由合作方规则（16byte）
				payCallback);

		callBack("pay end");
		return result;
	}



	private void getPurchaseInfo(){
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				NetThread thread = 
					new NetThread(
							"http://122.96.62.126:10086/SecureProxy/api.jsp?type=2&act=get&random="+getRandomNum(6),
							mResult);

				thread.setRequestMethod(NetThread.METHOD_GET);
				thread.setRequestHeader("content-type", "text/html");
				thread.start();
			}
		},2000);

	}

	/**
	 * Get a random number
	 *
	 * @param length
	 *            Length of generated number.
	 * @return Get a random num to indicate a unique request.
	 */
	private String getRandomNum(int length) {
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int seed = random.nextInt(10);
			sb.append(String.valueOf(seed));
		}
		callBack("pay RandomNum=" + sb.toString());
		return sb.toString();
	}

	private String getPaySerialNo() {
		String cpparam = new Date().getTime() + "XYZ";
		callBack("pay serial no=" + cpparam);
		return cpparam;
	}

	/**
	 * 付费回调 把付费结果传给AS端
	 */
	public void callBack(String status){
		Log.d(TAG, "pay callBack:"+status);
		_context.dispatchStatusEventAsync(TAG,status);
	}
}
