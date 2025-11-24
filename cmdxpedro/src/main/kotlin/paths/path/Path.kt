package com.millburnx.cmdxpedro.paths.path

import com.pedropathing.paths.PathBuilder

public interface Path {
    public fun register(pathBuilder: PathBuilder, mirrored: Boolean = false): PathBuilder
}