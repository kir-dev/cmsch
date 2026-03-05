import { Input } from '@/components/ui/input'
import { cn } from '@/lib/utils'
import { useSearch } from '@/util/useSearch'
import { Search, X } from 'lucide-react'

interface SearchBarProps extends ReturnType<typeof useSearch> {
  className?: string
  mb?: number | string
}

export function SearchBar({ inputRef, handleInput, setSearch, search, className }: SearchBarProps) {
  const clearSearch = () => {
    handleInput()
    setSearch('')
    if (inputRef.current) inputRef.current.value = ''
  }
  return (
    <div className={cn('relative flex items-center', className)}>
      <Search className="absolute left-3 h-4 w-4 text-muted-foreground" />
      <Input ref={inputRef} placeholder="Keresés..." className="h-12 pl-10 pr-10" onChange={handleInput} autoFocus={true} />
      {search && (
        <button onClick={clearSearch} className="absolute right-3 focus:outline-none">
          <X className="h-4 w-4 text-muted-foreground" />
        </button>
      )}
    </div>
  )
}
