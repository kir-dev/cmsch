import { CustomBreadcrumb } from '@/common-components/CustomBreadcrumb'
import Markdown from '@/common-components/Markdown'
import { Button } from '@/components/ui/button'
import { stringifyTimeStamp } from '@/util/core-functions.util'
import { AbsolutePaths } from '@/util/paths'
import type { NewsArticleView } from '@/util/views/news.view'
import { FaArrowLeft } from 'react-icons/fa'
import { Link } from 'react-router'

interface NewsProps {
  news: NewsArticleView
}

const News = ({ news }: NewsProps) => {
  const breadcrumbItems = [
    {
      title: 'Hírek',
      to: AbsolutePaths.NEWS
    },
    {
      title: news.title
    }
  ]
  return (
    <>
      <CustomBreadcrumb items={breadcrumbItems} />
      <p className="text-right text-xs font-light">{stringifyTimeStamp(news.timestamp)}</p>
      <h1 className="mb-2 text-4xl font-bold tracking-tight">{news.title}</h1>
      {news.imageUrl && <img className="mx-auto mb-4 block max-h-[20rem] max-w-full rounded-md" src={news.imageUrl} alt={news.title} />}
      <Markdown text={news.content} />
      <Button asChild className="mt-4">
        <Link to={AbsolutePaths.NEWS}>
          <FaArrowLeft className="mr-2" />
          Vissza a hírekhez
        </Link>
      </Button>
    </>
  )
}

export default News
