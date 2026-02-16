import axios from 'axios'
import { useState } from 'react'
import { type TeamEditDto, TeamResponses } from '../../../../util/views/team.view'

export const useTeamEdit = (onResponse: (response: TeamResponses) => void) => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error>()
  const teamEdit = (dto: TeamEditDto) => {
    setLoading(true)
    const data = new FormData()
    data.append('description', dto.description)
    if (dto.logo) {
      data.append('logo', dto.logo)
    }
    axios //todo
      .post<TeamResponses>(`/api/team/edit`, data)
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
  return { teamEditLoading: loading, teamEditError: error, teamEdit }
}
