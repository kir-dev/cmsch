import { createRef, useEffect, useState } from 'react'

export function useSearch<T>(data: T[], searchFn: (data: T, searchWord: string) => boolean) {
  const inputRef = createRef<HTMLInputElement>()
  const [search, setSearch] = useState('')
  const [filteredData, setFilteredData] = useState(data)
  const handleInput = () => {
    if (inputRef.current) setSearch(inputRef.current.value.toLowerCase())
  }

  useEffect(() => {
    if (!data || !search) {
      setFilteredData(data)
    } else {
      setFilteredData((d) => d.filter((item) => searchFn(item, search)))
    }
  }, [search, data])

  useEffect(() => {
    if (data) {
      if (inputRef.current) inputRef.current.value = ''
      setSearch('')
    }
  }, [data])

  return { inputRef, filteredData, handleInput, setSearch, search }
}
