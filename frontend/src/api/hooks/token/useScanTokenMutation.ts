import { useMutation } from 'react-query'
import axios from 'axios'
import { joinPath } from '../../../util/core-functions.util'
import { ScanResponseView } from '../../../util/views/token.view'

export function useScanTokenMutation(onSuccess?: () => void, onError?: () => void) {
  return useMutation({
    mutationFn: async (qrData: string) => {
      const token = new URL(qrData).searchParams.get('token')
      if (!token) throw new Error()
      const response = await axios.post<ScanResponseView>(joinPath('/api/token', token))
      return response.data
    },
    onSuccess,
    onError
  })
}
