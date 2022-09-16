import axios from 'axios'
import { useQuery } from 'react-query'
import { QrDto } from '../../util/views/qrFight.view'

export const useQrLevelsQuery = (onError?: (err: any) => void) => {
  return useQuery<QrDto, Error, QrDto>(
    'qrLevels',
    async () => {
      const response = await axios.get<QrDto>(`/api/levels`)
      return response.data
    },
    { onError: onError }
  )
}
