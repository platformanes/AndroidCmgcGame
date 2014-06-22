package com.cmgc.func;

import android.util.Log;

import cn.cmgame.billing.api.GameInterface;
import cn.cmgame.billing.api.GameInterface.GameExitCallback;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

/**
 * 退出SDK 清理环境
 * @author Rect
 * @see  rectvv@gmail.com<p>www.shadowkong.com
 * @version  Time：2013-5-8 
 */
public class CmgcExit implements FREFunction ,GameExitCallback{

	private String TAG = "CmgcExit";
	private FREContext _context;
	@Override
	public FREObject call(final FREContext context, FREObject[] arg1) {
		// TODO Auto-generated method stub
		_context = context;
		FREObject result = null; 
		// TODO Auto-generated method stub
		//--------------------------------
		GameInterface.exit(_context.getActivity(),this);
		 
		callBack("success");
		//--------------------------------
		
		return result;
	}
	
	public void onConfirmExit() {
		//确认退出逻辑
		callBack("确认退出");
	}
	@Override
	public void onCancelExit() {
		//取消退出逻辑
		callBack("取消退出");
	}

	/**
	 * 清理环境回调 把清理环境结果传给AS端
	 */
	public void callBack(String result){
		Log.d(TAG, "---------清理环境返回-------");
		_context.dispatchStatusEventAsync(TAG, "清理环境回调:"+result);
	}

}
