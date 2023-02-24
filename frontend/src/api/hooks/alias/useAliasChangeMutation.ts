import axios from 'axios'
import { useMutation } from 'react-query'
import { ApiPaths } from '../../../util/paths'
import { QueryKeys } from '../queryKeys'

export const useAliasChangeMutation = () => {
  return useMutation<boolean, Error, string>(QueryKeys.ALIAS_CHANGE, async (alias: string) => {
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
  })
}
