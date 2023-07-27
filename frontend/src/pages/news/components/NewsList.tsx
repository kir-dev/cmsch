import { CloseIcon, SearchIcon } from '@chakra-ui/icons'
import { Divider, Grid, Heading, Input, InputGroup, InputLeftElement, InputRightElement } from '@chakra-ui/react'
import { debounce } from 'lodash'
import { createRef, useMemo, useState } from 'react'

import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { NewsArticleView } from '../../../util/views/news.view'
import NewsListItem from './NewsListItem'

interface NewsListProps {
  newsList: NewsArticleView[]
}

const NewsList = ({ newsList }: NewsListProps) => {
  const config = useConfigContext()
  const [search, setSearch] = useState('')
  const sortedNewsList = useMemo(() => newsList.sort((a, b) => b.timestamp - a.timestamp), [newsList])
  const filteredNews = useMemo(() => {
    return sortedNewsList?.filter((c) => {
      return c.title.toLocaleLowerCase().includes(search)
    })
  }, [sortedNewsList, search])
  const inputRef = createRef<HTMLInputElement>()
  const handleInput = () => {
    const searchFieldValue = inputRef?.current?.value.toLowerCase()
    if (!searchFieldValue) setSearch('')
    else setSearch(searchFieldValue)
  }
  const highlighted = filteredNews.filter((news) => news.highlighted)
  const common = filteredNews.filter((news) => !news.highlighted)

  return (
    <>
      <Heading>{config?.components.news.title}</Heading>
      <InputGroup my={10}>
        <InputLeftElement h="100%">
          <SearchIcon />
        </InputLeftElement>
        <Input ref={inputRef} placeholder="Keresés címre..." size="lg" onChange={debounce(handleInput, 500)} autoFocus={true} />
        {search && (
          <InputRightElement
            h="100%"
            onClick={() => {
              setSearch('')
              if (inputRef.current?.value) inputRef.current.value = ''
            }}
          >
            <CloseIcon />
          </InputRightElement>
        )}
      </InputGroup>
      <Grid templateColumns="repeat(1, 1fr)" gap={4}>
        {highlighted.map((n: NewsArticleView) => (
          <NewsListItem news={n} fontSize="2xl" useLink={config?.components.news.showDetails} key={n.title + n.timestamp} />
        ))}
      </Grid>
      {highlighted.length > 0 && common.length > 0 && <Divider my={10} />}
      <Grid templateColumns="1fr" gap={4}>
        {common.map((n: NewsArticleView) => (
          <NewsListItem news={n} fontSize="xl" useLink={config?.components.news.showDetails} key={n.title + n.timestamp} />
        ))}
      </Grid>
    </>
  )
}

export default NewsList
