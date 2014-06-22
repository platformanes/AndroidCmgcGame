package com.cmgc.ane 
{ 
	/**
	 * 
	 * @author Rect  2013-5-6 
	 * @see www.shadowkong.com
	 * @see rectvv#gamil.com 
	 */
	public class CmgcEvents 
	{ 
		public function CmgcEvents()
		{
		} 
		/**************************平台通知************************************/
		/**
		 *init 
		 */		
		public static const CMGC_SDK_STATUS:String = "CmgcInit";
		/**
		 * music
		 */
		public static const CMGC_MORE_STATUS : String = "CmgcMoreGame";
		
		/**
		 * 用户注销
		 */
		public static const CMGC_EXIT_STATUS : String = "CmgcExit";
		
		/**
		 * 充值
		 */
		public static const CMGC_PAY_STATUS : String = "CmgcPay";
		
		public static const CMGC_MUSIC_STATUS:String = "CmgcIsMusic";
	} 
}