import axios from 'axios'
import { useState } from 'react'
import { TeamResponses } from '../../../../util/views/team.view'

export const useTeamJoin = (onResponse: (response: TeamResponses) => void) => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error>()
  const joinTeam = (id: number) => {
    setLoading(true)
    axios //todo
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
