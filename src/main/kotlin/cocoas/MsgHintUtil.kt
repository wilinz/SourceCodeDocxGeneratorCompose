package cocoas

import javax.swing.JOptionPane

/**
 * 描述：消息提示弹窗
 * 作者：蒋庆意
 * 日期时间：2021/1/22 13:44
 *
 *
 * cocoasjiang@foxmail.com
 */
object MsgHintUtil {
    /**
     * 显示提示对话框
     * @param msg
     */
    @JvmStatic
    fun showHint(msg: String?) {
        JOptionPane.showMessageDialog(null, msg, "提示", JOptionPane.PLAIN_MESSAGE)
    }

    /**
     * 源代码文档生成成功提示对话框
     * @param savePath  文档保存位置
     * @param pages     文档页数
     * @param lineNums  文档行数
     */
    fun showFinishHint(savePath: String, pages: Int, lineNums: Int) {
        val sb = """
             源代码文档生成成功！
             文档位置：$savePath
             文档页数：${pages}页
             文档行数：${lineNums}行
             """.trimIndent()
        JOptionPane.showMessageDialog(null, sb, "完成", JOptionPane.PLAIN_MESSAGE)
    }
}
