package cocoas

import cocoas.LogUtils.error
import java.io.*
import java.util.*
import kotlin.system.exitProcess

/**
 * 描述：文件操作帮助类
 * 作者：蒋庆意
 * 日期时间：2021/1/18 11:27
 *
 *
 * cocoasjiang@foxmail.com
 */
object FileUtils {
    /**
     * 获取文件名
     * @param filePath 文件路径
     * @return
     */
    fun getFileName(filePath: String?): String {
        if (null == filePath || File(filePath).isDirectory) {
            return ""
        }
        val file = File(filePath)
        return file.name
    }

    /**
     * 判断文件是否符合指定的文件类型
     * @param f
     * @param fileTypes
     * @return
     */
    fun matchFile(f: File?, fileTypes: List<String>): Boolean {
        if (f == null || f.isDirectory) {
            return false
        }
        for (fileType in fileTypes) {
            if (f.absolutePath.endsWith(fileType)) {
                return true
            }
        }
        return false
    }


    /**
     * 按行读取文件内容：过滤空行和注释
     * @param filePath
     * @return
     */
    /**
     * 按行读取文件内容：过滤空行和注释
     * @param filePath
     * @return
     */
    fun readFile(filePath: String): List<String> {
        if (File(filePath).isDirectory) {
            return ArrayList()
        }
        val lines: MutableList<String> = ArrayList()
        try {
            val reader = BufferedReader(File(filePath).reader())
            var line = reader.readLine()
            while (null != line) {
                if (filterLine(line)) { // 过滤不符合要求的代码行
                    lines.add(line)
                }
                line = reader.readLine()
            }
            return lines
        } catch (e: FileNotFoundException) {
            error("读取文件<" + filePath + ">出错：" + e.message)
        } catch (e: IOException) {
            error("读取文件<" + filePath + ">出错：" + e.message)
        }
        return ArrayList()
    }

    /**
     * 过滤不符合要求的代码行
     * @param line
     * @return
     */
    fun filterLine(line: String): Boolean {
        // 过滤空行
        if (line.isBlank()) {
            return false
        }
        val lineTrim = line.trim { it <= ' ' }
        // 过滤一般的行注释//、块注释/* */、文档注释/** */
        if (lineTrim.startsWith("//") || lineTrim.startsWith("/*")
            || lineTrim.startsWith("*")
        ) {
            return false
        }
        // 过滤html、xml注释：<!-- -->
        if (lineTrim.startsWith("<!--")) {
            return false
        }
        // 过滤PHP、Python的行注释：#
        if (lineTrim.startsWith("#")) {
            return false
        }
        // Python的块注释用法比较复杂，不太适合自动过滤
        return true
    }

    /**
     * 扫描项目中符合要求文件，并将文件路径存放到列表中
     * @param dir
     * @param fileTypes
     * @param ignoreDirs
     * @return
     */
    fun scanFiles(dir: String, fileTypes: List<String>, ignoreDirs: List<String>): List<String> {
        val rootFile = File(dir)
        if (!rootFile.isDirectory) {
            LogUtils.println("项目路径错误：$dir")
            return emptyList()
        }
        return collectFilesFromDir(dir, fileTypes, ignoreDirs)
    }

    /**
     * 将文件目录中符合要求的文件的文件路径添加到List中
     * @param dir
     * @param fileTypes
     * @param ignoreDirs
     * @return
     */
    private fun collectFilesFromDir(dir: String, fileTypes: List<String>, ignoreDirs: List<String>): List<String> {
        val dirFile = File(dir)
        if (!dirFile.isDirectory) {
            return ArrayList()
        }
        val files: MutableList<String> = ArrayList() // 扫描到的文件路径
        val subFiles = dirFile.listFiles()
        if (subFiles == null || subFiles.isEmpty()) {
            return files
        }
        subFiles
            .forEach { f: File ->
                // 特殊目录过滤
                if (f.isDirectory && f.name != "build" && f.name != "zxing" && !isIgnoreDir(f, ignoreDirs)) {
                    // 继续迭代目录
                    files.addAll(collectFilesFromDir(f.absolutePath, fileTypes, ignoreDirs))
                } else if (matchFile(f, fileTypes)) {
                    // 添加文件路径
                    files.add(f.absolutePath)
                }
            }
        return files
    }

    /**
     * 判断文件目录是否是需要过滤的目录
     * @param f
     * @param ignoreDirs
     * @return
     */
    private fun isIgnoreDir(f: File, ignoreDirs: List<String>?): Boolean {
        if (ignoreDirs.isNullOrEmpty()) {
            return false
        }
        for (ignoreDir in ignoreDirs) {
            if (f.name == ignoreDir) {
                return true
            }
        }
        return false
    }

    /**
     * 退出
     */
    private fun exit() {
        exitProcess(0)
    }
}
