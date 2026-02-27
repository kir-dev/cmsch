import debounce from 'lodash/debounce'
import { Search, X } from 'lucide-react'
import { createRef, useMemo, useState } from 'react'

import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { Input } from '@/components/ui/input'
import { Separator } from '@/components/ui/separator'
import type { NewsArticleView } from '@/util/views/news.view'
import { NewsListItem } from './NewsListItem'

interface NewsListProps {
  newsList: NewsArticleView[]
}

const NewsList = ({ newsList }: NewsListProps) => {
  const news = useConfigContext()?.components?.news
  const [search, setSearch] = useState('')
  const sortedNewsList = useMemo(() => newsList.sort((a, b) => b.timestamp - a.timestamp), [newsList])
  const filteredNews = useMemo(() => {
    return sortedNewsList?.filter((c) => {
      return c.title.toLocaleLowerCase().includes(search)
    })
  }, [sortedNewsList, search])
  const inputRef = createRef<HTMLInputElement>()
  const clearSearch = () => {
    setSearch('')
    if (inputRef.current?.value) inputRef.current.value = ''
  }
  const handleInput = () => {
    const searchFieldValue = inputRef?.current?.value.toLowerCase()
    if (!searchFieldValue) setSearch('')
    else setSearch(searchFieldValue)
  }
  const highlighted = filteredNews.filter((news) => news.highlighted)
  const common = filteredNews.filter((news) => !news.highlighted)

  return (
    <>
      <h1 className="mb-5 text-4xl font-bold tracking-tight">{news?.title}</h1>
      <div className="relative my-10 flex items-center">
        <Search className="absolute left-3 h-4 w-4 text-muted-foreground" />
        <Input
          ref={inputRef}
          placeholder="Keresés címre..."
          className="h-12 pl-10 pr-10"
          // eslint-disable-next-line react-hooks/refs
          onChange={debounce(handleInput, 500)}
          autoFocus={true}
        />
        {search && (
          <button onClick={clearSearch} className="absolute right-3 focus:outline-none">
            <X className="h-4 w-4 text-muted-foreground" />
          </button>
        )}
      </div>
      <div className="grid grid-cols-1 gap-4">
        {highlighted.map((n: NewsArticleView) => (
          <NewsListItem news={n} fontSize="2xl" useLink={news?.showDetails} key={n.title + n.timestamp} />
        ))}
      </div>
      {highlighted.length > 0 && common.length > 0 && <Separator className="my-10" />}
      <div className="grid grid-cols-1 gap-4">
        {common.map((n: NewsArticleView) => (
          <NewsListItem news={n} fontSize="xl" useLink={news?.showDetails} key={n.title + n.timestamp} />
        ))}
      </div>
    </>
  )
}

export default NewsList
