import axios from 'axios'
import { useQuery } from 'react-query'
import { LeaderBoardView } from '../../../util/views/leaderBoardView'
import { joinPath } from '../../../util/core-functions.util'
import { QueryKeys } from '../queryKeys'

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

export const useLeaderBoardQuery = (type: 'short' | 'detailed' | 'categorized' = 'short', onError?: (err: any) => void) => {
  let url = 'leaderboard'
  switch (type) {
    case 'detailed':
      url = 'detailed-leaderboard'
      break
    case 'categorized':
      url = 'detailed-leaderboard-by-category'
      break
  }
  async function fetchLeaderBoard() {
    const result = await axios.get<TempLeaderBoardView>(joinPath('/api', url))
    return {
      ...result.data,
      userBoard: result.data.userBoard ? result.data.userBoard.map(mapBoard) : undefined,
      groupBoard: result.data.groupBoard ? result.data.groupBoard.map(mapBoard) : undefined
    }
  }

  return useQuery<TempLeaderBoardView, Error, LeaderBoardView>(QueryKeys.LEADERBOARD, fetchLeaderBoard, { onError: onError })
}

const mapBoard = (boardItems: TempLeaderBoardItemView) => ({
  ...boardItems,
  items: Object.entries(boardItems.items || {}).map(([key, value]) => ({ name: key, value }))
})
