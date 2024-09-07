import { useQuery } from '@tanstack/react-query'
import { QueryKeys } from '../queryKeys.ts'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths.ts'
import { GalleryView } from '../../../util/views/gallery.view.ts'

export const useHomeGallery = () => {
  return useQuery<GalleryView, Error>({
    queryKey: [QueryKeys.HOME_GALLERY],
    queryFn: async () => {
      const response = await axios.get<GalleryView>(ApiPaths.HOME_GALLERY)
      return response.data
    }
  })
}
