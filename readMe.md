## **happy_media** ## 快速开启音视频播放
**功能简介**　音频播放，视频全屏\小窗切换播放，缓存播放，横竖屏幕随系统，视频自适应

**使用简介**

           <com.hapi.player.video.HappyVideoPlayer
            android:id="@+id/mHappyVideoPlayer"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:loop="true"
            app:heightRatio="0.5625"
            app:isFromLastPosition="false"
            app:isUseCache="true"
            app:isFirstFrameAsCover="true"
            app:centerCropError="0.1"
            app:autoChangeOrientation ="true"
           />




**属性**

 
属性 | 默认值 |  功能  
-|-|-
loop | false |  是否循环播放 |
heightRatio | 0.0 |  　wrap_content（wrap_content默认控件高度按视频尺寸和控件宽度适应），heightRatio宽高比例如果设置，控件高度＝　宽度＊heightRatio|
isFromLastPosition | false |  是否继续上一次退出时间点播放 |
isUseCache | false |  是否变下边播 |
autoChangeOrientation | false |  　播放后，全屏和非全屏是否跟随手机旋转而切换 |
isFirstFrameAsCover | false |  是否用第一帧作为封面 |
centerCropError | false |  　非全屏播放时（视频尺寸和控件尺寸不黄金比例时，比如固定控件大小和match_parent,视频缩调整后会有小黑边）　如果宽度和高度小黑边在多少比例内，无视它使用centerCrop去充满屏幕，（比如竖屏视频抖音这类还是没有小黑边好） |


----------

　**基本使用步骤**
　　
　　　　

        val audioPlayer = HappyPlayerHelper.newPlayer(context) //音频播放器（如果使用仅仅音频播放
        
        mHappyVideoPlayer.addController(DefaultController(this))　//视频播放器 设置控制器　(DefaultController　为默认控制器,如果要实现自己控制器ui，则继承IController)
        
        设置播放参数 有两个接口
           fun setUp(uir: Uri, headers :Map<String, String> ?=null,preLoading:Boolean = false)
           fun setUp(uir: Uri, headers :Map<String, String> ?=null,cover:Uri, preLoading:Boolean = false)
           
        @param uir :        播放地址
        @param headers :    请求头
        @param cover :      封面（会覆盖第一帧封面配置）
        @param preLoading : 提前预装装载视频 （预装装载后start（）会加快播放
        
        mHappyVideoPlayer.setUp(Uri.parse("https://media.w3.org/2010/05/sintel/trailer.mp4")         , mheaher, true)
        
        开始播放
        mHappyVideoPlayer.startPlay()
        监听播放状态：（播放控制器本身也是一个监听器）
        mHappyVideoPlayer.setListener(lister: PlayerStatusListener,isAdd: Boolean)


**播放器状态监听**

    
    interface PlayerStatusListener {
       //状态变化 暂停、缓冲等待 参考播放器状态
      fun onPlayStateChanged(status:Int)
      //播放器窗口变化 全屏小窗
      fun onPlayModeChanged(model:Int)

}

**自定义控制器**

     控制控制器本质是带播放监听的view 根据回调去改变你的ui
    interface IController : PlayerStatusListener {
      // 要添加的控制器视图
      fun getView(): View
      // 绑定播放器
      fun attach(player: IVideoPlayer){}
      // 释放删除
      fun detach()
       // 重置控制器，将控制器恢复到初始状态。
      fun reset()
}

