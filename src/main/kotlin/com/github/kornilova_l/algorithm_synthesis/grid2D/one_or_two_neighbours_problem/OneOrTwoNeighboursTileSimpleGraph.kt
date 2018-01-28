package com.github.kornilova_l.algorithm_synthesis.grid2D.one_or_two_neighbours_problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.independent_set.IndependentSetTileGenerator.Companion.removeInvalid
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.Expand.HEIGHT
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.Expand.WIDTH
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.Part.E
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.Part.S
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.BinaryTile.Companion.parseNumber
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.Tile
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.SimpleGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.SimpleGraphWithTiles
import com.github.kornilova_l.util.ProgressBar
import org.apache.commons.collections4.bidimap.DualHashBidiMap
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap


/**
 * Precalculate graphs and export them to files.
 * So [SimpleGraph] instances can be created and used.
 */
fun main(args: Array<String>) {
    val outputDirName = "one_or_two_neighbours_tiles/simple_graphs"
    val outputDir = File(outputDirName)
    val tilesDir = File("one_or_two_neighbours_tiles")
    val files = tilesDir.listFiles()
    for (i in 0 until files.size) {
        val file = files[i]
        if (file.isFile && OneOrTwoNeighboursTile.oneOrTwoNeighboursTilesFilePattern.matcher(file.name).matches()) {
            val n = parseNumber(file.name, 1)
            val m = parseNumber(file.name, 2)
            if (n != 4 || m != 5) {
                continue
            }
            if (File("$outputDirName/$n-$m.graph").exists())
                continue
            val tileSet = OneOrTwoNeighboursTile.parseTiles(file)
            println("n = $n m = $m")
            val graph = OneOrTwoNeighboursTileSimpleGraph.createInstance(tileSet)
            println("Start export")
            graph.exportTiles(outputDir)
            println("Export graph")
            graph.export(File("$outputDirName/${graph.n}-${graph.m}.graph"))
        }
    }
}

class OneOrTwoNeighboursTileSimpleGraph(n: Int, m: Int, graph: Map<Int, Set<Int>>, ids: DualHashBidiMap<Tile, Int>) :
        SimpleGraphWithTiles(n, m, graph, ids) {

    /**
     * Format:
     * for each tile:
     * <id>
     * <tile's array of longs>
     */
    fun exportTiles(dir: File) {
        val file = Paths.get(dir.toString(), "${OneOrTwoNeighboursTile.name}-$n-$m-${ids.size}.$tilesFileExtension").toFile()
        file.outputStream().use { outputStream ->
            for (tileAndId in ids) {
                outputStream.write(tileAndId.value.toString().toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write((tileAndId.key as BinaryTile).longsToString().toByteArray())
                outputStream.write("\n".toByteArray())
            }
        }
    }

    companion object {
        fun createInstance(tilesFile: File, simpleGraph: SimpleGraph): OneOrTwoNeighboursTileSimpleGraph {
            val ids = DualHashBidiMap<Tile, Int>()
            val n = BinaryTile.parseNumber(tilesFile.name, 1)
            val m = BinaryTile.parseNumber(tilesFile.name, 2)
            val size = BinaryTile.parseNumber(tilesFile.name, 3)
            if (n != simpleGraph.n || m != simpleGraph.m) {
                throw IllegalArgumentException("Parameters of graph do not match size of tiles. Graph: n = ${simpleGraph.n} " +
                        "m = ${simpleGraph.m}. IndependentSetTile: n = $n m = $m.")
            }
            BufferedReader(FileReader(tilesFile)).use { reader ->
                for (i in 0 until size) {
                    var line = reader.readLine()
                    while (line.isEmpty()) {
                        line = reader.readLine()
                    }
                    val id = Integer.parseInt(line)
                    val grid = BinaryTile.parseBitSet(reader.readLine())
                    ids[BinaryTile(n, m, grid)] = id
                }
                return OneOrTwoNeighboursTileSimpleGraph(simpleGraph.n, simpleGraph.m, simpleGraph.graph, ids)
            }
        }

        fun createInstance(tiles: Set<OneOrTwoNeighboursTile>): OneOrTwoNeighboursTileSimpleGraph {
            val progressBar = ProgressBar(tiles.size)
            val n = tiles.first().n
            val m = tiles.first().m
            val ids = ConcurrentHashMap<Tile, Int>()
            val graph = ConcurrentHashMap<Int, MutableSet<Int>>()
            tiles.parallelStream().forEach { tile ->

                val neighbours = HashSet<Int>()
                var expandedTiles = removeInvalid(tile.getAllExpandedTiles(WIDTH))
                for (expandedTile in expandedTiles) {
                    val rightTile = expandedTile.clonePart(E)
                    neighbours.add(getId(rightTile, ids))
                }
                expandedTiles = removeInvalid(tile.getAllExpandedTiles(HEIGHT))
                for (expandedTile in expandedTiles) {
                    val bottomTile = expandedTile.clonePart(S)
                    neighbours.add(getId(bottomTile, ids))
                }
                graph[getId(tile, ids)] = neighbours
                progressBar.updateProgress()
            }
            return OneOrTwoNeighboursTileSimpleGraph(n, m, graph, DualHashBidiMap(ids))
        }
    }
}