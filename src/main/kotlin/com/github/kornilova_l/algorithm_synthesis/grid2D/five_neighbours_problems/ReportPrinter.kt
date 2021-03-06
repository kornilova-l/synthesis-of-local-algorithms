package com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FiveNeighboursNonTrivialProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FiveNeighboursProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.getNextProblemId
import com.github.kornilova_l.util.ProgressBar
import gnu.trove.list.array.TIntArrayList

private const val rulesCount = 30
private val combinationsCount = Math.pow(2.toDouble(), rulesCount.toDouble()).toInt()

fun main(args: Array<String>) {
    ReportPrinter(parseInts(solvableFile), parseInts(unsolvableFile))
            .printReport(atLeastOneIncludedAndOneExcluded(), true)
//    ReportPrinter(parseInts(solvableFile), parseInts(unsolvableFile)).printReport()
}


class ReportPrinter(private val solvable: TIntArrayList, private val unsolvable: TIntArrayList) {
    /**
     * Checks all 2 ^ 30 problems
     */
    fun printReport() {
        val progressBar = ProgressBar(combinationsCount, "Count solvable and unsolvable")
        var solvableCount = 0
        var unsolvableCount = 0
        for (i in 0 until combinationsCount) {
            if (isUnsolvable(i, unsolvable)) {
                unsolvableCount++
            } else if (isSolvable(i, solvable)) {
                solvableCount++
            }
            progressBar.updateProgress(1)
        }
        progressBar.finish()
        printReport(solvableCount, unsolvableCount, combinationsCount)
    }

    /**
     * Checks all possible problems that can be constructed by removing
     * set of rules from original problem
     */
    fun printReport(problem: FiveNeighboursNonTrivialProblem, printSolvable: Boolean = false) {
        val combinationsCount = Math.pow(2.toDouble(), problem.rules.size.toDouble()).toInt()
        val progressBar = ProgressBar(combinationsCount, "Count solvable and unsolvable")
        var solvableCount = 0
        var unsolvableCount = 0
        var combinationNum: Int? = problem.getId()
        val foundSolvable = ArrayList<Int>()

        while (combinationNum != null) {
            if (isUnsolvable(combinationNum, unsolvable)) {
                unsolvableCount++
            } else if (isSolvable(combinationNum, solvable)) {
                solvableCount++
                if (printSolvable) {
                    foundSolvable.add(combinationNum)
                }
            }
            progressBar.updateProgress(1)
            combinationNum = getNextProblemId(combinationNum, problem)
        }
        progressBar.finish()
        println("Problem $problem:")
        printReport(solvableCount, unsolvableCount, combinationsCount)
        if (printSolvable) {
            for (s in foundSolvable) {
                println(FiveNeighboursProblem(s))
            }
        }
    }

    private fun printReport(solvableCount: Int, unsolvableCount: Int, combinationsCount: Int) {
        println("Solvable problems count: $solvableCount")
        println("Unsolvable problems count: $unsolvableCount")
        println("Unknown: ${combinationsCount - solvableCount - unsolvableCount}")
    }
}