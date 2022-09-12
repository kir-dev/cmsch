import axios from 'axios'
import { TeamResponses } from '../../../../util/views/team.view'
import { useState } from 'react'

export const useTeamJoin = (onResponse: (response: TeamResponses) => void) => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error>()
  const joinTeam = (id: number) => {
    setLoading(true)
    axios
      .post<TeamResponses>(`/api/team/join`, { id })
      .then((res) => {
        onResponse(res.data)
      })
      .catch((err) => {
        setError(err)
        onResponse(TeamResponses.ERROR)
      })
      .finally(() => {
        setLoading(false)
      })
  }
  return { joinTeamLoading: loading, joinTeamError: error, joinTeam }
}
