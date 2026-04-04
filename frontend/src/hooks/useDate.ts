import { useCallback, useEffect, useState } from 'react'

export const useRefreshedInterval = <T>(supplier: () => T, refreshIntervalMs?: number): T => {
  const [data, setData] = useState<T>(supplier)

  useEffect(() => {
    if (refreshIntervalMs === undefined) return
    const interval = setInterval(() => setData(supplier), refreshIntervalMs)
    return () => clearInterval(interval)
  }, [refreshIntervalMs, supplier])

  return data
}

export const useDate = (refreshIntervalMs?: number) => {
  const dateSupplier = useCallback(() => new Date(), [])
  return useRefreshedInterval(dateSupplier, refreshIntervalMs)
}

export const useTime = (refreshIntervalMs?: number) => {
  const timeSupplier = useCallback(() => Date.now(), [])
  return useRefreshedInterval(timeSupplier, refreshIntervalMs)
}

export const useTimeInSeconds = (refreshIntervalMs?: number) => {
  const timeInSecondsSupplier = useCallback(() => Date.now() / 1000, [])
  return useRefreshedInterval(timeInSecondsSupplier, refreshIntervalMs)
}
