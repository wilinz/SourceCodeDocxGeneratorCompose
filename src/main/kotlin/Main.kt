@file:OptIn(ExperimentalComposeUiApi::class)

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import cocoas.CodeDocxGenerator
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import wilinz.ui.theme.AppTheme
import java.awt.Cursor
import java.awt.Desktop
import java.io.File


@Composable
@Preview
fun App() {
    var dirPath by remember { mutableStateOf("") }
    var isHalf by remember { mutableStateOf(true) }
    var fileTypes by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var version by remember { mutableStateOf("") }
    var ignoreDirs by remember { mutableStateOf("") }

    AppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(
                    rememberScrollState()
                ), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.padding(it).padding(16.dp).wrapContentHeight().widthIn(0.dp, 400.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("--------必填部分--------", color = MaterialTheme.colorScheme.error)
                    // Directory chooser

                    var showDirPicker by remember { mutableStateOf(false) }
                    val initialDirectory by remember(dirPath) {
                        mutableStateOf(
                            dirPath.ifBlank {
                                System.getProperty("user.home") ?: ""
                            }
                        )
                    }
                    DirectoryPicker(
                        showDirPicker,
                        initialDirectory = initialDirectory,
                        title = "选择文件夹"
                    ) { path ->
                        showDirPicker = false
                        path?.let {
                            dirPath = path
                        }
                    }

                    OutlinedTextField(
                        value = dirPath,
                        onValueChange = { dirPath = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 5,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    showDirPicker = true
                                },
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
                            ) {
                                Icon(painterResource("folder_open_black_24dp.svg"), contentDescription = null)
                            }
                        },
                        label = {
                            Text("选择项目目录")
                        }
                    )

                    // Write mode radio buttons
                    Row(
                        modifier = Modifier
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("写入方式")

                        Spacer(modifier = Modifier.width(8.dp))
                        // Wrap the Text in a Row and add a clickable modifier

                        RadioButtonWithLabel(
                            isSelected = !isHalf,
                            onSelected = { isHalf = false }
                        ) {
                            Text("顺序写入60页")
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        // Wrap the Text in a Row and add a clickable modifier
                        RadioButtonWithLabel(
                            isSelected = isHalf,
                            onSelected = { isHalf = true }
                        ) {
                            Text("前后各30页")
                        }
                    }

                    // File types input
                    OutlinedTextField(
                        value = fileTypes,
                        onValueChange = { fileTypes = it },
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("文件类型（用空格隔开）") },
                        placeholder = { Text(".java .kt") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("--------非必填部分--------", modifier = Modifier.padding(vertical = 8.dp))
                    // Project name input
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("软件名称") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Version input
                    OutlinedTextField(
                        value = version,
                        onValueChange = { version = it },
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("版本号") },
                        placeholder = { Text("V1.0.0") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Directories to ignore input
                    OutlinedTextField(
                        value = ignoreDirs,
                        onValueChange = { ignoreDirs = it },
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("忽略的文件夹（用空格隔开）") },
                        placeholder = { Text("dir1 dir2") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    var hint by remember {
                        mutableStateOf("")
                    }

                    var isShowFinishDialog by remember {
                        mutableStateOf(false)
                    }

                    var finishDialogContentText by remember {
                        mutableStateOf("")
                    }

                    var finishFile by remember {
                        mutableStateOf<File?>(null)
                    }

                    AnimatedVisibility(
                        visible = isShowFinishDialog,
                        enter = fadeIn(initialAlpha = 0.3f), // 进入时的动画效果
                        exit = fadeOut(targetAlpha = 0.3f) // 退出时的动画效果
                    ) {
                        AlertDialog(
                            properties = DialogProperties(dismissOnClickOutside = false),
                            confirmButton = {
                                TextButton(onClick = {
                                    isShowFinishDialog = false
                                    if (finishFile == null || !Desktop.isDesktopSupported()) {
                                        println("Desktop is not supported on this platform.")
                                        return@TextButton
                                    }
                                    val desktop = Desktop.getDesktop()
                                    if (desktop.isSupported(Desktop.Action.OPEN)) {
                                        // 打开资源管理器并显示用户的主目录
                                        desktop.open(finishFile!!.parentFile)
                                    }
                                }, modifier = Modifier.pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))) {
                                    Text("打开")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    isShowFinishDialog = false
                                }, modifier = Modifier.pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))) {
                                    Text("关闭")
                                }
                            },
                            onDismissRequest = {
                                isShowFinishDialog = false
                            },
                            title = {
                                Text("消息")
                            },
                            text = {
                                SelectionContainer {
                                    Text(finishDialogContentText)
                                }
                            }
                        )
                    }


                    // Start button
                    ElevatedButton(
                        onClick = {
                            GlobalScope.launch(Dispatchers.IO) {
                                kotlin.runCatching {
                                    start(
                                        dir = dirPath,
                                        isHalf = isHalf,
                                        fileTypes = fileTypes,
                                        name = name,
                                        version = version,
                                        ignoreDirs = ignoreDirs,
                                        onHint = {
                                            hint = it
                                        },
                                        finishCallback = { savePath: String, pages: Int, lineNums: Int ->
                                            finishFile = File(savePath)
                                            finishDialogContentText = """
             源代码文档生成成功！
             文档位置：$savePath
             文档页数：${pages}页
             文档行数：${lineNums}行
             """.trimIndent()
                                            isShowFinishDialog = true
                                        }
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
                            .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
                    ) {
                        Text("开始")
                    }

                    Spacer(Modifier.height(12.dp))

                    SelectionContainer {
                        Text(hint)
                    }

                }
            }
        }

    }
}

