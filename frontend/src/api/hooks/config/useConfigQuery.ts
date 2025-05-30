import axios, { AxiosError } from 'axios'
import { useQuery } from '@tanstack/react-query'
import { ConfigDto } from '../../contexts/config/types'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'
import { APP_CONFIG_CACHE_TTL_SECONDS, DISABLE_APP_CONFIG_CACHE } from '../../../util/configs/environment.config.ts'

export const useConfigQuery = () => {
  return useQuery<ConfigDto, AxiosError>({
    initialData: getCachedConfig(),
    queryKey: [QueryKeys.CONFIG],
    queryFn: async () => {
      const response = await axios.get<ConfigDto>(ApiPaths.CONFIG)
      if (response.data) {
        saveCachedConfig(response.data)
      }
      return response.data
    }
  })
}

const CacheKey = '__configQueryCache'

const getCachedConfig = (): ConfigDto | undefined => {
  if (DISABLE_APP_CONFIG_CACHE) return
  if (isNaN(APP_CONFIG_CACHE_TTL_SECONDS)) return

  try {
    const cachedJson = localStorage.getItem(CacheKey)
    if (!cachedJson) return

    const now = new Date().getTime()
    const { savedAt, data } = JSON.parse(cachedJson)
    if (savedAt + APP_CONFIG_CACHE_TTL_SECONDS * 1000 < now) {
      return
    }
    return data
  } catch (error) {
    console.error('Failed to parse cached app config', error)
  }
  return
}

const saveCachedConfig = (config: ConfigDto) =>
  localStorage.setItem(
    CacheKey,
    JSON.stringify({
      savedAt: new Date().getTime(),
      data: config
    })
  )
