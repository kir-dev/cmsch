import axios from 'axios'
import { useQuery } from 'react-query'
import { ExtraPageDto, ExtraPageView } from '../../../util/views/extraPage.view'
import { QueryKeys } from '../queryKeys'
import { joinPath } from '../../../util/core-functions.util'
import { ApiPaths } from '../../../util/paths'

export const useExtraPage = (slug: string, onError?: (err: any) => void) => {
  return useQuery<ExtraPageView, Error>(
    [QueryKeys.EXTRA, slug],
    async () => {
      const response = await axios.get<ExtraPageDto>(joinPath(ApiPaths.PAGE, slug))
      return response.data.page
    },
    { onError: onError }
  )
}
