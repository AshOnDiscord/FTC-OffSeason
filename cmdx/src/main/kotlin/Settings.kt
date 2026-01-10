package com.millburnx.cmdx

public object Settings {
    public var verbose: Boolean = true;
    public var debug: Boolean = true;

    public fun verboseLog(message: Any?) {
        if (verbose) println(message)
    }

    public fun debugLog(message: Any?) {
        if (debug) println(message)
    }
}