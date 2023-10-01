import { CloseIcon, SearchIcon } from '@chakra-ui/icons'
import { Input, InputGroup, InputGroupProps, InputLeftElement, InputRightElement } from '@chakra-ui/react'
import { useSearch } from '../util/useSearch'

interface SearchBarProps extends ReturnType<typeof useSearch>, InputGroupProps {}

export function SearchBar({ inputRef, handleInput, setSearch, search, filteredData, ...inputGroupProps }: SearchBarProps) {
  return (
    <InputGroup {...inputGroupProps}>
      <InputLeftElement h="100%">
        <SearchIcon />
      </InputLeftElement>
      <Input ref={inputRef} placeholder="Keresés..." size="lg" onChange={handleInput} autoFocus={true} />
      {search && (
        <InputRightElement
          h="100%"
          onClick={() => {
            handleInput()
            setSearch('')
            if (inputRef.current?.value) inputRef.current.value = ''
          }}
        >
          <CloseIcon />
        </InputRightElement>
      )}
    </InputGroup>
  )
}
