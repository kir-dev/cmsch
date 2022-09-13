import axios from 'axios'
import { useState } from 'react'

export const useTeamPromoteLeadership = (onSuccess: (response: boolean) => void, onError = () => {}) => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error>()
  const promoteLeadership = (id: number) => {
    setLoading(true)
    axios
      .put<boolean>(`/api/team/admin/switch-leadership`, { id })
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
  return { promoteLeadershipLoading: loading, promoteLeadershipError: error, promoteLeadership }
}
