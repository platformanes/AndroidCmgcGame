AndroidCmgcGame
===============

中国移动游戏基地Android游戏

## 中国移动游戏基地Android平台的的ANE

* 参照官方DEMO所编写.
* 特别提醒:此SDK相对AIR项目来说需要修改几个特别的地方.正常的方式打包的APK是无法运行的.
* 此ANE调用DEMO在aneTest文件夹中
* 若SDK版本不对会导致闪退，请在本机替换jar ,资源 重新生成ANE
* 注意使用前检查SDK版本
* 不保证SDK升级后完全适用。

## 编写ANE过程

> A.参照我博客的教程[传送门](http://www.shadowkong.com/archives/1090)的前提下
>  
> B.修改ANE源码中的CmgameApplication类的路径为AIR项目包名(eg:air.com.cmge.ane.CmgameApplication)
>
> C.取消合并jar,直接取eclipse中的 `bin/cmgcane.jar` 到`Android-ARM`中 
>  
> D.取官方SDK提供的res 到 Android-ARM中 
>  
> E.修改`android-ARM\res\values\g_string.xml`中的 `g_class_name` 对应值为你项目的包名+`.AppEntry`
>  
> F.取官方DEMO中的libs中的libmegjb.so库 到 `Android-ARM/libs/armeabi-v7a` 中
>  
> G.按照`buildANE`下的bat命令生成ANE(注意配置`本地路径`)
		ANE编写到此结束.下面打包APK才是重中之重

## 打包APK过程
> A.参照 `AndroidCmgcGame\buildAPK\cmgc.bat`中的命令 其中adt路径修改为你的本地路径
>  
> B.下载`apktool`工具(自行g.cn)
>  
> C.在A中生成的APK假设为 `demo.apk`,使用工具反编译:`apktool d demo.apk demo`
>  
> D.修改`demo/AndroidManifest.xml`文件 例如:

### 未修改之前的AndroidManifest.xml

    <application android:label="@string/app_name" android:icon="@drawable/icon" android:hardwareAccelerated="false">
        <activity android:theme="@style/Theme.NoShadow" android:label="@string/app_name" android:name=".AppEntry" android:launchMode="standard" android:screenOrientation="landscape" android:configChanges="keyboardHidden|orientation|screenSize" android:windowSoftInputMode="stateHidden|adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            <meta-data android:name="namespaceVersion" android:value="3.5" />
            <meta-data android:name="autoOrients" android:value="true" />
            <meta-data android:name="fullScreen" android:value="false" />
            <meta-data android:name="uniqueappversionid" android:value="635d4ef7-9e13-464c-b457-b0945c3e9ba3" />
            <meta-data android:name="initialcontent" android:value="androidCmgcSample.swf" />
        </activity>
   

### 修改之后的AndroidManifest.xml

    <application android:label="@string/app_name" android:icon="@drawable/icon" android:hardwareAccelerated="false" android:name=".CmgameApplication"
        android:debuggable="true">
        <activity android:theme="@style/Theme.NoShadow" android:label="@string/app_name" android:name=".AppEntry" android:launchMode="standard" android:configChanges="keyboardHidden|orientation|screenSize" android:windowSoftInputMode="stateHidden|adjustResize">
           
            <meta-data android:name="namespaceVersion" android:value="3.5" />
            <meta-data android:name="autoOrients" android:value="true" />
            <meta-data android:name="fullScreen" android:value="false" />
            <meta-data android:name="uniqueappversionid" android:value="635d4ef7-9e13-464c-b457-b0945c3e9ba3" />
            <meta-data android:name="initialcontent" android:value="androidCmgcSample.swf" />
        </activity>
        
        <activity android:name="cn.cmgame.billing.ui.GameOpenActivity"
						android:theme="@android:style/Theme.NoTitleBar.Fullscreen"
						android:screenOrientation="portrait">
						<intent-filter>
							<action android:name="android.intent.action.MAIN" />
							<category android:name="android.intent.category.LAUNCHER" />
						</intent-filter>
						<intent-filter>
							<action android:name="android.intent.action.CHINAMOBILE_OMS_GAME" />
							<category android:name="android.intent.category.CHINAMOBILE_GAMES" />
						</intent-filter>
					</activity>


    </application>
    
    <supports-screens android:largeScreens="true"
                      android:smallScreens="true"
                      android:anyDensity="true"
                      android:normalScreens="true"/>

> F.适用工具编译APK `apktool b demo demo_sig.apk`
>
> G.使用APK签名工具签名`demo_sig.apk`
>  
> H.`demo_sig.apk` 便是aneTest中的`cmgc_demo.apk`

## 最后提醒(你能看到这的话 代表你距离成功不远了)

> 提醒(1):若以上那么多步 错了任何一步,都会失败
>
> 提醒(2):若SDK文档有任何错看的地方,都会失败

## 作者

* [platformANEs](https://github.com/platformanes)由 [zrong](http://zengrong.net) 和 [rect](http://www.shadowkong.com/) 共同发起并完成。
