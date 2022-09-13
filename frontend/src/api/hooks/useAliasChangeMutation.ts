import axios from 'axios'
import { useMutation } from 'react-query'

export const useAliasChangeMutation = () => {
  return useMutation<boolean, Error, string>('aliasChange', async (alias: string) => {
    const res = await axios.put(
      `/api/profile/change-alias`,
      { alias },
      {
        headers: {
          'Content-Type': 'application/json'
        }
      }
    )
    return res.data
  })
}
