package com.jibase.helper

import java.io.File

class FileBackupHelper(private val file: File) {

    private val backupFile: File by lazy { makeBackupFile(file) }

    /*
        - Before write to file, setup a backup plan
        If file exist:
            + If backupFile exist -> Use backupFile as backup plan
            + Otherwise -> Rename file to backupFile
        - After write to file
            + If success -> delete backupFile
            + If failed -> rename backupFile to file
    */
    fun write(writeAction: (file: File) -> Boolean): Boolean {
        // Create backupFile
        if (file.exists() && !backupFile.exists()) file.renameTo(backupFile)
        // Write to file
        val isSuccess = writeAction.invoke(file)
        // Restore / Delete backupFile
        if (isSuccess) backupFile.deleteRecursively()
        else {
            file.deleteRecursively()
            backupFile.renameTo(file)
        }
        return isSuccess
    }

    /*
        - If backupFile exist then: Delete file -> Rename backupFile to file -> Use file
        - Otherwise: Use file
    */
    fun <T> read(readAction: (File) -> T): T {
        syncBackup()
        return readAction.invoke(file)
    }

    fun exist() = file.exists() || backupFile.exists()

    fun delete() {
        if (file.exists()) {
            file.deleteRecursively()
        }
        if (backupFile.exists()) {
            backupFile.deleteRecursively()
        }
    }

    fun syncBackup() {
        if (backupFile.exists()) {
            file.deleteRecursively()
            backupFile.renameTo(file)
        }
    }
}

const val backupExtension = ".bak"
fun makeBackupFile(file: File) = File(file.absolutePath + backupExtension)