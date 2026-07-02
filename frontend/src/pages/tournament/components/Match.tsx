import { stringifyTimeStamp } from '@/util/core-functions.util.ts'
import type { MatchView, ParticipantView } from '@/util/views/tournament.view.ts'

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

  if (match.status === 'BYE') {
    return <div style={{ width: '16rem', padding: '0.5rem', backgroundColor: 'white' }} />
  }

  return (
    <div
      style={{
        width: '16rem',
        padding: '0.5rem',
        backgroundColor: 'white',
        border: '1px solid #e2e8f0',
        borderRadius: '0.5rem'
      }}
    >
      <div style={{ fontSize: '0.875rem', color: '#6b7280' }}>Game {match.gameId}</div>

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.25rem' }}>
        <div style={{ fontSize: '0.875rem', fontWeight: 500 }}>{getParticipantName(match.homeSeed, match.home)}</div>
        <div
          style={{
            fontSize: '0.875rem',
            color: getScoreColor(match, match.homeScore, match.awayScore),
            fontWeight: match.status === 'COMPLETED' ? 700 : 400
          }}
        >
          {match.homeScore ?? '-'}
        </div>
      </div>

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ fontSize: '0.875rem', fontWeight: 500 }}>{getParticipantName(match.awaySeed, match.away)}</div>
        <div
          style={{
            fontSize: '0.875rem',
            color: getScoreColor(match, match.awayScore, match.homeScore),
            fontWeight: match.status === 'COMPLETED' ? 700 : 400
          }}
        >
          {match.awayScore ?? '-'}
        </div>
      </div>

      <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '0.25rem' }}>
        <div style={{ fontSize: '0.75rem', color: '#6b7280' }}>{match.status}</div>
        <div style={{ fontSize: '0.75rem', color: '#6b7280' }}>{formatKickOffTime(match.kickoffTime)}</div>
      </div>

      <div style={{ fontSize: '0.75rem', color: '#6b7280', marginTop: '0.25rem' }}>
        {match.location != '' ? match.location : 'Location TBD'}
      </div>
    </div>
  )
}

export default Match
