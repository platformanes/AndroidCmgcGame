package com.cmgc.func;

import android.util.Log;

import cn.cmgame.billing.api.GameInterface;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

/**
 * @author Rect
 * @see rectvv@gmail.com<p>www.shadowkong.com
 * @date 2013-11-10
 */
public class CmgcMoreGame implements FREFunction {
	private String TAG = "CmgcMoreGame";
	private FREContext _context;
	@Override
	public FREObject call(final FREContext context, FREObject[] arg1) {
		// TODO Auto-generated method stub
		_context = context;
		FREObject result = null; 
		
		GameInterface.viewMoreGames(_context.getActivity());
		callBack("success");
		return result;
	}
	
	/**
	 * 初始化回调 把初始化结果传给AS端
	 */
	public void callBack(String result){
		Log.d(TAG, "moreGame:"+result);
		_context.dispatchStatusEventAsync(TAG, "moreGame:"+result);
	}

}
