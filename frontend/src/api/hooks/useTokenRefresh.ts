import axios from 'axios'
import { useState } from 'react'

export const useTokenRefresh = (onError?: (err: any) => void) => {
  const [loading, setLoading] = useState<boolean>(false)
  const refresh = (onSuccess: (token: string) => void) => {
    setLoading(true)
    axios
      .post<string>(`/api/control/refresh`)
      .then((res) => {
        onSuccess(res.data)
      })
      .catch(onError)
      .finally(() => setLoading(false))
  }
  return { refresh, loading }
}
