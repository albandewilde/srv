package srv

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

import io.javalin.http.Context
import org.apache.tika.Tika

// Actions on files presont on the server

const val BASEDIR = "/files"

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

// Delete a file
fun Delete(ctx: Context) {
    val f = File(BASEDIR + rmTrailingSlash(ctx.path()))

    // Check if file exist before deletion
    if (!f.exists()) {
        ctx.status(404)
        ctx.result("File doesn't exist")
        return
    }

    f.walk().forEach {
        // If the path is the same as the file directory, we delete his content, not de directory him self
        if (it.toString() != BASEDIR) it.deleteRecursively() else null
    }
    ctx.result("Deleted")
}

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

// Remove trailing / in the url
fun rmTrailingSlash(path: String) = if (path.last() == '/') path.slice(0 until path.length-1) else path

// Get file name
fun extractFileName(path: String) = rmTrailingSlash(path).split("/").last()

// Remove the root directory to the path (hide it to the user)
fun relPath(file: File) = ".${file.getAbsolutePath().slice(BASEDIR.length until file.getAbsolutePath().length)}${if (file.isDirectory()) "/" else ""}"