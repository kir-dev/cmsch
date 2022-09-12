import axios from 'axios'
import { TeamResponses } from '../../../util/views/team.view'
import { useState } from 'react'

export const useTeamTogglePermissions = (onResponse: (response: TeamResponses) => void) => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error>()
  const togglePermissions = (id: number) => {
    setLoading(true)
    axios
      .put<TeamResponses>(`/api/team/admin/toggle-permissions`, id)
      .then((res) => {
        onResponse(res.data)
      })
      .catch(setError)
      .finally(() => {
        setLoading(false)
      })
  }
  return { togglePermissionsLoading: loading, togglePermissionsError: error, togglePermissions }
}
