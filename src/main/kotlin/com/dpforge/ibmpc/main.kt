package com.dpforge.ibmpc

import java.io.File

fun main(args: Array<String>) {
    val config = Config.read(File(args[0]))
    PC(config).start()
}


