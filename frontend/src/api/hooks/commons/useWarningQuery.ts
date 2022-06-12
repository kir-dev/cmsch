import axios from 'axios'
import { useQuery } from 'react-query'
import { WarningView } from '../../../util/views/warning.view'

export const useWarningQuery = () => {
  return useQuery<WarningView>(['warning'], async () => {
    const response = await axios.get('/api/warning')
    return response.data
  })
}
