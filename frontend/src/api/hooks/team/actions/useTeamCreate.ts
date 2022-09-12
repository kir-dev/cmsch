import axios from 'axios'
import { CreateTeamDto, TeamResponses } from '../../../../util/views/team.view'
import { useState } from 'react'

export const useTeamCreate = (onResponse: (response: TeamResponses) => void) => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error>()
  const createTeam = (body: CreateTeamDto) => {
    setLoading(true)
    axios
      .post<TeamResponses>(`/api/team/create`, body)
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
  return { createTeamLoading: loading, createTeamError: error, createTeam }
}