@Composable
fun RadioButtonWithLabel(
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
        .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
    label: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onSelected() }
            .padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelected() }
        )
        label()
    }
}


/**
 * 输入参数检测
 * @param dir
 * @param isHalf
 * @param fileTypes
 * @param name
 * @param version
 * @param ignoreDirs
 */
private fun start(
    dir: String,
    isHalf: Boolean,
    fileTypes: String,
    name: String,
    version: String,
    ignoreDirs: String,
    onHint: ((msg: String) -> Unit)? = null,
    finishCallback: ((savePath: String, pages: Int, lineNums: Int) -> Unit)? = null
) {
    if (dir.isBlank()) { // 项目目录空判断
        onHint?.invoke("请选择或输入项目源码目录！")
        return
    } else if (!(File(dir).isDirectory)) {
        onHint?.invoke("请选择或输入正确的项目源码目录！")
        return
    }
    if (fileTypes.isBlank()) {
        onHint?.invoke("请输入需要写入的源码文件类型！（提示：源码文件类型应以.开头，如.java，多个文件类型间以空格区分）")
        return
    }

    // 收集数据
    val params = arrayOfNulls<String>(15) // 参数数组
    params[0] = dir.trim()
    params[1] = name.trim()
    params[2] = version.trim()
    params[3] = isHalf.toString() // 写入方式
    // 源码文件类型检查
    var fileTypeArray: Array<String?> = fileTypes.trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
        .toTypedArray()
    for (i in fileTypeArray.indices) {
        if (fileTypeArray[i] == null || fileTypeArray[i]!!.trim { it <= ' ' }.isEmpty() || !fileTypeArray[i]!!
                .trim { it <= ' ' }.startsWith(".")
        ) {
            onHint?.invoke("请输入的源码文件类型有误，请重新输入！\n（提示：源码文件类型应以.开头，如.java，多个文件类型间以空格区分）")
            return
        } else {
            params[3 + 1 + i] = fileTypeArray[i]!!.trim { it <= ' ' }
        }
    }
    // 忽略目录检查
    fileTypeArray = ignoreDirs.trim().split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val ignoreDirsList = ArrayList<String>()
    if (fileTypeArray.isNotEmpty()) {
        for (i in fileTypeArray.indices) {
            if (fileTypeArray[i] != null && fileTypeArray[i]!!.trim { it <= ' ' }.isNotEmpty()) {
                ignoreDirsList.add(fileTypeArray[i]!!.trim { it <= ' ' })
            }
        }
    }
    val cdg = CodeDocxGenerator()
    cdg.start(params.filterNotNull().toTypedArray(), ignoreDirsList, onHint, finishCallback)
}

fun main() = application {
    val windowState = rememberWindowState(
        width = 800.dp,  // 设置窗口的初始宽度
        height = 640.dp, // 设置窗口的初始高度
        position = WindowPosition(Alignment.Center) // 设置窗口的初始位置
    )
    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        title = "软著源代码文档自动生成工具",
        icon = painterResource("logo.png")
    ) {
        App()
    }
}

