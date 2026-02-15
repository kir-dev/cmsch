import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths'
import type { QrDto } from '../../../util/views/qrFight.view'
import { QueryKeys } from '../queryKeys'

export const useQrLevelsQuery = () => {
  return useQuery<QrDto, Error>({
    queryKey: [QueryKeys.QR_LEVELS],
    queryFn: async () => {
      const response = await axios.get<QrDto>(ApiPaths.LEVELS)
      return response.data
    }
  })
}
