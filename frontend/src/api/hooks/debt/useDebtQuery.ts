import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths'
import type { DebtDto } from '@/util/views/debt.view.ts'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useDebtQuery = () => {
  return useQuery<DebtDto, Error>({
    queryKey: [QueryKeys.DEBTS],
    queryFn: async () => {
      const response = await axios.get<DebtDto>(ApiPaths.DEBTS)
      return response.data
    },
    refetchOnMount: 'always'
  })
}
