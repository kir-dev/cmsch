import axios from 'axios'
import { useState } from 'react'

export const useTeamAcceptJoin = (onSuccess: (response: boolean) => void, onError = () => {}) => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error>()
  const acceptJoin = (id: number) => {
    setLoading(true)
    axios
      .put<boolean>(`/api/team/admin/accept-join`, { id })
      .then((res) => {
        onSuccess(res.data)
      })
      .catch((err) => {
        setError(err)
        onError()
      })
      .finally(() => {
        setLoading(false)
      })
  }
  return { acceptJoinLoading: loading, acceptJoinError: error, acceptJoin }
}
