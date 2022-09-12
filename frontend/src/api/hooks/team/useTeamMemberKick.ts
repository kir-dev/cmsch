import axios from 'axios'
import { TeamResponses } from '../../../util/views/team.view'
import { useState } from 'react'
import { useToast } from '@chakra-ui/react'

export const useTeamMemberKick = (onResponse: (response: TeamResponses) => void) => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error>()
  const toast = useToast()
  const kickMember = (id: number) => {
    setLoading(true)
    axios
      .put<TeamResponses>(`/api/team/admin/kick-user`, id)
      .then((res) => {
        onResponse(res.data)
      })
      .catch((err) => {
        setError(err)
        toast({ status: 'error', title: 'Sikertelen!' })
      })
      .finally(() => {
        setLoading(false)
      })
  }
  return { kickLoading: loading, kickError: error, kickMember }
}
