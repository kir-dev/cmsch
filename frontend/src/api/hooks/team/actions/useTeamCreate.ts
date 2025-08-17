import axios from 'axios'
import { useState } from 'react'
import { CreateTeamDto, TeamResponses } from '../../../../util/views/team.view'

export const useTeamCreate = (onResponse: (response: TeamResponses) => void) => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error>()
  const createTeam = (body: CreateTeamDto) => {
    setLoading(true)
    axios //todo
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
