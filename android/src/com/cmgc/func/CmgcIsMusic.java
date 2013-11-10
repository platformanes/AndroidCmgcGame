package com.cmgc.func;

import android.util.Log;

import cn.cmgame.billing.api.GameInterface;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

/**
 * @author Rect
 * @see rectvv@gmail.com<p>www.shadowkong.com
 * @version 2013-11-10
 */
public class CmgcIsMusic implements FREFunction {
	private String TAG = "CmgcIsMusic";
	private FREContext _context;
	@Override
	public FREObject call(FREContext context, FREObject[] arg1) {
		// TODO Auto-generated method stub
		_context = context;
		if(GameInterface.isMusicEnabled)
			callBack("yes");
		else
			callBack("no");
		return null;
	}

	/**
	 * CmgcIsMusic传给AS端
	 */
	public void callBack(String result){
		Log.d(TAG, "CmgcIsMusic:"+result);
		_context.dispatchStatusEventAsync(TAG, "CmgcIsMusic:"+result);
	}
	
}
