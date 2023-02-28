import axios from 'axios'
import { useState } from 'react'
import { ApiPaths } from '../../util/paths'

export const useTokenRefresh = (onError?: (err: any) => void) => {
  const [loading, setLoading] = useState<boolean>(false)
  const refresh = (onSuccess: (token: string) => void) => {
    setLoading(true)
    axios
      .post<string>(ApiPaths.REFRESH)
      .then((res) => {
        onSuccess(res.data)
      })
      .catch(onError)
      .finally(() => setLoading(false))
  }
  return { refresh, loading }
}
