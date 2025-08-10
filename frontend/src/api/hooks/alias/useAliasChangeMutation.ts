import { useMutation } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths'
import { QueryKeys } from '../queryKeys'

export const useAliasChangeMutation = () => {
  return useMutation<boolean, Error, string>({
    mutationKey: [QueryKeys.ALIAS_CHANGE],
    mutationFn: async (alias: string) => {
      const res = await axios.put(
        ApiPaths.CHANGE_ALIAS,
        { alias },
        {
          headers: {
            'Content-Type': 'application/json'
          }
        }
      )
      return res.data
    }
  })
}
