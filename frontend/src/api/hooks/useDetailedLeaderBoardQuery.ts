import axios from 'axios'
import { useQuery } from 'react-query'
import { DetailedLeaderBoardView } from '../../util/views/leaderBoardView'

export const useDetailedLeaderBoardQuery = (onError: (err: any) => void) => {
  async function fetchLeaderBoard() {
    const result = await axios.get<DetailedLeaderBoardView>(`/api/detailed-leaderboard`)
    return result.data
  }

  return useQuery<DetailedLeaderBoardView, Error, DetailedLeaderBoardView>(['leaderboard'], fetchLeaderBoard, { onError: onError })
}
