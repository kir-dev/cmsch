import React from 'react'
import type { MatchTree } from '../util/matchTree'
import KnockoutBracket from './KnockoutBracket.tsx'

import { groupBy, keys } from 'lodash'

type TournamentStageView = import('../../../util/views/tournament.view').TournamentStageView
type MatchView = import('../../../util/views/tournament.view').MatchView

interface TournamentBracketProps {
  stage: TournamentStageView
}

const TournamentBracket: React.FC<TournamentBracketProps> = ({ stage }: TournamentBracketProps) => {
  const levels = groupBy(stage.matches, (match: MatchView) => match.level)
  const levelCount = keys(levels).length

  const buildTree = (level: number, rootNum: number): MatchTree => {
    const root = levels[level][rootNum]!
    const upperTree = level > 1 && (levels[level - 1]?.length ?? 0) > 2 * rootNum ? buildTree(level - 1, 2 * rootNum) : null
    const lowerTree = level > 1 && (levels[level - 1]?.length ?? 0) > 2 * rootNum + 1 ? buildTree(level - 1, 2 * rootNum + 1) : null
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
  for (let i = 0; i < (levels[levelCount]?.length ?? 0); i++) {
    trees.push(buildTree(levelCount, i))
  }

  return (
    <>
      <h2 style={{ marginBottom: '1.25rem', fontSize: '1.125rem', fontWeight: 700 }}>{stage.name}</h2>
      {trees.map((tree) => (
        <KnockoutBracket tree={tree} key={tree.root.id} />
      ))}
    </>
  )
}

export default TournamentBracket
