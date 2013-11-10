package com.cmgc.ane 
{ 
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	import flash.events.StatusEvent;
	import flash.external.ExtensionContext;
	
	/**
	 * 
	 * @author Rect  2013-5-6 
	 * @see www.shadowkong.com
	 * @see rectvv#gamil.com
	 * 
	 */
	public class CmgcExtension extends EventDispatcher 
	{ 
		public static const CMGC_FUNCTION_INIT:String = "cmgc_function_init";//与java端中Map里的key一致
		public static const CMGC_FUNCTION_MORE:String = "cmgc_function_more";//与java端中Map里的key一致
		public static const CMGC_FUNCTION_PAY:String = "cmgc_function_pay";//与java端中Map里的key一致
		public static const CMGC_FUNCTION_MUSIC:String = "cmgc_function_music";//与java端中Map里的key一致
		public static const CMGC_FUNCTION_EXIT:String = "cmgc_function_exit";//与java端中Map里的key一致
		
		public static const EXTENSION_ID:String = "com.cmgc.ane";//与extension.xml中的id标签一致
		private var extContext:ExtensionContext;
		
		/**单例的实例*/
		private static var _instance:CmgcExtension; 
		public function CmgcExtension(target:IEventDispatcher=null)
		{
			super(target);
			if(extContext == null) {
				extContext = ExtensionContext.createExtensionContext(EXTENSION_ID, "");
				extContext.addEventListener(StatusEvent.STATUS, statusHandler);
			}
			
		} 
		
		//第二个为参数，会传入java代码中的FREExtension的createContext方法
		/**
		 * 获取实例
		 * @return DLExtension 单例
		 */
		public static function getInstance():CmgcExtension
		{
			if(_instance == null) 
				_instance = new CmgcExtension();
			return _instance;
		}
		
		/**
		 * 转抛事件
		 * @param event 事件
		 */
		private function statusHandler(event:StatusEvent):void
		{
			dispatchEvent(event);
		}
		
		/**
		 * 
		 * @param isCustom 是否自定义计费界面
		 * @param arguments 标识串
		 * @param gameName  移动计费界面展现的游戏名称
		 * @param provider 移动计费界面展现的游戏提供商名称
		 * @param serviceTel 移动计费界面展现的客服电话
		 * @return 
		 * 
		 */			
		public function CmgcInit(
			isCustom:Boolean,arguments:String,
			gameName:String = "Rect",provider:String = "www.shadowkong.com",serviceTel:String = "rectvv@gmail.com"):String{
			if(extContext ){
				return extContext.call(CMGC_FUNCTION_INIT,isCustom,arguments,gameName,provider,serviceTel) as String;
			}
			return "call CmgcInit failed";
		} 
		
		/**
		 * 更多游戏 
		 * @param key
		 * @return 
		 * 
		 */			
		public function CmgcMoreGame(key:int):String{
			if(extContext ){
				return extContext.call(CMGC_FUNCTION_MORE,key) as String;
			}
			return "call CmgcMoreGame failed";
		} 
		
		public function CmgcIsMusic():String
		{
			if(extContext){ 
				return extContext.call(CMGC_FUNCTION_MUSIC)as String;
			}
			return "call CmgcIsMusic failed";
		}
		
		/**
		 * 
		 * @param isUseSms 选择采用短信计费还是联网计费方式
		 * @param isRepeated 该计费点是否是非强制计费点
		 * @param billingIndex 计费点索引
		 * @return 
		 * 
		 */		
		public function CmgcPay(isUseSms:Boolean,isRepeated:Boolean,billingIndex:String):String{
			if(extContext){ 
				return extContext.call(CMGC_FUNCTION_PAY,isUseSms,isRepeated,billingIndex)as String;
			}
			return "call CmgcPay failed";
		}
		
		/**
		 *退出SDK时候调用   这个函数只在退出游戏的时候调用  
		 * @param key
		 * @return 
		 * 
		 */		
		public function CmgcExit(key:int):String{
			if(extContext){ 
				return extContext.call(CMGC_FUNCTION_EXIT,key) as String;
			}
			return "call exit failed";
		}
	} 
}