**播放状态回调**

        /**
     * 播放未开始
     **/
    public static final int STATE_IDLE = 0;
    /**
     * 播放准备中
     **/
    public static final int STATE_PREPARING = 1;
    /**
     * 播放准备就绪
     **/
    public static final int STATE_PREPARED = 2;
    /**
     * 预装载完成　等等通知播放
     */
    public static final int STATE_PRELOADED_WAITING = 3;
    /**
     * 正在播放
     **/
    public static final int STATE_PLAYING = 4;
    /**
     * 暂停播放
     **/
    public static final int STATE_PAUSED = 5;
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
     **/
    public static final int STATE_BUFFERING_PLAYING = 6;
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
     **/
    public static final int STATE_BUFFERING_PAUSED = 7;
    /**
     * 播放完成
     **/
    public static final int STATE_COMPLETED = 8;
    /**
     * 播放错误
     **/
    public static final int STATE_ERROR = 9;
   
   
   播放器窗口状态
   

     /**
     * 普通模式
     **/
    public static final int MODE_NORMAL = 10;
    /**
     * 全屏模式
     **/
    public static final int MODE_FULL_SCREEN = 11;
    /**
     * 小窗口模式
     **/
    public static final int MODE_TINY_WINDOW = 12;


　　

**方法介绍**
　　
　　
  

    基数音频播放视频播放方法
interface IPlayer {
    /**
     * 获得当前播放url
     */

    fun getCurrentUrl():Uri?

    /**
     * 设置播放参数参数
     * @param preLoading  预加载　　提前异步装载视频　　如果true 装载完成将等待　　noticePreLoading播放
     */
    fun setUp(uir: Uri, headers :Map<String, String> ?=null,preLoading:Boolean = false)

    /**
     * 取消preLoading　的等待　如果　preLoading　　如果装载完成将直接播放　否则还是等装载完成才播放
     */
    fun  startPlay()


    fun addPlayStatusListener(lister: PlayerStatusListener,isAdd: Boolean)
    /**
     * 暂停
     */
    fun pause()

    /**
     * 恢复
     */
    fun resume()

    /**
     * 播放配置
     */
    fun setPlayerConfig(playerConfig:PlayerConfig)
    fun getPlayerConfig():PlayerConfig


    fun seekTo(pos: Int)


    /**
     * 设置音量
     *
     * @param volume 音量值
     */
    fun setVolume(volume: Int)


    fun getCurrentPlayStatus(): Int

    fun getBufferPercentage():Int

    /**
     * 获取最大音量
     *
     * @return 最大音量值
     */
    fun getMaxVolume(): Int

    /**
     * 获取当前音量
     *
     * @return 当前音量值
     */
    fun getVolume(): Int

    /**
     * 获取办法给总时长，毫秒
     *
     * @return 视频总时长ms
     */
    fun getDuration(): Long

    /**
     * 获取当前播放的位置，毫秒
     *
     * @return 当前播放位置，ms
     */
    fun getCurrentPosition(): Long


     fun releasePlayer()

     fun isIdle(): Boolean
     fun isPreparing(): Boolean
     fun isPrepared(): Boolean
     fun isBufferingPlaying(): Boolean
     fun isBufferingPaused(): Boolean
     fun isPlaying(): Boolean
     fun isPaused(): Boolean
     fun isError(): Boolean
     fun isCompleted(): Boolean

}

视频播放器继承IPlayer

interface IVideoPlayer : IPlayer {

    /**
     * @param cover 封面
     * @param preLoading   预加载　　提前异步装载视频　　如果true 装载完成将等待　　startPlay
     */
    fun setUp(uir: Uri, headers :Map<String, String> ?=null,cover:Uri, preLoading:Boolean = false)


    /**
     * 锁定屏幕
     * @return　成功失败
     */
    fun lockScreen(toLock:Boolean):Boolean

    fun isLock():Boolean

    /**
     * 设置背景
     */
    fun setCover(uir: Uri?)

    /**
     * 添加控制器
     */
    fun addController(controller: IController)
    /**
     * 窗口模式
     */
    fun getPlayerWindowStatus(): Int


    /**
     * 进入全屏模式
     */
    fun enterFullScreen()

    /**
     * 退出全屏模式
     *
     * @return true 退出
     */
    fun exitFullScreen(): Boolean

    /**
     * 进入小窗口模式
     */
    fun enterTinyWindow()

    /**
     * 退出小窗口模式
     *
     * @return true 退出小窗口
     */
    fun exitTinyWindow(): Boolean


     fun isFullScreen(): Boolean
     fun isTinyWindow(): Boolean
     fun isNormal(): Boolean
}

