import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cocoas.CodeDocxGenerator
import cocoas.MsgHintUtil.showHint
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import wilinz.ui.theme.AppTheme
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
                    DirectoryPicker(showDirPicker) { path ->
                        showDirPicker = false
                        path?.let {
                            dirPath = path
                        }
                        // do something with path
                    }

                    OutlinedTextField(
                        value = dirPath,
                        onValueChange = { dirPath = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 5,
                        trailingIcon = {
                            IconButton(onClick = {
                                showDirPicker = true
                            }) {
                                Icon(Icons.Default.FolderOpen, contentDescription = null)
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
                    Text("--------非必填部分--------")
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

                    // Start button
                    ElevatedButton(
                        onClick = {
                            start(
                                dir = dirPath,
                                isHalf = isHalf,
                                fileTypes = fileTypes,
                                name = name,
                                version = version,
                                ignoreDirs = ignoreDirs,
                            )
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    ) {
                        Text("开始")
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
    modifier: Modifier = Modifier,
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
    ignoreDirs: String
) {
    if (dir.isBlank()) { // 项目目录空判断
        showHint("请选择或输入项目源码目录！")
        return
    } else if (!(File(dir).isDirectory)) {
        showHint("请选择或输入正确的项目源码目录！")
        return
    }
    if (fileTypes.isBlank()) {
        showHint("请输入需要写入的源码文件类型！（提示：源码文件类型应以.开头，如.java，多个文件类型间以空格区分）")
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
            showHint("请输入的源码文件类型有误，请重新输入！\n（提示：源码文件类型应以.开头，如.java，多个文件类型间以空格区分）")
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
    cdg.start(params.filterNotNull().toTypedArray(), ignoreDirsList)
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "源代码文档自动生成工具",
        icon = painterResource("logo.jpg")
    ) {
        App()
    }
}

