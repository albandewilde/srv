package handlers

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

import io.javalin.http.Context

// Add a file
fun Add(ctx: Context) {
    val path = ctx.path()

    val fname = extractFileName(path)
    val absPath = BASEDIR + path

    val savefile = fun() {
        val bdy = ctx.bodyAsBytes()
        File(absPath).writeBytes(bdy)
        ctx.result("Ok, 200")
    }

    // Check if there is a filename
    if (fname == "") {
        ctx.status(400)
        ctx.result("No filename given")
        return
    }

    // Direcories and file name
    // `drop(1)` to remove first empty element that is a /
    val drs = absPath.split("/").drop(1)
    for (i in drs.indices) {
        // Path to check
        val p = "/" + drs.slice(0..i).joinToString("/")
        val f = File(p)

        // Check if the file exist on disk
        if (f.exists()) {
            // Check if on disk it's a direcotry on disk and also in the path request
            if (f.isDirectory() && i < drs.size-1) {
                continue
            } else
            // If it's a file on disk but not in the path request or or vice versa
            if (!f.isDirectory() && i < drs.size-1) {
                ctx.status(400)
                ctx.result("A file already exist with the name: ${relPath(f)}")
                return
            } else if (f.isDirectory() && i == drs.size-1) {
                ctx.status(400)
                ctx.result("A directory already exist with the name: ${relPath(f)}")
                return
            } else 
            // It's a file on disk and also on path request
            if (!f.isDirectory() && i == drs.size-1) {
                savefile()
                return
            }
        } else {
            // Create directory if it's a directory on path request otherwise save file
            if (i < drs.size-1) Files.createDirectory(Paths.get(p)) else {
                savefile()
                return
            }
        }
    }
}