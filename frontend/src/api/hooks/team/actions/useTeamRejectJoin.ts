import axios from 'axios'
import { useState } from 'react'

export const useTeamRejectJoin = (onSuccess: (response: boolean) => void, onError = () => {}) => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error>()
  const rejectJoin = (id: number) => {
    setLoading(true)
    axios
      .put<boolean>(`/api/team/admin/reject-join`, { id })
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
  return { rejectJoinLoading: loading, rejectJoinError: error, rejectJoin }
}
