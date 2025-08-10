import axios from 'axios'
import { useState } from 'react'
import { TeamResponses } from '../../../../util/views/team.view'

export const useTeamCancelJoin = (onResponse: (response: TeamResponses) => void) => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error>()
  const cancelJoin = () => {
    setLoading(true)
    axios //todo
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
