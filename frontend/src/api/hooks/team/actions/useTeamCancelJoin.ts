import axios from 'axios'
import { TeamResponses } from '../../../../util/views/team.view'
import { useState } from 'react'

export const useTeamCancelJoin = (onResponse: (response: TeamResponses) => void) => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error>()
  const cancelJoin = () => {
    setLoading(true)
    axios
      .post<TeamResponses>(`/api/team/cancel-join`)
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
  return { cancelLoading: loading, cancelError: error, cancelJoin }
}
