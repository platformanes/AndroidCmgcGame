package com.cmgc.ane;

import java.util.HashMap;
import java.util.Map;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.cmgc.func.CmgcExit;
import com.cmgc.func.CmgcInit;
import com.cmgc.func.CmgcIsMusic;
import com.cmgc.func.CmgcMoreGame;
import com.cmgc.func.CmgcPay;

/**
 * @author Rect
 * @see rectvv@gmail.com<p>www.shadowkong.com
 * @version  Time：2013-5-8 
 */
public class CmgcContext extends FREContext {
	/**
	 * INIT sdk
	 */
	public static final String CMGC_FUNCTION_INIT = "cmgc_function_init";
	/**
	 * 付费Key
	 */
	public static final String CMGC_FUNCTION_PAY = "cmgc_function_pay";
	/**
	 * more game
	 */
	public static final String CMGC_FUNCTION_MORE = "cmgc_function_more";
	/**
	 * music game
	 */
	public static final String CMGC_FUNCTION_MUSIC = "cmgc_function_music";
	/**
	 * 退出Key
	 */
	public static final String CMGC_FUNCTION_EXIT = "cmgc_function_exit";
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, FREFunction> getFunctions() {
		// TODO Auto-generated method stub
		Map<String, FREFunction> map = new HashMap<String, FREFunction>();
//	       //映射
		   map.put(CMGC_FUNCTION_INIT, new CmgcInit());
	       map.put(CMGC_FUNCTION_PAY, new CmgcPay());
	       map.put(CMGC_FUNCTION_MORE, new CmgcMoreGame());
	       map.put(CMGC_FUNCTION_MUSIC, new CmgcIsMusic());
	       map.put(CMGC_FUNCTION_EXIT, new CmgcExit());
	       return map;
	}

}
