import axios from 'axios'
import { useQuery } from 'react-query'
import { QrDto } from '../../../util/views/qrFight.view'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'

export const useQrLevelsQuery = (onError?: (err: any) => void) => {
  return useQuery<QrDto, Error>(
    QueryKeys.QR_LEVELS,
    async () => {
      const response = await axios.get<QrDto>(ApiPaths.LEVELS)
      return response.data
    },
    { onError: onError }
  )
}
