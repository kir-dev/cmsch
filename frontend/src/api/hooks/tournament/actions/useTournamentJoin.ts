import axios from 'axios'
import { TournamentResponses } from '../../../../util/views/tournament.view'
import { useState } from 'react'

export const useTournamentJoin = (onResponse: (response: TournamentResponses) => void) => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error>()

  const joinTournament = (id: number) => {
    setLoading(true)
    axios
      .post<TournamentResponses>(`/api/tournament/register`, { id })
      .then((res) => {
        onResponse(res.data)
      })
      .catch((err) => {
        setError(err)
        onResponse(TournamentResponses.ERROR)
      })
      .finally(() => {
        setLoading(false)
      })
  }

  return { joinTournamentLoading: loading, joinTournamentError: error, joinTournament }
}
