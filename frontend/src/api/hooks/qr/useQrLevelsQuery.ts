import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths'
import type { QrDto } from '@/util/views/qrFight.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useQrLevelsQuery = () => {
  return useQuery<QrDto, Error>({
    queryKey: [QueryKeys.QR_LEVELS],
    queryFn: async () => {
      const response = await axios.get<QrDto>(ApiPaths.LEVELS)
      return response.data
    }
  })
}
