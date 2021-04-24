package handlers

import java.io.File

import io.javalin.http.Context
import org.apache.tika.Tika

// List files
fun List(ctx: Context) {
    val path = BASEDIR + ctx.path()
    val f = File(path)

    // Check if the directory of file exist
    if (!f.exists()) {
        ctx.status(404)
        ctx.result("Directory of file not found")
        return
    }

    // Check if it's a directory or a file
    // If a directory we list his content
    // Otherwise we serve the file
    if (f.isDirectory()) {
        var fls = mutableListOf<String>()
        f.walk().forEach {
            // Absulute path on disk
            val absPath = it.toString()
            fls.add(relPath(it))
        }
        ctx.json(fls)
    } else {
        val cnt = f.readBytes()
        val mimeType = Tika().detect(f)
        ctx.result(cnt).contentType(mimeType)
    }
}