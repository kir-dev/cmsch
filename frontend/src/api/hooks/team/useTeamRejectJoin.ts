import axios from 'axios'
import { TeamResponses } from '../../../util/views/team.view'
import { useState } from 'react'

export const useTeamRejectJoin = (onResponse: (response: TeamResponses) => void) => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error>()
  const rejectJoin = (id: number) => {
    setLoading(true)
    axios
      .put<TeamResponses>(`/api/team/admin/reject-join`, id)
      .then((res) => {
        onResponse(res.data)
      })
      .catch(setError)
      .finally(() => {
        setLoading(false)
      })
  }
  return { rejectJoinLoading: loading, rejectJoinError: error, rejectJoin }
}
