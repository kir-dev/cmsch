import { Heading } from '@chakra-ui/react'
import { groupBy, keys } from 'lodash'
import React from 'react'
import { MatchView, TournamentStageView } from '../../../util/views/tournament.view.ts'
import { MatchTree } from '../util/matchTree.ts'
import KnockoutBracket from './KnockoutBracket.tsx'

interface TournamentBracketProps {
  stage: TournamentStageView
}

const TournamentBracket: React.FC<TournamentBracketProps> = ({ stage }: TournamentBracketProps) => {
  const levels = groupBy(stage.matches, (match: MatchView) => match.level)
  const levelCount = keys(levels).length

  const buildTree = (level: number, rootNum: number): MatchTree => {
    const root = levels[level][rootNum]
    const upperTree = level > 1 && levels[level - 1].length > 2 * rootNum ? buildTree(level - 1, 2 * rootNum) : null
    const lowerTree = level > 1 && levels[level - 1].length > 2 * rootNum + 1 ? buildTree(level - 1, 2 * rootNum + 1) : null
    return {
      root: root,
      lowerTree: lowerTree,
      upperTree: upperTree
    }
  }

  const trees: MatchTree[] = []
  if (levelCount < 1) {
    return null
  }
  for (let i = 0; i < levels[levelCount].length; i++) {
    trees.push(buildTree(levelCount, i))
  }

  return (
    <>
      <Heading as="h2" size="lg" mb={5}>
        {stage.name}
      </Heading>
      {trees.map((tree) => (
        <KnockoutBracket tree={tree} key={tree.root.id} />
      ))}
    </>
  )
}

export default TournamentBracket
