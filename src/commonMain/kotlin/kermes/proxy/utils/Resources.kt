package kermes.proxy.utils

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

const val resourcesPath = "src/commonMain/resources"

fun resPathByNameOrNull(
    fileName: String
): Path? = (Path(resourcesPath) + Path(fileName)).takeIf {
    SystemFileSystem.exists(it) 
}
