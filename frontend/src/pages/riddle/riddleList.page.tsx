import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useRiddleListQuery } from '@/api/hooks/riddle/useRiddleListQuery'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { PageStatus } from '@/common-components/PageStatus'
import { Button } from '@/components/ui/button'
import { useToast } from '@/hooks/use-toast'
import { l } from '@/util/language'
import { AbsolutePaths } from '@/util/paths'
import type { RiddleCategory } from '@/util/views/riddle.view.ts'
import { Link, useNavigate } from 'react-router'
import { RiddleCategoryListItem } from './components/RiddleCategoryListItem'

const RiddleCategoryList = () => {
  const navigate = useNavigate()
  const { toast } = useToast()
  const component = useConfigContext()?.components?.riddle
  const { isLoading, isError, data } = useRiddleListQuery()

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  const onRiddleCategoryClick = (category: RiddleCategory) => {
    if (category.nextRiddles.length > 1) {
      navigate(`${AbsolutePaths.RIDDLE}/category/${category.categoryId}`)
    } else if (category.nextRiddles[0]) {
      navigate(`${AbsolutePaths.RIDDLE}/solve/${category.nextRiddles[0].id}`)
    } else {
      toast({
        title: l('riddle-completed-category-title'),
        description: l('riddle-completed-category-description')
      })
    }
  }

  return (
    <CmschPage title={component?.title}>
      <div className="flex flex-col justify-between md:flex-row md:items-end">
        <h1 className="mb-5 text-4xl font-bold tracking-tight">Riddleök</h1>
        <Button asChild>
          <Link to={AbsolutePaths.RIDDLE_HISTORY}>Megoldott riddleök</Link>
        </Button>
      </div>
      <div className="mt-5 flex flex-col gap-4">
        {(data ?? []).length > 0 ? (
          data.map((riddleCategory) => (
            <RiddleCategoryListItem
              category={riddleCategory}
              key={riddleCategory.categoryId}
              onClick={() => onRiddleCategoryClick(riddleCategory)}
            />
          ))
        ) : (
          <p>Nincs egyetlen riddle feladat sem.</p>
        )}
      </div>
    </CmschPage>
  )
}

export default RiddleCategoryList
