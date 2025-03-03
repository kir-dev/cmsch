import React from "react"
import { TournamentStageView, MatchView } from "../../../util/views/tournament.view.ts"
import {Heading, Stack} from "@chakra-ui/react";
import Match from "./Match.tsx";

interface TournamentBracketProps {
  stage: TournamentStageView
}

const TournamentBracket: React.FC<TournamentBracketProps> = ({ stage }: TournamentBracketProps) => {
  return (
    <>
      <Heading as="h2" size="lg">
        {stage.name}
      </Heading>
      <Stack spacing={4}>
        {stage.matches.map((match: MatchView) => (
          <Match key={match.id} match={match} />
        ))}
      </Stack>
    </>
  )
}

export default TournamentBracket

