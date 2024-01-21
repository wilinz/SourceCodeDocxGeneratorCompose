package cocoas

/**
 * 描述：生成源代码文档全过程的相关事件
 * 作者：蒋庆意
 * 日期时间：2021/1/22 9:52
 *
 *
 * cocoasjiang@foxmail.com
 */
interface GenerateEvent {
    companion object {
        /**未扫描到文件 */
        const val EVENT_NO_FILE: Int = 0

        /**生成源代码文档完成 */
        const val EVENT_FINISH: Int = 999
    }
}
