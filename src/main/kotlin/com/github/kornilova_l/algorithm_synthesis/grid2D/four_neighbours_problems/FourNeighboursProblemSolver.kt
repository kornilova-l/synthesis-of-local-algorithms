package com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems

import com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.graphs.FourNeighboursDirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.graphs.FourNeighboursGraphsIterator
import com.github.kornilova_l.algorithm_synthesis.grid2D.four_neighbours_problems.problem.FourNeighboursProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.FourNeighbourhood
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.ProblemSolver
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.SatSolver
import java.io.File


class FourNeighboursProblemSolver : ProblemSolver<FourNeighboursProblem, FourNeighboursDirectedGraph>() {
    override val graphsIterator: Iterable<FourNeighboursDirectedGraph> = FourNeighboursGraphsIterator(File("independent_set_tiles/four_neighbours_directed_graphs"))

    override fun rotateProblem(problem: FourNeighboursProblem): FourNeighboursProblem = problem.rotate()

    override fun reverseProblem(problem: FourNeighboursProblem): FourNeighboursProblem = problem.reverse()

    override fun formClauses(graph: FourNeighboursDirectedGraph,
                             reversedProblem: FourNeighboursProblem,
                             satSolver: SatSolver) {
        for (neighbourhood in graph.neighbourhoods) {
            formClause(neighbourhood, reversedProblem, satSolver)
        }
    }

    private fun formClause(neighbourhood: FourNeighbourhood, reversedProblem: FourNeighboursProblem, satSolver: SatSolver) {
        // todo: implement
    }
}