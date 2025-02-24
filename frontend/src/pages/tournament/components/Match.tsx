import type React from "react"
import { MatchView, MatchStatus } from "../../../util/views/tournament.view.ts"

interface MatchProps {
  match: MatchView
}

const Match: React.FC<MatchProps> = ({ match }) => {
  const getScoreColor = (score1?: number, score2?: number) => {
    if (match.status in [MatchStatus.CANCELLED, MatchStatus.NOT_STARTED] || score1 === undefined || score2 === undefined) return "text-gray-600"
    return score1 > score2 ? "text-green-600 font-bold" : "text-red-600"
  }

  const formatKickOffTime = (timestamp?: number) => {
    if (!timestamp) return "TBD"
    const date = new Date(timestamp)
    return date.toLocaleString("en-US", {
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    })
  }

  const getParticipantName = (seed: number, participant?: { name: string }) => {
    if (participant) return participant.name
    if (seed < 0) return `Winner of Game ${-seed}`
    return "TBD"
  }

  return (
    <div className="border rounded-lg p-2 w-64 bg-white">
      <div className="text-xs text-gray-500 mb-1">Game {match.gameId}</div>
      <div className="flex justify-between items-center mb-1">
        <span className="text-sm font-medium">{getParticipantName(match.seed1, match.participant1)}</span>
        <span className={`text-sm ${getScoreColor(match.score1, match.score2)}`}>{match.score1 ?? "-"}</span>
      </div>
      <div className="flex justify-between items-center">
        <span className="text-sm font-medium">{getParticipantName(match.seed2, match.participant2)}</span>
        <span className={`text-sm ${getScoreColor(match.score2, match.score1)}`}>{match.score2 ?? "-"}</span>
      </div>
      <div className="text-xs text-gray-500 mt-1 flex justify-between">
        <span>{match.status}</span>
        <span>{formatKickOffTime(match.kickoffTime)}</span>
      </div>
      <div className="text-xs text-gray-500 mt-1">{match.location}</div>
    </div>
  )
}

export default Match

