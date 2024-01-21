package cocoas

/**
 * 描述：日志输出管理
 * 作者：蒋庆意
 * 日期时间：2021/1/18 11:36
 *
 *
 * cocoasjiang@foxmail.com
 */
object LogUtils {
    // 打印信息
    fun print(msg: String) {
        kotlin.io.print("$msg ")
    }

    // 打印信息（自动换行）
    @JvmStatic
    fun println(msg: String?) {
        kotlin.io.println(msg)
    }

    // 打印错误信息
    fun error(msg: String?) {
        System.err.println(msg)
    }
}
