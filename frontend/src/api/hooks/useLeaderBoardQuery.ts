import axios from 'axios'
import { useQuery } from 'react-query'
import { LeaderBoardView } from '../../util/views/leaderBoardView'

type TempLeaderBoardItemView = {
  id?: number
  name: string
  groupName: string
  score?: number
  items?: Object
  total?: number
}

export type TempLeaderBoardView = {
  userScore?: number
  userBoard?: Array<TempLeaderBoardItemView>
  groupScore?: number
  groupBoard?: Array<TempLeaderBoardItemView>
}

export const useLeaderBoardQuery = (onError: (err: any) => void, detailed: boolean = false) => {
  async function fetchLeaderBoard() {
    const result = await axios.get<TempLeaderBoardView>(`/api/${detailed ? 'detailed-' : ''}leaderboard`)
    return {
      ...result.data,
      userBoard: result.data.userBoard
        ? result.data.userBoard.map((boardItems) => ({
            ...boardItems,
            items: Object.entries(boardItems.items || {}).map(([key, value]) => ({ name: key, value }))
          }))
        : undefined,
      groupBoard: result.data.groupBoard
        ? result.data.groupBoard.map((boardItems) => ({
            ...boardItems,
            items: Object.entries(boardItems.items || {}).map(([key, value]) => ({ name: key, value }))
          }))
        : undefined
    }
  }

  return useQuery<TempLeaderBoardView, Error, LeaderBoardView>(['leaderboard'], fetchLeaderBoard, { onError: onError })
}
