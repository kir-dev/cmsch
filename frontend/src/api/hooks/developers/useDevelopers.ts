import { useConfigContext } from '../../contexts/config/ConfigContext'
import { useMemo } from 'react'

export type Dev = { name: string; img: string; tags: string[] }

export const useDevelopers = () => {
  const config = useConfigContext()
  const impressumConfig = config?.components.impressum
  return useMemo<Dev[]>(() => {
    return [
      {
        name: 'Schámi',
        img: impressumConfig?.developerSchamiUrl,
        tags: ['Project Leader', 'Backend']
      },
      {
        name: 'Bálint',
        img: impressumConfig?.developerBalintUrl,
        tags: ['Frontend Leader']
      },
      {
        name: 'Laci',
        img: impressumConfig?.developerLaciUrl,
        tags: ['Frontend']
      },
      {
        name: 'Beni',
        img: impressumConfig?.developerBeniUrl,
        tags: ['Frontend']
      },
      {
        name: 'Trisz',
        img: impressumConfig?.developerTriszUrl,
        tags: ['Frontend']
      },
      {
        name: 'Samu',
        img: impressumConfig?.developerSamuUrl,
        tags: ['Frontend']
      },
      {
        name: 'Dani',
        img: impressumConfig?.developerDaniUrl,
        tags: ['Frontend']
      },
      {
        name: 'Máté',
        img: impressumConfig?.developerMateUrl,
        tags: ['Frontend']
      }
    ] as Dev[]
  }, [impressumConfig])
}
