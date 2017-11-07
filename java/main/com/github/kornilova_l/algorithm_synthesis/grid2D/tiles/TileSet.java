package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Contains set of tiles of one size
 */
public class TileSet {
    private final int n;
    private final int m;
    private final int k;
    private final Set<Tile> tiles = new HashSet<>();

    /**
     * Generates set of possible valid tiles
     */
    TileSet(int n, int m, int k) {
        this.n = n;
        this.m = m;
        this.k = k;
        // TODO: make this recursive
        tiles.add(new Tile(n, m, k));

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                Set<Tile> newTiles = new HashSet<>();
                for (Tile tile : tiles) {
                    if (tile.canBeI(i, j)) {
                        newTiles.add(new Tile(tile, i, j));
                    }
                }
                tiles.addAll(newTiles);
                newTiles.clear();
            }
        }
    }

    public TileSet(@NotNull File file) {
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("File does not exist or it is not a file");
        }
        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            n = scanner.nextInt();
            m = scanner.nextInt();
            k = scanner.nextInt();
            int size = scanner.nextInt();
            for (int i = 0; i < size; i++) {
                Set<Tile.Coordinate> is = new HashSet<>();
                for (int row = 0; row < n; row++) {
                    for (int column = 0; column < m; column++) {
                        if (scanner.nextInt() == 1) {
                            is.add(new Tile.Coordinate(row, column));
                        }
                    }
                }
                tiles.add(new Tile(n, m, k, is));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("File was not found");
        }
    }

    TileSet(Collection<Tile> tiles) {
        Tile someTile = tiles.iterator().next();
        n = someTile.getN();
        m = someTile.getM();
        k = someTile.getK();
        for (Tile tile : tiles) {
            addTile(tile); // check that all tiles have same size
        }
    }

    public int size() {
        return tiles.size();
    }

    boolean isEmpty() {
        return tiles.isEmpty();
    }

    int getN() {
        return n;
    }

    int getM() {
        return m;
    }

    public int getK() {
        return k;
    }

    /**
     * Please update tiles using only {@link #addTile(Tile)} method
     */
    public Set<Tile> getTiles() {
        return tiles;
    }

    private void addTile(Tile tile) {
        if (tile.getK() != k || tile.getM() != m || tile.getN() != n) {
            throw new IllegalArgumentException("Tile must have the same parameters as tile set");
        }

        tiles.add(tile);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Tile tile : tiles) {
            stringBuilder.append(tile).append("\n");
        }
        return stringBuilder.toString();
    }
}