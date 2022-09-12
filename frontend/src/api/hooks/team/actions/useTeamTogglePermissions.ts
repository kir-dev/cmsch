import axios from 'axios'
import { useState } from 'react'

export const useTeamTogglePermissions = (onSuccess: (response: boolean) => void, onError = () => {}) => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error>()
  const togglePermissions = (id: number) => {
    setLoading(true)
    axios
      .put<boolean>(`/api/team/admin/toggle-permissions`, { id })
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
  return { togglePermissionsLoading: loading, togglePermissionsError: error, togglePermissions }
}
