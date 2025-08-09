import { MatchView, ParticipantView } from '../../../util/views/tournament.view.ts'
import { Box, Flex, Text } from '@chakra-ui/react'
import { stringifyTimeStamp } from '../../../util/core-functions.util.ts'

interface MatchProps {
  match: MatchView
}

const getScoreColor = (match: MatchView, score1?: number, score2?: number) => {
  if (match.status !== 'COMPLETED' || score1 === undefined || score2 === undefined) return 'gray.600'
  return score1 > score2 ? 'green.600' : 'red.600'
}

const Match = ({ match }: MatchProps) => {
  const formatKickOffTime = (timestamp?: number) => {
    if (!timestamp) return 'TBD'
    return stringifyTimeStamp(timestamp)
  }

  const getParticipantName = (seed: number, participant?: ParticipantView) => {
    if (participant) return participant.teamName
    if (seed < 0) return `Winner of Game ${-seed}`
    return 'TBD'
  }

  return (
    <Box borderWidth="1px" borderRadius="lg" p={2} width="64" bg="white">
      <Text fontSize="sm" color="gray.500">
        Game {match.gameId}
      </Text>
      <Flex justifyContent="space-between" alignItems="center" mb={1}>
        <Text fontSize="sm" fontWeight="medium">
          {getParticipantName(match.homeSeed, match.home)}
        </Text>
        <Text
          fontSize="sm"
          color={getScoreColor(match, match.homeScore, match.awayScore)}
          fontWeight={match.status === 'COMPLETED' ? 'bold' : 'normal'}
        >
          {match.homeScore ?? '-'}
        </Text>
      </Flex>
      <Flex justifyContent="space-between" alignItems="center">
        <Text fontSize="sm" fontWeight="medium">
          {getParticipantName(match.awaySeed, match.away)}
        </Text>
        <Text
          fontSize="sm"
          color={getScoreColor(match, match.awayScore, match.homeScore)}
          fontWeight={match.status === 'COMPLETED' ? 'bold' : 'normal'}
        >
          {match.awayScore ?? '-'}
        </Text>
      </Flex>
      <Flex justifyContent="space-between" mt={1}>
        <Text fontSize="xs" color="gray.500">
          {match.status}
        </Text>
        <Text fontSize="xs" color="gray.500">
          {formatKickOffTime(match.kickoffTime)}
        </Text>
      </Flex>
      <Text fontSize="xs" color="gray.500" mt={1}>
        {match.location != '' ? match.location : 'Location TBD'}
      </Text>
    </Box>
  )
}

export default Match
