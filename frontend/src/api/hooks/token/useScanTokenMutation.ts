import { useMutation } from '@tanstack/react-query'
import axios from 'axios'
import { joinPath } from '../../../util/core-functions.util'
import type { ScanResponseView } from '../../../util/views/token.view'

export function useScanTokenMutation(onSuccess?: () => void, onError?: () => void) {
  return useMutation({
    mutationFn: async (qrData: string) => {
      let token
      try {
        token = new URL(qrData).searchParams.get('token')
      } catch (e) {
        console.error(e)
      }
      token = token || qrData
      if (!token) throw new Error()
      const response = await axios.post<ScanResponseView>(joinPath('/api/token', token))
      return response.data
    },
    onSuccess,
    onError
  })
}
