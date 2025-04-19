import React from "react"
import { TournamentStageView, MatchView } from "../../../util/views/tournament.view.ts"
import { Heading } from "@chakra-ui/react";
import { groupBy, keys } from 'lodash'
import KnockoutBracket from './KnockoutBracket.tsx'

interface TournamentBracketProps {
  stage: TournamentStageView
}

export type MatchTree = {
  root: MatchView
  lowerTree: MatchTree | null
  upperTree: MatchTree | null
}

const TournamentBracket: React.FC<TournamentBracketProps> = ({ stage }: TournamentBracketProps) => {
  let levels = groupBy(stage.matches, (match: MatchView) => match.level)
  let levelCount = keys(levels).length


  const buildTree = (level: number, rootNum: number): MatchTree => {
    {
      let root = levels[level][rootNum]
      let upperTree = (level>1 && levels[level-1].length>2*rootNum) ? buildTree(level-1, 2*rootNum) : null
      let lowerTree = (level>1 && levels[level-1].length>2*rootNum+1) ? buildTree(level-1, 2*rootNum+1) : null
      return {
        root: root,
        lowerTree: lowerTree,
        upperTree: upperTree
      }
    }
  }

  let trees: MatchTree[] = []
  if (levelCount < 1) {
    return <></>
  }
  for (let i = 0; i < levels[levelCount].length; i++) {
    trees.push(buildTree(levelCount, i))
  }

  return (
    <>
      <Heading as="h2" size="lg" mb={5}>
        {stage.name}
      </Heading>
      {
        trees.map((tree, index) => (
          <KnockoutBracket tree={tree} key={index} />
        ))
      }
    </>
  )
}

export default TournamentBracket

