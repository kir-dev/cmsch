import axios from 'axios'
import { useState } from 'react'

export const useTeamMemberKick = (onSuccess: (response: boolean) => void, onError = () => {}) => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error>()
  const kickMember = (id: number) => {
    setLoading(true)
    axios
      .put<boolean>(`/api/team/admin/kick-user`, { id })
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
  return { kickLoading: loading, kickError: error, kickMember }
}
