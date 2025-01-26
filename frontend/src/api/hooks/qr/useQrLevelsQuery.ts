import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { QrDto } from '../../../util/views/qrFight.view'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'

export const useQrLevelsQuery = () => {
  return useQuery<QrDto, Error>({
    queryKey: [QueryKeys.QR_LEVELS],
    queryFn: async () => {
      const response = await axios.get<QrDto>(ApiPaths.LEVELS)
      return response.data
    }
  })
}
