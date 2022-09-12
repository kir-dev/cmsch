import axios from 'axios'
import { TeamResponses } from '../../../../util/views/team.view'
import { useState } from 'react'

export const useTeamLeave = (onResponse: (response: TeamResponses) => void) => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error>()
  const leaveTeam = () => {
    setLoading(true)
    axios
      .post<TeamResponses>(`/api/team/leave`)
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
  return { leaveTeamLoading: loading, leaveTeamError: error, leaveTeam }
}
