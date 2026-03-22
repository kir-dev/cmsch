import Markdown from '@/common-components/Markdown'
import { cn } from '@/lib/utils'
import { stringifyTimeStamp } from '@/util/core-functions.util'
import { AbsolutePaths } from '@/util/paths'
import type { NewsArticleView } from '@/util/views/news.view'
import { FaExclamation } from 'react-icons/fa'
import { Link } from 'react-router'

type Props = {
  news: NewsArticleView
  fontSize: string
  useLink?: boolean
}

export const NewsListItem = ({ news, fontSize, useLink }: Props) => {
  return (
    <div
      className={cn(
        'rounded-md border p-4 transition-colors',
        news.highlighted ? 'border-primary' : 'border-border',
        useLink ? 'hover:bg-accent/50' : ''
      )}
    >
      <div className="flex flex-col-reverse gap-4 md:flex-row">
        {news.imageUrl && <img className="h-32 w-32 rounded-md object-cover object-center" src={news.imageUrl} alt={news.title} />}
        <div className="w-full">
          <div className="flex items-center justify-between">
            {!!news.timestamp && <div className="mb-2 text-sm font-light">Közzétéve: {stringifyTimeStamp(news.timestamp)}</div>}
            {news.highlighted && (
              <div>
                <FaExclamation className="h-8 w-8 text-primary" />
              </div>
            )}
          </div>
          <h2 className={cn('my-2 font-bold tracking-tight', fontSize === '2xl' ? 'text-2xl' : 'text-xl')}>
            {useLink ? (
              <Link to={`${AbsolutePaths.NEWS}/${news.url}`} className="hover:underline">
                {news.title}
              </Link>
            ) : (
              news.title
            )}
          </h2>
        </div>
      </div>
      {news.briefContent && (
        <div className="mt-3">
          <Markdown text={news.briefContent} />
        </div>
      )}
    </div>
  )
}
