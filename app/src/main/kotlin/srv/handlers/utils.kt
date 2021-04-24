package handlers

import java.io.File

// Remove trailing / in the url
fun rmTrailingSlash(path: String) = if (path.last() == '/') path.slice(0 until path.length-1) else path

// Get file name
fun extractFileName(path: String) = rmTrailingSlash(path).split("/").last()

// Remove the root directory to the path (hide it to the user)
fun relPath(file: File) = ".${file.getAbsolutePath().slice(BASEDIR.length until file.getAbsolutePath().length)}${if (file.isDirectory()) "/" else ""}"