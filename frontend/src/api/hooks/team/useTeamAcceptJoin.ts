import axios from 'axios'
import { TeamResponses } from '../../../util/views/team.view'
import { useState } from 'react'

export const useTeamAcceptJoin = (onResponse: (response: TeamResponses) => void) => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error>()
  const acceptJoin = (id: number) => {
    setLoading(true)
    axios
      .put<TeamResponses>(`/api/team/admin/accept-join`, id)
      .then((res) => {
        onResponse(res.data)
      })
      .catch(setError)
      .finally(() => {
        setLoading(false)
      })
  }
  return { acceptJoinLoading: loading, acceptJoinError: error, acceptJoin }
}
