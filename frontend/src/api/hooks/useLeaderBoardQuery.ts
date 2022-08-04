import axios from 'axios'
import { useQuery } from 'react-query'
import { LeaderBoardView } from '../../util/views/leaderBoardView'

export const useLeaderBoardQuery = (onError: (err: any) => void) => {
  async function fetchLeaderBoard() {
    const result = await axios.get<LeaderBoardView>(`/api/leaderboard`)
    return result.data
  }

  return useQuery<LeaderBoardView, Error, LeaderBoardView>(['leaderboard'], fetchLeaderBoard, { onError: onError })
}
