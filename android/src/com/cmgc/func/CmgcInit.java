package com.cmgc.func;

import android.util.Log;

import cn.cmgame.billing.api.GameInterface;
import cn.cmgame.billing.api.LoginResult;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

/**
 * 初始化SDK
 * @author Rect
 * @see rectvv@gmail.com<p>www.shadowkong.com
 * @version  Time：2013-5-8 
 */
public class CmgcInit implements FREFunction {

	private String TAG = "CmgcInit";
	private FREContext _context;
	@Override
	public FREObject call(FREContext context, FREObject[] arg1) {
		// TODO Auto-generated method stub
		_context = context;
		FREObject result = null; 
		Boolean isCustom = false;
		String arguments = null;
		try
		{
			//是否自定义计费界面
			isCustom = arg1[0].getAsBool();
			arguments = arg1[1].getAsString();
			if(!isCustom)
				GameInterface.initializeApp(_context.getActivity());
			else
			{
				//移动计费界面展现的游戏名称
				String gameName = arg1[2].getAsString();
				//移动计费界面展现的游戏提供商名称
				String provider = arg1[3].getAsString();
				//移动计费界面展现的客服电话
				String serviceTel = arg1[4].getAsString();
				GameInterface.initializeApp(_context.getActivity(),gameName,provider,serviceTel);

				//args test
				callBack("$gameName="+gameName+"$provider="+provider+"$serviceTel="+serviceTel);
			}

			GameInterface.setExtraArguments(new String[]{arguments});

			GameInterface.setLoginListener(_context.getActivity(),new GameInterface.ILoginCallback() {
				@Override
				public void onResult(int i, String userID, Object o) {

					callBack("Login.Result=" + userID);

					if(i == LoginResult.SUCCESS_IMPLICIT){
						callBack("Login 隐士登录成功 userID:"+userID);
					}
					if(i == LoginResult.SUCCESS_EXPLICIT){
						callBack("Login 显示登录成功 userID:"+userID);
					}
					if(i == LoginResult.FAILED_EXPLICIT){
						callBack("Login 显示登录失败");
					}
				}
			});

		}
		catch (Exception e) {
			// TODO: handle exception
			callBack("error:"+e.getMessage());
			return result;
		}



		callBack("success");
		//--------------------------------

		return result;
	}

	/**
	 * 初始化回调 把初始化结果传给AS端
	 */
	public void callBack(String result){
		Log.d(TAG, "初始化返回:"+result);
		_context.dispatchStatusEventAsync(TAG, result);
	}

}
